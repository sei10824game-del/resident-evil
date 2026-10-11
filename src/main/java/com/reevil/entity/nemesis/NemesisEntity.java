package com.reevil.entity.nemesis;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Nemesis (RE3): the relentless pursuer. Tracks players at 64 blocks without needing sight, smashes doors, kneels when badly hurt, and mutates twice.
 *  Form 0 (coat): punch, jab-combo, choke-and-throw, kick, tentacle lash, ROCKET LAUNCHER (shoulder-fired, delayed explosion, no block damage).
 *  Form 1 (&lt;60% HP, torn coat, tentacles): adds the tentacle snare-and-drag, faster.
 *  Form 2 (&lt;25% HP, hunched, tentacle mass): adds the all-round tentacle barrage, faster still.
 */
public class NemesisEntity extends BioMonster {
    public static final int PUNCH = 1, COMBO = 2, CHOKE = 3, KICK = 4, WHIP = 5, SNARE = 6, ROCKET = 7, BARRAGE = 8, MUTATE = 9, STAGGER = 10;

    private static final AttackMove M_PUNCH = new AttackMove(PUNCH, "punch", 30, new int[]{15}, 0, 3.0, 18, 6).dmg(10f).kb(1.4).face(14);
    private static final AttackMove M_COMBO = new AttackMove(COMBO, "jab_hook", 28, new int[]{9, 19}, 0, 2.7, 14, 6).dmg(6.5f).kb(0.6).face(18);
    private static final AttackMove M_CHOKE = new AttackMove(CHOKE, "throat_choke", 72, new int[]{14, 30, 44, 60}, 0, 2.6, 150, 2).dmg(3f).kb(0).face(14);
    private static final AttackMove M_KICK = new AttackMove(KICK, "kick", 24, new int[]{12}, 0, 2.5, 16, 4).dmg(8f).kb(1.9).face(11);
    private static final AttackMove M_WHIP = new AttackMove(WHIP, "tentacle_lash", 28, new int[]{13}, 2.5, 9.5, 24, 5).dmg(9f).kb(1.0).face(12);
    private static final AttackMove M_SNARE = new AttackMove(SNARE, "tentacle_snare", 58, new int[]{14, 40}, 3.5, 12, 80, 4).dmg(4f).kb(0).face(14);
    private static final AttackMove M_ROCKET = new AttackMove(ROCKET, "rocket_launcher", 56, new int[]{26}, 7, 30, 110, 6).dmg(16f).kb(0).face(26);
    private static final AttackMove M_BARRAGE = new AttackMove(BARRAGE, "tentacle_barrage", 50, new int[]{14, 22, 30, 38}, 0, 9, 60, 5).dmg(8f).kb(1.0).face(10);
    private static final AttackMove M_MUTATE = new AttackMove(MUTATE, "mutation", 90, new int[]{44}, 0, 99, 20, 0).face(0);
    private static final AttackMove M_STAGGER = new AttackMove(STAGGER, "stagger_kneel", 90, new int[]{}, 0, 99, 20, 0).face(0);
    private static final List<AttackMove> F0 = List.of(M_PUNCH, M_COMBO, M_CHOKE, M_KICK, M_WHIP, M_ROCKET);
    private static final List<AttackMove> F1 = List.of(M_PUNCH, M_COMBO, M_CHOKE, M_KICK, M_WHIP, M_ROCKET, M_SNARE);
    private static final List<AttackMove> F2 = List.of(M_PUNCH, M_COMBO, M_KICK, M_WHIP, M_ROCKET, M_SNARE, M_BARRAGE);

    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6);
    private final List<double[]> rockets = new ArrayList<>(); // {ticksLeft, x, y, z}
    private LivingEntity held;
    private float staggerAcc;
    private int doorCd;
    private boolean invulnMove;

    public NemesisEntity(EntityType<NemesisEntity> type, Level level) {
        super(type, level);
        xpReward = 200;
        setMaxUpStep(1.2f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 400.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
            .add(Attributes.ATTACK_DAMAGE, 10.0)
            .add(Attributes.FOLLOW_RANGE, 64.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
            .add(Attributes.ARMOR, 10.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null));
    }

    @Override public List<AttackMove> moves() { return getForm() == 0 ? F0 : getForm() == 1 ? F1 : F2; }
    @Override public String getTextureSuffix() { return getForm() == 0 ? "" : getForm() == 1 ? "_mutated" : "_final"; }
    @Override public boolean removeWhenFarAway(double d) { return false; }
    @Override protected boolean isImmobile() { return super.isImmobile() || getMoveId() == STAGGER || getMoveId() == MUTATE; }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (invulnMove) return false;
        float before = getHealth();
        boolean r = super.hurt(src, getMoveId() == STAGGER ? amount * 0.5f : amount);
        if (r && !level().isClientSide && isAlive() && getMoveId() != STAGGER && getMoveId() != MUTATE) {
            staggerAcc += before - getHealth();
            if (staggerAcc >= getMaxHealth() * 0.15f && !shouldMutate()) { staggerAcc = 0; cancelMove(); startMove(M_STAGGER); }
        }
        return r;
    }

    private boolean shouldMutate() {
        float f = getHealth() / getMaxHealth();
        return (getForm() == 0 && f < 0.6f) || (getForm() == 1 && f < 0.25f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (isAlive() && getMoveId() != MUTATE && shouldMutate()) { cancelMove(); startMove(M_MUTATE); }
        if (doorCd > 0) doorCd--;
        if (getTarget() != null && horizontalCollision && doorCd <= 0 && !isMoving()) breakDoors();
        for (Iterator<double[]> it = rockets.iterator(); it.hasNext();) { // in-flight rockets
            double[] r = it.next();
            if (--r[0] <= 0) { explode(r[1], r[2], r[3]); it.remove(); }
        }
    }

    private void explode(double x, double y, double z) {
        level().explode(this, x, y, z, 2.2f, false, Level.ExplosionInteraction.NONE);
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, new AABB(x - 3.2, y - 2, z - 3.2, x + 3.2, y + 2, z + 3.2), a -> a != this && a.isAlive())) {
            if (e.distanceToSqr(x, y, z) < 10.0) e.hurt(damageSources().mobAttack(this), M_ROCKET.damage);
        }
    }

    private void breakDoors() {
        if (!level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;
        Vec3 f = Vec3.directionFromRotation(0, getYRot()).scale(0.9);
        for (int dy = 0; dy <= 1; dy++) {
            BlockPos p = BlockPos.containing(getX() + f.x, getY() + dy + 0.2, getZ() + f.z);
            Block b = level().getBlockState(p).getBlock();
            if (b instanceof DoorBlock || b instanceof FenceGateBlock || b instanceof TrapDoorBlock) {
                level().destroyBlock(p, true, this);
                playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 1.5f, 0.5f);
                doorCd = 12;
                return;
            }
        }
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        held = null;
        invulnMove = m.id == MUTATE;
        switch (m.id) {
            case MUTATE: playSound(SoundEvents.RAVAGER_ROAR, 3.5f, 0.3f); break;
            case ROCKET: playSound(SoundEvents.RAVAGER_ROAR, 2f, 0.6f); break;   // "S.T.A.R.S....!"
            case WHIP: case SNARE: case BARRAGE: playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.4f, 0.4f); break;
            default: break;
        }
    }

    @Override protected void onMoveEnd(AttackMove m) { held = null; invulnMove = false; }
    @Override public void cancelMove() { super.cancelMove(); held = null; invulnMove = false; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case PUNCH: if (t >= 12 && t <= 15) lunge(0.12, 0); break;
            case SNARE:
                if (held != null && held.isAlive() && t >= 16 && t <= 40) {
                    Vec3 d = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.3)).subtract(held.position());
                    double len = d.length();
                    held.setDeltaMovement(len > 1.2 ? d.normalize().scale(Math.min(0.6, len * 0.22)) : Vec3.ZERO);
                    held.hurtMarked = true;
                }
                break;
            case CHOKE:
                if (held != null && held.isAlive() && t >= 14 && t <= 60) {
                    Vec3 hold = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.0)).add(0, 1.7 - held.getBbHeight() * 0.5 + (t < 30 ? (t - 14) * 0.03 : 0.5), 0);
                    held.setDeltaMovement(hold.subtract(held.position().add(0, held.getBbHeight() * 0.5, 0)).scale(0.5));
                    held.hurtMarked = true; held.fallDistance = 0;
                }
                break;
            case ROCKET:
                if (t >= 8 && t <= 26 && target != null) { /* tracks the prey while shouldering the launcher */ }
                if (t == 26 && level() instanceof ServerLevel sl) {
                    Vec3 aim = target != null ? target.position().add(0, target.getBbHeight() * 0.4, 0) : position().add(Vec3.directionFromRotation(0, getYRot()).scale(12));
                    Vec3 from = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.2)).add(0, 1.9, 0);
                    double dist = from.distanceTo(aim);
                    rockets.add(new double[]{Math.min(30, Math.max(3, dist / 1.4)), aim.x, aim.y, aim.z});
                    for (int i = 0; i < 14; i++) { Vec3 p = from.lerp(aim, i / 14.0); sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0); sl.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0); }
                    sl.sendParticles(ParticleTypes.EXPLOSION, from.x, from.y, from.z, 1, 0, 0, 0, 0);
                    playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 2.5f, 0.5f);
                }
                break;
            case MUTATE:
                if (t >= 30 && t <= 80 && t % 4 == 0 && level() instanceof ServerLevel sl)
                    sl.sendParticles(ParticleTypes.CRIMSON_SPORE, getX(), getY() + 1.6, getZ(), 8, 0.6, 0.9, 0.6, 0.06);
                break;
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case ROCKET -> { /* the launch tick is handled in onMoveTick; damage comes from the explosion */ }
            case MUTATE -> {
                setForm(getForm() + 1);
                invulnMove = false;
                var sp = getAttribute(Attributes.MOVEMENT_SPEED); if (sp != null) sp.setBaseValue(getForm() == 1 ? 0.30 : 0.34);
                playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 3f, 0.3f);
                if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1.5, getZ(), 4, 0.8, 0.8, 0.8, 0.0);
            }
            case CHOKE -> {
                if (hitIndex == 0) { if (target != null && distanceTo(target) <= 3.0) { held = target; strike(target, m.damage, 0); playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.5f, 0.5f); } }
                else if (held != null && held.isAlive()) {
                    if (hitIndex < 3) strike(held, 3f, 0);
                    else { Vec3 f = Vec3.directionFromRotation(0, getYRot()); strike(held, 11f, 0); held.setDeltaMovement(f.x * 1.5, 0.5, f.z * 1.5); held.hurtMarked = true; held = null; playSound(SoundEvents.GENERIC_EXPLODE, 1f, 1.4f); }
                }
            }
            case SNARE -> {
                if (hitIndex == 0) { if (target != null && distanceTo(target) <= m.maxRange + 1) { held = target; strike(target, m.damage, 0); playSound(SoundEvents.LEASH_KNOT_PLACE, 1.5f, 0.5f); } }
                else if (held != null && held.isAlive()) { Vec3 f = Vec3.directionFromRotation(0, getYRot()); strike(held, 12f, 0.3); held.setDeltaMovement(f.x * 0.8, 0.4, f.z * 0.8); held.hurtMarked = true; held = null; }
            }
            case BARRAGE -> {
                for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(m.maxRange - 1, 1.5, m.maxRange - 1), x -> x != this && x.isAlive())) {
                    Vec3 away = e.position().subtract(position()).multiply(1, 0, 1).normalize();
                    e.hurt(damageSources().mobAttack(this), m.damage); e.push(away.x * 0.9, 0.35, away.z * 0.9); e.hurtMarked = true;
                }
                playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.4f, 0.4f);
            }
            default -> { super.onMoveHit(m, hitIndex, target); playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.5f); }
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossEvent.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossEvent.removePlayer(p); }
    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, 1.1f, 0.5f); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.RAVAGER_STUNNED; }
}
