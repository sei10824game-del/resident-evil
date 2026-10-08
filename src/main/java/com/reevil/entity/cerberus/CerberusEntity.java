package com.reevil.entity.cerberus;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.List;

/**
 * Cerberus (RE1 zombie dobermann). Fragile but fast pack hunter.
 * Moves: leaping bite (+thrash), snap bite, rear-up claw rake, latch-and-shake bite (drags target),
 * window-dive ambush through glass (mobGriefing), pack-alert howl, hit-stagger yelp. Bites cause bleeding.
 */
public class CerberusEntity extends BioMonster {
    public static final int LEAP = 1, SNAP = 2, CLAW = 3, SHAKE = 4, DIVE = 5, HOWL = 6, YELP = 7;

    // hitTicks are mirrored by the impact keys in CerberusModel (hitTick / duration).
    static final AttackMove M_LEAP = new AttackMove(LEAP, "leap_bite", 30, new int[]{14, 20}, 2.5, 7.5, 18, 4).dmg(4f).kb(0.2).face(8).move();
    static final AttackMove M_SNAP = new AttackMove(SNAP, "snap_bite", 14, new int[]{7}, 0, 1.6, 8, 5).dmg(3f).kb(0.25).face(7).move();
    static final AttackMove M_CLAW = new AttackMove(CLAW, "rear_claw", 22, new int[]{12}, 0, 1.5, 14, 2).dmg(3.5f).kb(0.55).face(11);
    static final AttackMove M_SHAKE = new AttackMove(SHAKE, "shake_bite", 40, new int[]{10, 16, 22, 28}, 0, 1.2, 30, 2).dmg(1.5f).kb(0).face(40);
    static final AttackMove M_DIVE = new AttackMove(DIVE, "window_dive", 26, new int[]{16}, 1.0, 10, 40, 1).dmg(4f).kb(0.6).face(6).move();
    static final AttackMove M_HOWL = new AttackMove(HOWL, "howl", 40, new int[]{16}, 3, 24, 20, 1).dmg(0f).kb(0).face(10);
    static final AttackMove M_YELP = new AttackMove(YELP, "yelp", 16, new int[]{}, 0, 0, 6, 1).face(0).move();
    private static final List<AttackMove> MOVES = List.of(M_LEAP, M_SNAP, M_CLAW, M_SHAKE, M_DIVE, M_HOWL, M_YELP);

    private int howlCooldown;
    private boolean latched;
    private Vec3 leapDir = Vec3.ZERO;

    public CerberusEntity(EntityType<CerberusEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 12.0)
            .add(Attributes.MOVEMENT_SPEED, 0.42)
            .add(Attributes.ATTACK_DAMAGE, 3.0)
            .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override public List<AttackMove> moves() { return MOVES; }

    @Override
    public boolean canStartMove(AttackMove m) {
        if (m == M_DIVE || m == M_YELP) return false; // triggered explicitly (glass ambush / hurt)
        if (m == M_HOWL) return howlCooldown <= 0;
        return onGround();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        if (howlCooldown > 0) howlCooldown--;
        LivingEntity t = getTarget();
        // Window ambush: BioCombatGoal needs line of sight, glass blocks it -> trigger the dive here.
        if (t != null && t.isAlive() && !isMoving() && getMoveCooldown() <= 0 && onGround() && tickCount % 5 == 0
            && level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
            && distanceTo(t) < 10f && glassBetween(t)) {
            startMove(M_DIVE);
        }
    }

    private static boolean isGlass(BlockState s) {
        return s.is(Tags.Blocks.GLASS) || s.is(Tags.Blocks.GLASS_PANES);
    }

    /** Glass on the straight line to the target, with nothing else solid in the way. */
    private boolean glassBetween(LivingEntity t) {
        Vec3 from = position().add(0, 0.5, 0);
        Vec3 d = t.position().add(0, 0.5, 0).subtract(from);
        double len = d.length();
        if (len < 0.5) return false;
        Vec3 step = d.scale(0.4 / len);
        Vec3 p = from;
        boolean glass = false;
        for (double s = 0; s < len; s += 0.4, p = p.add(step)) {
            BlockPos bp = BlockPos.containing(p);
            BlockState st = level().getBlockState(bp);
            if (isGlass(st)) glass = true;
            else if (!st.isAir() && st.isCollisionShapeFullBlock(level(), bp)) return false;
        }
        return glass;
    }

    /** Shatter glass blocks/panes in the volume the dog is about to fly through. */
    private void smashGlass(Vec3 ahead) {
        AABB bb = getBoundingBox().expandTowards(ahead).inflate(0.25);
        for (BlockPos bp : BlockPos.betweenClosed(Mth.floor(bb.minX), Mth.floor(bb.minY), Mth.floor(bb.minZ),
                Mth.floor(bb.maxX), Mth.floor(bb.maxY), Mth.floor(bb.maxZ))) {
            if (isGlass(level().getBlockState(bp))) level().destroyBlock(bp.immutable(), false, this);
        }
    }

    private Vec3 flatDirTo(Entity t) {
        Vec3 d = t.position().subtract(position());
        return new Vec3(d.x, 0, d.z).normalize();
    }

    private void bleed(LivingEntity t, int ticks, boolean wither) {
        t.addEffect(new MobEffectInstance(wither ? MobEffects.WITHER : MobEffects.POISON, ticks, 0));
    }

    private boolean inReach(LivingEntity t, double extra) {
        return distanceTo(t) <= extra + (t.getBbWidth() + getBbWidth()) * 0.5;
    }

    private void halt() {
        Vec3 v = getDeltaMovement();
        setDeltaMovement(0, v.y, 0);
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        getNavigation().stop();
        latched = false;
        if (m == M_HOWL) playSound(SoundEvents.WOLF_GROWL, 1.0f, 0.6f);
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        latched = false;
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m == M_LEAP) {
            if (t < 8) halt();                                   // crouch / anticipation
            else if (t == 8 && target != null) {                 // spring
                leapDir = flatDirTo(target);
                double sp = Mth.clamp(distanceTo(target) * 0.13, 0.5, 1.25);
                setDeltaMovement(leapDir.x * sp, 0.42, leapDir.z * sp);
                hasImpulse = true;
                playSound(SoundEvents.WOLF_GROWL, 1.0f, 1.3f);
            } else if (t > 14 && latched && target != null) {     // hang on and thrash
                Vec3 v = getDeltaMovement();
                Vec3 to = target.position().subtract(position()).multiply(0.15, 0, 0.15);
                setDeltaMovement(to.x, v.y, to.z);
            } else if (t > 22) halt();
        } else if (m == M_SNAP) {
            if (t < 5) halt();
            else if (t == 5) lunge(0.45, 0);
            else if (t > 9) halt();
        } else if (m == M_SHAKE) {
            if (t == 4) { lunge(0.3, 0); playSound(SoundEvents.WOLF_GROWL, 1.0f, 0.8f); }
            if (t == 8 && target != null) latched = inReach(target, 1.0);
        } else if (m == M_DIVE) {
            if (t < 6) halt();
            else if (t == 6 && target != null) {
                leapDir = flatDirTo(target);
                double sp = Mth.clamp(distanceTo(target) * 0.12, 0.7, 1.3);
                smashGlass(leapDir.scale(3.0));
                setDeltaMovement(leapDir.x * sp, 0.45, leapDir.z * sp);
                hasImpulse = true;
            } else if (t <= 18) {
                Vec3 v = getDeltaMovement();
                if (v.horizontalDistance() < 0.3) setDeltaMovement(leapDir.x * 0.6, v.y, leapDir.z * 0.6); // keep momentum through the pane
                smashGlass(leapDir.scale(1.5));
            } else halt();
        } else if (m == M_YELP) {
            if (t > 8) halt();
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        if (m == M_HOWL) {
            playSound(SoundEvents.WOLF_HOWL, 2.0f, 0.7f);
            howlCooldown = 400;
            if (target == null) return;
            for (CerberusEntity c : level().getEntitiesOfClass(CerberusEntity.class, getBoundingBox().inflate(16.0))) {
                if (c == this || !c.isAlive()) continue;
                c.setTarget(target);
                c.howlCooldown = Math.max(c.howlCooldown, 200); // don't chain-howl
            }
            return;
        }
        if (target == null) return;
        if (m == M_LEAP) {
            if (i == 0 && inReach(target, 1.3) && strike(target, m.damage, m.knockback)) {
                latched = true;
                bleed(target, 60, false);
            } else if (i == 1 && latched && inReach(target, 1.6)) {
                strike(target, 2f, 0);
                drag(target, 0.3, 1);
            }
        } else if (m == M_SNAP) {
            if (inReach(target, 1.4) && strike(target, m.damage, m.knockback)) bleed(target, 40, false);
        } else if (m == M_CLAW) {
            if (inReach(target, 1.5)) strike(target, m.damage, m.knockback);
        } else if (m == M_SHAKE) {
            if (latched && inReach(target, 1.4)) {
                strike(target, m.damage, 0);
                drag(target, 0.35, (i % 2 == 0) ? 1 : -1);
                if (i == 3) bleed(target, 50, true);
            }
        } else if (m == M_DIVE) {
            if (inReach(target, 1.5) && strike(target, m.damage, m.knockback)) bleed(target, 60, false);
        }
    }

    /** Whip the victim sideways and slightly toward the dog. */
    private void drag(LivingEntity t, double side, int sign) {
        Vec3 fwd = Vec3.directionFromRotation(0, getYRot());
        t.push(-fwd.z * side * sign - fwd.x * 0.12, 0.05, fwd.x * side * sign - fwd.z * 0.12);
        t.hurtMarked = true;
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        boolean r = super.hurt(src, amount);
        if (r && !level().isClientSide && isAlive() && amount >= 3f && getMoveId() != YELP) {
            cancelMove();
            startMove(M_YELP);
            playSound(SoundEvents.WOLF_HURT, 1.2f, 1.5f);
            Entity a = src.getEntity();
            if (a != null) {
                Vec3 d = position().subtract(a.position());
                Vec3 h = new Vec3(d.x, 0, d.z).normalize();
                setDeltaMovement(h.x * 0.5, 0.25, h.z * 0.5);
                hasImpulse = true;
            }
        }
        return r;
    }

    @Override public boolean causeFallDamage(float dist, float mult, DamageSource src) { return false; }
    @Override public int getMaxSpawnClusterSize() { return 4; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.WOLF_GROWL; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.WOLF_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.WOLF_DEATH; }
}
