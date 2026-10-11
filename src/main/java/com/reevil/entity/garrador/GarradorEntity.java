package com.reevil.entity.garrador;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Garrador (RE4): hulking blind brute with blade claws, hunts by sound (sprinters, anyone close, whoever hurt it). Armoured everywhere but the Plaga on its BACK:
 * hits from behind do triple damage, hits from the front are heavily reduced. Moves: double blade slash, charging rush, grab-and-crush, ground slam.
 */
public class GarradorEntity extends BioMonster {
    public static final int SLASH = 1, RUSH = 2, CRUSH = 3, SLAM = 4;

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(SLASH, "blade_slash", 32, new int[]{11, 21}, 0, 3.6, 16, 7).dmg(8f).kb(0.9).face(21),
        new AttackMove(RUSH, "charging_rush", 40, new int[]{16, 24}, 4, 14, 70, 4).dmg(9f).kb(1.8).face(8).move(),
        new AttackMove(CRUSH, "grab_crush", 64, new int[]{16, 32, 48}, 0, 2.4, 140, 3).dmg(3f).kb(0).face(16),
        new AttackMove(SLAM, "ground_slam", 40, new int[]{22}, 0, 4.5, 60, 4).dmg(10f).kb(1.4).face(14)
    );
    private LivingEntity held;

    public GarradorEntity(EntityType<GarradorEntity> type, Level level) { super(type, level); xpReward = 50; setMaxUpStep(1.1f); }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 150.0)
            .add(Attributes.MOVEMENT_SPEED, 0.22)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.FOLLOW_RANGE, 26.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
            .add(Attributes.ARMOR, 8.0);
    }

    /** blind: sprinters, close players, and attackers only. */
    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.7));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 6, false, false, p -> p.isSprinting() || p.swinging || p.distanceToSqr(this) < 36.0));
    }

    @Override public List<AttackMove> moves() { return MOVES; }

    /** the back plate is open: attacker behind = x3, in front = x0.4. */
    @Override
    public boolean hurt(DamageSource src, float amount) {
        Entity a = src.getEntity();
        if (a != null) {
            Vec3 face = Vec3.directionFromRotation(0, yBodyRot);
            Vec3 to = a.position().subtract(position()).multiply(1, 0, 1).normalize();
            boolean behind = face.dot(to) < -0.25;
            amount *= behind ? 3f : 0.4f;
            if (behind && level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.CRIT, getX() - face.x * 0.6, getY() + 1.8, getZ() - face.z * 0.6, 8, 0.2, 0.2, 0.2, 0.1);
        }
        return super.hurt(src, amount);
    }

    @Override protected void onMoveStart(AttackMove m) { held = null; playSound(m.id == SLAM || m.id == RUSH ? SoundEvents.RAVAGER_ROAR : SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, 0.5f); }
    @Override protected void onMoveEnd(AttackMove m) { held = null; }
    @Override public void cancelMove() { super.cancelMove(); held = null; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case SLASH: if (t == 10 || t == 20) lunge(0.15, 0); break;
            case RUSH: if (t >= 8 && t <= 30) lunge(0.5, 0); break;
            case CRUSH:
                if (held != null && held.isAlive() && t >= 16 && t <= 52) {
                    Vec3 hold = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.0)).add(0, 1.8, 0);
                    held.setDeltaMovement(hold.subtract(held.position().add(0, held.getBbHeight() * 0.5, 0)).scale(0.5));
                    held.hurtMarked = true; held.fallDistance = 0;
                }
                break;
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case CRUSH -> {
                if (hitIndex == 0) { if (target != null && distanceTo(target) <= 2.8) { held = target; strike(target, m.damage, 0); playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.4f, 0.5f); } }
                else if (held != null && held.isAlive()) {
                    if (hitIndex == 1) strike(held, 5f, 0);
                    else { Vec3 f = Vec3.directionFromRotation(0, getYRot()); strike(held, 12f, 0); held.setDeltaMovement(f.x * 1.5, 0.5, f.z * 1.5); held.hurtMarked = true; held = null; }
                }
            }
            case SLAM -> {
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(m.maxRange, 1.5, m.maxRange), x -> x != this && x.isAlive())) {
                    if (!e.onGround() && e.getY() - getY() > 1.2) continue;
                    Vec3 away = e.position().subtract(position()).multiply(1, 0, 1).normalize();
                    e.hurt(damageSources().mobAttack(this), m.damage); e.push(away.x, 0.6, away.z); e.hurtMarked = true;
                }
                playSound(SoundEvents.GENERIC_EXPLODE, 1.6f, 0.7f);
            }
            default -> {
                if (target != null && distanceTo(target) <= m.maxRange + 1.0 && strike(target, m.damage, m.knockback)) playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.6f);
            }
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.RAVAGER_STUNNED; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
    @Override public float getVoicePitch() { return 0.6f; }
}
