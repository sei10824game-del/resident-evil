package com.reevil.entity.crimsonhead;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * REmake Crimson Head: a zombie corpse that was not burned, reborn skinless, fast and clawed.
 * Sprints, screams on spotting prey, 2-hit / 3-hit alternating claw combos, leaping tackle-bite, lunge grab-bite.
 */
public class CrimsonHeadEntity extends BioMonster {
    public static final int SLASH2 = 1, SLASH3 = 2, LEAP = 3, LUNGE_GRAB = 4, SCREAM = 5, RISE = 6;

    // hitTick/duration == impact keyframe time in CrimsonHeadModel
    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(SLASH2, "claw_combo_2", 26, new int[]{9, 17}, 0, 1.8, 10, 5).dmg(5f).kb(0.3).face(17),
        new AttackMove(SLASH3, "claw_combo_3", 38, new int[]{9, 18, 28}, 0, 1.8, 16, 4).dmg(4.5f).kb(0.25).face(28),
        new AttackMove(LEAP, "leap_tackle_bite", 48, new int[]{18, 28, 37}, 2.8, 7.5, 40, 3).dmg(5f).kb(0).face(10).move(),
        new AttackMove(LUNGE_GRAB, "lunge_grab_bite", 40, new int[]{8, 19, 29}, 0, 2.6, 24, 3).dmg(4f).kb(0.8).face(32),
        new AttackMove(SCREAM, "scream", 30, new int[]{10}, 2.5, 24, 8, 60).face(30)
    );
    private static final AttackMove RISE_MOVE = new AttackMove(RISE, "rise", 40, new int[]{22}, 0, 0, 5, 0).face(0);

    private boolean screamReady;
    private boolean grabbing;
    private Vec3 grabAnchor = Vec3.ZERO;

    public CrimsonHeadEntity(EntityType<CrimsonHeadEntity> type, Level level) {
        super(type, level);
        xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40.0)
            .add(Attributes.MOVEMENT_SPEED, 0.36)
            .add(Attributes.ATTACK_DAMAGE, 5.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    public List<AttackMove> moves() { return MOVES; }

    /** Called by ZombieEntity when an unburned corpse mutates: play the rise-and-scream move. */
    public void reviveFromCorpse() {
        screamReady = false;
        startMove(RISE_MOVE);
    }

    @Override
    public boolean canStartMove(AttackMove m) {
        return m.id != SCREAM || screamReady;
    }

    @Override
    public void setTarget(LivingEntity target) {
        if (target != null && getTarget() == null) screamReady = true;
        super.setTarget(target);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        LivingEntity t = getTarget();
        if (t != null && !isMoving() && isAlive()) {
            // very quick turns: snap body/head toward the prey instead of the vanilla lazy body rotation
            float yaw = (float) (Mth.atan2(t.getZ() - getZ(), t.getX() - getX()) * (180.0 / Math.PI)) - 90f;
            yBodyRot = Mth.approachDegrees(yBodyRot, yaw, 35f);
            yHeadRot = Mth.approachDegrees(yHeadRot, yaw, 50f);
        }
    }

    // ---------------------------------------------------------------- moves

    @Override
    protected void onMoveStart(AttackMove m) {
        grabbing = false;
        getNavigation().stop();
        if (m.id == SCREAM) screamReady = false;
        if (m.id == LEAP || m.id == LUNGE_GRAB) playSound(SoundEvents.ZOMBIE_VILLAGER_HURT, 1.4f, 0.55f);
    }

    @Override
    protected void onMoveEnd(AttackMove m) { grabbing = false; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case SLASH2 -> { if ((t >= 5 && t <= 8) || (t >= 13 && t <= 16)) lunge(0.14, 0); }
            case SLASH3 -> { if ((t >= 5 && t <= 8) || (t >= 14 && t <= 17) || (t >= 24 && t <= 27)) lunge(0.14, 0); }
            case LUNGE_GRAB -> {
                if (!grabbing && t >= 4 && t <= 8) lunge(0.45, 0);
                if (grabbing) hold(target, t < 34);
            }
            case LEAP -> {
                if (t < 10) setDeltaMovement(0, getDeltaMovement().y, 0);           // coil
                else if (t == 10) {
                    double d = target != null ? Math.sqrt(distanceToSqr(target)) : 4;
                    lunge(Math.min(1.15, 0.2 + d * 0.14), 0.42);                     // launch
                } else if (t > 18 && onGround()) setDeltaMovement(0, getDeltaMovement().y, 0);
                if (grabbing) hold(target, t < 42);
            }
            default -> { }
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        switch (m.id) {
            case SLASH2, SLASH3 -> {
                if (target == null) return;
                if (distanceTo(target) <= m.maxRange + 0.9 + target.getBbWidth() * 0.5) {
                    target.invulnerableTime = 0;                      // combo hits land inside i-frames
                    boolean last = i == m.hitTicks.length - 1;
                    if (strike(target, last ? m.damage * 1.3f : m.damage, last ? m.knockback * 2 : m.knockback)) bleed(target);
                    playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.8f, 0.7f);
                }
            }
            case LUNGE_GRAB, LEAP -> {
                int last = m.hitTicks.length - 1;
                if (i == 0) {
                    double reach = m.id == LEAP ? 2.4 : 2.0;
                    if (target != null && target.isAlive() && distanceTo(target) <= reach + target.getBbWidth() * 0.5) {
                        grabbing = true;
                        grabAnchor = target.position();
                        target.invulnerableTime = 0;
                        if (m.id == LEAP) strike(target, 6f, 0);                // the tackle itself
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4), this);
                    }
                } else if (grabbing && target != null && target.isAlive()) {
                    target.invulnerableTime = 0;
                    if (strike(target, m.damage, i == last ? m.knockback : 0)) {
                        bleed(target);
                        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1), this);
                        target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 1), this);
                        playSound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 0.7f, 1.4f);
                    }
                    if (i == last) grabbing = false;
                }
            }
            case SCREAM, RISE -> {
                playSound(SoundEvents.ENDERMAN_SCREAM, 2.0f, 0.5f);
                playSound(SoundEvents.ZOMBIE_VILLAGER_AMBIENT, 2.0f, 0.45f);
            }
            default -> super.onMoveHit(m, i, target);
        }
    }

    private void hold(LivingEntity target, boolean active) {
        if (target == null || !target.isAlive()) { grabbing = false; return; }
        if (!active) return;
        target.setDeltaMovement(0, Math.min(0, target.getDeltaMovement().y), 0);
        if (target.position().distanceToSqr(grabAnchor) > 0.09) target.teleportTo(grabAnchor.x, grabAnchor.y, grabAnchor.z);
        target.hurtMarked = true;
    }

    private void bleed(LivingEntity target) {
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()),
                target.getX(), target.getY() + target.getBbHeight() * 0.7, target.getZ(), 16, 0.25, 0.25, 0.25, 0.12);
        }
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (src.getDirectEntity() instanceof Projectile p && p.getY() > getY() + getBbHeight() * 0.72) amount *= 1.5f;
        return super.hurt(src, amount);
    }

    // ---------------------------------------------------------------- sounds

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.ZOMBIE_VILLAGER_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.ZOMBIE_VILLAGER_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ZOMBIE_VILLAGER_DEATH; }

    @Override
    public float getVoicePitch() { return 0.5f + random.nextFloat() * 0.15f; }

    @Override
    public int getAmbientSoundInterval() { return 100; }
}
