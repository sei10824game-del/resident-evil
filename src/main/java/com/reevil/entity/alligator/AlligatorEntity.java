package com.reevil.entity.alligator;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.List;

/**
 * Giant Alligator (RE2 sewers): amphibious ambusher - lumbers on land, fast and silent in water. Moves: snapping bite, surging lunge,
 * clamp-and-death-roll (drags the prey to the jaws, then spins with a hit every 8 ticks), tail sweep that clears everything round it,
 * and a submerged ambush launch from the water when the prey stands near the bank.
 */
public class AlligatorEntity extends BioMonster {
    public static final int SNAP = 1, LUNGE = 2, ROLL = 3, SWEEP = 4, AMBUSH = 5;

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(SNAP, "snap", 22, new int[]{10}, 0, 3.2, 14, 7).dmg(9f).kb(0.5).face(9),
        new AttackMove(LUNGE, "lunge", 30, new int[]{14}, 3, 7.5, 28, 5).dmg(11f).kb(0.8).face(8).move(),
        new AttackMove(ROLL, "death_roll", 64, new int[]{12, 20, 28, 36, 44, 52}, 0, 2.6, 160, 3).dmg(4f).kb(0).face(12),
        new AttackMove(SWEEP, "tail_sweep", 28, new int[]{14}, 0, 3.4, 40, 4).dmg(7f).kb(1.6).face(10),
        new AttackMove(AMBUSH, "water_ambush", 36, new int[]{18}, 3, 8, 90, 0).dmg(12f).kb(1.0).face(10).move()
    );

    private LivingEntity held;

    public AlligatorEntity(EntityType<AlligatorEntity> type, Level level) {
        super(type, level);
        xpReward = 25;
        setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        setMaxUpStep(1.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 100.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(ForgeMod.SWIM_SPEED.get(), 2.2)
            .add(Attributes.ATTACK_DAMAGE, 9.0)
            .add(Attributes.FOLLOW_RANGE, 28.0)
            .add(Attributes.ARMOR, 6.0);
    }

    @Override public List<AttackMove> moves() { return MOVES; }
    @Override public boolean removeWhenFarAway(double d) { return false; }
    @Override public boolean canBreatheUnderwater() { return true; }

    @Override
    public boolean canStartMove(AttackMove m) {
        if (m.id == AMBUSH) return isInWater() && getTarget() != null && !getTarget().isInWater() && getTarget().onGround();
        if (m.id == LUNGE) return onGround() || isInWater();
        return true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        // prefers an ambush launch when the prey is on the bank: allow it by promoting a cooldown-free start
        LivingEntity t = getTarget();
        if (t != null && !isMoving() && getMoveCooldown() <= 0 && isInWater() && !t.isInWater()) {
            double d = distanceTo(t);
            if (d > 3 && d < 8 && hasLineOfSight(t) && getRandom().nextInt(25) == 0) startMove(MOVES.get(4));
        }
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        held = null;
        playSound(m.id == SWEEP ? SoundEvents.PLAYER_ATTACK_SWEEP : SoundEvents.RAVAGER_ATTACK, 1.2f, 0.5f);
    }

    @Override protected void onMoveEnd(AttackMove m) { held = null; }
    @Override public void cancelMove() { super.cancelMove(); held = null; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case LUNGE:
                if (t == 8) lunge(Math.min(1.1, target != null ? Math.max(0.4, (distanceTo(target) - 1.0) / 5.0) : 0.7), 0.18);
                break;
            case AMBUSH:
                if (t == 10) lunge(Math.min(1.2, target != null ? Math.max(0.5, (distanceTo(target) - 1.0) / 4.5) : 0.9), 0.5);
                break;
            case ROLL:
                if (held != null && held.isAlive() && t >= 12 && t <= 56) {
                    Vec3 mouth = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.8)).add(0, 0.4, 0);
                    Vec3 d = mouth.subtract(held.position().add(0, held.getBbHeight() * 0.5, 0));
                    held.setDeltaMovement(d.scale(0.5));
                    held.hurtMarked = true;
                    held.fallDistance = 0;
                    if (isInWater() && t % 6 == 0 && level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 0.5, getZ(), 12, 0.8, 0.2, 0.8, 0.2);
                }
                break;
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case ROLL -> {
                if (hitIndex == 0) {
                    if (target != null && distanceTo(target) <= 3.6) { held = target; strike(target, m.damage, 0); target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4)); playSound(SoundEvents.GENERIC_EAT, 1.5f, 0.4f); }
                } else if (held != null && held.isAlive()) {
                    strike(held, hitIndex == 5 ? 8f : 4f, 0);
                    if (hitIndex == 5) {
                        Vec3 f = Vec3.directionFromRotation(0, getYRot());
                        held.setDeltaMovement(f.x * 1.2, 0.5, f.z * 1.2);
                        held.hurtMarked = true;
                        held = null;
                    }
                }
            }
            case SWEEP -> {
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(m.maxRange - 0.4, 0.6, m.maxRange - 0.4), x -> x != this && x.isAlive())) {
                    Vec3 away = e.position().subtract(position()).multiply(1, 0, 1).normalize();
                    e.hurt(damageSources().mobAttack(this), m.damage);
                    e.push(away.x * 1.2, 0.45, away.z * 1.2);
                    e.hurtMarked = true;
                }
                playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.4f, 0.5f);
            }
            default -> {
                if (target != null && distanceTo(target) <= m.maxRange + 1.2 && strike(target, m.damage, m.knockback))
                    playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.3f, 0.5f);
            }
        }
    }

    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.TURTLE_SHAMBLE, 0.6f, 0.5f); }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.TURTLE_AMBIENT_LAND; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
    @Override public float getVoicePitch() { return 0.4f; }
}
