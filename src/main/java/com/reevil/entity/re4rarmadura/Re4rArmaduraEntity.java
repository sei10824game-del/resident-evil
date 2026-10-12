package com.reevil.entity.re4rarmadura;

import com.reevil.entity.armadura.ArmaduraEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Armadura: plumed helm, tower shield, halberd. */
public class Re4rArmaduraEntity extends ArmaduraEntity {
    public Re4rArmaduraEntity(EntityType<Re4rArmaduraEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ArmaduraEntity> unchecked(EntityType<Re4rArmaduraEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 144.0).add(Attributes.MOVEMENT_SPEED, 0.162).add(Attributes.ATTACK_DAMAGE, 9.2).add(Attributes.FOLLOW_RANGE, 22).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 14.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "halberd_thrust", 36, new int[]{18}, 0.8f, 5.2f, 36, 4).dmg(11f).kb(1.4).face(22);
    private static final AttackMove X31 = new AttackMove(31, "shield_bash", 28, new int[]{14}, 0f, 2.5f, 30, 3).dmg(6f).kb(2.4).face(16);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 12 && t <= 17) lunge(0.28f, 0f);
        if (m.id == 31 && t >= 9 && t <= 13) lunge(0.24f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
