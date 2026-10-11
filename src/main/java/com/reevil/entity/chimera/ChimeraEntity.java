package com.reevil.entity.chimera;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
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
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

/**
 * Chimera (RE1 / REmake): fly-human hybrid. Climbs walls like a spider, hangs from ceilings (synced CEILING flag,
 * model renders inverted) and drops on prey. Moves: 3-hit blade flurry, leaping lunge-stab/bite, grab-and-chew latch,
 * ceiling drop ambush, hit-and-run retreat, wall-run sprint, leap up to the ceiling.
 */
public class ChimeraEntity extends BioMonster {
    public static final int FLURRY = 1, LUNGE = 2, CHEW = 3, DROP = 4, RETREAT = 5, WALLRUN = 6, ASCEND = 7;

    private static final EntityDataAccessor<Boolean> DATA_CEILING = SynchedEntityData.defineId(ChimeraEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CLIMB = SynchedEntityData.defineId(ChimeraEntity.class, EntityDataSerializers.BOOLEAN);

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(FLURRY, "blade_flurry", 28, new int[]{9, 14, 19}, 0, 2.5, 14, 10).dmg(3.5f).kb(0.2).face(19),
        new AttackMove(LUNGE, "lunge_stab", 26, new int[]{13}, 2.5, 7, 20, 6).dmg(6f).kb(0.5).face(8).move(),
        new AttackMove(CHEW, "grab_chew", 64, new int[]{20, 28, 36, 44, 52}, 0, 1.8, 40, 3).dmg(2.5f).kb(0).face(64).move(),
        new AttackMove(DROP, "ceiling_drop", 30, new int[]{13}, 0, 6, 16, 0).dmg(7f).kb(0.1).face(13).move(),
        new AttackMove(RETREAT, "retreat", 16, new int[]{}, 0, 99, 10, 0).face(16).move(),
        new AttackMove(WALLRUN, "wall_run", 24, new int[]{}, 6, 16, 10, 5).face(24).move(),
        new AttackMove(ASCEND, "ascend", 18, new int[]{}, 0, 99, 10, 0).face(0)
    );

    /** client-side smoothed 0..1 for the ceiling flip and wall-climb pitch */
    public float ceilAnim, ceilAnimO, climbAnim, climbAnimO;
    private int ceilCd = 60;
    private boolean latched;

    public ChimeraEntity(EntityType<ChimeraEntity> type, Level level) {
        super(type, level);
        xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40.0)
            .add(Attributes.MOVEMENT_SPEED, 0.34)
            .add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.ARMOR, 2.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_CEILING, false);
        entityData.define(DATA_CLIMB, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new CeilingGoal());
    }

    @Override
    protected PathNavigation createNavigation(Level level) { return new WallClimberNavigation(this, level); }

    public boolean isCeiling() { return entityData.get(DATA_CEILING); }
    public boolean isClimbing() { return entityData.get(DATA_CLIMB); }

    @Override
    public boolean onClimbable() { return isClimbing(); }

    @Override
    public List<AttackMove> moves() { return MOVES; }

    private static AttackMove byId(int id) {
        for (AttackMove m : MOVES) if (m.id == id) return m;
        return MOVES.get(0);
    }

    @Override
    public boolean canStartMove(AttackMove m) {
        if (isCeiling()) return false; // the CeilingGoal launches the drop itself
        switch (m.id) {
            case DROP: case RETREAT: case ASCEND: return false;
            case LUNGE: return onGround() || isClimbing();
            default: return true;
        }
    }

    // ---------------------------------------------------------------- ceiling helpers

    private boolean solidUnder(double x, double y, double z) {
        BlockPos p = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
        return level().getBlockState(p).isFaceSturdy(level(), p, Direction.DOWN);
    }

    private boolean ceilingAbove() { return solidUnder(getX(), getY() + getBbHeight() + 0.1, getZ()); }

    /** gap (blocks) between head top and the first sturdy ceiling within 4 blocks, or -1. */
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
        fallDistance = 0f;
        if (ceilCd > 0) ceilCd--;
        if (!isAlive()) return;
        entityData.set(DATA_CLIMB, horizontalCollision && !isCeiling());
        if (isCeiling()) {
            if (!ceilingAbove()) { detach(); ceilCd = 60; }
            else {
                setNoGravity(true);
                Vec3 v = getDeltaMovement();
                setDeltaMovement(v.x, 0.04, v.z); // keep pressed against the ceiling
            }
        } else if (!isMoving() && onGround() && ceilCd <= 0 && wantsAscend()) {
            startMove(byId(ASCEND));
        }
    }

    private boolean wantsAscend() {
        double gap = ceilingGap();
        if (gap < 0.3 || gap > 3.6) return false;
        LivingEntity t = getTarget();
        if (t == null) return getRandom().nextInt(160) == 0;
        double d = distanceTo(t);
        return d > 4.5 && d < 14 && getRandom().nextInt(40) == 0;
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        boolean r = super.hurt(src, amount);
        if (r && !level().isClientSide && isCeiling() && !isMoving() && getRandom().nextFloat() < 0.3f) {
            detach(); // knocked off the ceiling
            ceilCd = 100;
        }
        return r;
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        latched = false;
        switch (m.id) {
            case DROP: {
                detach();
                ceilCd = 200;
                LivingEntity t = getTarget();
                if (t != null) {
                    Vec3 d = t.position().subtract(position()).multiply(1, 0, 1);
                    setDeltaMovement(d.x * 0.15, -0.25, d.z * 0.15);
                }
                playSound(SoundEvents.SPIDER_AMBIENT, 1.2f, 1.5f);
                break;
            }
            case FLURRY: case LUNGE: playSound(SoundEvents.SILVERFISH_AMBIENT, 1.2f, 0.6f); break;
            case CHEW: playSound(SoundEvents.SPIDER_AMBIENT, 1.0f, 0.7f); break;
            case ASCEND: playSound(SoundEvents.SPIDER_STEP, 1.0f, 1.4f); break;
            default: break;
        }
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        // hit-and-run: scuttle back out of reach after an attack
        if ((m.id == FLURRY || m.id == LUNGE || m.id == CHEW || m.id == DROP) && isAlive() && getRandom().nextFloat() < 0.6f) {
            startMove(byId(RETREAT));
        }
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case FLURRY:
                if (t == 7 || t == 12 || t == 17) lunge(0.18, 0);
                break;
            case LUNGE:
                if (t == 8) {
                    double h = 0.8;
                    if (target != null) h = Math.min(1.3, Math.max(0.3, (distanceTo(target) - 0.8) / 6.0));
                    lunge(h, 0.32);
                }
                break;
            case CHEW:
                if (target == null) break;
                if (t < 12 && distanceTo(target) > 1.2) lunge(0.25, 0);
                if (t == 12) latched = distanceTo(target) < 2.2;
                if (latched && t >= 12 && t < 58) {
                    Vec3 away = position().subtract(target.position()).multiply(1, 0, 1);
                    if (away.lengthSqr() < 1.0e-4) away = new Vec3(0, 0, 1);
                    Vec3 hold = target.position().add(away.normalize().scale(0.9));
                    setPos(hold.x, target.getY(), hold.z);
                    setDeltaMovement(Vec3.ZERO);
                    target.setDeltaMovement(0, Math.min(0, target.getDeltaMovement().y), 0);
                    target.hurtMarked = true;
                }
                if (t == 58 && latched) lunge(-0.6, 0.3);
                break;
            case DROP:
                if (t >= 13 && t <= 24 && target != null && distanceTo(target) < 1.8) {
                    target.setDeltaMovement(0, Math.min(0, target.getDeltaMovement().y), 0);
                    target.hurtMarked = true;
                }
                break;
            case RETREAT:
                if (t == 2) lunge(-0.5, 0.25);
                else if (t > 2 && t < 12) lunge(-0.38, 0);
                break;
            case WALLRUN:
                if (target != null && t >= 3 && t <= 22) {
                    Vec3 d = target.position().subtract(position()).multiply(1, 0, 1);
                    if (d.lengthSqr() > 1.0e-4) d = d.normalize().scale(0.42);
                    double vy = horizontalCollision ? 0.3 : getDeltaMovement().y;
                    setDeltaMovement(d.x, vy, d.z);
                    if (distanceTo(target) < 2.0) setDeltaMovement(0, getDeltaMovement().y, 0);
                }
                break;
            case ASCEND: {
                double gap = ceilingGap();
                if (t == 6 && gap >= 0) setDeltaMovement(0, Math.min(1.0, 0.42 + gap * 0.12), 0);
                if (t > 6 && !isCeiling() && ceilingAbove()) attach();
                if (t == 15 && !isCeiling()) ceilCd = 120; // failed: try again later
                break;
            }
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        if (target == null) return;
        double reach;
        switch (m.id) {
            case LUNGE: reach = 2.4; break;
            case DROP: reach = 2.2; break;
            case CHEW: reach = latched ? 2.6 : 0; break;
            default: reach = m.maxRange + 0.6; break;
        }
        if (distanceTo(target) > reach + target.getBbWidth() * 0.5) return;
        if (!strike(target, m.damage, m.knockback)) return;
        switch (m.id) {
            case DROP:
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4));
                playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.6f);
                break;
            case CHEW:
                playSound(SoundEvents.GENERIC_EAT, 1.0f, 0.6f);
                break;
            default:
                playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.9f, 1.6f);
                break;
        }
    }

    /** Ceiling crawl: holds MOVE/LOOK so BioCombatGoal pauses; skitters upside-down toward prey and drops on it. */
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
            if (solidUnder(getX() + dx / h * 0.6, getY() + getBbHeight() + 0.1, getZ() + dz / h * 0.6)) {
                setDeltaMovement(dx / h * sp, 0.04, dz / h * sp);
            } else {
                setDeltaMovement(0, 0.04, 0);
                if (below && h < 5 && hasLineOfSight(t) && getMoveCooldown() <= 0) startMove(byId(DROP)); // ceiling ends: leap down
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Ceiling", isCeiling());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("Ceiling")) attach();
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.SILVERFISH_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.SPIDER_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.SPIDER_DEATH; }

    @Override
    public float getVoicePitch() { return 0.7f + getRandom().nextFloat() * 0.2f; }
}
