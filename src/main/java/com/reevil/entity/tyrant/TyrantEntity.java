package com.reevil.entity.tyrant;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * T-002 Tyrant (RE1 1996 / REmake). Boss.
 * Spawns DORMANT (kneeling, eyes shut) until a player comes within 8 blocks or it is struck, then plays AWAKEN (invulnerable).
 * Forms: 0 Tyrant (coat tails) -> 1 Super Tyrant at <60% HP (MUTATE: coat torn off, exposed heart = weak spot, faster)
 *        -> 2 enraged at <25% HP (ENRAGE: roar, regeneration burst, longer claws, hunched, fastest).
 */
public class TyrantEntity extends BioMonster {
    public static final int CLAW_THRUST = 1, DECAPITATE = 2, GRAB_THROW = 3, GROUND_SLAM = 4, BACKHAND = 5, SHOULDER_CHARGE = 6, LUNGE_STAB = 7;
    public static final int AWAKEN = 10, MUTATE = 11, ENRAGE = 12;
    public static final int FORM_TYRANT = 0, FORM_SUPER = 1, FORM_ENRAGED = 2;

    private static final EntityDataAccessor<Boolean> DATA_DORMANT = SynchedEntityData.defineId(TyrantEntity.class, EntityDataSerializers.BOOLEAN);

    /** per form: animations play faster (durations and hit ticks scale together so impact keys stay synced), damage rises. */
    private static final List<List<AttackMove>> MOVES = List.of(moveSet(1f, 1f), moveSet(0.85f, 1.15f), moveSet(0.72f, 1.3f));
    private static final AttackMove AWAKEN_MOVE = new AttackMove(AWAKEN, "awaken", 90, new int[]{20, 70}, 0, 99, 20, 0).face(90);
    private static final AttackMove MUTATE_MOVE = new AttackMove(MUTATE, "mutate", 60, new int[]{30}, 0, 99, 10, 0).face(0);
    private static final AttackMove ENRAGE_MOVE = new AttackMove(ENRAGE, "enrage", 50, new int[]{20}, 0, 99, 10, 0).face(0);

    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private LivingEntity grabbed;

    public TyrantEntity(EntityType<TyrantEntity> type, Level level) {
        super(type, level);
        xpReward = 150;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 300.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.ATTACK_DAMAGE, 14.0)
            .add(Attributes.ARMOR, 8.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
            .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    private static int s(int t, float f) { return Math.max(1, Math.round(t * f)); }

    private static List<AttackMove> moveSet(float f, float dm) {
        return List.of(
            new AttackMove(CLAW_THRUST, "claw_thrust", s(34, f), new int[]{s(18, f)}, 0, 2.8, s(14, f), 10).dmg(14 * dm).kb(1.0),
            new AttackMove(DECAPITATE, "decapitation_slash", s(42, f), new int[]{s(26, f)}, 0, 2.8, s(30, f), 30).dmg(45 * dm).kb(1.4),
            new AttackMove(GRAB_THROW, "grab_throw", s(52, f), new int[]{s(16, f), s(38, f)}, 0, 2.2, s(30, f), 6).dmg(6 * dm).kb(0).face(s(16, f)),
            new AttackMove(GROUND_SLAM, "ground_slam", s(46, f), new int[]{s(28, f)}, 0, 3.5, s(30, f), 6).dmg(12 * dm).kb(1.5).face(s(20, f)),
            new AttackMove(BACKHAND, "backhand", s(30, f), new int[]{s(15, f)}, 0, 3.0, s(14, f), 8).dmg(10 * dm).kb(1.6),
            new AttackMove(SHOULDER_CHARGE, "shoulder_charge", s(44, f), new int[]{s(14, f), s(18, f), s(22, f), s(26, f), s(30, f)}, 5, 14, s(40, f), 5)
                .dmg(12 * dm).kb(2.0).face(s(10, f)).move(),
            new AttackMove(LUNGE_STAB, "lunge_stab", s(36, f), new int[]{s(22, f)}, 3.2, 8, s(24, f), 6).dmg(14 * dm).kb(1.2).face(s(14, f)).move()
        );
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_DORMANT, true);
    }

    public boolean isDormant() { return entityData.get(DATA_DORMANT); }
    public boolean isTransforming() { return getMoveId() >= AWAKEN; }

    @Override
    public List<AttackMove> moves() { return MOVES.get(Mth.clamp(getForm(), 0, 2)); }

    @Override
    public String getTextureSuffix() {
        if (isDormant()) return "_dormant";
        return switch (getForm()) { case FORM_SUPER -> "_super"; case FORM_ENRAGED -> "_enraged"; default -> ""; };
    }

    @Override
    public boolean canStartMove(AttackMove m) {
        LivingEntity t = getTarget();
        if (m.id == DECAPITATE) return t != null && t.getHealth() < t.getMaxHealth() * 0.35f;
        if (m.id == SHOULDER_CHARGE) return getForm() >= FORM_SUPER || getRandom().nextInt(3) == 0;
        return true;
    }

    /** goals are suspended while dormant / rising up. */
    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || isDormant() || getMoveId() == AWAKEN;
    }

    // ------------------------------------------------------------------ server tick
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        bossEvent.setProgress(getHealth() / getMaxHealth());
        bossEvent.setVisible(!isDormant());
        if (isDormant()) {
            Player p = level().getNearestPlayer(getX(), getY(), getZ(), 8.0, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
            if (p != null) wake(p);
            return;
        }
        if (isTransforming() || !isAlive()) return;
        float hp = getHealth() / getMaxHealth();
        if (getForm() == FORM_TYRANT && hp < 0.6f) transform(MUTATE_MOVE);
        else if (getForm() == FORM_SUPER && hp < 0.25f) transform(ENRAGE_MOVE);
    }

    private void wake(LivingEntity who) {
        entityData.set(DATA_DORMANT, false);
        if (who != null) setTarget(who);
        startMove(AWAKEN_MOVE);
    }

    private void transform(AttackMove m) {
        cancelMove();
        grabbed = null;
        startMove(m);
    }

    private void applyFormStats() {
        AttributeInstance spd = getAttribute(Attributes.MOVEMENT_SPEED);
        if (spd != null) spd.setBaseValue(switch (getForm()) { case FORM_SUPER -> 0.28; case FORM_ENRAGED -> 0.33; default -> 0.2; });
    }

    // ------------------------------------------------------------------ damage
    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (!level().isClientSide && !src.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (isDormant()) {
                wake(src.getEntity() instanceof LivingEntity le ? le : null);
                return false;
            }
            if (isTransforming()) {
                ((ServerLevel) level()).sendParticles(ParticleTypes.CRIT, getX(), getY() + 1.8, getZ(), 8, 0.5, 0.6, 0.5, 0.2);
                return false;
            }
            if (getForm() >= FORM_SUPER && hitsHeart(src)) {
                amount *= 1.75f;
                blood(getX(), getY() + 1.9, getZ(), 14);
                playSound(SoundEvents.WARDEN_HEARTBEAT, 1.5f, 1.4f);
            }
        }
        return super.hurt(src, amount);
    }

    /** exposed heart: hit from the front cone; projectiles must arrive at chest height. */
    private boolean hitsHeart(DamageSource src) {
        Entity d = src.getDirectEntity();
        if (d == null) return false;
        Vec3 to = new Vec3(d.getX() - getX(), 0, d.getZ() - getZ());
        if (to.lengthSqr() < 1.0E-4 || Vec3.directionFromRotation(0, yBodyRot).dot(to.normalize()) < 0.45) return false;
        if (!(d instanceof Projectile)) return true;
        double rel = d.getY() - getY();
        return rel > 1.4 && rel < 2.5;
    }

    // ------------------------------------------------------------------ moves
    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        switch (m.id) {
            case DECAPITATE -> playSound(SoundEvents.RAVAGER_ROAR, 1.4f, 0.55f);
            case SHOULDER_CHARGE -> playSound(SoundEvents.RAVAGER_ROAR, 1.6f, 0.7f);
            case MUTATE, ENRAGE -> playSound(SoundEvents.WARDEN_HEARTBEAT, 2.0f, 0.7f);
            case AWAKEN -> playSound(SoundEvents.WARDEN_EMERGE, 1.5f, 0.8f);
            default -> { }
        }
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        grabbed = null;
        if (m.id == SHOULDER_CHARGE || m.id == LUNGE_STAB) setDeltaMovement(getDeltaMovement().multiply(0.2, 1, 0.2));
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        float u = t / (float) m.duration;
        switch (m.id) {
            case GRAB_THROW -> {
                if (grabbed == null) return;
                if (!grabbed.isAlive()) { grabbed = null; return; }
                // lift overhead, then carry over the right shoulder for the wind-up before the hurl
                float lift = Mth.clamp((t - m.hitTicks[0]) / 10f, 0f, 1f);
                float wind = Mth.clamp((t - (m.hitTicks[1] - 9)) / 8f, 0f, 1f);
                Vec3 fwd = Vec3.directionFromRotation(0, getYRot());
                Vec3 right = new Vec3(-fwd.z, 0, fwd.x);
                holdAt(grabbed, position().add(fwd.scale(1.5 - wind * 1.3)).add(right.scale(-wind * 0.6)).add(0, 1.4 + lift * 1.7, 0));
            }
            case SHOULDER_CHARGE -> {
                if (u >= 0.18f && u <= 0.74f) {
                    lunge(0.3 + 0.45 * Mth.clamp((u - 0.18f) / 0.25f, 0f, 1f), 0);
                    if (t % 3 == 0) dust(6);
                } else if (u > 0.74f) {
                    setDeltaMovement(getDeltaMovement().multiply(0.55, 1, 0.55));
                    if (u < 0.86f) dust(3);
                }
            }
            case LUNGE_STAB -> {
                if (t == Math.round(m.duration * 0.42f)) { lunge(1.15, 0.25); dust(10); }
            }
            case ENRAGE -> {
                if (t <= m.hitTicks[0] + 10) heal(getMaxHealth() * 0.005f);
                if (t % 4 == 0) ((ServerLevel) level()).sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 2.0, getZ(), 3, 0.5, 0.6, 0.5, 0.02);
            }
            default -> { }
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        ServerLevel sl = (ServerLevel) level();
        Vec3 fwd = Vec3.directionFromRotation(0, getYRot());
        switch (m.id) {
            case CLAW_THRUST, LUNGE_STAB -> {
                playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.2f, 0.6f);
                if (target != null && inReach(target, 2.8, 0.3) && strike(target, m.damage, m.knockback)) blood(target.getX(), target.getY() + target.getBbHeight() * 0.6, target.getZ(), 16);
            }
            case DECAPITATE -> {
                // horizontal beheading sweep: everything in the frontal half-circle at neck height
                playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 2.0f, 0.5f);
                for (int k = -3; k <= 3; k++) {
                    Vec3 a = fwd.yRot(k * 0.4f).scale(2.2);
                    sl.sendParticles(ParticleTypes.SWEEP_ATTACK, getX() + a.x, getY() + 1.7, getZ() + a.z, 1, 0, 0, 0, 0);
                }
                for (LivingEntity v : victims(3.0, 0.0)) {
                    if (strike(v, m.damage, m.knockback)) blood(v.getX(), v.getEyeY(), v.getZ(), 40);
                }
            }
            case GRAB_THROW -> {
                if (i == 0) {
                    if (target != null && inReach(target, 2.6, 0.3) && strike(target, m.damage, 0)) {
                        grabbed = target;
                        playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.2f, 0.6f);
                    }
                } else if (grabbed != null) {
                    LivingEntity v = grabbed;
                    grabbed = null;
                    v.invulnerableTime = 0;
                    v.hurt(damageSources().mobAttack(this), m.damage + 4);
                    v.setDeltaMovement(fwd.x * 2.2, 0.8, fwd.z * 2.2);
                    v.hurtMarked = true;
                    playSound(SoundEvents.RAVAGER_ROAR, 1.2f, 0.8f);
                }
            }
            case GROUND_SLAM -> slam(sl, m, fwd);
            case BACKHAND -> {
                playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.4f, 0.6f);
                for (LivingEntity v : victims(3.0, -0.2)) {
                    if (strike(v, m.damage, m.knockback)) blood(v.getX(), v.getY() + v.getBbHeight() * 0.6, v.getZ(), 10);
                }
            }
            case SHOULDER_CHARGE -> {
                for (LivingEntity v : victims(1.7, 0.2)) {
                    if (strike(v, m.damage, m.knockback)) {
                        v.push(0, 0.4, 0);
                        playSound(SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.2f, 0.6f);
                    }
                }
            }
            case AWAKEN -> {
                if (i == 0) playSound(SoundEvents.WARDEN_HEARTBEAT, 2.0f, 0.6f);
                else {
                    playSound(SoundEvents.RAVAGER_ROAR, 2.5f, 0.45f);
                    sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 2.5, getZ(), 20, 0.6, 0.3, 0.6, 0.05);
                }
            }
            case MUTATE -> {
                setForm(FORM_SUPER);
                applyFormStats();
                playSound(SoundEvents.WARDEN_ROAR, 2.5f, 0.7f);
                blood(getX(), getY() + 1.9, getZ(), 60);
                shove(3.5, 1.0);
            }
            case ENRAGE -> {
                setForm(FORM_ENRAGED);
                applyFormStats();
                playSound(SoundEvents.WARDEN_ROAR, 3.0f, 0.5f);
                sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1.5, getZ(), 2, 0.4, 0.4, 0.4, 0);
                shove(4.5, 1.4);
            }
            default -> { }
        }
    }

    /** Overhead double-fist slam: shockwave ring, damage + knockback falling off with distance. */
    private void slam(ServerLevel sl, AttackMove m, Vec3 fwd) {
        Vec3 c = position().add(fwd.scale(1.6));
        playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 2.0f, 0.6f);
        playSound(SoundEvents.ANVIL_LAND, 1.0f, 0.4f);
        BlockState ground = level().getBlockState(blockPosition().below());
        if (ground.isAir()) ground = Blocks.STONE.defaultBlockState();
        BlockParticleOption debris = new BlockParticleOption(ParticleTypes.BLOCK, ground);
        sl.sendParticles(ParticleTypes.EXPLOSION, c.x, getY() + 0.2, c.z, 1, 0, 0, 0, 0);
        for (int r = 1; r <= 4; r++) {
            for (int k = 0; k < 8 * r; k++) {
                double a = k * Math.PI * 2 / (8 * r);
                sl.sendParticles(debris, c.x + Math.cos(a) * r, getY() + 0.1, c.z + Math.sin(a) * r, 2, 0.1, 0.1, 0.1, 0.15);
            }
        }
        for (LivingEntity v : level().getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(4.5, 2.0, 4.5), e -> e != this && e.isAlive())) {
            double d = v.position().distanceTo(c);
            if (d > 4.5 || v.getY() - getY() > 1.5) continue;
            if (v.hurt(damageSources().mobAttack(this), (float) (m.damage * (1.0 - d / 7.0)))) {
                Vec3 k = v.position().subtract(c).multiply(1, 0, 1).normalize().scale(m.knockback * (1.0 - d / 6.0));
                v.push(k.x, 0.6, k.z);
                v.hurtMarked = true;
            }
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
            e -> e != this && e.isAlive() && !(e instanceof TyrantEntity) && inReach(e, reach, minDot));
    }

    private void shove(double radius, double strength) {
        for (LivingEntity v : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(radius), e -> e != this && e.isAlive())) {
            Vec3 k = v.position().subtract(position()).multiply(1, 0, 1).normalize().scale(strength);
            v.push(k.x, 0.35, k.z);
            v.hurtMarked = true;
        }
    }

    private static void holdAt(LivingEntity v, Vec3 want) {
        v.setDeltaMovement(want.subtract(v.position()).scale(0.6));
        v.hurtMarked = true;
        v.fallDistance = 0;
    }

    private void blood(double x, double y, double z, int n) {
        ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()), x, y, z, n, 0.3, 0.3, 0.3, 0.2);
    }

    private void dust(int n) {
        BlockState g = level().getBlockState(blockPosition().below());
        if (g.isAir()) return;
        ((ServerLevel) level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, g), getX(), getY() + 0.1, getZ(), n, 0.5, 0.05, 0.5, 0.1);
    }

    // ------------------------------------------------------------------ boss bar / misc
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
    protected SoundEvent getAmbientSound() { return isDormant() ? null : SoundEvents.RAVAGER_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.IRON_GOLEM_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }

    @Override
    public float getVoicePitch() { return 0.55f + getRandom().nextFloat() * 0.1f; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.IRON_GOLEM_STEP, 1.3f, getForm() == FORM_TYRANT ? 0.5f : 0.65f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Dormant", isDormant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Dormant")) entityData.set(DATA_DORMANT, tag.getBoolean("Dormant"));
        applyFormStats();
    }
}
