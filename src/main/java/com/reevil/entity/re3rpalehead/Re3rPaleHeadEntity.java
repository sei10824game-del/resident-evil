package com.reevil.entity.re3rpalehead;

import com.reevil.entity.palehead.PaleHeadEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE3 Remake Pale Head: gaunt, domed head, bone spines. */
public class Re3rPaleHeadEntity extends PaleHeadEntity {
    public Re3rPaleHeadEntity(EntityType<Re3rPaleHeadEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<PaleHeadEntity> unchecked(EntityType<Re3rPaleHeadEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 108.0).add(Attributes.MOVEMENT_SPEED, 0.162).add(Attributes.ATTACK_DAMAGE, 6.9).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 0.7).add(Attributes.ARMOR, 3.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "rake_down", 36, new int[]{16}, 0.5f, 3.6f, 34, 4).dmg(6.5f).kb(0.8).face(22);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 10 && t <= 15) lunge(0.14f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
