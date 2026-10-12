package com.reevil.entity.re4rironmaiden;

import com.reevil.entity.ironmaiden.IronMaidenEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Iron Maiden: spiked cage on both sides. */
public class Re4rIronMaidenEntity extends IronMaidenEntity {
    public Re4rIronMaidenEntity(EntityType<Re4rIronMaidenEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<IronMaidenEntity> unchecked(EntityType<Re4rIronMaidenEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 192.0).add(Attributes.MOVEMENT_SPEED, 0.162).add(Attributes.ATTACK_DAMAGE, 10.3).add(Attributes.FOLLOW_RANGE, 22).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 6.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "spike_embrace", 56, new int[]{16, 30, 44}, 0f, 2.0f, 50, 3).dmg(5f).kb(0).face(52);
    private static final AttackMove X31 = new AttackMove(31, "cage_charge", 44, new int[]{26}, 3f, 8f, 70, 3).dmg(9f).kb(2.4).face(18).move();
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 31 && t >= 8 && t <= 26) lunge(0.34f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (m.id == 30 && hitIndex == 0 && target != null && distanceTo(target) < m.maxRange + 1.2) target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 70, 2), this);
    }
}
