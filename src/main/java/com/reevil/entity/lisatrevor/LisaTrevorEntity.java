package com.reevil.entity.lisatrevor;

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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Lisa Trevor (REmake). Shackled, long-armed, moaning boss with a pulsating flesh mass on her back.
 * Slow shamble with sudden speed bursts; periodically recoils in pain (regenerates, takes extra damage meanwhile).
 * Form 1 at <50% HP ("mutated"): MUTATE (invulnerable) bursts two extra thin arms out of her ribs -> 4 arms, faster, QUAD_FLURRY.
 */
public class LisaTrevorEntity extends BioMonster {
    public static final int ARM_SWIPE = 1, GRAB_HOLD = 2, FLING = 3, POUNCE = 4, WAIL = 5, CHAIN_SPIN = 6, RECOIL = 7, QUAD_FLURRY = 8;
    public static final int MUTATE = 10;
    public static final int FORM_NORMAL = 0, FORM_MUTATED = 1;

    private static final List<AttackMove> MOVES_0 = moveSet(false);
    private static final List<AttackMove> MOVES_1 = moveSet(true);
    private static final AttackMove MUTATE_MOVE = new AttackMove(MUTATE, "mutate", 70, new int[]{35}, 0, 99, 10, 0).face(0);

    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
    private LivingEntity grabbed;
    private int burst;
    private int recoilTimer = 200;

    public LisaTrevorEntity(EntityType<LisaTrevorEntity> type, Level level) {
        super(type, level);
        xpReward = 120;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 240.0)
            .add(Attributes.MOVEMENT_SPEED, 0.19)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.ARMOR, 10.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
            .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    private static int s(int t, float f) { return Math.max(1, Math.round(t * f)); }

    private static List<AttackMove> moveSet(boolean mutated) {
        float f = mutated ? 0.85f : 1f, dm = mutated ? 1.25f : 1f;
        List<AttackMove> l = new ArrayList<>();
        l.add(new AttackMove(ARM_SWIPE, "long_arm_swipe", s(30, f), new int[]{s(17, f)}, 0, 4.5, s(16, f), 10).dmg(8 * dm).kb(1.2));
        l.add(new AttackMove(GRAB_HOLD, "grab_and_hold", s(64, f), new int[]{s(14, f), s(26, f), s(38, f), s(50, f), s(58, f)}, 0, 4.0, s(40, f), 5)
            .dmg(3 * dm).kb(0).face(80));
        l.add(new AttackMove(FLING, "fling", s(48, f), new int[]{s(16, f), s(34, f)}, 0, 3.5, s(36, f), 5).dmg(5 * dm).kb(0).face(s(16, f)));
        l.add(new AttackMove(POUNCE, "pounce_crawl", s(44, f), new int[]{s(22, f), s(26, f), s(30, f)}, 4, 12, s(40, f), 6).dmg(9 * dm).kb(0.8).face(s(18, f)).move());
        l.add(new AttackMove(WAIL, "wail", s(54, f), new int[]{s(22, f)}, 0, 10, s(60, f), 3).dmg(0).kb(0));
        l.add(new AttackMove(CHAIN_SPIN, "chain_spin", s(46, f), new int[]{s(20, f), s(26, f)}, 0, 3.5, s(30, f), 6).dmg(9 * dm).kb(1.6).face(s(12, f)));
        l.add(new AttackMove(RECOIL, "flesh_recoil", 56, new int[]{28}, 0, 40, 20, 3).dmg(0).kb(0).face(0));
        if (mutated) l.add(new AttackMove(QUAD_FLURRY, "quad_flurry", 40, new int[]{12, 18, 24, 30}, 0, 3.2, 20, 9).dmg(7).kb(0.5).face(30));
        return List.copyOf(l);
    }

    @Override
    public List<AttackMove> moves() { return getForm() == FORM_MUTATED ? MOVES_1 : MOVES_0; }

    @Override
    public String getTextureSuffix() { return getForm() == FORM_MUTATED ? "_mutated" : ""; }

    @Override
    public boolean canStartMove(AttackMove m) {
        if (m.id == RECOIL) return recoilTimer <= 0 && getHealth() < getMaxHealth() * 0.95f;
        return true;
    }

    // ------------------------------------------------------------------ server tick
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (recoilTimer > 0) recoilTimer--;
        // shambling, then a sudden lurching burst of speed
        if (burst > 0) {
            if (--burst == 0) applySpeed();
        } else if (getTarget() != null && !isMoving() && getRandom().nextInt(getForm() == FORM_MUTATED ? 50 : 90) == 0) {
            burst = 25;
            applySpeed();
            playSound(SoundEvents.GHAST_WARN, 1.0f, 0.5f);
        }
        if (isAlive() && getForm() == FORM_NORMAL && getMoveId() != MUTATE && getHealth() < getMaxHealth() * 0.5f) {
            cancelMove();
            grabbed = null;
            startMove(MUTATE_MOVE);
        }
    }

    private void applySpeed() {
        AttributeInstance spd = getAttribute(Attributes.MOVEMENT_SPEED);
        if (spd == null) return;
        double base = getForm() == FORM_MUTATED ? 0.24 : 0.19;
        spd.setBaseValue(burst > 0 ? base * 2.1 : base);
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (!level().isClientSide && !src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (getMoveId() == MUTATE) return false;
            if (getMoveId() == RECOIL) amount *= 1.5f;   // the pain fit is the opening
        }
        return super.hurt(src, amount);
    }

    // ------------------------------------------------------------------ moves
    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        switch (m.id) {
            case WAIL -> playSound(SoundEvents.GHAST_AMBIENT, 2.0f, 0.4f);
            case POUNCE -> playSound(SoundEvents.GHAST_WARN, 1.5f, 0.6f);
            case RECOIL -> playSound(SoundEvents.GHAST_HURT, 1.5f, 0.45f);
            case MUTATE -> playSound(SoundEvents.GHAST_SCREAM, 2.0f, 0.4f);
            default -> { }
        }
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        grabbed = null;
        if (m.id == RECOIL) recoilTimer = 300 + getRandom().nextInt(200);
        if (m.id == POUNCE) setDeltaMovement(getDeltaMovement().multiply(0.2, 1, 0.2));
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        float u = t / (float) m.duration;
        Vec3 fwd = Vec3.directionFromRotation(0, getYRot());
        switch (m.id) {
            case GRAB_HOLD -> {
                if (grabbed == null) return;
                if (!grabbed.isAlive()) { grabbed = null; return; }
                // drag the victim in from arm's length to face height
                float in = Mth.clamp((t - m.hitTicks[0]) / 10f, 0f, 1f);
                holdAt(grabbed, position().add(fwd.scale(2.6 - in * 1.5)).add(0, 0.6 + in * 0.5, 0));
            }
            case FLING -> {
                if (grabbed == null) return;
                if (!grabbed.isAlive()) { grabbed = null; return; }
                // hoisted and swung up over her head before the release
                float sw = Mth.clamp((t - m.hitTicks[0]) / (float) (m.hitTicks[1] - m.hitTicks[0]), 0f, 1f);
                holdAt(grabbed, position().add(fwd.scale(1.8 - sw * 2.0)).add(0, 0.8 + Mth.sin(sw * (float) Math.PI) * 2.4, 0));
            }
            case POUNCE -> {
                int leap = Math.round(m.duration * 0.45f);
                if (u >= 0.15f && t < leap) lunge(0.32, 0);
                else if (t == leap) { lunge(1.0, 0.45); playSound(SoundEvents.GHAST_SCREAM, 1.2f, 0.7f); }
                else if (u > 0.72f) setDeltaMovement(getDeltaMovement().multiply(0.5, 1, 0.5));
            }
            case WAIL -> {
                int k = t - m.hitTicks[0];
                if (k >= 0 && k <= 18 && k % 3 == 0) ring(0.8 + k * 0.5);
            }
            case CHAIN_SPIN -> {
                if (u > 0.3f && u < 0.75f && t % 2 == 0) {
                    double a = t * 1.1;
                    ((ServerLevel) level()).sendParticles(ParticleTypes.SWEEP_ATTACK, getX() + Math.cos(a) * 2.2, getY() + 1.2, getZ() + Math.sin(a) * 2.2, 1, 0, 0, 0, 0);
                }
            }
            case RECOIL -> {
                if (t >= 10 && t <= 45) heal(0.3f);
            }
            case MUTATE -> {
                if (t % 5 == 0) blood(getX(), getY() + 1.4, getZ(), 6);
            }
            default -> { }
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        ServerLevel sl = (ServerLevel) level();
        Vec3 fwd = Vec3.directionFromRotation(0, getYRot());
        switch (m.id) {
            case ARM_SWIPE -> {
                playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.2f, 0.5f);
                playSound(SoundEvents.CHAIN_HIT, 1.2f, 0.7f);
                for (LivingEntity v : victims(4.5, -0.1)) strike(v, m.damage, m.knockback);
            }
            case GRAB_HOLD -> {
                if (i == 0) {
                    if (target != null && inReach(target, 4.2, 0.4) && strike(target, 2, 0)) {
                        grabbed = target;
                        playSound(SoundEvents.CHAIN_HIT, 1.2f, 0.5f);
                    }
                } else if (grabbed != null) {
                    if (i == m.hitTicks.length - 1) {
                        grabbed.setDeltaMovement(fwd.x * 0.6, 0.3, fwd.z * 0.6);
                        grabbed.hurtMarked = true;
                        grabbed = null;
                    } else {
                        grabbed.invulnerableTime = 0;
                        grabbed.hurt(damageSources().mobAttack(this), m.damage);
                        blood(grabbed.getX(), grabbed.getY() + grabbed.getBbHeight() * 0.6, grabbed.getZ(), 8);
                        playSound(SoundEvents.GHAST_AMBIENT, 0.8f, 0.4f);
                    }
                }
            }
            case FLING -> {
                if (i == 0) {
                    if (target != null && inReach(target, 3.5, 0.3) && strike(target, m.damage, 0)) grabbed = target;
                } else if (grabbed != null) {
                    LivingEntity v = grabbed;
                    grabbed = null;
                    v.invulnerableTime = 0;
                    v.hurt(damageSources().mobAttack(this), m.damage + 3);
                    v.setDeltaMovement(fwd.x * 2.4, 0.9, fwd.z * 2.4);
                    v.hurtMarked = true;
                    playSound(SoundEvents.GHAST_SCREAM, 1.2f, 0.5f);
                }
            }
            case POUNCE -> {
                for (LivingEntity v : victims(1.8, 0.0)) {
                    if (strike(v, m.damage, m.knockback)) blood(v.getX(), v.getY() + 1.0, v.getZ(), 8);
                }
            }
            case WAIL -> {
                playSound(SoundEvents.GHAST_SCREAM, 3.0f, 0.45f);
                playSound(SoundEvents.WARDEN_SONIC_CHARGE, 1.5f, 0.6f);
                sl.sendParticles(ParticleTypes.SONIC_BOOM, getX(), getEyeY(), getZ(), 1, 0, 0, 0, 0);
                for (Player p : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(10.0), pl -> !pl.isSpectator() && !pl.isCreative())) {
                    p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                    p.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0));
                }
            }
            case CHAIN_SPIN -> {
                playSound(SoundEvents.CHAIN_BREAK, 1.4f, 0.6f);
                for (LivingEntity v : victims(3.8, -1.1)) strike(v, m.damage, m.knockback);
            }
            case RECOIL -> {
                heal(10f);
                blood(getX(), getY() + 1.6, getZ(), 20);
                playSound(SoundEvents.GHAST_HURT, 1.5f, 0.4f);
            }
            case QUAD_FLURRY -> {
                playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 0.7f + i * 0.1f);
                if (target != null && inReach(target, 3.2, 0.2)) {
                    target.invulnerableTime = 0;
                    if (strike(target, m.damage, m.knockback)) blood(target.getX(), target.getY() + 1.0, target.getZ(), 6);
                }
            }
            case MUTATE -> {
                setForm(FORM_MUTATED);
                applySpeed();
                playSound(SoundEvents.GHAST_DEATH, 2.5f, 0.4f);
                blood(getX(), getY() + 1.4, getZ(), 60);
                for (LivingEntity v : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3.5), e -> e != this && e.isAlive())) {
                    Vec3 k = v.position().subtract(position()).multiply(1, 0, 1).normalize();
                    v.push(k.x, 0.35, k.z);
                    v.hurtMarked = true;
                }
            }
            default -> { }
        }
    }

    // ------------------------------------------------------------------ helpers
    private boolean inReach(LivingEntity e, double reach, double minDot) {
        Vec3 to = new Vec3(e.getX() - getX(), 0, e.getZ() - getZ());
        double dist = to.length() - e.getBbWidth() * 0.5 - getBbWidth() * 0.5;
        if (dist > reach) return false;
        if (to.lengthSqr() < 1.0E-4) return true;
        return Vec3.directionFromRotation(0, getYRot()).dot(to.normalize()) >= minDot;
    }

    private List<LivingEntity> victims(double reach, double minDot) {
        return level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(reach + 1.0, 1.0, reach + 1.0),
            e -> e != this && e.isAlive() && !(e instanceof LisaTrevorEntity) && inReach(e, reach, minDot));
    }

    private static void holdAt(LivingEntity v, Vec3 want) {
        v.setDeltaMovement(want.subtract(v.position()).scale(0.6));
        v.hurtMarked = true;
        v.fallDistance = 0;
    }

    /** expanding sound-wave ring of the wail. */
    private void ring(double r) {
        ServerLevel sl = (ServerLevel) level();
        int n = (int) (r * 6);
        for (int k = 0; k < n; k++) {
            double a = k * Math.PI * 2 / n;
            sl.sendParticles(ParticleTypes.POOF, getX() + Math.cos(a) * r, getEyeY() - 0.3, getZ() + Math.sin(a) * r, 1, 0, 0.05, 0, 0.01);
        }
    }

    private void blood(double x, double y, double z, int n) {
        ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()), x, y, z, n, 0.3, 0.3, 0.3, 0.2);
    }

    // ------------------------------------------------------------------ boss bar / sounds
    @Override
    public void startSeenByPlayer(ServerPlayer p) {
        super.startSeenByPlayer(p);
        bossEvent.addPlayer(p);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p) {
        super.stopSeenByPlayer(p);
        bossEvent.removePlayer(p);
    }

    @Override
    public boolean removeWhenFarAway(double dist) { return false; }

    @Override
    public int getAmbientSoundInterval() { return 140; }

    /** grieving moan: ghast voice pitched way down. */
    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.GHAST_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.GHAST_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.GHAST_DEATH; }

    @Override
    public float getVoicePitch() { return 0.4f + getRandom().nextFloat() * 0.12f; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.CHAIN_STEP, 0.8f, 0.7f);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        applySpeed();
    }
}
