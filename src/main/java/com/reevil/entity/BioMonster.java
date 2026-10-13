package com.reevil.entity;

import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import com.reevil.registry.ModSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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
 * Base for all Resident Evil creatures.
 *  - moves(): list of AttackMove, each with an id the client model maps to an AnimClip.
 *  - getForm()/setForm(): synced int for transformations (client reads it for model/texture switch).
 *  - hooks: onMoveStart / onMoveTick / onMoveHit / onMoveEnd for lunges, projectiles, status effects etc.
 */
public abstract class BioMonster extends Monster {
    private static final EntityDataAccessor<Integer> DATA_MOVE = SynchedEntityData.defineId(BioMonster.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MOVE_TICK = SynchedEntityData.defineId(BioMonster.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MOVE_DUR = SynchedEntityData.defineId(BioMonster.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_FORM = SynchedEntityData.defineId(BioMonster.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_MUT = SynchedEntityData.defineId(BioMonster.class, EntityDataSerializers.INT);
    private int persistentStage, chrysalisTicks;

    protected int moveCooldown = 20;
    private AttackMove current;

    protected BioMonster(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        if (canonSize() != null) refreshDimensions();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_MOVE, 0);
        entityData.define(DATA_MOVE_TICK, 0);
        entityData.define(DATA_MOVE_DUR, 0);
        entityData.define(DATA_FORM, 0);
        entityData.define(DATA_MUT, 0);
    }

    /** All moves of this creature in current form. */
    public abstract List<AttackMove> moves();

    /** moves() plus data-driven canon extras (CanonMoves). */
    public List<AttackMove> allMoves() { return CanonMoves.merge(this, moves()); }
    public boolean canStart(AttackMove m) { return m.id >= 40 ? CanonMoves.can(this, m) && canStartMove(m) : canStartMove(m); }
    /** mutation stage chosen by CanonMoves passives (0 = none); synced so models can show extra parts. */
    public int getMut() { return entityData.get(DATA_MUT); }
    public void setMut(int n) { entityData.set(DATA_MUT, n); }
    public int getPersistentStage() { return persistentStage; }
    public void setPersistentStage(int n) { persistentStage = n; }
    public int getChrysalisTicks() { return chrysalisTicks; }
    public void setChrysalisTicks(int n) { chrysalisTicks = n; }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ---- state accessors (client + server)
    public int getMoveId() { return entityData.get(DATA_MOVE); }
    public int getMoveTick() { return entityData.get(DATA_MOVE_TICK); }
    public int getMoveDuration() { return entityData.get(DATA_MOVE_DUR); }
    public int getForm() { return entityData.get(DATA_FORM); }
    public void setForm(int f) { entityData.set(DATA_FORM, f); }
    /** appended to texture name, e.g. "_enraged". Override per form. */
    public String getTextureSuffix() { return ""; }
    public boolean isMoving() { return current != null; }
    public AttackMove currentMove() { return current; }
    public int getMoveCooldown() { return moveCooldown; }

    // ---- server-side move lifecycle
    public void startMove(AttackMove m) {
        current = m;
        entityData.set(DATA_MOVE, m.id);
        entityData.set(DATA_MOVE_TICK, 0);
        entityData.set(DATA_MOVE_DUR, m.duration);
        if (random.nextInt(3) == 0) playVoice("attack", 1.0f);
        if (m.id >= 40) CanonMoves.start(this, m);
        onMoveStart(m);
    }

    private void endMove() {
        AttackMove m = current;
        current = null;
        entityData.set(DATA_MOVE, 0);
        entityData.set(DATA_MOVE_TICK, 0);
        entityData.set(DATA_MOVE_DUR, 0);
        moveCooldown = m.cooldown;
        if (m.id >= 40) CanonMoves.end(this, m);
        onMoveEnd(m);
    }

    /** Abort the running move without cooldown (stun, death, form change). */
    public void cancelMove() {
        if (current == null) return;
        current = null;
        entityData.set(DATA_MOVE, 0);
        entityData.set(DATA_MOVE_TICK, 0);
        entityData.set(DATA_MOVE_DUR, 0);
    }

    /** Layered synthesised voice: archetype by creature, lower and slower for bigger bodies. */
    public void playVoice(String kind, float volume) {
        if (level().isClientSide) return;
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath();
        float big = Math.min(1f, Math.max(0f, (getBbHeight() - 1f) / 3.5f));
        float pitch = (1.12f - 0.4f * big) + (random.nextFloat() - 0.5f) * 0.12f;
        level().playSound(null, getX(), getY(), getZ(), ModSounds.get(id, kind), getSoundSource(), volume * (0.9f + 0.6f * big), pitch);
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        if (getBbHeight() > 2.2f) playSound(ModSounds.STEP.get(), 0.5f + 0.15f * getBbHeight(), 1.1f - 0.08f * getBbHeight() + random.nextFloat() * 0.1f);
        else super.playStepSound(pos, state);
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (src.getEntity() instanceof BioMonster b && !Factions.hostile(b, this)) return false; // no friendly fire inside a faction
        amount = CanonMoves.modifyDamage(this, src, amount);
        if (amount <= 0 && !src.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;
        boolean r = super.hurt(src, amount);
        if (r && isAlive()) CanonMoves.hurt(this, src);
        if (r && isAlive() && random.nextInt(3) == 0) playVoice("hurt", 0.9f);
        return r;
    }

    @Override
    public void die(DamageSource src) {
        super.die(src);
        if (!level().isClientSide) CanonMoves.death(this);
        playVoice("death", 1.0f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        CanonMoves.passives(this);
        if (tickCount % 8 == 0 && !isMoving()) retarget();
        if (isAlive() && !isMoving() && random.nextInt(getTarget() != null ? 180 : 320) == 0) playVoice("ambient", getTarget() != null ? 1.0f : 0.7f);
        if (moveCooldown > 0 && current == null) moveCooldown--;
        if (current == null) return;
        if (!isAlive()) { cancelMove(); return; }
        int t = entityData.get(DATA_MOVE_TICK) + 1;
        entityData.set(DATA_MOVE_TICK, t);
        LivingEntity target = getTarget();
        if (current.freeze) {
            getNavigation().stop();
            setDeltaMovement(getDeltaMovement().multiply(0, 1, 0));
        }
        if (target != null && t <= current.faceUntil) {
            getLookControl().setLookAt(target, 60f, 60f);
            faceTarget(target);
        }
        if (current.id >= 40) CanonMoves.tick(this, current, t, target); else onMoveTick(current, t, target);
        for (int i = 0; i < current.hitTicks.length; i++) {
            if (current.hitTicks[i] == t) { if (current.id >= 40) CanonMoves.hit(this, current, i, target); else onMoveHit(current, i, target); }
        }
        if (t >= current.duration) endMove();
    }

    private void faceTarget(LivingEntity target) {
        double dx = target.getX() - getX(), dz = target.getZ() - getZ();
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        setYRot(yaw); yBodyRot = yaw; yHeadRot = yaw;
    }

    private float[] sizeEntry; private boolean sizeLooked;
    /** canonical size entry (CreatureSizes) or null */
    public float[] canonSize() {
        if (!sizeLooked) { sizeLooked = true; sizeEntry = CreatureSizes.of(BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath()); }
        return sizeEntry;
    }
    @Override
    public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {
        float[] s = canonSize();
        return s != null ? net.minecraft.world.entity.EntityDimensions.scalable(s[0], s[1]) : super.getDimensions(pose);
    }

    // ---- target switching
    private int lastRetarget;
    /**
     * Picks the most pressing enemy so a creature does not keep chasing a far target while a nearer one beats on it:
     * whoever hurt us recently and is close wins, otherwise a hostile within melee reach beats a distant target.
     */
    protected void retarget() {
        LivingEntity cur = getTarget();
        if (cur != null && !cur.isAlive()) cur = null;
        if (cur != null && tickCount - lastRetarget < 24 && distanceToSqr(cur) < 36) return;
        LivingEntity best = null; double bd = Double.MAX_VALUE;
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(9), x -> x != this && x.isAlive() && !(x instanceof Player p && (p.isCreative() || p.isSpectator())) && Factions.hostile(this, x))) {
            double d = distanceToSqr(e);
            boolean attacker = e == getLastHurtByMob() && tickCount - getLastHurtByMobTimestamp() < 100;
            double score = attacker ? d * 0.35 : d;
            if (score < bd) { bd = score; best = e; }
        }
        if (best == null || best == cur) return;
        if (cur == null || bd + 6.0 < distanceToSqr(cur) || !hasLineOfSight(cur)) { setTarget(best); lastRetarget = tickCount; }
    }

    // ---- hooks
    /** extra eligibility check (airborne, form, hp threshold...). */
    public boolean canStartMove(AttackMove m) { return true; }
    protected void onMoveStart(AttackMove m) {}
    protected void onMoveEnd(AttackMove m) {}
    /** called every tick of the move; t = ticks since start (1..duration). Use for lunges: push(). */
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {}

    /** Default hit: melee damage if target within reach in front. Override for special effects. */
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        if (target == null) return;
        double reach = m.maxRange + 0.8;
        if (distanceTo(target) <= reach + target.getBbWidth() * 0.5) strike(target, m.damage, m.knockback);
    }

    protected boolean strike(LivingEntity target, float dmg, double kb) {
        boolean ok = target.hurt(damageSources().mobAttack(this), dmg);
        if (ok) level().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.IMPACT.get(), getSoundSource(), 0.6f + Math.min(0.8f, dmg / 20f), 1.15f - Math.min(0.5f, dmg / 40f));
        if (ok && kb > 0) {
            Vec3 d = target.position().subtract(position()).multiply(1, 0, 1).normalize().scale(kb);
            target.push(d.x, 0.15 + kb * 0.2, d.z);
            target.hurtMarked = true;
        }
        return ok;
    }

    /** Horizontal impulse toward the current look direction (lunge). */
    protected void lunge(double h, double v) {
        Vec3 f = Vec3.directionFromRotation(0, getYRot()).scale(h);
        setDeltaMovement(f.x, v > 0 ? v : getDeltaMovement().y, f.z);
        hasImpulse = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Form", getForm());
        tag.putInt("Mut", getMut()); tag.putInt("MutStage", persistentStage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setForm(tag.getInt("Form"));
        setMut(tag.getInt("Mut")); persistentStage = tag.getInt("MutStage");
    }
}
