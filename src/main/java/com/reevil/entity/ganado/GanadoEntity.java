package com.reevil.entity.ganado;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Ganado (RE4): the Las Plagas-infected villager. Carries a random tool (pitchfork, axe, sickle, torch or bare hands); fights in a group, grabs and chokes.
 * When a Plaga parasite bursts from the neck (a headshot, or a big hit when badly hurt) the head explodes and one of three forms emerges:
 *  form 1 - whip tentacles (lash), form 2 - scythe arms (double slash), form 3 - spider legs (long piercing jabs).
 */
public class GanadoEntity extends BioMonster {
    public static final int SWING = 1, STAB = 2, GRAB = 3, BURST = 4, LASH = 5, SCYTHE = 6, PIERCE = 7, DECAP = 8, RUSH = 9, SHOOT = 10;
    public static final int W_NONE = 0, W_PITCHFORK = 1, W_AXE = 2, W_SICKLE = 3, W_TORCH = 4, W_CHAINSAW = 5, W_BOW = 6;

    protected static final EntityDataAccessor<Integer> DATA_WEAPON = SynchedEntityData.defineId(GanadoEntity.class, EntityDataSerializers.INT);

    private static final AttackMove M_SWING = new AttackMove(SWING, "swing", 26, new int[]{12}, 0, 2.8, 14, 7).dmg(5f).kb(0.7).face(11);
    private static final AttackMove M_STAB = new AttackMove(STAB, "thrust", 28, new int[]{14}, 1.5, 4.0, 22, 4).dmg(6f).kb(0.4).face(10).move();
    private static final AttackMove M_GRAB = new AttackMove(GRAB, "grab_choke", 60, new int[]{14, 28, 42}, 0, 1.9, 120, 3).dmg(2f).kb(0).face(14);
    private static final AttackMove M_BURST = new AttackMove(BURST, "plaga_burst", 44, new int[]{16}, 0, 99, 20, 0).face(0);
    private static final AttackMove M_LASH = new AttackMove(LASH, "tentacle_lash", 30, new int[]{13, 21}, 1.5, 7, 16, 6).dmg(6f).kb(0.8).face(21);
    private static final AttackMove M_SCYTHE = new AttackMove(SCYTHE, "scythe_slash", 30, new int[]{10, 19}, 0, 3.4, 14, 7).dmg(8f).kb(0.6).face(19);
    private static final AttackMove M_PIERCE = new AttackMove(PIERCE, "leg_pierce", 30, new int[]{14}, 2, 6, 20, 6).dmg(9f).kb(0.5).face(11).move();
    private static final List<AttackMove> HUMAN = List.of(M_SWING, M_STAB, M_GRAB);
    private static final List<AttackMove> F1 = List.of(M_SWING, M_LASH, M_GRAB);
    private static final List<AttackMove> F2 = List.of(M_SCYTHE, M_GRAB);
    private static final List<AttackMove> F3 = List.of(M_PIERCE, M_LASH);

    private LivingEntity held;
    private boolean bursting;

    public GanadoEntity(EntityType<? extends GanadoEntity> type, Level level) {
        super(type, level);
        xpReward = 6;
        if (!level.isClientSide) entityData.set(DATA_WEAPON, defaultWeapon());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 28.0)
            .add(Attributes.MOVEMENT_SPEED, 0.24)
            .add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.FOLLOW_RANGE, 26.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_WEAPON, 0);
    }

    protected int defaultWeapon() { return random.nextInt(5); }
    public int getWeapon() { return entityData.get(DATA_WEAPON); }
    @Override public List<AttackMove> moves() { return getForm() == 0 ? HUMAN : getForm() == 1 ? F1 : getForm() == 2 ? F2 : F3; }
    @Override public String getTextureSuffix() { return getForm() == 0 ? "" : "_plaga"; }
    @Override protected boolean isImmobile() { return super.isImmobile() || getMoveId() == BURST; }

    /** Headshot (projectile above the shoulders) or a heavy hit while hurt releases the parasite. */
    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (bursting) return false;
        boolean head = src.getDirectEntity() instanceof Projectile p && p.getY() > getY() + getBbHeight() * 0.78;
        boolean ok = super.hurt(src, amount);
        if (ok && !level().isClientSide && isAlive() && getForm() == 0 && getMoveId() != BURST && canBurst()) {
            float low = getHealth() / getMaxHealth();
            if ((head && random.nextFloat() < 0.5f) || (low < 0.5f && amount >= 4f && random.nextFloat() < 0.3f)) { cancelMove(); startMove(M_BURST); }
        }
        return ok;
    }

    protected boolean canBurst() { return true; }
    public boolean hasHat() { return true; }

    @Override
    protected void onMoveStart(AttackMove m) {
        held = null;
        bursting = m.id == BURST;
        if (m.id == BURST) playSound(SoundEvents.ZOMBIE_VILLAGER_CURE, 1.6f, 0.5f);
    }

    @Override protected void onMoveEnd(AttackMove m) { held = null; bursting = false; }
    @Override public void cancelMove() { super.cancelMove(); held = null; bursting = false; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case SWING: if (t == 11) lunge(0.12, 0); break;
            case STAB: if (t == 10) lunge(0.5, 0.1); break;
            case PIERCE: if (t == 11) lunge(0.5, 0.15); break;
            case GRAB:
                if (held != null && held.isAlive() && t >= 14 && t <= 54) {
                    Vec3 hold = position().add(Vec3.directionFromRotation(0, getYRot()).scale(0.8)).add(0, 0.9, 0);
                    held.setDeltaMovement(hold.subtract(held.position().add(0, held.getBbHeight() * 0.5, 0)).scale(0.5));
                    held.hurtMarked = true;
                }
                break;
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case BURST -> {
                setForm(1 + random.nextInt(3));
                bursting = false;
                setHealth(Math.min(getMaxHealth() * 1.4f, getHealth() + 14f));
                var hp = getAttribute(Attributes.MAX_HEALTH); if (hp != null) hp.setBaseValue(40.0);
                var sp = getAttribute(Attributes.MOVEMENT_SPEED); if (sp != null) sp.setBaseValue(0.3);
                playSound(SoundEvents.SLIME_SQUISH, 1.8f, 0.5f);
                if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.ITEM_SLIME, getX(), getY() + getBbHeight() * 0.9, getZ(), 30, 0.3, 0.3, 0.3, 0.2);
            }
            case GRAB -> {
                if (hitIndex == 0) { if (target != null && distanceTo(target) <= 2.3) { held = target; strike(target, m.damage, 0); } }
                else if (held != null && held.isAlive()) { strike(held, hitIndex == 2 ? 6f : 2f, hitIndex == 2 ? 0.6 : 0); if (hitIndex == 2) held = null; playSound(SoundEvents.PLAYER_HURT, 0.9f, 0.6f); }
            }
            case LASH, SCYTHE, PIERCE, SWING, STAB -> {
                if (target != null && distanceTo(target) <= m.maxRange + 0.9 && strike(target, m.damage * (m.id == SWING && getWeapon() == W_AXE ? 1.3f : 1f), m.knockback)) {
                    playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, m.id == LASH ? 0.7f : 1.1f);
                    if (m.id == SWING && getWeapon() == W_TORCH) target.setSecondsOnFire(3);
                }
            }
            default -> super.onMoveHit(m, hitIndex, target);
        }
    }

    @Override public void addAdditionalSaveData(CompoundTag t) { super.addAdditionalSaveData(t); t.putInt("Weapon", getWeapon()); }
    @Override public void readAdditionalSaveData(CompoundTag t) { super.readAdditionalSaveData(t); if (t.contains("Weapon")) entityData.set(DATA_WEAPON, t.getInt("Weapon")); }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.VILLAGER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.VILLAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.VILLAGER_DEATH; }
    @Override public float getVoicePitch() { return 0.7f + random.nextFloat() * 0.3f; }
}
