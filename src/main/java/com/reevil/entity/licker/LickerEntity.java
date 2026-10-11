package com.reevil.entity.licker;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Licker (RE2): skinless, BLIND crawler with an exposed brain and a prehensile tongue. It hunts by sound only: it notices players that
 * sprint, attack, or come within 5 blocks; sneaking players slip past. Moves: tongue lash, double claw slash, pouncing leap,
 * tongue snare-and-reel-in bite, ceiling ambush drop (it climbs walls and ceilings), shriek that alerts nearby Lickers.
 * Form 1 = Licker beta (pale, tougher, faster, 25% of spawns).
 */
public class LickerEntity extends BioMonster {
    public static final int LASH = 1, SLASH = 2, POUNCE = 3, GRAB = 4, DROP = 5, SHRIEK = 6, ASCEND = 7;

    private static final EntityDataAccessor<Boolean> DATA_CEILING = SynchedEntityData.defineId(LickerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CLIMB = SynchedEntityData.defineId(LickerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(LASH, "tongue_lash", 30, new int[]{14}, 2.2, 8, 30, 6).dmg(6f).kb(0.9).face(12),
        new AttackMove(SLASH, "claw_slash", 22, new int[]{8, 14}, 0, 2.6, 12, 8).dmg(4.5f).kb(0.3).face(14),
        new AttackMove(POUNCE, "pounce", 28, new int[]{14}, 3, 8, 40, 5).dmg(7f).kb(0.6).face(8).move(),
        new AttackMove(GRAB, "tongue_snare", 48, new int[]{16, 28, 40}, 3, 9, 120, 3).dmg(2f).kb(0).face(16),
        new AttackMove(DROP, "ceiling_drop", 26, new int[]{12}, 0, 6, 16, 0).dmg(8f).kb(0.2).face(12).move(),
        new AttackMove(SHRIEK, "shriek", 36, new int[]{18}, 0, 99, 400, 0).face(0),
        new AttackMove(ASCEND, "ascend", 18, new int[]{}, 0, 99, 10, 0).face(0)
    );

    public float ceilAnim, ceilAnimO, climbAnim, climbAnimO;
    private int ceilCd = 80;
    private boolean rolled;
    private LivingEntity snared;

    public LickerEntity(EntityType<LickerEntity> type, Level level) {
        super(type, level);
        xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 44.0)
            .add(Attributes.MOVEMENT_SPEED, 0.30)
            .add(Attributes.ATTACK_DAMAGE, 5.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.ARMOR, 1.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_CEILING, false);
        entityData.define(DATA_CLIMB, false);
    }

    /** Blind: only notices sprinters, anyone within 5 blocks, or whoever hurt it (HurtByTargetGoal). */
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new CeilingGoal());
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 5, false, false,
            p -> p.isSprinting() || p.swinging || p.distanceToSqr(this) < 25.0));
    }

    @Override protected PathNavigation createNavigation(Level level) { return new WallClimberNavigation(this, level); }

    public boolean isCeiling() { return entityData.get(DATA_CEILING); }
    public boolean isClimbing() { return entityData.get(DATA_CLIMB); }
    public boolean isBeta() { return getForm() == 1; }
    @Override public boolean onClimbable() { return isClimbing(); }
    @Override public List<AttackMove> moves() { return MOVES; }
    @Override public String getTextureSuffix() { return isBeta() ? "_beta" : ""; }
    private float dmg(AttackMove m) { return m.damage * (isBeta() ? 1.35f : 1f); }

    private static AttackMove byId(int id) {
        for (AttackMove m : MOVES) if (m.id == id) return m;
        return MOVES.get(0);
    }

    @Override
    public boolean canStartMove(AttackMove m) {
        if (isCeiling()) return false;
        switch (m.id) {
            case DROP: case ASCEND: case SHRIEK: return false;
            case POUNCE: return onGround();
            default: return true;
        }
    }

    // ---------------------------------------------------------------- ceiling helpers (same scheme as the Chimera)
    private boolean solidUnder(double x, double y, double z) {
        BlockPos p = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        return level().getBlockState(p).isFaceSturdy(level(), p, Direction.DOWN);
    }

    private boolean ceilingAbove() { return solidUnder(getX(), getY() + getBbHeight() + 0.1, getZ()); }

    private double ceilingGap() {
        double top = getY() + getBbHeight();
        for (int i = 0; i <= 4; i++) {
            int by = Mth.floor(top) + i;
            if (solidUnder(getX(), by, getZ())) return by - top;
        }
        return -1;
    }

    private void attach() {
        entityData.set(DATA_CEILING, true);
        setNoGravity(true);
        setDeltaMovement(Vec3.ZERO);
        getNavigation().stop();
    }

    private void detach() {
        entityData.set(DATA_CEILING, false);
        setNoGravity(false);
    }

    // ---------------------------------------------------------------- behavior
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            ceilAnimO = ceilAnim; climbAnimO = climbAnim;
            ceilAnim += ((isCeiling() ? 1f : 0f) - ceilAnim) * 0.3f;
            climbAnim += ((isClimbing() && !onGround() ? 1f : 0f) - climbAnim) * 0.25f;
            return;
        }
        if (!rolled && tickCount > 1) {
            rolled = true;
            if (canBeta() && getForm() == 0 && getRandom().nextFloat() < 0.25f) makeBeta();
        }
        fallDistance = 0f;
        if (ceilCd > 0) ceilCd--;
        if (!isAlive()) return;
        entityData.set(DATA_CLIMB, horizontalCollision && !isCeiling());
        if (isCeiling()) {
            if (!ceilingAbove()) { detach(); ceilCd = 60; }
            else { setNoGravity(true); Vec3 v = getDeltaMovement(); setDeltaMovement(v.x, 0.04, v.z); }
        } else if (!isMoving() && onGround() && ceilCd <= 0 && wantsAscend()) {
            startMove(byId(ASCEND));
        }
        // a lone shriek when it first notices someone
        if (getTarget() != null && !isMoving() && !isCeiling() && getMoveCooldownFor(SHRIEK) && getRandom().nextInt(120) == 0) startMove(byId(SHRIEK));
    }

    private boolean shrieked;
    private boolean getMoveCooldownFor(int id) { return !shrieked; }

    private void makeBeta() {
        setForm(1);
        var hp = getAttribute(Attributes.MAX_HEALTH); if (hp != null) hp.setBaseValue(64.0);
        var sp = getAttribute(Attributes.MOVEMENT_SPEED); if (sp != null) sp.setBaseValue(0.36);
        setHealth(getMaxHealth());
    }

    protected boolean canBeta() { return true; }
    protected boolean canCeiling() { return true; }

    private boolean wantsAscend() {
        if (!canCeiling()) return false;
        double gap = ceilingGap();
        if (gap < 0.3 || gap > 3.6) return false;
        LivingEntity t = getTarget();
        if (t == null) return getRandom().nextInt(200) == 0;
        double d = distanceTo(t);
        return d > 4.5 && d < 14 && getRandom().nextInt(40) == 0;
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        boolean r = super.hurt(src, amount);
        if (r && !level().isClientSide && isCeiling() && !isMoving() && getRandom().nextFloat() < 0.3f) { detach(); ceilCd = 100; }
        return r;
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        snared = null;
        switch (m.id) {
            case DROP: {
                detach(); ceilCd = 200;
                LivingEntity t = getTarget();
                if (t != null) { Vec3 d = t.position().subtract(position()).multiply(1, 0, 1); setDeltaMovement(d.x * 0.15, -0.25, d.z * 0.15); }
                playSound(SoundEvents.CAT_HISS, 1.4f, 0.5f);
                break;
            }
            case LASH: case GRAB: playSound(SoundEvents.CAT_HISS, 1.2f, 0.4f); break;
            case SLASH: playSound(SoundEvents.RAVAGER_ATTACK, 0.8f, 1.5f); break;
            case SHRIEK: playSound(SoundEvents.RAVAGER_ROAR, 1.6f, 1.6f); break;
            case ASCEND: playSound(SoundEvents.SPIDER_STEP, 1.0f, 1.2f); break;
            default: break;
        }
    }

    @Override protected void onMoveEnd(AttackMove m) { snared = null; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case SLASH:
                if (t == 7 || t == 13) lunge(0.16, 0);
                break;
            case POUNCE:
                if (t == 9) {
                    double h = 0.9;
                    if (target != null) h = Math.min(1.3, Math.max(0.35, (distanceTo(target) - 0.8) / 6.0));
                    lunge(h, 0.34);
                }
                break;
            case GRAB:
                if (snared != null && snared.isAlive()) {
                    if (t >= 18 && t <= 38) { // reel it in along the tongue
                        Vec3 d = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.1)).subtract(snared.position());
                        double len = d.length();
                        snared.setDeltaMovement(len > 1.0 ? d.normalize().scale(Math.min(0.5, len * 0.2)) : Vec3.ZERO);
                        snared.hurtMarked = true;
                    }
                } else if (t > 16) snared = null;
                break;
            case DROP:
                if (t >= 12 && t <= 22 && target != null && distanceTo(target) < 1.8) {
                    target.setDeltaMovement(0, Math.min(0, target.getDeltaMovement().y), 0);
                    target.hurtMarked = true;
                }
                break;
            case ASCEND: {
                double gap = ceilingGap();
                if (t == 6 && gap >= 0) setDeltaMovement(0, Math.min(1.0, 0.42 + gap * 0.12), 0);
                if (t > 6 && !isCeiling() && ceilingAbove()) attach();
                if (t == 15 && !isCeiling()) ceilCd = 120;
                break;
            }
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        if (m.id == SHRIEK) {
            shrieked = true;
            for (LickerEntity o : level().getEntitiesOfClass(LickerEntity.class, getBoundingBox().inflate(20), x -> x != this && x.isAlive())) {
                if (getTarget() != null) o.setTarget(getTarget());
            }
            return;
        }
        if (target == null) return;
        double reach;
        switch (m.id) {
            case LASH: reach = 8.4; break;
            case GRAB: reach = 9.4; break;
            case POUNCE: reach = 2.4; break;
            case DROP: reach = 2.2; break;
            default: reach = m.maxRange + 0.6; break;
        }
        if (distanceTo(target) > reach + target.getBbWidth() * 0.5) return;
        if (m.id == GRAB) {
            if (hitIndex == 0) {
                snared = target;
                strike(target, dmg(m), 0);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 3));
                playSound(SoundEvents.SLIME_ATTACK, 1.4f, 0.6f);
            } else if (snared != null && snared.isAlive()) {
                if (hitIndex == 1) strike(snared, 2f, 0);
                else { strike(snared, 8f * (isBeta() ? 1.35f : 1f), 0.3); playSound(SoundEvents.GENERIC_EAT, 1.2f, 0.5f); snared = null; }
            }
            return;
        }
        if (!strike(target, dmg(m), m.knockback)) return;
        if (m.id == DROP || m.id == POUNCE) target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 3));
        playSound(m.id == LASH ? SoundEvents.PLAYER_ATTACK_KNOCKBACK : SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, m.id == LASH ? 0.7f : 1.5f);
    }

    /** Ceiling crawl: skitters upside-down toward prey and drops on it. */
    private class CeilingGoal extends Goal {
        CeilingGoal() { setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP)); }
        @Override public boolean canUse() { return isCeiling(); }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void stop() { setZza(0f); }

        @Override
        public void tick() {
            getNavigation().stop();
            setZza(0f);
            if (isMoving()) return;
            LivingEntity t = getTarget();
            if (t == null || !t.isAlive()) return;
            getLookControl().setLookAt(t, 30f, 30f);
            double dx = t.getX() - getX(), dz = t.getZ() - getZ();
            double h = Math.sqrt(dx * dx + dz * dz);
            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            setYRot(yaw); yBodyRot = yaw;
            boolean below = t.getY() < getY();
            if (h < 2.2 && below) {
                if (getMoveCooldown() <= 0) startMove(byId(DROP));
                setDeltaMovement(0, 0.04, 0);
                return;
            }
            if (h < 1.0e-3) return;
            double sp = 0.12;
            if (solidUnder(getX() + dx / h * 0.6, getY() + getBbHeight() + 0.1, getZ() + dz / h * 0.6)) setDeltaMovement(dx / h * sp, 0.04, dz / h * sp);
            else {
                setDeltaMovement(0, 0.04, 0);
                if (below && h < 5 && hasLineOfSight(t) && getMoveCooldown() <= 0) startMove(byId(DROP));
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Ceiling", isCeiling());
        tag.putBoolean("Rolled", rolled);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        rolled = tag.getBoolean("Rolled");
        if (getForm() == 1) makeBeta();
        if (tag.getBoolean("Ceiling")) attach();
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.CAT_HISS; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
    @Override public float getVoicePitch() { return 0.5f + getRandom().nextFloat() * 0.2f; }
}
