package com.reevil.entity.webspinner;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.giantspider.SpiderKit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Web Spinner (RE1 spiderling swarm).
 * Moves: leaping poison bite, slowing silk strand, dangle-on-thread ceiling drop, swarm alert (threat display + rally),
 * wall climbing.
 */
public class WebSpinnerEntity extends BioMonster {
    public static final int LEAP = 1, STRAND = 2, DROP = 3, ALERT = 4;
    static final AttackMove M_LEAP = new AttackMove(LEAP, "leap_bite", 22, new int[]{11}, 0.4, 5, 25, 5).dmg(3f).kb(0.1).face(7).move();
    static final AttackMove M_STRAND = new AttackMove(STRAND, "web_strand", 26, new int[]{15}, 3, 12, 70, 3).dmg(1f);
    static final AttackMove M_DROP = new AttackMove(DROP, "silk_drop", 18, new int[]{}, 0, 99, 10, 1).dmg(4f).kb(0.3);
    static final AttackMove M_ALERT = new AttackMove(ALERT, "swarm_alert", 24, new int[]{}, 0, 99, 5, 1).face(14).move();
    private static final List<AttackMove> MOVES = List.of(M_LEAP, M_STRAND, M_DROP, M_ALERT);

    private static final EntityDataAccessor<Byte> DATA_FLAGS = SynchedEntityData.defineId(WebSpinnerEntity.class, EntityDataSerializers.BYTE);
    private static final int F_CLIMB = 1, F_DANGLE = 2;
    /** Vertical distance from the thread anchor (ceiling) to the spider's feet while dangling; matches the model thread. */
    public static final double THREAD_DROP = 2.3;

    private int dangleState;        // 0 ground, 1 rising on silk, 2 dangling
    private int riseTicks, targetTicks, idleTicks;
    private double dangleY;
    private boolean alerted, pendingAlert, dropLanded;
    private final List<SpiderKit.Shot> shots = new ArrayList<>();

    public WebSpinnerEntity(EntityType<WebSpinnerEntity> type, Level level) {
        super(type, level);
        xpReward = 4;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 12)
            .add(Attributes.MOVEMENT_SPEED, 0.32)
            .add(Attributes.ATTACK_DAMAGE, 2)
            .add(Attributes.FOLLOW_RANGE, 24);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_FLAGS, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new DangleGoal());
    }

    @Override
    protected PathNavigation createNavigation(Level level) { return new WallClimberNavigation(this, level); }

    private boolean flag(int f) { return (entityData.get(DATA_FLAGS) & f) != 0; }
    private void setFlag(int f, boolean on) {
        byte b = entityData.get(DATA_FLAGS);
        entityData.set(DATA_FLAGS, (byte) (on ? (b | f) : (b & ~f)));
    }
    public boolean isClimbing() { return flag(F_CLIMB); }
    /** True while rising on / hanging from its silk thread (model shows the thread, head-down pose). */
    public boolean isDangling() { return flag(F_DANGLE); }

    private void setDangleState(int s) {
        dangleState = s;
        riseTicks = 0;
        setFlag(F_DANGLE, s != 0);
        setNoGravity(s != 0);
    }

    @Override public boolean onClimbable() { return isClimbing(); }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) setFlag(F_CLIMB, horizontalCollision && dangleState == 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        shots.removeIf(s -> s.tick(this));
        LivingEntity t = getTarget();
        idleTicks = t == null ? idleTicks + 1 : 0;
        targetTicks = t == null ? 0 : targetTicks + 1;
        if (t == null) {
            alerted = false;
        } else if (!alerted) { // swarm: rally every idle spinner nearby onto this target
            alerted = true;
            pendingAlert = true;
            for (WebSpinnerEntity s : level().getEntitiesOfClass(WebSpinnerEntity.class, getBoundingBox().inflate(16))) {
                if (s != this && s.getTarget() == null) { s.setTarget(t); s.alerted = true; s.pendingAlert = true; }
            }
        }
        if (pendingAlert && !isMoving() && dangleState == 0) { pendingAlert = false; startMove(M_ALERT); }
    }

    @Override
    public List<AttackMove> moves() { return MOVES; }

    @Override
    public boolean canStartMove(AttackMove m) { return dangleState == 0 && (m == M_LEAP || m == M_STRAND); }

    @Override
    protected void onMoveStart(AttackMove m) {
        dropLanded = false;
        getNavigation().stop();
        if (m == M_ALERT) playSound(SoundEvents.SPIDER_AMBIENT, 1f, 1.8f);
        else if (m == M_LEAP) playSound(SoundEvents.SPIDER_HURT, 0.8f, 1.7f);
        else if (m == M_DROP) setDeltaMovement(0, -0.3, 0);
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m == M_LEAP && t == 7 && target != null) {
            Vec3 d = new Vec3(target.getX() - getX(), 0, target.getZ() - getZ());
            double h = Mth.clamp(d.length() * 0.18, 0.4, 0.9);
            d = d.normalize().scale(h);
            setDeltaMovement(d.x, 0.38, d.z);
            hasImpulse = true;
        } else if (m == M_ALERT && t == 16) {
            lunge(0.45, 0.15); // scuttle burst toward the prey after the display
        } else if (m == M_DROP && !dropLanded && t > 2 && onGround()) {
            dropLanded = true;
            SpiderKit.splash(this, position(), 1.3, m.damage, () -> new MobEffectInstance(MobEffects.POISON, 80, 0), ParticleTypes.POOF, 6);
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        if (target == null) return;
        if (m == M_LEAP) {
            if (distanceTo(target) <= 1.2 + (getBbWidth() + target.getBbWidth()) * 0.5 && strike(target, m.damage, m.knockback)) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0), this);
            }
        } else if (m == M_STRAND) {
            Vec3 from = position().add(0, 0.35, 0);
            Vec3 to = target.position().add(0, target.getBbHeight() * 0.5, 0);
            int ticks = Mth.clamp((int) (from.distanceTo(to) / 1.1), 3, 14);
            double g = 0.01;
            shots.add(new SpiderKit.Shot(from, SpiderKit.Shot.aim(from, to, ticks, g), g, ParticleTypes.ITEM_SNOWBALL,
                at -> SpiderKit.splash(this, at, 1.2, m.damage, () -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2), ParticleTypes.ITEM_SNOWBALL, 12)));
            playSound(SoundEvents.WOOL_PLACE, 0.8f, 1.6f);
        }
    }

    // ---- dangle from the ceiling on a silk thread, drop on prey
    private class DangleGoal extends Goal {
        DangleGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override
        public boolean canUse() {
            if (dangleState != 0) return true;
            if (getTarget() != null || isMoving() || idleTicks < 100 || !onGround() || getRandom().nextInt(40) != 0) return false;
            double c = SpiderKit.findCeiling(WebSpinnerEntity.this, 3, 8);
            if (Double.isNaN(c) || c - THREAD_DROP < getY() + 0.5) return false;
            dangleY = c - THREAD_DROP;
            return true;
        }

        @Override public boolean canContinueToUse() { return dangleState != 0; }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void start() { if (dangleState == 0) setDangleState(1); getNavigation().stop(); }

        @Override
        public void tick() {
            getNavigation().stop();
            LivingEntity t = getTarget();
            if (dangleState == 1) {
                if (++riseTicks > 160) { setDangleState(0); return; }
                setDeltaMovement(0, 0.1, 0);
                if (getY() >= dangleY || (verticalCollision && riseTicks > 3)) { setDangleState(2); setDeltaMovement(Vec3.ZERO); }
            } else {
                setDeltaMovement(Vec3.ZERO);
                if (t != null && t.isAlive()) {
                    double dx = t.getX() - getX(), dz = t.getZ() - getZ(), d2 = dx * dx + dz * dz;
                    if (d2 < 16 || (alerted && d2 < 100) || targetTicks > 80) { setDangleState(0); startMove(M_DROP); }
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (!level().isClientSide) {
            if (dangleState != 0) { setDangleState(0); if (!isMoving()) startMove(M_DROP); }
        }
        return super.hurt(src, amount);
    }

    @Override public MobType getMobType() { return MobType.ARTHROPOD; }
    @Override public boolean canBeAffected(MobEffectInstance e) { return e.getEffect() != MobEffects.POISON && super.canBeAffected(e); }
    @Override public void makeStuckInBlock(BlockState state, Vec3 v) { if (!state.is(Blocks.COBWEB)) super.makeStuckInBlock(state, v); }
    @Override public boolean causeFallDamage(float dist, float mult, DamageSource src) { return false; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SPIDER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.SPIDER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SPIDER_DEATH; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.SPIDER_STEP, 0.1f, 1.6f); }
    @Override protected float getSoundVolume() { return 0.6f; }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DangleState", dangleState);
        tag.putDouble("DangleY", dangleY);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setDangleState(tag.getInt("DangleState"));
        dangleY = tag.getDouble("DangleY");
    }
}
