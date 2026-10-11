package com.reevil.entity.mrx;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import com.reevil.entity.ai.BioCombatGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
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

import java.util.List;

/**
 * Mr. X / Tyrant T-103 (RE2): relentless trench-coated stalker. Tracks players through walls of distance (no line of sight needed, 48 block range),
 * smashes doors/gates/trapdoors in his way, staggers to one knee after taking 20% of his health in damage, then rises again.
 * Moves: haymaker, jab-hook combo, throat grab + choke + throw, front stomp-kick. At 50% HP: roars, sheds the coat, becomes the SUPER TYRANT (form 1):
 * adds claw stab, claw sweep and ground pound and is faster.
 */
public class MrXEntity extends BioMonster {
    public static final int HAYMAKER = 1, JAB_HOOK = 2, CHOKE = 3, STOMP = 4, CLAW_STAB = 6, CLAW_SWEEP = 7, POUND = 8, TRANSFORM = 9, STAGGER = 10;

    private static final AttackMove M_HAY = new AttackMove(HAYMAKER, "haymaker", 30, new int[]{15}, 0, 3.0, 20, 6).dmg(9f).kb(1.3).face(14);
    private static final AttackMove M_JAB = new AttackMove(JAB_HOOK, "jab_hook", 28, new int[]{9, 19}, 0, 2.7, 16, 6).dmg(6f).kb(0.6).face(18);
    private static final AttackMove M_CHOKE = new AttackMove(CHOKE, "throat_choke", 72, new int[]{14, 30, 44, 60}, 0, 2.6, 140, 3).dmg(3f).kb(0).face(14);
    private static final AttackMove M_STOMP = new AttackMove(STOMP, "stomp_kick", 24, new int[]{12}, 0, 2.5, 18, 4).dmg(7f).kb(1.8).face(11);
    private static final AttackMove M_STAB = new AttackMove(CLAW_STAB, "claw_stab", 30, new int[]{15}, 1.5, 4.8, 24, 6).dmg(15f).kb(0.8).face(12).move();
    private static final AttackMove M_SWEEP = new AttackMove(CLAW_SWEEP, "claw_sweep", 36, new int[]{11, 24}, 0, 4.2, 22, 5).dmg(10f).kb(1.0).face(24);
    private static final AttackMove M_POUND = new AttackMove(POUND, "ground_pound", 42, new int[]{23}, 0, 5.5, 90, 3).dmg(11f).kb(1.4).face(12);
    private static final AttackMove M_TRANSFORM = new AttackMove(TRANSFORM, "super_transform", 90, new int[]{44}, 0, 99, 20, 0).face(0);
    private static final AttackMove M_STAGGER = new AttackMove(STAGGER, "stagger_kneel", 90, new int[]{}, 0, 99, 20, 0).face(0);
    private static final List<AttackMove> MOVES_0 = List.of(M_HAY, M_JAB, M_CHOKE, M_STOMP);
    private static final List<AttackMove> MOVES_1 = List.of(M_HAY, M_JAB, M_CHOKE, M_STOMP, M_STAB, M_SWEEP, M_POUND);

    private final ServerBossEvent bossEvent = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_6);
    private LivingEntity held;
    private float staggerAcc;
    private int doorCd;
    private boolean invulnMove;

    public MrXEntity(EntityType<MrXEntity> type, Level level) {
        super(type, level);
        xpReward = 100;
        setMaxUpStep(1.1f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 240.0)
            .add(Attributes.MOVEMENT_SPEED, 0.23)
            .add(Attributes.ATTACK_DAMAGE, 9.0)
            .add(Attributes.FOLLOW_RANGE, 48.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
            .add(Attributes.ARMOR, 8.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12f));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null)); // relentless: no line of sight required
    }

    public boolean isSuper() { return getForm() == 1; }
    public boolean isKneeling() { return getMoveId() == STAGGER; }
    @Override public List<AttackMove> moves() { return isSuper() ? MOVES_1 : MOVES_0; }
    @Override public String getTextureSuffix() { return isSuper() ? "_super" : ""; }
    @Override public boolean removeWhenFarAway(double d) { return false; }

    @Override
    protected boolean isImmobile() { return super.isImmobile() || getMoveId() == STAGGER || getMoveId() == TRANSFORM; }

    // ---- stagger + phases
    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (invulnMove) return false;
        float before = getHealth();
        boolean r = super.hurt(src, getMoveId() == STAGGER ? amount * 0.5f : amount);
        if (r && !level().isClientSide && isAlive() && getMoveId() != STAGGER && getMoveId() != TRANSFORM) {
            staggerAcc += before - getHealth();
            if (staggerAcc >= getMaxHealth() * 0.2f && !(canTransform() && getForm() == 0 && getHealth() < getMaxHealth() * 0.5f)) {
                staggerAcc = 0;
                cancelMove();
                startMove(M_STAGGER);
            }
        }
        return r;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        bossEvent.setProgress(getHealth() / getMaxHealth());
        if (canTransform() && isAlive() && getForm() == 0 && getHealth() < getMaxHealth() * 0.5f && getMoveId() != TRANSFORM) {
            cancelMove();
            startMove(M_TRANSFORM);
        }
        if (doorCd > 0) doorCd--;
        if (getTarget() != null && horizontalCollision && doorCd <= 0 && !isMoving()) breakDoors();
    }

    /** El Gigante never sheds into a Super Tyrant. */
    protected boolean canTransform() { return true; }

    private void breakDoors() {
        if (!level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) return;
        Vec3 f = Vec3.directionFromRotation(0, getYRot()).scale(0.9);
        for (int dy = 0; dy <= 1; dy++) {
            BlockPos p = BlockPos.containing(getX() + f.x, getY() + dy + 0.2, getZ() + f.z);
            BlockState s = level().getBlockState(p);
            Block b = s.getBlock();
            if (b instanceof DoorBlock || b instanceof FenceGateBlock || b instanceof TrapDoorBlock) {
                level().destroyBlock(p, true, this);
                playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 1.5f, 0.6f);
                doorCd = 12;
                return;
            }
        }
    }

    // ---- moves
    @Override
    protected void onMoveStart(AttackMove m) {
        held = null;
        invulnMove = m.id == TRANSFORM;
        switch (m.id) {
            case TRANSFORM: playSound(SoundEvents.RAVAGER_ROAR, 3f, 0.4f); break;
            case POUND: case HAYMAKER: case CLAW_SWEEP: playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.4f, 0.5f); break;
            default: break;
        }
    }

    @Override
    protected void onMoveEnd(AttackMove m) { held = null; invulnMove = false; }

    @Override
    public void cancelMove() { super.cancelMove(); held = null; invulnMove = false; }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        switch (m.id) {
            case CLAW_STAB:
                if (t >= 8 && t <= 14) lunge(0.42, 0);
                break;
            case HAYMAKER:
                if (t >= 12 && t <= 15) lunge(0.12, 0);
                break;
            case CHOKE:
                if (held != null && held.isAlive() && t >= 14 && t <= 60) {
                    Vec3 hold = position().add(Vec3.directionFromRotation(0, getYRot()).scale(1.0)).add(0, 1.55 - held.getBbHeight() * 0.5 + (t < 30 ? (t - 14) * 0.03 : 0.5), 0);
                    Vec3 d = hold.subtract(held.position().add(0, held.getBbHeight() * 0.5, 0));
                    held.setDeltaMovement(d.scale(0.5));
                    held.hurtMarked = true;
                    held.fallDistance = 0;
                }
                break;
            case TRANSFORM:
                if (t >= 30 && t <= 80 && t % 4 == 0 && level() instanceof ServerLevel sl)
                    sl.sendParticles(ParticleTypes.SQUID_INK, getX(), getY() + 1.6, getZ(), 6, 0.5, 0.8, 0.5, 0.05);
                break;
            default: break;
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        switch (m.id) {
            case CHOKE -> {
                if (hitIndex == 0) {
                    if (target != null && distanceTo(target) <= 3.0) { held = target; strike(target, m.damage, 0); playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.5f, 0.5f); }
                } else if (held != null && held.isAlive()) {
                    if (hitIndex < 3) { strike(held, 3f, 0); playSound(SoundEvents.PLAYER_HURT, 1f, 0.6f); }
                    else { // slam-throw
                        Vec3 f = Vec3.directionFromRotation(0, getYRot());
                        strike(held, 10f, 0);
                        held.setDeltaMovement(f.x * 1.5, 0.5, f.z * 1.5);
                        held.hurtMarked = true;
                        playSound(SoundEvents.GENERIC_EXPLODE, 1f, 1.4f);
                        held = null;
                    }
                }
            }
            case POUND -> pound(m);
            case TRANSFORM -> {
                setForm(1);
                var sp = getAttribute(Attributes.MOVEMENT_SPEED); if (sp != null) sp.setBaseValue(0.31);
                invulnMove = false;
                playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 3f, 0.4f);
                if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1.5, getZ(), 4, 0.8, 0.8, 0.8, 0.0);
            }
            case STOMP -> {
                super.onMoveHit(m, hitIndex, target);
                playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.5f, 0.5f);
            }
            default -> {
                super.onMoveHit(m, hitIndex, target);
                playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.2f, 0.5f);
            }
        }
    }

    private void pound(AttackMove m) {
        double r = 5.5;
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(r, 1.5, r), x -> x != this && x.isAlive())) {
            if (!e.onGround() && e.getY() - getY() > 1.2) continue;
            Vec3 away = e.position().subtract(position()).multiply(1, 0, 1).normalize();
            e.hurt(damageSources().mobAttack(this), m.damage);
            e.push(away.x * 1.1, 0.7, away.z * 1.1);
            e.hurtMarked = true;
        }
        playSound(SoundEvents.GENERIC_EXPLODE, 2f, 0.6f);
        if (level() instanceof ServerLevel sl) {
            BlockState below = level().getBlockState(BlockPos.containing(getX(), getY() - 0.5, getZ()));
            for (int i = 0; i < 24; i++) {
                double a = i * Math.PI * 2 / 24;
                for (double d = 1.2; d <= r; d += 1.4) sl.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, below), getX() + Math.cos(a) * d, getY() + 0.1, getZ() + Math.sin(a) * d, 1, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }

    @Override public void startSeenByPlayer(ServerPlayer p) { super.startSeenByPlayer(p); bossEvent.addPlayer(p); }
    @Override public void stopSeenByPlayer(ServerPlayer p) { super.stopSeenByPlayer(p); bossEvent.removePlayer(p); }

    @Override protected void playStepSound(BlockPos pos, BlockState state) { playSound(SoundEvents.IRON_GOLEM_STEP, 1.0f, 0.6f); }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.IRON_GOLEM_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.IRON_GOLEM_DEATH; }
}
