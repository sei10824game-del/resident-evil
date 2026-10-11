package com.reevil.entity.giantmoth;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
 * Giant Moth (RE2 lab): hovering insect that rains poisonous, blinding scales. Moves: scale burst (toxic cloud), diving ram, proboscis jab,
 * wing buffet (gust that throws everything back). Keeps 2-3 blocks above its prey while hovering.
 */
public class GiantMothEntity extends BioMonster {
    public static final int DUST = 1, RAM = 2, STING = 3, BUFFET = 4;

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(DUST, "scale_burst", 40, new int[]{20}, 0, 12, 160, 4).dmg(0f).face(0),
        new AttackMove(RAM, "dive_ram", 34, new int[]{17}, 4, 12, 50, 5).dmg(8f).kb(1.3).face(8).move(),
        new AttackMove(STING, "proboscis_jab", 24, new int[]{11}, 0, 3.2, 18, 6).dmg(6f).kb(0.3).face(10),
        new AttackMove(BUFFET, "wing_buffet", 28, new int[]{14}, 0, 5, 60, 4).dmg(3f).kb(0).face(12)
    );

    public GiantMothEntity(EntityType<GiantMothEntity> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 20, true);
        xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 36.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.FLYING_SPEED, 0.5)
            .add(Attributes.ATTACK_DAMAGE, 6.0)
            .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override protected PathNavigation createNavigation(Level level) { return new FlyingPathNavigation(this, level); }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 0.8));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override public List<AttackMove> moves() { return MOVES; }
    @Override public boolean causeFallDamage(float d, float m, DamageSource s) { return false; }
    @Override protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState st, net.minecraft.core.BlockPos pos) {}

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        setNoGravity(isAlive());
        LivingEntity t = getTarget();
        if (t != null && !isMoving() && isAlive()) { // hold hover height above the prey
            double want = t.getY() + 2.2, dy = want - getY();
            setDeltaMovement(getDeltaMovement().add(0, Math.max(-0.05, Math.min(0.05, dy * 0.04)), 0));
        }
        if (tickCount % 12 == 0 && level() instanceof ServerLevel sl)
            sl.sendParticles(ParticleTypes.ASH, getX(), getY() + 0.6, getZ(), 3, 0.6, 0.3, 0.6, 0.0); // shed scales
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        playSound(m.id == RAM ? SoundEvents.BEE_LOOP_AGGRESSIVE : SoundEvents.PHANTOM_FLAP, 1.2f, 0.5f);
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m.id == RAM) {
            if (t == 6 && target != null) {
                Vec3 d = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(position()).normalize().scale(1.1);
                setDeltaMovement(d);
                hasImpulse = true;
            }
            if (t > 20 && t < 30) setDeltaMovement(getDeltaMovement().scale(0.85).add(0, 0.03, 0));
        } else if (m.id == DUST && t >= 6 && t <= 30 && t % 3 == 0 && level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.ASH, getX(), getY() + 0.4, getZ(), 14, 1.2, 0.5, 1.2, 0.03);
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case DUST -> {
                AreaEffectCloud c = new AreaEffectCloud(level(), getX(), getY() - 0.3, getZ());
                c.setOwner(this); c.setRadius(3.4f); c.setDuration(120); c.setWaitTime(4); c.setRadiusPerTick(-3.4f / 120f); c.setFixedColor(0xd8c8a0);
                c.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                c.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                level().addFreshEntity(c);
                playSound(SoundEvents.PHANTOM_AMBIENT, 1.4f, 0.6f);
            }
            case BUFFET -> {
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(m.maxRange, 3, m.maxRange), x -> x != this && x.isAlive())) {
                    Vec3 away = e.position().subtract(position()).multiply(1, 0, 1).normalize();
                    e.hurt(damageSources().mobAttack(this), m.damage);
                    e.push(away.x * 1.5, 0.35, away.z * 1.5);
                    e.hurtMarked = true;
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
                }
                playSound(SoundEvents.ELYTRA_FLYING, 1.5f, 0.5f);
            }
            case RAM -> { if (target != null && distanceTo(target) <= 2.6) { strike(target, m.damage, m.knockback); playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.7f); } }
            default -> {
                if (target != null && distanceTo(target) <= m.maxRange + 0.8 && strike(target, m.damage, m.knockback)) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
                    playSound(SoundEvents.BEE_STING, 1.2f, 0.6f);
                }
            }
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PHANTOM_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.PHANTOM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.PHANTOM_DEATH; }
    @Override public float getVoicePitch() { return 0.6f; }
}
