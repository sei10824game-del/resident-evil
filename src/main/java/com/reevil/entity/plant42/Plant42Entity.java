package com.reevil.entity.plant42;

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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Plant 42 (RE1 boss): rooted, never moves. Moves: tentacle whip, vine grab (snare -> drag to the maw -> squeeze -> devour),
 * spore cloud, maw bite, ground slam, phase-2 flurry. Weak point = the base (hits landing low deal x2, the thick bulb/hide takes x0.6).
 * Phase 2 (form 1) at 50% HP after an invulnerable roar; wilts over 60 ticks on death.
 * Animation impact keys sit at hitTick/duration in client/plant42/Plant42Model.
 */
public class Plant42Entity extends BioMonster {
    public static final int WHIP = 1, GRAB = 2, GAS = 3, DEVOUR = 4, SLAM = 5, FLURRY = 6, ROAR = 7;
    public static final int WILT_TICKS = 60;

    private static final AttackMove M_WHIP = new AttackMove(WHIP, "tentacle_whip", 24, new int[]{12}, 1.0, 7, 20, 5).dmg(7f).kb(0.8).face(11);
    private static final AttackMove M_GRAB = new AttackMove(GRAB, "vine_grab", 60, new int[]{14, 30, 46}, 3, 8, 60, 3).dmg(3f).kb(0).face(14);
    private static final AttackMove M_GAS = new AttackMove(GAS, "spore_cloud", 50, new int[]{25}, 0, 9, 200, 2).face(0);
    private static final AttackMove M_DEVOUR = new AttackMove(DEVOUR, "maw_bite", 36, new int[]{20}, 0, 3.0, 30, 6).dmg(14f).kb(0.3).face(18);
    private static final AttackMove M_SLAM = new AttackMove(SLAM, "tentacle_slam", 40, new int[]{22}, 0, 6, 90, 3).dmg(8f).kb(1.2).face(14);
    private static final AttackMove M_FLURRY = new AttackMove(FLURRY, "thrash_flurry", 44, new int[]{10, 20, 30}, 1.0, 8, 40, 4).dmg(4.5f).kb(0.5).face(28);
    private static final AttackMove M_ROAR = new AttackMove(ROAR, "withering_roar", 40, new int[]{20}, 0, 99, 30, 0).face(0);
    private static final List<AttackMove> MOVES_0 = List.of(M_WHIP, M_GRAB, M_GAS, M_DEVOUR, M_SLAM);
    private static final List<AttackMove> MOVES_1 = List.of(M_WHIP, M_GRAB, M_GAS, M_DEVOUR, M_SLAM, M_FLURRY);

    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10);
    private LivingEntity grabbed;
    private boolean roaring;

    public Plant42Entity(EntityType<Plant42Entity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
        xpReward = 150;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 260.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
            .add(Attributes.FOLLOW_RANGE, 20.0)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.ARMOR, 4.0);
    }

    @Override public List<AttackMove> moves() { return getForm() == 1 ? MOVES_1 : MOVES_0; }
    @Override public String getTextureSuffix() { return getForm() == 1 ? "_phase2" : ""; }
    public boolean isGrabbing() { return grabbed != null; }

    // ---- rooted: no pushing, no knockback, no leash
    @Override public boolean isPushable() { return false; }
    @Override public void push(Entity e) {}
    @Override public void push(double x, double y, double z) {}
    @Override public void knockback(double s, double dx, double dz) {}
    @Override public boolean canBeLeashed(net.minecraft.world.entity.player.Player p) { return false; }
    @Override public boolean removeWhenFarAway(double d) { return false; }
    /** herbicide-proof: spore poison never affects it. */
    @Override public boolean canBeAffected(MobEffectInstance eff) { return false; }

    /** Weak point = base: arrows/blows landing below ~1.5 blocks deal x2, the thick bulb x0.6; invulnerable while roaring. */
    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (roaring) return false;
        Entity direct = src.getDirectEntity();
        Entity owner = src.getEntity();
        Vec3 hit = null;
        if (direct != null && direct != owner) hit = direct.position();
        else if (owner != null) hit = owner.getEyePosition().add(owner.getLookAngle().scale(owner.distanceTo(this)));
        if (hit != null && amount > 0) {
            boolean weak = hit.y < getY() + 1.5;
            amount *= weak ? 2.0f : 0.6f;
            if (weak && level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.CRIT, hit.x, hit.y, hit.z, 8, 0.3, 0.3, 0.3, 0.1);
        }
        if (src.is(DamageTypeTags.IS_FIRE)) amount *= 1.4f;
        return super.hurt(src, amount);
    }

    // ---- boss bar + phases
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (isAlive() && getForm() == 0 && getHealth() < getMaxHealth() * 0.5f) {
            setForm(1);
            cancelMove();
            startMove(M_ROAR);
        }
        if (tickCount % 10 == 0 && level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, getX(), getY() + 3.2, getZ(), getForm() == 1 ? 6 : 2, 1.2, 0.8, 1.2, 0.0);
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossEvent.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossEvent.removePlayer(p); }

    // ---- moves
    @Override
    public boolean canStartMove(AttackMove m) {
        if (roaring) return false;
        return m.id != GRAB || grabbed == null;
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        if (m.id == ROAR) { roaring = true; playSound(SoundEvents.RAVAGER_ROAR, 3f, 0.5f); }
        else if (m.id == GRAB || m.id == SLAM) playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, 0.5f);
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        roaring = false;
        grabbed = null;
    }

    @Override
    public void cancelMove() {
        super.cancelMove();
        roaring = false;
        grabbed = null;
    }

    /** the mouth: ~2.9 blocks up, a little in front of the stem, in the facing direction. */
    private Vec3 mouthPos() {
        return position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.3)).add(0, 3.0, 0);
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m.id == GRAB && grabbed != null) {
            if (!grabbed.isAlive() || grabbed.isRemoved()) { grabbed = null; return; }
            if (t >= 14 && t <= 46) {
                Vec3 d = mouthPos().subtract(grabbed.position().add(0, grabbed.getBbHeight() * 0.5, 0));
                double len = d.length();
                Vec3 v = len > 0.9 ? d.normalize().scale(Math.min(0.55, len * 0.22)) : Vec3.ZERO;
                grabbed.setDeltaMovement(v);
                grabbed.hurtMarked = true;
                grabbed.fallDistance = 0f;
            }
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case GRAB -> {
                if (hitIndex == 0) {
                    if (target != null && distanceTo(target) <= m.maxRange + 1.5) {
                        grabbed = target;
                        strike(target, m.damage, 0);
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
                        playSound(SoundEvents.LEASH_KNOT_PLACE, 1.5f, 0.6f);
                    }
                } else if (grabbed != null && grabbed.isAlive()) {
                    if (hitIndex == 1) { strike(grabbed, 3f, 0); playSound(SoundEvents.SLIME_SQUISH, 1.5f, 0.6f); }
                    else { // devoured at the end of the drag
                        strike(grabbed, 14f, 0.3);
                        playSound(SoundEvents.GENERIC_EAT, 2f, 0.5f);
                        grabbed = null;
                    }
                } else if (hitIndex == 2) { /* prey escaped or died: nothing to devour */ }
            }
            case GAS -> gas(getForm() == 1 ? 6.5f : 4.5f);
            case ROAR -> { gas(7f); roaring = false; }
            case SLAM -> shockwave(m);
            case DEVOUR -> {
                super.onMoveHit(m, hitIndex, target);
                playSound(SoundEvents.GENERIC_EAT, 2f, 0.6f);
            }
            default -> {
                super.onMoveHit(m, hitIndex, target);
                playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.5f, 1.4f); // whip crack
            }
        }
    }

    private void gas(float radius) {
        AreaEffectCloud c = new AreaEffectCloud(level(), getX(), getY() + 0.2, getZ());
        c.setOwner(this);
        c.setRadius(radius);
        c.setDuration(180);
        c.setWaitTime(6);
        c.setRadiusPerTick(-radius / 180f);
        c.setFixedColor(0x8fbf3a);
        c.addEffect(new MobEffectInstance(MobEffects.POISON, 120, getForm() == 1 ? 1 : 0));
        level().addFreshEntity(c);
        playSound(SoundEvents.BREWING_STAND_BREW, 3f, 0.5f);
        if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, getX(), getY() + 1.5, getZ(), 80, radius * 0.4, 1.0, radius * 0.4, 0.02);
    }

    private void shockwave(AttackMove m) {
        double r = getForm() == 1 ? 7.0 : 6.0;
        AABB box = getBoundingBox().inflate(r, 1.5, r);
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, box, x -> x != this && x.isAlive())) {
            if (e.onGround() || e.getY() - getY() < 1.2) {
                Vec3 away = e.position().subtract(position()).multiply(1, 0, 1).normalize();
                e.hurt(damageSources().mobAttack(this), m.damage);
                e.push(away.x * 1.0, 0.6, away.z * 1.0);
                e.hurtMarked = true;
            }
        }
        playSound(SoundEvents.GENERIC_EXPLODE, 2f, 0.6f);
        if (level() instanceof ServerLevel sl) {
            BlockState below = level().getBlockState(BlockPos.containing(getX(), getY() - 0.5, getZ()));
            for (int i = 0; i < 24; i++) {
                double a = i * Math.PI * 2 / 24;
                for (double d = 1.5; d <= r; d += 1.6) {
                    sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, below), getX() + Math.cos(a) * d, getY() + 0.1, getZ() + Math.sin(a) * d, 1, 0.1, 0.1, 0.1, 0.05);
                }
            }
        }
    }

    // ---- wilting death (60 ticks instead of 20)
    @Override
    protected void tickDeath() {
        deathTime++;
        grabbed = null;
        if (deathTime % 3 == 0 && level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, getX(), getY() + 3.0 - deathTime * 0.03, getZ(), 6, 1.0, 0.6, 1.0, 0.0);
        }
        if (deathTime >= WILT_TICKS && !level().isClientSide() && !isRemoved()) {
            level().broadcastEntityEvent(this, (byte) 60);
            remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.GRASS_BREAK; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
}
