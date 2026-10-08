package com.reevil.entity.crow;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Infected crow. Flies (FlyingMoveControl + FlyingPathNavigation, so BioCombatGoal's moveTo works in the air),
 * perches / hops / preens on the ground when idle (form 0 = grounded, 1 = airborne; the model blends on it).
 * Moves: circling dive-bomb, hovering peck barrage, talon strike, scatter when hurt. Flock shares targets.
 */
public class CrowEntity extends BioMonster {
    public static final int DIVE = 1, PECK = 2, TALON = 3, SCATTER = 4;

    // hitTicks mirrored by impact keys in CrowModel (hitTick / duration).
    static final AttackMove M_DIVE = new AttackMove(DIVE, "dive_bomb", 44, new int[]{30}, 2.0, 12, 30, 3).dmg(3f).kb(0.3).face(44).move();
    static final AttackMove M_PECK = new AttackMove(PECK, "peck_barrage", 36, new int[]{10, 16, 22, 28}, 0, 1.6, 20, 4).dmg(1f).kb(0).face(36).move();
    static final AttackMove M_TALON = new AttackMove(TALON, "talon_strike", 24, new int[]{13}, 0, 2.2, 16, 3).dmg(2f).kb(0.35).face(13).move();
    static final AttackMove M_SCATTER = new AttackMove(SCATTER, "scatter", 24, new int[]{}, 0, 0, 10, 1).face(0).move();
    private static final List<AttackMove> MOVES = List.of(M_DIVE, M_PECK, M_TALON, M_SCATTER);

    /** Client-side flight blend for the model (0 perched .. 1 flying). */
    public float fly, flyO;
    private double circleSeed;

    public CrowEntity(EntityType<CrowEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 4.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.FLYING_SPEED, 0.6)
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 0.7));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override public List<AttackMove> moves() { return MOVES; }

    @Override
    public boolean canStartMove(AttackMove m) {
        return m != M_SCATTER; // scatter is only a hurt reaction
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            flyO = fly;
            fly += ((getForm() == 1 ? 1f : 0f) - fly) * 0.25f;
            return;
        }
        if (!isAlive()) { setNoGravity(false); return; }
        LivingEntity t = getTarget();
        boolean engaged = (t != null && t.isAlive()) || isMoving();
        if (engaged) {
            setNoGravity(true);
        } else if (getNavigation().isDone()) {
            setNoGravity(false);                                   // idle: settle down and perch
            Vec3 v = getDeltaMovement();
            if (!onGround() && v.y < -0.1) setDeltaMovement(v.x * 0.9, -0.1, v.z * 0.9); // glide down
            else if (onGround() && getRandom().nextInt(50) == 0) {                      // hop around
                float a = getRandom().nextFloat() * (float) (Math.PI * 2);
                setDeltaMovement(Mth.cos(a) * 0.12, 0.28, Mth.sin(a) * 0.12);
                float yaw = (float) Math.toDegrees(a) - 90f;
                setYRot(yaw); yBodyRot = yaw; yHeadRot = yaw;
            }
        }
        setForm(onGround() && !engaged ? 0 : 1);
    }

    /** Flock: any crow that picks a target pulls nearby idle crows into the attack. */
    @Override
    public void setTarget(LivingEntity t) {
        LivingEntity old = getTarget();
        super.setTarget(t);
        if (t != null && t != old && !level().isClientSide) {
            for (CrowEntity c : level().getEntitiesOfClass(CrowEntity.class, getBoundingBox().inflate(14.0))) {
                if (c != this && c.isAlive() && c.getTarget() == null) c.setTarget(t);
            }
        }
    }

    private static Vec3 cap(Vec3 v, double max) {
        double l = v.length();
        return l > max ? v.scale(max / l) : v;
    }

    private void steer(Vec3 want, double gain, double max) {
        setDeltaMovement(cap(want.subtract(position()).scale(gain), max));
        hasImpulse = true;
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        circleSeed = getRandom().nextDouble() * Math.PI * 2;
        if (m == M_DIVE || m == M_SCATTER) playSound(SoundEvents.PARROT_AMBIENT, 1.0f, 0.5f);
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m == M_SCATTER) {
            if (t == 1) {
                LivingEntity from = getLastHurtByMob() != null ? getLastHurtByMob() : target;
                Vec3 away = from == null ? Vec3.directionFromRotation(0, getYRot() + 180f)
                    : position().subtract(from.position()).multiply(1, 0, 1).normalize();
                double j = (getRandom().nextDouble() - 0.5) * 0.6;
                setDeltaMovement(away.x * 0.6 - away.z * j, 0.5, away.z * 0.6 + away.x * j);
                hasImpulse = true;
            } else if (t < 16) {
                Vec3 v = getDeltaMovement();
                setDeltaMovement(v.x * 1.02, Math.max(v.y, 0.15), v.z * 1.02);
            }
            return;
        }
        if (target == null) return;
        Vec3 tp = target.position();
        if (m == M_DIVE) {
            if (t <= 18) {                                   // climb and circle overhead
                double ang = circleSeed + t * 0.35;
                steer(tp.add(Math.cos(ang) * 2.5, 4.0, Math.sin(ang) * 2.5), 0.2, 0.6);
            } else if (t <= 22) {                            // stall at apex, wings fold
                setDeltaMovement(getDeltaMovement().scale(0.5));
            } else if (t <= 30) {                            // plummet beak-first
                setDeltaMovement(target.getEyePosition().subtract(position()).normalize().scale(0.95));
                hasImpulse = true;
            } else {                                         // pull up and away
                Vec3 back = position().subtract(tp).multiply(1, 0, 1).normalize();
                setDeltaMovement(back.x * 0.4, 0.4, back.z * 0.4);
            }
        } else if (m == M_PECK) {                            // hover in the victim's face
            Vec3 off = position().subtract(tp).multiply(1, 0, 1).normalize().scale(0.7);
            steer(new Vec3(tp.x + off.x, target.getEyeY() - 0.35, tp.z + off.z), 0.35, 0.5);
        } else if (m == M_TALON) {
            if (t <= 8) {                                    // rise and rear back
                Vec3 off = position().subtract(tp).multiply(1, 0, 1).normalize().scale(1.2);
                steer(tp.add(off.x, 1.8, off.z), 0.3, 0.5);
            } else if (t <= 13) {                            // feet-first thrust
                setDeltaMovement(tp.add(0, target.getBbHeight() * 0.6, 0).subtract(position()).normalize().scale(0.7));
                hasImpulse = true;
            } else {                                         // beat back
                Vec3 back = position().subtract(tp).multiply(1, 0, 1).normalize();
                setDeltaMovement(back.x * 0.35, 0.2, back.z * 0.35);
            }
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        if (target == null) return;
        double reach = (m == M_DIVE ? 1.6 : m == M_TALON ? 1.5 : 1.3) + (target.getBbWidth() + getBbWidth()) * 0.5;
        if (distanceTo(target) <= reach) strike(target, m.damage, m.knockback);
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        boolean r = super.hurt(src, amount);
        if (r && !level().isClientSide && isAlive() && getMoveId() != SCATTER) {
            cancelMove();
            startMove(M_SCATTER);
            for (CrowEntity c : level().getEntitiesOfClass(CrowEntity.class, getBoundingBox().inflate(5.0))) {
                if (c != this && c.isAlive() && !c.isMoving() && getRandom().nextBoolean()) c.startMove(M_SCATTER);
            }
        }
        return r;
    }

    @Override public boolean causeFallDamage(float dist, float mult, DamageSource src) { return false; }
    @Override public int getMaxSpawnClusterSize() { return 6; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PARROT_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.PARROT_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PARROT_DEATH; }
}
