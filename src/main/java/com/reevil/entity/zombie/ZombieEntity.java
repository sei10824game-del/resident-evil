package com.reevil.entity.zombie;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.crimsonhead.CrimsonHeadEntity;
import com.reevil.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * RE1 zombie.
 * Forms: STAND (shamble), CRAWL (legs gone, after heavy/low damage), FEIGN (lying "dead" until a player comes close),
 * CORPSE (killed, waiting to rise again as a Crimson Head).
 *
 * Crimson Head mutation, chosen implementation: the zombie never really dies from non-fire damage. die() is intercepted:
 * health is set back to 1 and the zombie becomes a CORPSE (lying, immobile, immune to everything except fire and
 * /kill-type damage) with a 600..2400 tick revive timer saved in NBT. When the timer runs out the corpse is replaced by a
 * CrimsonHeadEntity that plays its own rise animation. Fire damage (or dying while burning) is a real death: burn the bodies.
 * This needs no per-level scheduler/SavedData, survives chunk unload and save/load, and the body stays visible as in REmake.
 */
public class ZombieEntity extends BioMonster {
    public static final int FORM_STAND = 0, FORM_CRAWL = 1, FORM_FEIGN = 2, FORM_CORPSE = 3;
    public static final int SWIPE = 1, GRAB_BITE = 2, VOMIT = 3, ANKLE_BITE = 4, COLLAPSE = 5, RISE = 6;
    private static final String[] VARIANTS = {"civilian", "labcoat", "stars"};
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(ZombieEntity.class, EntityDataSerializers.INT);

    // ids double as model clip ids; hitTick/duration == impact keyframe time in ZombieModel
    private static final List<AttackMove> STAND_MOVES = List.of(
        new AttackMove(SWIPE, "claw_swipe", 24, new int[]{13}, 0, 1.6, 18, 5).dmg(3f).kb(0.35),
        new AttackMove(GRAB_BITE, "grab_bite", 60, new int[]{14, 26, 36, 46}, 0, 1.3, 30, 4).dmg(3.5f).kb(0.7).face(56),
        new AttackMove(VOMIT, "vomit", 40, new int[]{24}, 0.2, 2.6, 60, 2).dmg(2f).kb(0)
    );
    private static final List<AttackMove> CRAWL_MOVES = List.of(
        new AttackMove(ANKLE_BITE, "ankle_bite", 40, new int[]{12, 22, 32}, 0, 1.3, 25, 1).dmg(2.5f).kb(0).face(36)
    );
    private static final AttackMove COLLAPSE_MOVE = new AttackMove(COLLAPSE, "collapse", 26, new int[0], 0, 0, 10, 0).face(0);
    private static final AttackMove RISE_MOVE = new AttackMove(RISE, "rise", 50, new int[0], 0, 0, 10, 0).face(0);

    private int reviveTimer;
    private boolean grabbing;
    private Vec3 grabAnchor = Vec3.ZERO;
    private float voice;
    // client+server: ticks since the synced form last changed (drives fall transitions in the model)
    private int lastForm = -1;
    private int formTicks = 100;

    public ZombieEntity(EntityType<ZombieEntity> type, Level level) {
        super(type, level);
        voice = 0.62f + random.nextFloat() * 0.4f;
        if (!level.isClientSide) entityData.set(DATA_VARIANT, random.nextInt(VARIANTS.length));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 24.0)
            .add(Attributes.MOVEMENT_SPEED, 0.17)
            .add(Attributes.ATTACK_DAMAGE, 3.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_VARIANT, 0);
    }

    public int getVariant() { return entityData.get(DATA_VARIANT); }
    public int getFormTicks() { return formTicks; }

    @Override
    public String getTextureSuffix() {
        int v = getVariant();
        return "_" + VARIANTS[v >= 0 && v < VARIANTS.length ? v : 0];
    }

    @Override
    public List<AttackMove> moves() {
        int f = getForm();
        if (f == FORM_STAND) return STAND_MOVES;
        if (f == FORM_CRAWL) return CRAWL_MOVES;
        return List.of();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor lvl, DifficultyInstance diff, MobSpawnType reason, SpawnGroupData data, CompoundTag tag) {
        SpawnGroupData r = super.finalizeSpawn(lvl, diff, reason, data, tag);
        if (random.nextFloat() < 0.2f) setForm(FORM_FEIGN); // lies among the bodies, plays dead
        return r;
    }

    // ---------------------------------------------------------------- forms / state

    @Override
    public void tick() {
        super.tick();
        int f = getForm();
        if (f != lastForm) {
            formTicks = lastForm < 0 ? 100 : 0;
            lastForm = f;
            refreshDimensions();
        } else if (formTicks < 1000) {
            formTicks++;
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getForm() == FORM_STAND ? super.getDimensions(pose) : EntityDimensions.scalable(0.8f, 0.6f);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || getForm() >= FORM_FEIGN;
    }

    @Override
    public boolean isPushable() {
        return super.isPushable() && getForm() < FORM_FEIGN;
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(getForm() == FORM_CRAWL ? speed * 0.45f : speed);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide || !isAlive()) return;
        int f = getForm();
        if (f == FORM_CORPSE) {
            if (--reviveTimer <= 0) mutate();
        } else if (f == FORM_FEIGN && getMoveId() == 0 && level().getNearestPlayer(this, 3.5) != null) {
            startMove(RISE_MOVE);
        }
    }

    private void mutate() {
        CrimsonHeadEntity ch = ModEntities.CRIMSON_HEAD.get().create(level());
        if (ch == null) { reviveTimer = 200; return; }
        ch.moveTo(getX(), getY(), getZ(), getYRot(), 0f);
        ch.yBodyRot = getYRot();
        ch.yHeadRot = getYRot();
        if (hasCustomName()) ch.setCustomName(getCustomName());
        if (isPersistenceRequired()) ch.setPersistenceRequired();
        ch.reviveFromCorpse();
        level().addFreshEntity(ch);
        discard();
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        grabbing = false;
        if (m.id == COLLAPSE) setForm(FORM_CRAWL);
        else if (m.id == RISE) setForm(FORM_STAND);
    }

    // ---------------------------------------------------------------- damage

    /** subclasses (Pale Head) that stay dead. */
    protected boolean revives() { return true; }

    private static boolean isTrueDeath(DamageSource src) {
        return src.is(DamageTypeTags.IS_FIRE) || src.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        int f = getForm();
        if (f == FORM_CORPSE && !isTrueDeath(src)) return false; // only fire finishes a body
        boolean legHit = false;
        if (src.getDirectEntity() instanceof Projectile p && f == FORM_STAND) {
            if (p.getY() > getY() + getBbHeight() * 0.72) amount *= 2f;          // headshot
            else if (p.getY() < getY() + getBbHeight() * 0.4) legHit = true;    // knee-capped
        }
        boolean ok = super.hurt(src, amount);
        if (ok && !level().isClientSide && isAlive()) {
            int nf = getForm();
            if (nf == FORM_FEIGN && getMoveId() != RISE) {
                startMove(RISE_MOVE);
            } else if (nf == FORM_STAND && getMoveId() != COLLAPSE && getHealth() < getMaxHealth() * 0.4f
                && random.nextFloat() < (legHit ? 0.85f : 0.35f)) {
                cancelMove();
                startMove(COLLAPSE_MOVE);
            }
        }
        return ok;
    }

    @Override
    public void die(DamageSource src) {
        if (revives() && !level().isClientSide && getForm() != FORM_CORPSE && !isTrueDeath(src) && !isOnFire()) {
            setHealth(1f);
            cancelMove();
            grabbing = false;
            setForm(FORM_CORPSE);
            reviveTimer = 600 + random.nextInt(1801);
            getNavigation().stop();
            setTarget(null);
            return;
        }
        super.die(src);
    }

    // ---------------------------------------------------------------- moves

    @Override
    protected void onMoveStart(AttackMove m) {
        grabbing = false;
        switch (m.id) {
            case GRAB_BITE -> playSound(SoundEvents.ZOMBIE_AMBIENT, 1.2f, voice * 1.15f);
            case VOMIT -> playSound(SoundEvents.ZOMBIE_AMBIENT, 1.2f, voice * 0.6f);
            case COLLAPSE -> playSound(SoundEvents.ZOMBIE_HURT, 1.0f, voice * 0.8f);
            case RISE -> playSound(SoundEvents.ZOMBIE_AMBIENT, 1.1f, voice * 0.7f);
            default -> { }
        }
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case SWIPE -> { if (t >= 9 && t <= 13) lunge(0.06, 0); }
            case GRAB_BITE -> {
                if (!grabbing && t >= 9 && t <= 14) lunge(0.22, 0);
                if (grabbing) hold(target, t < 50);
            }
            case ANKLE_BITE -> {
                if (!grabbing && t >= 8 && t <= 12) lunge(0.16, 0);
                if (grabbing) hold(target, t < 36);
            }
            case VOMIT -> { if (t >= 21 && t <= 32) spew(); }
            default -> { }
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        switch (m.id) {
            case GRAB_BITE, ANKLE_BITE -> {
                int last = m.hitTicks.length - 1;
                if (i == 0) {
                    if (target != null && target.isAlive() && distanceTo(target) <= 1.9 + target.getBbWidth() * 0.5) {
                        grabbing = true;
                        grabAnchor = target.position();
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3), this);
                        playSound(SoundEvents.ZOMBIE_INFECT, 1.0f, voice);
                    }
                } else if (grabbing && target != null && target.isAlive()) {
                    bite(target, m.damage, i == last ? m.knockback : 0);
                    if (i == last) grabbing = false;
                }
            }
            case VOMIT -> vomitHit(m);
            default -> super.onMoveHit(m, i, target);
        }
    }

    /** Pin the grabbed target in place (no riding): zero its velocity and snap it back to the grab anchor. */
    private void hold(LivingEntity target, boolean active) {
        if (target == null || !target.isAlive()) { grabbing = false; return; }
        if (!active) return;
        target.setDeltaMovement(0, Math.min(0, target.getDeltaMovement().y), 0);
        if (target.position().distanceToSqr(grabAnchor) > 0.09) target.teleportTo(grabAnchor.x, grabAnchor.y, grabAnchor.z);
        target.hurtMarked = true;
    }

    private void bite(LivingEntity target, float dmg, double kb) {
        target.invulnerableTime = 0;
        if (strike(target, dmg, kb)) {
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0), this);   // torn flesh
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 1), this);     // T-virus infection
            playSound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 0.6f, 1.6f);
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState()),
                    target.getX(), target.getY() + target.getBbHeight() * 0.8, target.getZ(), 14, 0.2, 0.2, 0.2, 0.1);
            }
        }
    }

    private Vec3 mouth() {
        Vec3 f = Vec3.directionFromRotation(0, getYRot());
        return new Vec3(getX() + f.x * 0.5, getY() + getBbHeight() * 0.82, getZ() + f.z * 0.5);
    }

    private void spew() {
        if (!(level() instanceof ServerLevel sl)) return;
        Vec3 f = Vec3.directionFromRotation(15, getYRot());
        Vec3 o = mouth();
        for (int n = 0; n < 4; n++) {
            double s = 0.25 + random.nextDouble() * 0.25;
            sl.sendParticles(ParticleTypes.SNEEZE, o.x, o.y, o.z, 0, f.x * s, f.y * s - 0.05, f.z * s, 1.0);
        }
        sl.sendParticles(ParticleTypes.ITEM_SLIME, o.x, o.y, o.z, 2, 0.1, 0.05, 0.1, 0.05);
        if (random.nextInt(3) == 0) playSound(SoundEvents.SLIME_SQUISH, 0.7f, 0.6f);
    }

    private void vomitHit(AttackMove m) {
        Vec3 look = Vec3.directionFromRotation(0, getYRot());
        for (LivingEntity le : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(3.0))) {
            if (le == this || le instanceof BioMonster) continue;
            Vec3 d = le.position().subtract(position());
            if (d.length() > 3.2 || d.normalize().dot(look) < 0.55) continue;
            if (le.hurt(damageSources().mobAttack(this), m.damage)) {
                le.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0), this);
                le.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0), this);
            }
        }
    }

    // ---------------------------------------------------------------- sounds

    @Override
    protected SoundEvent getAmbientSound() { return getForm() >= FORM_FEIGN ? null : SoundEvents.ZOMBIE_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.ZOMBIE_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ZOMBIE_DEATH; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(getForm() == FORM_CRAWL ? SoundEvents.WOOL_STEP : SoundEvents.ZOMBIE_STEP, 0.15f, 0.8f);
    }

    /** per-zombie groan pitch: each one moans in its own voice */
    @Override
    public float getVoicePitch() { return voice + (random.nextFloat() - 0.5f) * 0.08f; }

    @Override
    public int getAmbientSoundInterval() { return 160; }

    // ---------------------------------------------------------------- save

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant());
        tag.putInt("Revive", reviveTimer);
        tag.putFloat("Voice", voice);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(DATA_VARIANT, tag.getInt("Variant"));
        reviveTimer = tag.getInt("Revive");
        if (tag.contains("Voice")) voice = tag.getFloat("Voice");
        if (getForm() == FORM_CORPSE && reviveTimer <= 0) reviveTimer = 600;
    }
}
