package com.reevil.entity.hunter;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * MA-121 Hunter (RE1 / REmake).
 * form 0 = Hunter, form 1 = enraged red Hunter (below 40% HP: faster, stronger, regenerates, texture "_red").
 * Moves: double slash, leaping claw (double slash on landing), decapitation, pounce-pin maul,
 * backflip / side-hop dodges when shot, roar, obstacle vault (3 blocks).
 */
public class HunterEntity extends BioMonster {
    public static final int SLASH = 1, LEAP = 2, DECAP = 3, PIN = 4, BACKFLIP = 5, SIDE_L = 6, ROAR = 7, VAULT = 8, SIDE_R = 9;

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(SLASH, "double_slash", 24, new int[]{9, 15}, 0, 2.2, 12, 10).dmg(5f).kb(0.35),
        new AttackMove(LEAP, "leap_claw", 40, new int[]{26, 31}, 3.5, 11, 30, 6).dmg(6f).kb(0.6).face(9).move(),
        new AttackMove(DECAP, "decapitation", 36, new int[]{21}, 0, 2.0, 30, 30).dmg(40f).kb(0.2).face(21),
        new AttackMove(PIN, "pounce_pin", 56, new int[]{20, 28, 36, 44, 50}, 2.0, 6.0, 60, 3).dmg(3f).kb(0).face(8).move(),
        new AttackMove(BACKFLIP, "backflip", 18, new int[]{}, 0, 99, 6, 0).face(2).move(),
        new AttackMove(SIDE_L, "side_hop_left", 14, new int[]{}, 0, 99, 4, 0).face(14).move(),
        new AttackMove(SIDE_R, "side_hop_right", 14, new int[]{}, 0, 99, 4, 0).face(14).move(),
        new AttackMove(ROAR, "roar", 30, new int[]{}, 0, 99, 6, 0).face(30),
        new AttackMove(VAULT, "vault", 20, new int[]{}, 0, 99, 0, 0).face(5).move()
    );

    private LivingEntity roarTarget;
    private int dodgeCd, vaultCd, regenTick;

    public HunterEntity(EntityType<HunterEntity> type, Level level) {
        super(type, level);
        setMaxUpStep(1.5f);
        xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 60.0)
            .add(Attributes.MOVEMENT_SPEED, 0.33)
            .add(Attributes.ATTACK_DAMAGE, 6.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.ARMOR, 4.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    @Override
    public List<AttackMove> moves() { return MOVES; }

    private static AttackMove byId(int id) {
        for (AttackMove m : MOVES) if (m.id == id) return m;
        return MOVES.get(0);
    }

    @Override
    public String getTextureSuffix() { return getForm() == 1 ? "_red" : ""; }

    @Override
    public boolean canStartMove(AttackMove m) {
        LivingEntity t = getTarget();
        switch (m.id) {
            case DECAP: {
                if (t == null) return false;
                boolean low = t.getHealth() <= t.getMaxHealth() * 0.25f;
                float chance = (low ? 0.35f : 0.08f) + (getForm() == 1 ? 0.05f : 0f);
                return getRandom().nextFloat() < chance;
            }
            case LEAP: return onGround();
            case PIN: return onGround() && getRandom().nextFloat() < 0.3f;
            case BACKFLIP: case SIDE_L: case SIDE_R: case ROAR: case VAULT: return false; // triggered by behavior only
            default: return true;
        }
    }

    // ---------------------------------------------------------------- behavior

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        fallDistance = 0f; // built for huge leaps
        if (dodgeCd > 0) dodgeCd--;
        if (vaultCd > 0) vaultCd--;
        LivingEntity t = getTarget();
        if (!isAlive()) return;
        // hiss/roar when a new prey is spotted
        if (t != null && t != roarTarget && !isMoving() && onGround()) {
            roarTarget = t;
            startMove(byId(ROAR));
            return;
        }
        // pounce over obstacles up to 3 blocks
        if (t != null && !isMoving() && onGround() && horizontalCollision && vaultCd <= 0) {
            vaultCd = 30;
            startMove(byId(VAULT));
        }
        // REmake: the enraged Hunter regenerates
        if (getForm() == 1 && ++regenTick >= 20) {
            regenTick = 0;
            if (getHealth() < getMaxHealth()) heal(0.5f);
        }
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        boolean r = super.hurt(src, amount);
        if (r && !level().isClientSide && isAlive()) {
            if (getForm() == 0 && getHealth() < getMaxHealth() * 0.4f) {
                enrage();
            } else if (src.is(DamageTypeTags.IS_PROJECTILE) && dodgeCd <= 0 && onGround() && !isMoving()) {
                dodgeCd = 50;
                int pick = getRandom().nextInt(3);
                startMove(byId(pick == 0 ? BACKFLIP : pick == 1 ? SIDE_L : SIDE_R));
            }
        }
        return r;
    }

    private void enrage() {
        setForm(1);
        cancelMove();
        AttributeInstance spd = getAttribute(Attributes.MOVEMENT_SPEED);
        if (spd != null) spd.setBaseValue(0.42);
        AttributeInstance dmg = getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) dmg.setBaseValue(9.0);
        startMove(byId(ROAR));
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        float pitch = getForm() == 1 ? 0.8f : 1.0f;
        switch (m.id) {
            case ROAR: playSound(SoundEvents.RAVAGER_ROAR, 1.6f, 1.35f * pitch); break;
            case DECAP: playSound(SoundEvents.CAT_HISS, 1.5f, 0.45f); break;
            case SLASH: playSound(SoundEvents.CAT_HISS, 1.0f, 0.6f * pitch); break;
            case LEAP: case PIN: playSound(SoundEvents.RAVAGER_ATTACK, 1.0f, 1.5f * pitch); break;
            default: break;
        }
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        if (getForm() == 1) moveCooldown = moveCooldown * 2 / 3; // red Hunter attacks relentlessly
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case LEAP:
                if (t == 9) { leapAt(target, 0.6, 1.2); playSound(SoundEvents.RAVAGER_STEP, 1f, 1.6f); }
                break;
            case PIN:
                if (t == 8) leapAt(target, 0.42, 0.6);
                if (t >= 19 && t <= 52 && target != null && distanceTo(target) < 2.6) {
                    // pinned: prey cannot escape while being mauled
                    target.setDeltaMovement(0, Math.min(0, target.getDeltaMovement().y), 0);
                    target.hurtMarked = true;
                    setDeltaMovement(0, getDeltaMovement().y, 0);
                }
                break;
            case DECAP:
                // stalk forward into scissor range, then drag the prey in ("grab")
                if (t >= 10 && t < 20 && target != null && distanceTo(target) > 1.5) lunge(0.2, 0);
                if (t >= 16 && t < 21 && target != null && distanceTo(target) < 3.0) {
                    Vec3 pull = position().subtract(target.position()).multiply(1, 0, 1).normalize().scale(0.12);
                    target.setDeltaMovement(pull.x, target.getDeltaMovement().y, pull.z);
                    target.hurtMarked = true;
                }
                break;
            case SLASH:
                if (t == 8 || t == 14) lunge(0.25, 0);
                break;
            case BACKFLIP:
                if (t == 2) { Vec3 b = Vec3.directionFromRotation(0, getYRot()).scale(-0.75); setDeltaMovement(b.x, 0.55, b.z); hasImpulse = true; }
                break;
            case SIDE_L: case SIDE_R:
                if (t == 2) {
                    Vec3 s = Vec3.directionFromRotation(0, getYRot() + (m.id == SIDE_L ? -90f : 90f)).scale(0.8);
                    setDeltaMovement(s.x, 0.38, s.z); hasImpulse = true;
                }
                break;
            case VAULT:
                if (t == 5) { Vec3 f = Vec3.directionFromRotation(0, getYRot()).scale(0.22); setDeltaMovement(f.x, 0.75, f.z); hasImpulse = true; }
                if (t > 5 && t < 16 && !onGround()) { Vec3 f = Vec3.directionFromRotation(0, getYRot()).scale(0.22); setDeltaMovement(f.x, getDeltaMovement().y, f.z); }
                break;
            case ROAR:
                if (t == 12 && level() instanceof ServerLevel sl)
                    sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 1.6, getZ(), 6, 0.3, 0.2, 0.3, 0.02);
                break;
            default: break;
        }
    }

    /** Ballistic leap that lands {@code stopShort} blocks in front of the target. */
    private void leapAt(LivingEntity target, double vy, double stopShort) {
        if (target == null) { lunge(0.6, vy); return; }
        double dx = target.getX() - getX(), dz = target.getZ() - getZ();
        double d = Math.sqrt(dx * dx + dz * dz);
        double h = Math.min(1.7, Math.max(0, d - stopShort) / airTravel(vy));
        double inv = d > 1.0e-4 ? 1.0 / d : 0.0;
        setDeltaMovement(dx * inv * h, vy, dz * inv * h);
        hasImpulse = true;
    }

    /** Horizontal distance per unit launch speed for a jump of vertical speed vy (vanilla gravity 0.08, drag 0.98, air friction 0.91). */
    private static double airTravel(double vy) {
        double y = 0, v = vy, f = 0, k = 1;
        for (int n = 0; n < 80; n++) {
            y += v; v = (v - 0.08) * 0.98; f += k; k *= 0.91;
            if (y <= 0) break;
        }
        return f;
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        if (target == null) return;
        float mult = getForm() == 1 ? 1.35f : 1f;
        double reach;
        switch (m.id) {
            case LEAP: reach = 2.6; break;
            case PIN: reach = 2.4; break;
            case DECAP: reach = 2.8; break;
            default: reach = m.maxRange + 0.8; break;
        }
        if (distanceTo(target) > reach + target.getBbWidth() * 0.5) {
            playSound(SoundEvents.PLAYER_ATTACK_NODAMAGE, 0.8f, 0.7f);
            return;
        }
        boolean ok = strike(target, m.damage * mult, m.knockback);
        if (!ok) return;
        if (m.id == DECAP) {
            playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.5f, 0.6f);
            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, 0.5f);
            if (level() instanceof ServerLevel sl) {
                double y = target.getY() + target.getBbHeight() * 0.85;
                sl.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), y, target.getZ(), 2, 0.2, 0.05, 0.2, 0.0);
                sl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), y, target.getZ(), 14, 0.25, 0.1, 0.25, 0.25);
            }
        } else {
            playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 1.3f);
        }
    }

    // ---------------------------------------------------------------- sounds

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.CAT_HISS; }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.RAVAGER_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }

    @Override
    public float getVoicePitch() { return (getForm() == 1 ? 0.45f : 0.55f) + getRandom().nextFloat() * 0.1f; }
}
