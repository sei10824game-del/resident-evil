package com.reevil.entity.larva;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Giant moth larva (RE2 lab): crawling grub. Moves: snapping bite, rearing lunge-bite, and an arcing acid spit (server-simulated, lands where the prey stood). */
public class LarvaEntity extends BioMonster {
    public static final int BITE = 1, LUNGE = 2, SPIT = 3;

    private static final List<AttackMove> MOVES = List.of(
        new AttackMove(BITE, "bite", 22, new int[]{10}, 0, 2.4, 12, 7).dmg(5f).kb(0.2).face(9),
        new AttackMove(LUNGE, "rearing_lunge", 28, new int[]{13}, 2.5, 6, 30, 5).dmg(7f).kb(0.5).face(9).move(),
        new AttackMove(SPIT, "acid_spit", 36, new int[]{19}, 4, 11, 70, 4).dmg(3f).face(15)
    );
    private Vec3 aim;

    public LarvaEntity(EntityType<LarvaEntity> type, Level level) { super(type, level); xpReward = 8; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.MOVEMENT_SPEED, 0.22)
            .add(Attributes.ATTACK_DAMAGE, 5.0)
            .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    @Override public List<AttackMove> moves() { return MOVES; }

    @Override
    protected void onMoveStart(AttackMove m) {
        aim = null;
        playSound(SoundEvents.SILVERFISH_AMBIENT, 1.2f, 0.4f);
    }

    private Vec3 mouth() { return position().add(Vec3.directionFromRotation(0, getYRot()).scale(0.9)).add(0, 0.55, 0); }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m.id == LUNGE && t == 8) lunge(Math.min(0.9, target != null ? Math.max(0.3, (distanceTo(target) - 0.6) / 5.0) : 0.5), 0.25);
        if (m.id == SPIT) {
            if (t == 12 && target != null) aim = target.position();
            if (aim != null && t >= 12 && t <= 19 && level() instanceof ServerLevel sl) {
                float u = (t - 12) / 7f;
                Vec3 a = mouth();
                Vec3 p = a.lerp(aim, u).add(0, Math.sin(u * Math.PI) * 1.4, 0);
                sl.sendParticles(ParticleTypes.ITEM_SLIME, p.x, p.y, p.z, 4, 0.06, 0.06, 0.06, 0.01);
                sl.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, p.x, p.y, p.z, 1, 0.03, 0.03, 0.03, 0.0);
            }
        }
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        if (m.id == SPIT) {
            if (aim == null) return;
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, new AABB(aim, aim).inflate(1.8), x -> x != this && x.isAlive())) {
                if (e.hurt(damageSources().mobAttack(this), m.damage)) e.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
            }
            if (level() instanceof ServerLevel sl) sl.sendParticles(ParticleTypes.ITEM_SLIME, aim.x, aim.y + 0.2, aim.z, 24, 0.7, 0.2, 0.7, 0.12);
            playSound(SoundEvents.SLIME_SQUISH, 1.2f, 0.6f);
            return;
        }
        if (target != null && distanceTo(target) <= m.maxRange + 1.0 && strike(target, m.damage, m.knockback)) playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 1.2f);
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SILVERFISH_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.SILVERFISH_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.SILVERFISH_DEATH; }
    @Override public float getVoicePitch() { return 0.5f; }
}
