package com.reevil.entity.brainsucker;

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
 * Brain Sucker (RE3): bat-winged flier with a needle proboscis. Moves: proboscis stab (nausea), raking swoop, sonic screech (slows and sickens everyone near),
 * and CLING - it latches onto the prey's head and drains it with repeated stabs.
 */
public class BrainSuckerEntity extends BioMonster {
    public static final int STAB = 1, SWOOP = 2, SCREECH = 3, CLING = 4;

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(STAB, "proboscis_stab", 26, new int[]{12}, 0, 3.2, 14, 6).dmg(5f).kb(0.2).face(11),
        new AttackMove(SWOOP, "swoop", 34, new int[]{16}, 4, 12, 40, 5).dmg(7f).kb(0.9).face(8).move(),
        new AttackMove(SCREECH, "screech", 36, new int[]{16}, 0, 9, 160, 3).face(0),
        new AttackMove(CLING, "head_cling", 52, new int[]{12, 22, 32, 42}, 0, 2.2, 120, 3).dmg(3f).kb(0).face(52).move()
    );
    private LivingEntity clung;

    public BrainSuckerEntity(EntityType<BrainSuckerEntity> type, Level level) {
        super(type, level);
        moveControl = new FlyingMoveControl(this, 20, true);
        xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 26.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28)
            .add(Attributes.FLYING_SPEED, 0.6)
            .add(Attributes.ATTACK_DAMAGE, 5.0)
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
    public boolean isClinging() { return clung != null; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        setNoGravity(isAlive());
        LivingEntity t = getTarget();
        if (t != null && !isMoving() && isAlive()) {
            double dy = (t.getY() + 1.8) - getY();
            setDeltaMovement(getDeltaMovement().add(0, Math.max(-0.05, Math.min(0.05, dy * 0.04)), 0));
        }
    }

    @Override protected void onMoveStart(AttackMove m) { clung = null; playSound(m.id == SCREECH ? SoundEvents.BAT_LOOP : SoundEvents.BAT_TAKEOFF, 1.4f, 0.5f); }
    @Override protected void onMoveEnd(AttackMove m) { clung = null; }
    @Override public void cancelMove() { super.cancelMove(); clung = null; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case SWOOP:
                if (t == 8 && target != null) { setDeltaMovement(target.position().add(0, target.getBbHeight() * 0.6, 0).subtract(position()).normalize().scale(1.0)); hasImpulse = true; }
                if (t > 22) setDeltaMovement(getDeltaMovement().scale(0.85).add(0, 0.04, 0));
                break;
            case CLING:
                if (t == 6 && target != null) { setDeltaMovement(target.getEyePosition().subtract(position()).normalize().scale(0.8)); hasImpulse = true; }
                if (clung != null && clung.isAlive() && t >= 10 && t <= 48) { // glued to the victim's head
                    Vec3 head = clung.getEyePosition().add(0, 0.25, 0);
                    setPos(head.x, head.y - 0.3, head.z);
                    setDeltaMovement(Vec3.ZERO);
                }
                break;
            case SCREECH:
                if (t >= 6 && t <= 30 && t % 4 == 0 && level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getY() + 0.4, getZ(), 1, 0, 0, 0, 0);
                break;
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case SCREECH -> {
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(m.maxRange), x -> x != this && x.isAlive() && !(x instanceof BrainSuckerEntity))) {
                    e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 0));
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                }
                playSound(SoundEvents.WARDEN_SONIC_BOOM, 1.2f, 1.6f);
            }
            case CLING -> {
                if (hitIndex == 0) { if (target != null && distanceTo(target) <= 3.0) { clung = target; strike(target, m.damage, 0); } }
                else if (clung != null && clung.isAlive()) { strike(clung, m.damage, 0); clung.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0)); heal(1.5f); playSound(SoundEvents.BEE_STING, 1.0f, 0.6f); }
            }
            case SWOOP -> { if (target != null && distanceTo(target) <= 2.6) strike(target, m.damage, m.knockback); }
            default -> {
                if (target != null && distanceTo(target) <= m.maxRange + 0.8 && strike(target, m.damage, m.knockback)) { target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0)); playSound(SoundEvents.BEE_STING, 1.2f, 0.5f); }
            }
        }
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.BAT_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.BAT_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.BAT_DEATH; }
    @Override public float getVoicePitch() { return 0.5f; }
}
