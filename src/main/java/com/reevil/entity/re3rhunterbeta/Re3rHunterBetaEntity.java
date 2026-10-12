package com.reevil.entity.re3rhunterbeta;

import com.reevil.entity.hunterbeta.HunterBetaEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE3 Remake Hunter Beta: armoured shoulders, head spines. */
public class Re3rHunterBetaEntity extends HunterBetaEntity {
    public Re3rHunterBetaEntity(EntityType<Re3rHunterBetaEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<HunterBetaEntity> unchecked(EntityType<Re3rHunterBetaEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 108.0).add(Attributes.MOVEMENT_SPEED, 0.367).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 0.4).add(Attributes.ARMOR, 4.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X31 = new AttackMove(31, "decapitation_slash", 30, new int[]{13}, 0f, 3.0f, 40, 4).dmg(13f).kb(1.6).face(16);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 31 && t >= 8 && t <= 12) lunge(0.3f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
