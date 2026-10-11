package com.reevil.entity.neptune;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Neptune: giant mutated shark of the Spencer estate's aqua ring. Water-bound (smooth swimming, dries out and flops on land).
 * Moves: lunging bite from below (latches and drags), breach-and-snap at shore targets, death roll (chained when latched),
 * tail slap sweep (knocks victims off boats), circling feint. Form 1 = wounded retreat: dives deep and regenerates.
 */
public class NeptuneEntity extends BioMonster {
    public static final int BITE = 1, BREACH = 2, ROLL = 3, SLAP = 4, CIRCLE = 5;
    static final AttackMove M_BITE = new AttackMove(BITE, "lunge_bite", 30, new int[]{18}, 1.5, 10, 30, 5).dmg(8f).kb(0).face(18);
    static final AttackMove M_BREACH = new AttackMove(BREACH, "breach_snap", 40, new int[]{24}, 0.5, 5, 60, 4).dmg(10f).kb(0).move();
    static final AttackMove M_ROLL = new AttackMove(ROLL, "death_roll", 50, new int[]{10, 20, 30, 40}, 0, 99, 40, 1).dmg(5f).kb(0).face(0);
    static final AttackMove M_SLAP = new AttackMove(SLAP, "tail_slap", 30, new int[]{15}, 0, 3.5, 35, 3).dmg(6f).kb(1.2).face(8);
    static final AttackMove M_CIRCLE = new AttackMove(CIRCLE, "circle_feint", 60, new int[]{}, 4, 18, 10, 3).face(0).move();
    private static final List<AttackMove> MOVES = List.of(M_BITE, M_BREACH, M_ROLL, M_SLAP, M_CIRCLE);

    private LivingEntity latched;
    private Vec3 breachOrigin;
    private int dryTicks, retreats;

    public NeptuneEntity(EntityType<NeptuneEntity> type, Level level) {
        super(type, level);
        xpReward = 30;
        moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        lookControl = new SmoothSwimmingLookControl(this, 10);
        setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 90)
            .add(Attributes.MOVEMENT_SPEED, 1.3)
            .add(Attributes.ATTACK_DAMAGE, 8)
            .add(Attributes.ARMOR, 4)
            .add(Attributes.FOLLOW_RANGE, 32)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void registerGoals() { // no FloatGoal / land strolling: it lives under water
        goalSelector.addGoal(1, new RetreatGoal());
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.3));
        goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0, 40));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
            e -> e.isInWater() || e.distanceToSqr(this) < 100));
    }

    @Override
    protected PathNavigation createNavigation(Level level) { return new WaterBoundPathNavigation(this, level); }

    @Override
    public void travel(Vec3 input) { // dolphin-style swimming
        if (isEffectiveAi() && isInWater()) {
            moveRelative(getSpeed(), input);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9));
            if (getTarget() == null && !isMoving()) setDeltaMovement(getDeltaMovement().add(0, -0.005, 0));
        } else {
            super.travel(input);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        // stranded: dries out and flops back toward the water it came from
        if (!isInWaterOrBubble()) {
            dryTicks++;
            if (dryTicks > 60 && dryTicks % 20 == 0) hurt(damageSources().dryOut(), 2f);
            if (onGround() && !isMoving() && getRandom().nextInt(8) == 0) {
                Vec3 dir = breachOrigin != null ? breachOrigin.subtract(position()).multiply(1, 0, 1) : new Vec3(getRandom().nextDouble() - 0.5, 0, getRandom().nextDouble() - 0.5);
                dir = dir.normalize().scale(0.3);
                setDeltaMovement(dir.x, 0.4, dir.z);
                hasImpulse = true;
                playSound(SoundEvents.GUARDIAN_FLOP, 1.2f, 0.5f);
            }
        } else {
            dryTicks = 0;
        }
        // wounded: retreat to the depths and regenerate (twice per life)
        if (getForm() != 1 && retreats < 2 && isAlive() && getHealth() < getMaxHealth() * 0.3f) {
            setForm(1);
            retreats++;
            cancelMove();
            release();
        }
        if (getForm() == 1) {
            if (tickCount % 10 == 0 && isInWater()) heal(1.5f);
            if (getHealth() >= getMaxHealth() * 0.7f) setForm(2);
        }
    }

    // ---- moves
    @Override
    public List<AttackMove> moves() { return MOVES; }

    private static boolean wet(LivingEntity t) { return t.isInWater() || t.getVehicle() instanceof Boat; }

    @Override
    public boolean canStartMove(AttackMove m) {
        LivingEntity t = getTarget();
        if (getForm() == 1 || !isInWater() || t == null || m == M_ROLL) return false;
        if (m == M_BREACH) return !wet(t) && t.getY() > getY() - 1 && t.getY() < getY() + 5;
        if (m == M_CIRCLE) return wet(t) && getRandom().nextInt(3) == 0;
        return wet(t); // bite, slap
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0);
        setXRot(0);
        if (m == M_BREACH) breachOrigin = position();
        if (m != M_ROLL) latched = null;
        if (m == M_ROLL) playSound(SoundEvents.GUARDIAN_ATTACK, 1.5f, 0.5f);
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        if ((m == M_BITE || m == M_BREACH) && latched != null && latched.isAlive()) startMove(M_ROLL);
        else release();
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case BITE -> biteTick(t, target);
            case BREACH -> breachTick(t, target);
            case ROLL -> {
                setDeltaMovement(0, isInWater() ? -0.06 : -0.1, 0); // pull the victim under
                dragLatched(-0.03);
                if (t % 3 == 0) bubbles(6);
            }
            case CIRCLE -> circleTick(t, target);
            default -> { }
        }
    }

    private void biteTick(int t, LivingEntity target) {
        if (latched != null) { dragLatched(-0.04); return; }
        if (target == null) return;
        Vec3 to = target.position().add(0, target.getBbHeight() * 0.4, 0).subtract(position().add(0, getBbHeight() * 0.5, 0));
        if (t <= 10) { // coil: sink and back off below the prey
            Vec3 back = to.multiply(1, 0, 1).normalize().scale(-0.06);
            setDeltaMovement(back.x, -0.05, back.z);
        } else if (t <= 18) { // explosive burst up at it
            Vec3 v = to.normalize().scale(0.85);
            setDeltaMovement(v);
            if (t == 11) playSound(SoundEvents.GENERIC_SPLASH, 1.2f, 0.6f);
            bubbles(4);
        }
    }

    private void breachTick(int t, LivingEntity target) {
        if (latched != null) { dragLatched(0); return; }
        if (target == null) return;
        Vec3 h = target.position().subtract(position()).multiply(1, 0, 1);
        if (t <= 12) {
            Vec3 b = h.normalize().scale(-0.05);
            setDeltaMovement(b.x, -0.04, b.z);
        } else if (t == 13) { // leap fully out of the water at the shore
            double sp = Mth.clamp(h.length() * 0.11, 0.35, 0.8);
            Vec3 n = h.normalize();
            setDeltaMovement(n.x * sp, 0.78, n.z * sp);
            hasImpulse = true;
            playSound(SoundEvents.GENERIC_SPLASH, 2f, 0.5f);
            if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 0.5, getZ(), 60, 1.2, 0.3, 1.2, 0.3);
        }
    }

    private void circleTick(int t, LivingEntity target) {
        if (target == null) return;
        Vec3 r = position().subtract(target.position()).multiply(1, 0, 1);
        double dist = r.length();
        if (dist < 0.1) return;
        Vec3 n = r.scale(1 / dist);
        Vec3 v;
        if (t >= 26 && t <= 32) v = n.scale(-0.35);           // feint: dart at the prey...
        else if (t > 32 && t <= 38) v = n.scale(0.3);          // ...and veer away
        else v = new Vec3(-n.z, 0, n.x).scale(0.28).add(n.scale((6.0 - dist) * 0.05)); // orbit at ~6 blocks
        double vy = Mth.clamp((target.getY() - 1.5 - getY()) * 0.03, -0.08, 0.08);
        setDeltaMovement(v.x, vy, v.z);
        float yaw = (float) (Mth.atan2(v.z, v.x) * Mth.RAD_TO_DEG) - 90f;
        setYRot(yaw);
        yBodyRot = yaw;
        yHeadRot = yaw;
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        if (m == M_ROLL) {
            if (latched != null && latched.distanceTo(this) < 4) {
                latched.hurt(damageSources().mobAttack(this), m.damage);
                latched.setAirSupply(Math.max(0, latched.getAirSupply() - 60));
                playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.2f, 0.6f);
                if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, latched.getX(), latched.getY() + 0.5, latched.getZ(), 6, 0.3, 0.3, 0.3, 0.1);
            }
            return;
        }
        if (m == M_SLAP) { tailSlap(m); return; }
        if (target == null) return;
        if (m == M_BITE || m == M_BREACH) {
            playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.5f, 0.5f);
            if (distanceTo(target) <= 3.2 + target.getBbWidth() * 0.5 && strike(target, m.damage, 0)) {
                target.stopRiding(); // snatched out of the boat / off the ledge
                latched = target;
            }
        }
    }

    private void tailSlap(AttackMove m) {
        playSound(SoundEvents.GENERIC_SPLASH, 2f, 0.4f);
        if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 1, getZ(), 80, 2.0, 0.4, 2.0, 0.4);
        for (Entity e : level().getEntities(this, getBoundingBox().inflate(3.5, 1.5, 3.5))) {
            Vec3 d = e.position().subtract(position()).multiply(1, 0, 1).normalize().scale(1.3);
            if (e instanceof Boat) {
                e.ejectPassengers();
                e.push(d.x, 0.3, d.z);
            } else if (e instanceof LivingEntity le && !(e instanceof NeptuneEntity)) {
                le.stopRiding();
                strike(le, m.damage, m.knockback);
            }
        }
    }

    /** Hold the latched victim in the jaws (in front of the snout), optionally sinking it. */
    private void dragLatched(double sink) {
        if (latched == null) return;
        if (!latched.isAlive() || latched.distanceTo(this) > 6) { release(); return; }
        Vec3 fwd = Vec3.directionFromRotation(0, getYRot());
        Vec3 mouth = position().add(fwd.scale(getBbWidth() * 0.5 + 0.9)).add(0, 0.2, 0);
        Vec3 pull = mouth.subtract(latched.position()).scale(0.5);
        latched.setDeltaMovement(pull.x, pull.y + sink, pull.z);
        latched.hurtMarked = true;
        latched.fallDistance = 0;
    }

    private void release() {
        if (latched != null && latched.isAlive()) {
            Vec3 d = latched.position().subtract(position()).multiply(1, 0, 1).normalize().scale(0.4);
            latched.push(d.x, 0.2, d.z);
            latched.hurtMarked = true;
        }
        latched = null;
    }

    private void bubbles(int n) {
        if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.BUBBLE, getX(), getY() + 0.6, getZ(), n, 0.8, 0.4, 0.8, 0.05);
    }

    /** Form 1: swim away from the threat and down, regenerating (see aiStep). Blocks BioCombatGoal via the MOVE flag. */
    private class RetreatGoal extends Goal {
        RetreatGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() { return getForm() == 1; }

        @Override
        public void tick() {
            if (getNavigation().isDone()) {
                LivingEntity t = getTarget();
                Vec3 away = t != null ? position().subtract(t.position()).multiply(1, 0, 1) : Vec3.directionFromRotation(0, getYRot());
                if (away.lengthSqr() < 1e-4) away = new Vec3(1, 0, 0);
                BlockPos p = BlockPos.containing(position().add(away.normalize().scale(12)).add(0, -6, 0));
                getNavigation().moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, 1.5);
            }
            if (isInWater()) setDeltaMovement(getDeltaMovement().add(0, -0.01, 0));
            if (tickCount % 5 == 0) bubbles(2);
        }
    }

    @Override
    public void die(DamageSource src) {
        release();
        super.die(src);
    }

    @Override public boolean canBreatheUnderwater() { return true; }
    @Override public boolean isPushedByFluid() { return false; }
    @Override public MobType getMobType() { return MobType.WATER; }
    @Override public int getMaxHeadXRot() { return 1; }
    @Override public int getMaxHeadYRot() { return 1; }
    @Override protected SoundEvent getAmbientSound() { return isInWater() ? SoundEvents.GUARDIAN_AMBIENT : SoundEvents.GUARDIAN_AMBIENT_LAND; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return isInWater() ? SoundEvents.GUARDIAN_HURT : SoundEvents.GUARDIAN_HURT_LAND; }
    @Override protected SoundEvent getDeathSound() { return isInWater() ? SoundEvents.GUARDIAN_DEATH : SoundEvents.GUARDIAN_DEATH_LAND; }
    @Override protected float getSoundVolume() { return 1.6f; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Retreats", retreats);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        retreats = tag.getInt("Retreats");
    }
}
