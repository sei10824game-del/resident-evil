package com.reevil.entity.yawn;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Yawn - the giant mutated snake (RE1 / REmake). Form 0 = first encounter, form 1 = enraged second encounter
 * (bigger, faster, darker "_enraged" skin), entered below 50% HP through an invulnerable skin-shedding roar.
 * Moves: venom strike, constricting coil, tail whip, rear-up crash, venom spit (simulated arcing blobs),
 * burrow retreat with regeneration (low HP), plus the TRANSFORM move (not selectable, forced).
 */
public class YawnEntity extends BioMonster {
    public static final int BITE = 1, CONSTRICT = 2, TAIL_WHIP = 3, REAR_CRASH = 4, SPIT = 5, BURROW = 6, TRANSFORM = 7;
    public static final float RAGE_SCALE = 1.25f;
    private static final double SPIT_G = 0.05;

    private static final List<AttackMove> MOVES_0 = build(1f, 1f);
    private static final List<AttackMove> MOVES_1 = build(0.8f, 1.3f);
    private static final AttackMove TRANSFORM_MOVE = new AttackMove(TRANSFORM, "shed_transform", 70, new int[]{35}, 0, 99, 30, 0).face(0);

    private final ServerBossEvent boss = new ServerBossEvent(getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_10);
    /** in-flight venom blobs: x,y,z, vx,vy,vz, life, damage */
    private final List<double[]> spit = new ArrayList<>();
    private LivingEntity coiled;
    private int retreats = 2;
    private int lastForm = -1;

    public YawnEntity(EntityType<YawnEntity> type, Level level) {
        super(type, level);
        xpReward = 60;
        setMaxUpStep(1.0f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 180.0)
            .add(Attributes.MOVEMENT_SPEED, 0.26)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
            .add(Attributes.ARMOR, 4.0);
    }

    private static int d(int ticks, float f) { return Math.max(1, Math.round(ticks * f)); }

    /** f = duration factor (form 1 is faster), dm = damage multiplier. Hit ticks scale with duration so clips stay in sync. */
    private static List<AttackMove> build(float f, float dm) {
        List<AttackMove> l = new ArrayList<>();
        l.add(new AttackMove(BITE, "venom_strike", d(30, f), new int[]{d(16, f)}, 0, 4.0, 14, 5).dmg(7f * dm).kb(0.5).face(d(13, f)));
        l.add(new AttackMove(CONSTRICT, "constrict", d(80, f), new int[]{d(20, f), d(32, f), d(44, f), d(56, f), d(68, f)}, 0, 2.2, 40, 2)
            .dmg(4f * dm).kb(0).face(d(20, f)));
        l.add(new AttackMove(TAIL_WHIP, "tail_whip", d(34, f), new int[]{d(18, f)}, 0, 3.5, 18, 3).dmg(6f * dm).kb(1.4).face(d(8, f)));
        l.add(new AttackMove(REAR_CRASH, "rear_crash", d(50, f), new int[]{d(32, f)}, 0, 4.5, 22, 3).dmg(10f * dm).kb(0.9).face(d(30, f)));
        l.add(new AttackMove(SPIT, "venom_spit", d(32, f), new int[]{d(18, f)}, 4.5, 16, 26, 3).dmg(5f * dm).face(d(18, f)));
        l.add(new AttackMove(BURROW, "burrow_retreat", 100, new int[]{50}, 0, 32, 30, 50).face(0));
        return l;
    }

    @Override public List<AttackMove> moves() { return getForm() == 1 ? MOVES_1 : MOVES_0; }
    @Override public String getTextureSuffix() { return getForm() == 1 ? "_enraged" : ""; }
    public float bodyScale() { return getForm() == 1 ? RAGE_SCALE : 1f; }

    @Override
    public boolean canStartMove(AttackMove m) {
        if (m.id == BURROW) return retreats > 0 && getHealth() < getMaxHealth() * 0.3f;
        return true;
    }

    // ------------------------------------------------------------------ size / form
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dim = super.getDimensions(pose);
        return getForm() == 1 ? dim.scale(RAGE_SCALE) : dim;
    }

    @Override
    public void tick() {
        super.tick();
        if (getForm() != lastForm) { lastForm = getForm(); refreshDimensions(); }
    }

    @Override
    public AABB getBoundingBoxForCulling() { return getBoundingBox().inflate(5.0 * bodyScale(), 2.0, 5.0 * bodyScale()); }

    // ------------------------------------------------------------------ server AI
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        boss.setProgress(getHealth() / getMaxHealth());
        AttackMove cur = currentMove();
        if (getForm() == 0 && isAlive() && getHealth() < getMaxHealth() * 0.5f && (cur == null || cur.id != TRANSFORM)) {
            releaseCoil(false);
            cancelMove();
            startMove(TRANSFORM_MOVE);
        }
        tickSpit();
    }

    @Override
    protected void onMoveStart(AttackMove m) {
        switch (m.id) {
            case BITE, SPIT -> playSound(SoundEvents.CAT_HISS, 1.6f, 0.45f);
            case CONSTRICT -> playSound(SoundEvents.CAT_HISS, 1.8f, 0.35f);
            case REAR_CRASH -> playSound(SoundEvents.CAT_HISS, 2.2f, 0.3f);
            case TAIL_WHIP -> playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 0.5f);
            case BURROW -> { retreats--; playSound(SoundEvents.CAT_HISS, 1.5f, 0.6f); }
            case TRANSFORM -> playSound(SoundEvents.RAVAGER_STUNNED, 2.0f, 0.5f);
            default -> {}
        }
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        int hit = m.hitTicks.length > 0 ? m.hitTicks[0] : m.duration;
        switch (m.id) {
            case BITE -> { if (t == hit - 2) lunge(1.0 * bodyScale(), 0); }
            case REAR_CRASH -> { if (t == hit - 3) lunge(0.6 * bodyScale(), 0); }
            case TAIL_WHIP -> { if (t == hit - 3) playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f, 0.4f); }
            case SPIT -> {
                if (t == hit / 3 || t == hit * 2 / 3) playSound(SoundEvents.SLIME_SQUISH, 1.0f, 0.5f); // gulping venom
            }
            case CONSTRICT -> {
                if (coiled != null) {
                    if (!coiled.isAlive() || coiled.distanceTo(this) > 8) coiled = null;
                    else {
                        Vec3 c = headPos(1.8);
                        Vec3 dv = c.subtract(coiled.position());
                        coiled.setDeltaMovement(dv.x * 0.5, Math.min(0.1, dv.y * 0.5), dv.z * 0.5);
                        coiled.hurtMarked = true;
                        coiled.fallDistance = 0f;
                    }
                }
            }
            case BURROW -> tickBurrow(t, target);
            case TRANSFORM -> {
                if (t <= 42 && t % 3 == 0) {
                    shedFlakes(10);
                    if (t % 9 == 0) playSound(SoundEvents.CAT_HISS, 1.5f, 0.3f + random.nextFloat() * 0.3f);
                }
            }
            default -> {}
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int i, LivingEntity target) {
        switch (m.id) {
            case BITE -> {
                playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.3f, 0.6f);
                if (target != null && near(target, headPos(2.6), 1.6) && strike(target, m.damage, m.knockback))
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 140, 1), this);
            }
            case CONSTRICT -> squeeze(m, i, target);
            case TAIL_WHIP -> {
                double r = 3.6 * bodyScale();
                for (LivingEntity le : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(r, 1.0, r),
                        x -> x != this && x.isAlive() && !(x instanceof YawnEntity))) {
                    strike(le, m.damage, m.knockback);
                }
                playSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 2.0f, 0.5f);
                groundBurst(position(), 30, 2.5);
            }
            case REAR_CRASH -> {
                Vec3 p = headPos(2.9);
                for (LivingEntity le : level().getEntitiesOfClass(LivingEntity.class, new AABB(p, p).inflate(2.6, 2.0, 2.6),
                        x -> x != this && x.isAlive())) {
                    if (strike(le, m.damage, m.knockback)) le.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1), this);
                }
                playSound(SoundEvents.RAVAGER_ATTACK, 2.0f, 0.5f);
                groundBurst(p, 40, 1.5);
                burst(ParticleTypes.POOF, p, 12, 1.0, 0.05);
            }
            case SPIT -> launchSpit(m, target);
            case BURROW -> relocate(target);
            case TRANSFORM -> evolve();
            default -> {}
        }
    }

    @Override
    protected void onMoveEnd(AttackMove m) {
        if (m.id == CONSTRICT) releaseCoil(true);
        if (m.id == BURROW) setInvisible(false);
    }

    // ------------------------------------------------------------------ move details
    private Vec3 forward() { return Vec3.directionFromRotation(0f, getYRot()); }

    /** World position roughly where the head is, dist blocks in front at rest scale. */
    private Vec3 headPos(double dist) { return position().add(forward().scale(dist * bodyScale())).add(0, 0.5 * bodyScale(), 0); }

    private static boolean near(LivingEntity e, Vec3 p, double r) { return e.getBoundingBox().inflate(r).contains(p); }

    private void squeeze(AttackMove m, int i, LivingEntity target) {
        if (i == 0) {
            if (target != null && near(target, headPos(2.0), 2.2)) {
                coiled = target;
                strike(target, m.damage, 0);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4), this);
                playSound(SoundEvents.EVOKER_FANGS_ATTACK, 1.2f, 0.5f);
            }
            return;
        }
        if (coiled == null || !coiled.isAlive()) return;
        coiled.hurt(damageSources().mobAttack(this), m.damage + i);   // each squeeze crushes harder
        coiled.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0), this);
        coiled.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4), this);
        playSound(SoundEvents.SLIME_SQUISH, 1.6f, 0.4f);
        playSound(SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, 0.6f, 0.5f);
        burst(ParticleTypes.CRIT, coiled.position().add(0, coiled.getBbHeight() * 0.5, 0), 10, 0.4, 0.1);
    }

    private void releaseCoil(boolean toss) {
        if (coiled != null && toss && coiled.isAlive()) {
            Vec3 dv = coiled.position().subtract(position()).multiply(1, 0, 1);
            if (dv.lengthSqr() > 1.0E-4) {
                dv = dv.normalize().scale(0.9);
                coiled.push(dv.x, 0.4, dv.z);
                coiled.hurtMarked = true;
            }
        }
        coiled = null;
    }

    private void launchSpit(AttackMove m, LivingEntity target) {
        if (target == null) return;
        Vec3 from = headPos(2.4).add(0, 0.6, 0);
        int n = getForm() == 1 ? 3 : 1;
        for (int k = 0; k < n; k++) {
            Vec3 to = target.position().add(0, target.getBbHeight() * 0.5, 0);
            if (k > 0) to = to.add((random.nextDouble() - 0.5) * 3.0, 0, (random.nextDouble() - 0.5) * 3.0);
            Vec3 dv = to.subtract(from);
            double flight = Math.max(8.0, Math.sqrt(dv.x * dv.x + dv.z * dv.z) / 0.8);
            double vy = dv.y / flight + 0.5 * SPIT_G * flight;
            spit.add(new double[]{from.x, from.y, from.z, dv.x / flight, vy, dv.z / flight, 70, m.damage});
        }
        playSound(SoundEvents.LLAMA_SPIT, 1.6f, 0.5f);
        burst(ParticleTypes.ITEM_SLIME, from, 12, 0.3, 0.1);
    }

    /** Server-side simulated venom blobs: gravity arc, trail particles, splash damage + poison + acid puddle on landing. */
    private void tickSpit() {
        if (spit.isEmpty() || !(level() instanceof ServerLevel sl)) return;
        Iterator<double[]> it = spit.iterator();
        while (it.hasNext()) {
            double[] s = it.next();
            s[0] += s[3]; s[1] += s[4]; s[2] += s[5];
            s[4] -= SPIT_G;
            s[6] -= 1;
            Vec3 p = new Vec3(s[0], s[1], s[2]);
            sl.sendParticles(ParticleTypes.ITEM_SLIME, p.x, p.y, p.z, 3, 0.08, 0.08, 0.08, 0.01);
            sl.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0);
            BlockPos bp = BlockPos.containing(p.x, p.y, p.z);
            boolean ground = !level().getBlockState(bp).getCollisionShape(level(), bp).isEmpty();
            boolean hitMob = !level().getEntitiesOfClass(LivingEntity.class, new AABB(p, p).inflate(0.6), e -> e != this && e.isAlive()).isEmpty();
            if (ground || hitMob || s[6] <= 0) {
                splash(sl, p, (float) s[7]);
                it.remove();
            }
        }
    }

    private void splash(ServerLevel sl, Vec3 p, float dmg) {
        for (LivingEntity le : level().getEntitiesOfClass(LivingEntity.class, new AABB(p, p).inflate(1.8), e -> e != this && e.isAlive())) {
            if (le.hurt(damageSources().mobAttack(this), dmg)) le.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1), this);
        }
        sl.sendParticles(ParticleTypes.ITEM_SLIME, p.x, p.y, p.z, 30, 0.6, 0.3, 0.6, 0.15);
        sl.sendParticles(ParticleTypes.SNEEZE, p.x, p.y, p.z, 12, 0.5, 0.2, 0.5, 0.02);
        level().playSound(null, p.x, p.y, p.z, SoundEvents.SLIME_SQUISH, getSoundSource(), 1.2f, 0.6f);
        AreaEffectCloud acid = new AreaEffectCloud(level(), p.x, p.y, p.z);
        acid.setOwner(this);
        acid.setRadius(1.6f);
        acid.setDuration(70);
        acid.setWaitTime(0);
        acid.setRadiusPerTick(-0.01f);
        acid.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
        level().addFreshEntity(acid);
    }

    private void tickBurrow(int t, LivingEntity target) {
        if (t <= 28 && target != null) {
            Vec3 away = position().subtract(target.position()).multiply(1, 0, 1);
            if (away.lengthSqr() > 1.0E-4) {
                away = away.normalize();
                float yaw = (float) (Math.toDegrees(Math.atan2(away.z, away.x)) - 90.0);
                setYRot(yaw); yBodyRot = yaw; yHeadRot = yaw;
                if (t > 8) setDeltaMovement(away.x * 0.3, getDeltaMovement().y, away.z * 0.3);
            }
        }
        if ((t >= 10 && t <= 32 && t % 2 == 0) || (t >= 72 && t <= 90 && t % 2 == 0)) groundBurst(position(), 14, 1.2 * bodyScale());
        if (t == 26) {
            setInvisible(true);
            addEffect(new MobEffectInstance(MobEffects.REGENERATION, 140, 2));
            playSound(SoundEvents.GRAVEL_BREAK, 2.0f, 0.5f);
        }
        if (t == 76) {
            setInvisible(false);
            playSound(SoundEvents.GRAVEL_BREAK, 2.0f, 0.6f);
            playSound(SoundEvents.CAT_HISS, 2.0f, 0.4f);
        }
    }

    /** Slither away underground: reappear 9-15 blocks away from the target. */
    private void relocate(LivingEntity target) {
        Vec3 away = target != null ? position().subtract(target.position()).multiply(1, 0, 1) : forward().scale(-1);
        if (away.lengthSqr() < 1.0E-4) away = new Vec3(1, 0, 0);
        away = away.normalize();
        for (int k = 0; k < 10; k++) {
            Vec3 dir = away.yRot((float) ((random.nextDouble() - 0.5) * 2.0));
            double dist = 9.0 + random.nextDouble() * 6.0;
            if (randomTeleport(getX() + dir.x * dist, getY() + 3.0, getZ() + dir.z * dist, false)) break;
        }
        getNavigation().stop();
    }

    private void evolve() {
        setForm(1);
        AttributeInstance sp = getAttribute(Attributes.MOVEMENT_SPEED);
        if (sp != null) sp.setBaseValue(0.33);
        AttributeInstance ad = getAttribute(Attributes.ATTACK_DAMAGE);
        if (ad != null) ad.setBaseValue(10.0);
        heal(getMaxHealth() * 0.15f);
        boss.setColor(BossEvent.BossBarColor.RED);
        playSound(SoundEvents.RAVAGER_ROAR, 3.0f, 0.5f);
        shedFlakes(80);
        burst(ParticleTypes.LARGE_SMOKE, position().add(0, 0.6, 0), 20, 1.5, 0.02);
    }

    /** Shedding skin: leathery flakes + ash peeling off the whole length of the body. */
    private void shedFlakes(int n) {
        Vec3 f = forward();
        for (int k = -1; k <= 2; k++) {
            Vec3 p = position().add(f.scale(-k * 1.6 * bodyScale())).add(0, 0.6, 0);
            burst(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.LEATHER)), p, n / 4 + 1, 0.8, 0.08);
            burst(ParticleTypes.WHITE_ASH, p, n / 2 + 1, 0.9, 0.02);
        }
    }

    private void groundBurst(Vec3 p, int n, double spread) {
        BlockState st = level().getBlockState(BlockPos.containing(p.x, p.y - 0.5, p.z));
        burst(new BlockParticleOption(ParticleTypes.BLOCK, st), p.add(0, 0.2, 0), n, spread, 0.15);
    }

    private void burst(ParticleOptions type, Vec3 p, int n, double spread, double speed) {
        if (level() instanceof ServerLevel sl) sl.sendParticles(type, p.x, p.y, p.z, n, spread, spread * 0.5, spread, speed);
    }

    // ------------------------------------------------------------------ damage / effects
    private boolean shielded() {
        AttackMove m = currentMove();
        if (m == null) return false;
        int t = getMoveTick();
        if (m.id == TRANSFORM) return t <= 45;
        if (m.id == BURROW) return t >= 25 && t <= 78;
        return false;
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (!src.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && shielded()) return false;
        return super.hurt(src, amount);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return effect.getEffect() != MobEffects.POISON && super.canBeAffected(effect);
    }

    @Override
    public void die(DamageSource src) {
        releaseCoil(false);
        super.die(src);
    }

    @Override public boolean removeWhenFarAway(double dist) { return false; }
    @Override protected void playStepSound(BlockPos pos, BlockState state) {}
    @Override protected SoundEvent getAmbientSound() { return SoundEvents.CAT_HISS; }
    @Override public int getAmbientSoundInterval() { return 240; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.RAVAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.RAVAGER_DEATH; }
    @Override public float getVoicePitch() { return 0.6f; }

    // ------------------------------------------------------------------ boss bar
    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        boss.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        boss.removePlayer(player);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Retreats", retreats);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Retreats")) retreats = tag.getInt("Retreats");
        if (getForm() == 1) boss.setColor(BossEvent.BossBarColor.RED);
    }
}
