package com.reevil.entity.birkin;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * William Birkin, G-virus mutant (RE2): one boss with three mutations driven by HP.
 *  G1 (form 0): upright, huge claw arm and a giant eye on the shoulder - claw slam, backhand, grab-squeeze-throw.
 *  G2 (form 1, <65% HP): hunched, faster, adds claw flurry, long leap and a trampling charge.
 *  G3 (form 2, <30% HP): bloated giant with back tendrils, adds tendril whip, crushing stomp and a bite; drops the grab.
 * Each mutation starts with an invulnerable screaming transformation (partial heal).
 */
public class BirkinEntity extends BioMonster {
    public static final int SLAM = 1, BACKHAND = 2, GRAB = 3, FLURRY = 4, LEAP = 5, CHARGE = 6, WHIP = 7, STOMP = 8, BITE = 9, MUTATE = 10;

    private static final AttackMove M_SLAM = new AttackMove(SLAM, "claw_slam", 34, new int[]{18}, 0, 4.2, 22, 6).dmg(12f).kb(1.2).face(16);
    private static final AttackMove M_BACK = new AttackMove(BACKHAND, "backhand_sweep", 28, new int[]{14}, 0, 3.8, 18, 5).dmg(9f).kb(1.7).face(12);
    private static final AttackMove M_GRAB = new AttackMove(GRAB, "grab_squeeze", 72, new int[]{16, 34, 50, 62}, 0, 3.0, 150, 3).dmg(4f).kb(0).face(16);
    private static final AttackMove M_FLURRY = new AttackMove(FLURRY, "claw_flurry", 32, new int[]{8, 15, 22}, 0, 3.4, 20, 7).dmg(7f).kb(0.6).face(22);
    private static final AttackMove M_LEAP = new AttackMove(LEAP, "great_leap", 38, new int[]{21}, 4, 13, 70, 4).dmg(11f).kb(1.4).face(8).move();
    private static final AttackMove M_CHARGE = new AttackMove(CHARGE, "trample_charge", 42, new int[]{14, 22, 30}, 6, 17, 80, 4).dmg(8f).kb(2f).face(8).move();
    private static final AttackMove M_WHIP = new AttackMove(WHIP, "tendril_whip", 36, new int[]{14, 23}, 1.5, 9, 28, 6).dmg(9f).kb(1.0).face(22);
    private static final AttackMove M_STOMP = new AttackMove(STOMP, "crushing_stomp", 42, new int[]{23}, 0, 6.5, 90, 4).dmg(12f).kb(1.5).face(14);
    private static final AttackMove M_BITE = new AttackMove(BITE, "gaping_bite", 34, new int[]{17}, 0, 3.6, 40, 4).dmg(16f).kb(0.8).face(15);
    private static final AttackMove M_MUTATE = new AttackMove(MUTATE, "mutation", 80, new int[]{40}, 0, 99, 30, 0).face(0);
    private static final List<AttackMove> G1 = List.of(M_SLAM, M_BACK, M_GRAB);
    private static final List<AttackMove> G2 = List.of(M_SLAM, M_BACK, M_GRAB, M_FLURRY, M_LEAP, M_CHARGE);
    private static final List<AttackMove> G3 = List.of(M_SLAM, M_FLURRY, M_CHARGE, M_WHIP, M_STOMP, M_BITE);
    /** G4: legs ruined, scuttles on its arms - fast and low. G5: the final bloated mass. */
    private static final List<AttackMove> G4 = List.of(M_SLAM, M_BACK, M_FLURRY, M_LEAP, M_CHARGE);
    private static final List<AttackMove> G5 = List.of(M_SLAM, M_WHIP, M_STOMP, M_BITE);

    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private LivingEntity held;
    private boolean mutating;
    private int lastForm;

    public BirkinEntity(EntityType<BirkinEntity> type, Level level) {
        super(type, level);
        xpReward = 200;
        setMaxUpStep(1.4f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 360.0)
            .add(Attributes.MOVEMENT_SPEED, 0.24)
            .add(Attributes.ATTACK_DAMAGE, 10.0)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
            .add(Attributes.ARMOR, 6.0);
    }

    /** model stage: G4 reuses the hunched G2 pose, G5 the bloated G3 one */
    public int stage() { int f = getForm(); return f == 3 ? 1 : f == 4 ? 2 : f; }
    @Override public List<AttackMove> moves() { return switch (getForm()) { case 0 -> G1; case 1 -> G2; case 2 -> G3; case 3 -> G4; default -> G5; }; }
    @Override public String getTextureSuffix() { return new String[]{"", "_g2", "_g3", "_g4", "_g5"}[Math.min(4, getForm())]; }
    @Override public boolean removeWhenFarAway(double d) { return false; }
    @Override protected boolean isImmobile() { return super.isImmobile() || getMoveId() == MUTATE; }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return switch (getForm()) { case 1 -> EntityDimensions.scalable(1.5f, 2.6f); case 2 -> EntityDimensions.scalable(2.3f, 3.8f); case 3 -> EntityDimensions.scalable(1.6f, 2.1f); case 4 -> EntityDimensions.scalable(3.0f, 4.6f); default -> EntityDimensions.scalable(1.2f, 3.1f); };
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (mutating) return false;
        return super.hurt(src, amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (getForm() != lastForm) { lastForm = getForm(); refreshDimensions(); }
        if (level().isClientSide) return;
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (isAlive() && getMoveId() != MUTATE) {
            float frac = getHealth() / getMaxHealth();
            if ((getForm() == 0 && frac < 0.65f) || (getForm() == 1 && frac < 0.30f) || (getForm() == 2 && frac < 0.12f) || (getForm() == 3 && frac < 0.05f)) { cancelMove(); startMove(M_MUTATE); }
        }
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        held = null;
        mutating = m.id == MUTATE;
        if (m.id == MUTATE) playSound(SoundEvents.RAVAGER_ROAR, 3.5f, 0.35f);
        else if (m.id == SLAM || m.id == STOMP || m.id == BACKHAND) playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, 0.45f);
    }

    @Override protected void onMoveEnd(AttackMove m) { held = null; mutating = false; }
    @Override public void cancelMove() { super.cancelMove(); held = null; mutating = false; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case FLURRY: if (t == 7 || t == 14 || t == 21) lunge(0.2, 0); break;
            case SLAM: if (t >= 15 && t <= 17) lunge(0.12, 0); break;
            case LEAP:
                if (t == 10) {
                    double h = target != null ? Math.min(1.5, Math.max(0.5, (distanceTo(target) - 1.0) / 7.0)) : 0.9;
                    lunge(h, 0.55);
                }
                break;
            case CHARGE: if (t >= 6 && t <= 34) lunge(getForm() == 2 ? 0.34 : 0.46, 0); break;
            case GRAB:
                if (held != null && held.isAlive() && t >= 16 && t <= 62) {
                    Vec3 hold = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.4)).add(0, 1.9, 0);
                    Vec3 d = hold.subtract(held.position().add(0, held.getBbHeight() * 0.5, 0));
                    held.setDeltaMovement(d.scale(0.45));
                    held.hurtMarked = true;
                    held.fallDistance = 0;
                }
                break;
            case MUTATE:
                if (t >= 20 && t <= 70 && t % 3 == 0 && level() instanceof ServerLevel sl)
                    sl.sendParticles(ParticleTypes.CRIMSON_SPORE, getX(), getY() + getBbHeight() * 0.6, getZ(), 10, getBbWidth() * 0.5, getBbHeight() * 0.3, getBbWidth() * 0.5, 0.1);
                break;
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case MUTATE -> {
                setForm(getForm() + 1);
                mutating = false;
                setHealth(Math.min(getMaxHealth(), getHealth() + getMaxHealth() * 0.12f));
                var sp = getAttribute(Attributes.MOVEMENT_SPEED); if (sp != null) sp.setBaseValue(switch (getForm()) { case 1 -> 0.30; case 3 -> 0.36; case 4 -> 0.22; default -> 0.27; });
                playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 3f, 0.3f);
                if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1.5, getZ(), 5, 1.0, 1.0, 1.0, 0.0);
            }
            case GRAB -> {
                if (hitIndex == 0) {
                    if (target != null && distanceTo(target) <= 3.4) { held = target; strike(target, m.damage, 0); playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.5f, 0.4f); }
                } else if (held != null && held.isAlive()) {
                    if (hitIndex < 3) { strike(held, 4f, 0); playSound(SoundEvents.PLAYER_HURT, 1f, 0.5f); }
                    else {
                        Vec3 f = Vec3.directionFromRotation(0, getYRot());
                        strike(held, 12f, 0);
                        held.setDeltaMovement(f.x * 1.6, 0.6, f.z * 1.6);
                        held.hurtMarked = true;
                        playSound(SoundEvents.GENERIC_EXPLODE, 1.2f, 1.2f);
                        held = null;
                    }
                }
            }
            case STOMP -> stomp(m);
            case LEAP -> {
                super.onMoveHit(m, hitIndex, target);
                stomp(m, 3.5);
            }
            case BITE -> {
                if (target != null && distanceTo(target) <= m.maxRange + 1 && strike(target, m.damage, m.knockback)) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
                    playSound(SoundEvents.GENERIC_EAT, 1.5f, 0.4f);
                }
            }
            case CHARGE -> {
                if (target != null && distanceTo(target) <= 3.2 && strike(target, m.damage, m.knockback)) playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.4f, 0.5f);
            }
            default -> {
                super.onMoveHit(m, hitIndex, target);
                playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.3f, 0.45f);
            }
        }
    }

    private void stomp(AttackMove m) { stomp(m, getForm() == 2 ? 6.5 : 5.0); }

    private void stomp(AttackMove m, double r) {
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(r, 1.5, r), x -> x != this && x.isAlive())) {
            if (!e.onGround() && e.getY() - getY() > 1.4) continue;
            Vec3 away = e.position().subtract(position()).multiply(1, 0, 1).normalize();
            e.hurt(damageSources().mobAttack(this), m.damage * 0.8f);
            e.push(away.x * 1.2, 0.7, away.z * 1.2);
            e.hurtMarked = true;
        }
        playSound(SoundEvents.GENERIC_EXPLODE, 2f, 0.55f);
        if (level() instanceof ServerLevel sl) {
            BlockState below = level().getBlockState(BlockPos.containing(getX(), getY() - 0.5, getZ()));
            for (int i = 0; i < 28; i++) {
                double a = i * Math.PI * 2 / 28;
                for (double d = 1.2; d <= r; d += 1.4) sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, below), getX() + Math.cos(a) * d, getY() + 0.1, getZ() + Math.sin(a) * d, 1, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossEvent.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossEvent.removePlayer(p); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, 1.2f, 0.5f - getForm() * 0.1f); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
    @Override public float getVoicePitch() { return 0.5f - getForm() * 0.08f; }
}
