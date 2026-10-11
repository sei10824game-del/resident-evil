package com.reevil.entity.giantspider;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.webspinner.WebSpinnerEntity;
import com.reevil.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Giant Spider "Black Tiger" (RE1 / REmake).
 * Moves: venom bite (leg pin + fang plunge), venom spit (arcing glob), leg impale, web trap (cobwebs),
 * ambush drop from the ceiling, brood scatter (form 1 below 30% HP: spawns Web Spinners and enrages).
 * Climbs walls like a vanilla spider; idles up to a ceiling and hangs there upside-down to ambush.
 */
public class GiantSpiderEntity extends BioMonster {
    public static final int BITE = 1, SPIT = 2, IMPALE = 3, WEB = 4, DROP = 5, BROOD = 6;
    static final AttackMove M_BITE = new AttackMove(BITE, "venom_bite", 34, new int[]{12, 20}, 0, 1.6, 20, 5).dmg(7f).kb(0.2);
    static final AttackMove M_SPIT = new AttackMove(SPIT, "venom_spit", 36, new int[]{22}, 3.5, 14, 45, 3).dmg(4f);
    static final AttackMove M_IMPALE = new AttackMove(IMPALE, "leg_impale", 30, new int[]{17}, 0, 2.4, 30, 4).dmg(10f).kb(0.3);
    static final AttackMove M_WEB = new AttackMove(WEB, "web_trap", 40, new int[]{26}, 2.5, 12, 140, 2);
    static final AttackMove M_DROP = new AttackMove(DROP, "ambush_drop", 28, new int[]{}, 0, 99, 15, 1).dmg(9f).kb(0.8);
    static final AttackMove M_BROOD = new AttackMove(BROOD, "brood_scatter", 50, new int[]{30}, 0, 99, 20, 1);
    private static final List<AttackMove> MOVES = List.of(M_BITE, M_SPIT, M_IMPALE, M_WEB, M_DROP, M_BROOD);

    private static final EntityDataAccessor<Byte> DATA_FLAGS = SynchedEntityData.defineId(GiantSpiderEntity.class, EntityDataSerializers.BYTE);
    private static final int F_CLIMB = 1, F_HANG = 2, F_ASCEND = 4;
    private static final int WEB_LIFETIME = 300;

    private int hangState;          // 0 ground, 1 ascending, 2 hanging
    private int ascendTicks;
    private double ceilingY;
    private int idleTicks;
    private boolean dropLanded;
    private final List<SpiderKit.Shot> shots = new ArrayList<>();
    private final List<long[]> webs = new ArrayList<>(); // {blockPos, expireGameTime}

    public GiantSpiderEntity(EntityType<GiantSpiderEntity> type, Level level) {
        super(type, level);
        xpReward = 25;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 70)
            .add(Attributes.MOVEMENT_SPEED, 0.27)
            .add(Attributes.ATTACK_DAMAGE, 6)
            .add(Attributes.ARMOR, 4)
            .add(Attributes.FOLLOW_RANGE, 32)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_FLAGS, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new HangGoal());
    }

    @Override
    protected PathNavigation createNavigation(Level level) { return new WallClimberNavigation(this, level); }

    // ---- flags (client reads these for pose)
    private boolean flag(int f) { return (entityData.get(DATA_FLAGS) & f) != 0; }
    private void setFlag(int f, boolean on) {
        byte b = entityData.get(DATA_FLAGS);
        entityData.set(DATA_FLAGS, (byte) (on ? (b | f) : (b & ~f)));
    }
    public boolean isClimbing() { return flag(F_CLIMB); }
    public boolean isHanging() { return flag(F_HANG); }
    public boolean isAscending() { return flag(F_ASCEND); }

    private void setHangState(int s) {
        hangState = s;
        ascendTicks = 0;
        setFlag(F_ASCEND, s == 1);
        setFlag(F_HANG, s == 2);
        setNoGravity(s != 0);
    }

    @Override
    public boolean onClimbable() { return isClimbing(); }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) setFlag(F_CLIMB, horizontalCollision && hangState == 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        idleTicks = getTarget() == null ? idleTicks + 1 : 0;
        shots.removeIf(s -> s.tick(this));
        clearWebs(false);
        if (getForm() == 0 && hangState == 0 && isAlive() && getHealth() < getMaxHealth() * 0.3f) {
            setForm(1);
            cancelMove();
            startMove(M_BROOD);
        }
    }

    // ---- moves
    @Override
    public List<AttackMove> moves() { return MOVES; }

    @Override
    public boolean canStartMove(AttackMove m) {
        // drop is started by HangGoal, brood by the HP threshold
        return hangState == 0 && m != M_DROP && m != M_BROOD;
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        dropLanded = false;
        if (m == M_DROP) setDeltaMovement(0, -0.4, 0);
        if (m == M_BROOD) playSound(SoundEvents.SPIDER_DEATH, 2f, 0.4f);
        else playSound(SoundEvents.SPIDER_AMBIENT, 1.5f, 0.6f);
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        if (getForm() == 1) moveCooldown = moveCooldown / 2; // enraged: relentless
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m == M_BITE && t >= 8 && t <= 12) lunge(0.22, 0);
        else if (m == M_IMPALE && t >= 12 && t <= 16) lunge(0.12, 0);
        else if (m == M_DROP && !dropLanded) {
            if (t > 2 && onGround()) { dropLanded = true; landingStrike(m); }
            else if (target != null) {
                Vec3 d = new Vec3(target.getX() - getX(), 0, target.getZ() - getZ());
                if (d.lengthSqr() > 0.01) { d = d.normalize().scale(0.12); setDeltaMovement(d.x, getDeltaMovement().y, d.z); }
            }
        } else if (m == M_BROOD && t < 30 && t % 4 == 0 && level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.ITEM_SLIME, getX(), getY() + 1.0, getZ(), 4, 0.7, 0.4, 0.7, 0.02);
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        if (m == M_BROOD) { broodScatter(); return; }
        if (target == null) return;
        if (m == M_BITE) {
            if (!inReach(target, 1.8)) return;
            if (i == 0) { // front legs pin the victim
                strike(target, 3f, 0);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 4), this);
            } else if (strike(target, m.damage, m.knockback)) { // fangs plunge
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 160, getForm() == 1 ? 2 : 1), this);
            }
        } else if (m == M_IMPALE) {
            if (inReach(target, 2.6) && strike(target, m.damage, m.knockback)) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2), this);
                if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 12, 0.3, 0.4, 0.3, 0.2);
            }
        } else if (m == M_SPIT) {
            spit(target);
        } else if (m == M_WEB) {
            webTrap(target);
        }
    }

    private boolean inReach(LivingEntity t, double r) {
        return distanceTo(t) <= r + (getBbWidth() + t.getBbWidth()) * 0.5;
    }

    private void landingStrike(AttackMove m) {
        playSound(SoundEvents.HOSTILE_BIG_FALL, 1.5f, 0.7f);
        if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 0.2, getZ(), 14, 1.0, 0.1, 1.0, 0.02);
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(1.5))) {
            if (e == this || e instanceof Monster) continue;
            if (strike(e, m.damage, m.knockback)) e.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0), this);
        }
    }

    /** Venom glob lobbed in a ballistic arc from the raised abdomen. */
    private void spit(LivingEntity target) {
        Vec3 from = position().add(0, getBbHeight() + 0.5, 0);
        Vec3 to = target.position().add(0, target.getBbHeight() * 0.5, 0);
        int ticks = Mth.clamp((int) (from.distanceTo(to) * 1.4), 8, 24);
        double g = 0.05;
        shots.add(new SpiderKit.Shot(from, SpiderKit.Shot.aim(from, to, ticks, g), g, ParticleTypes.ITEM_SLIME, at -> {
            SpiderKit.splash(this, at, 2.0, 4f, () -> new MobEffectInstance(MobEffects.POISON, 140, 1), ParticleTypes.ITEM_SLIME, 30);
            level().playSound(null, at.x, at.y, at.z, SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1f, 0.6f);
        }));
        playSound(SoundEvents.LLAMA_SPIT, 1.5f, 0.5f);
    }

    /** Silk volley: cobwebs around the target (mobGriefing) or heavy slowness; webs dissolve after WEB_LIFETIME ticks. */
    private void webTrap(LivingEntity target) {
        Vec3 from = position().add(0, 0.6, 0), to = target.position().add(0, 0.5, 0);
        if (level() instanceof ServerLevel sl) {
            for (int k = 0; k <= 16; k++) {
                Vec3 p = from.lerp(to, k / 16.0);
                sl.sendParticles(ParticleTypes.ITEM_SNOWBALL, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0);
            }
        }
        playSound(SoundEvents.WOOL_PLACE, 1.5f, 0.6f);
        if (!level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3), this);
            return;
        }
        BlockPos c = target.blockPosition();
        BlockPos[] ps = {c, c.above(), c.north(), c.south(), c.east(), c.west()};
        long expire = level().getGameTime() + WEB_LIFETIME;
        for (int k = 0; k < ps.length; k++) {
            if (k >= 2 && getRandom().nextBoolean()) continue;
            if (level().getBlockState(ps[k]).isAir() && level().setBlock(ps[k], Blocks.COBWEB.defaultBlockState(), 3)) {
                webs.add(new long[]{ps[k].asLong(), expire});
            }
        }
    }

    // ponytail: web list is not saved; webs placed right before a chunk unload stay. Save to NBT if that matters.
    private void clearWebs(boolean all) {
        long now = level().getGameTime();
        Iterator<long[]> it = webs.iterator();
        while (it.hasNext()) {
            long[] w = it.next();
            if (all || now >= w[1]) {
                BlockPos p = BlockPos.of(w[0]);
                if (level().getBlockState(p).is(Blocks.COBWEB)) level().removeBlock(p, false);
                it.remove();
            }
        }
    }

    /** Form change: abdomen bursts, 2-4 Web Spinners scatter, spider enrages (faster, shorter cooldowns, stronger venom). */
    private void broodScatter() {
        int n = 2 + getRandom().nextInt(3);
        for (int k = 0; k < n; k++) {
            WebSpinnerEntity s = ModEntities.WEB_SPINNER.get().create(level());
            if (s == null) continue;
            float a = getRandom().nextFloat() * Mth.TWO_PI;
            s.moveTo(getX(), getY() + 0.6, getZ(), a * Mth.RAD_TO_DEG, 0);
            s.setDeltaMovement(Mth.cos(a) * 0.45, 0.45, Mth.sin(a) * 0.45);
            s.setTarget(getTarget());
            level().addFreshEntity(s);
        }
        if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.ITEM_SLIME, getX(), getY() + 0.8, getZ(), 40, 1.0, 0.5, 1.0, 0.15);
        playSound(SoundEvents.SLIME_SQUISH, 2f, 0.4f);
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(speed.getBaseValue() * 1.45);
    }

    // ---- ceiling ambush
    private class HangGoal extends Goal {
        HangGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override
        public boolean canUse() {
            if (hangState != 0) return true;
            if (getTarget() != null || isMoving() || idleTicks < 120 || !onGround() || getRandom().nextInt(60) != 0) return false;
            double c = SpiderKit.findCeiling(GiantSpiderEntity.this, 3, 9);
            if (Double.isNaN(c)) return false;
            ceilingY = c;
            return true;
        }

        @Override public boolean canContinueToUse() { return hangState != 0; }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() { if (hangState == 0) setHangState(1); getNavigation().stop(); }

        @Override
        public void tick() {
            getNavigation().stop();
            LivingEntity t = getTarget();
            if (hangState == 1) {
                if (t != null || ++ascendTicks > 200) { setHangState(0); return; }
                setDeltaMovement(0, 0.12, 0);
                if (getBoundingBox().maxY >= ceilingY - 0.02 || (verticalCollision && ascendTicks > 3)) {
                    setHangState(2);
                    setDeltaMovement(Vec3.ZERO);
                }
            } else if (hangState == 2) {
                setDeltaMovement(Vec3.ZERO);
                if (Double.isNaN(SpiderKit.findCeiling(GiantSpiderEntity.this, 1, 3))) { setHangState(0); return; }
                if (t != null && t.isAlive()) {
                    double dx = t.getX() - getX(), dz = t.getZ() - getZ();
                    if (dx * dx + dz * dz < 36 && t.getY() < getY()) { setHangState(0); startMove(M_DROP); }
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (!level().isClientSide && hangState != 0) {
            boolean wasHanging = hangState == 2;
            setHangState(0);
            if (wasHanging && !isMoving()) startMove(M_DROP);
        }
        return super.hurt(src, amount);
    }

    @Override
    public void die(DamageSource src) {
        super.die(src);
        if (!level().isClientSide) clearWebs(true);
    }

    // ---- spider traits
    @Override public MobType getMobType() { return MobType.ARTHROPOD; }
    @Override public boolean canBeAffected(MobEffectInstance e) { return e.getEffect() != MobEffects.POISON && super.canBeAffected(e); }
    @Override public void makeStuckInBlock(BlockState state, Vec3 v) { if (!state.is(Blocks.COBWEB)) super.makeStuckInBlock(state, v); }
    @Override public boolean causeFallDamage(float dist, float mult, DamageSource src) { return false; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SPIDER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.SPIDER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SPIDER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SPIDER_STEP, 0.4f, 0.6f); }
    @Override protected float getSoundVolume() { return 1.5f; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("HangState", hangState);
        tag.putDouble("CeilingY", ceilingY);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setHangState(tag.getInt("HangState"));
        ceilingY = tag.getDouble("CeilingY");
    }
}
