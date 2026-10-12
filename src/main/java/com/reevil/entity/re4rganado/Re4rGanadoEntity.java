package com.reevil.entity.re4rganado;

import com.reevil.entity.ganado.GanadoEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake villager: bandana, scruffy beard, wicker pack. */
public class Re4rGanadoEntity extends GanadoEntity {
    public Re4rGanadoEntity(EntityType<Re4rGanadoEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<Re4rGanadoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 33.6).add(Attributes.MOVEMENT_SPEED, 0.259).add(Attributes.ATTACK_DAMAGE, 4.6).add(Attributes.FOLLOW_RANGE, 26);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "overhead_chop", 30, new int[]{15}, 0f, 2.6f, 30, 4).dmg(7f).kb(0.9).face(20);
    private static final AttackMove X31 = new AttackMove(31, "wrestle_bite", 44, new int[]{14, 28, 38}, 0f, 1.7f, 44, 3).dmg(4f).kb(0.2).face(40);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 9 && t <= 14) lunge(0.18f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (m.id == 31 && hitIndex == 0 && target != null && distanceTo(target) < m.maxRange + 1.2) target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 50, 2), this);
    }
}
