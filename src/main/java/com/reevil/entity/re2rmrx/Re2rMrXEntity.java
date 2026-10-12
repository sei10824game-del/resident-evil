package com.reevil.entity.re2rmrx;

import com.reevil.entity.mrx.MrXEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE2 Remake Tyrant T-103: long leather coat, wide fedora, scarred face. */
public class Re2rMrXEntity extends MrXEntity {
    public Re2rMrXEntity(EntityType<Re2rMrXEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<Re2rMrXEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 288.0).add(Attributes.MOVEMENT_SPEED, 0.248).add(Attributes.ATTACK_DAMAGE, 10.3).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.85).add(Attributes.ARMOR, 8.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "door_smash_uppercut", 34, new int[]{16}, 0f, 3.4f, 40, 4).dmg(14f).kb(1.0).face(22);
    private static final AttackMove X31 = new AttackMove(31, "shoulder_charge", 42, new int[]{22}, 3f, 9f, 70, 3).dmg(12f).kb(2.4).face(18).move();
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 10 && t <= 15) lunge(0.32f, 0f);
        if (m.id == 31 && t >= 8 && t <= 22) lunge(0.42f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (m.id == 30 && hitIndex == 0) com.reevil.entity.RemakeMoves.launch(target, 0.85f);
        if (m.id == 31 && hitIndex == 0) com.reevil.entity.RemakeMoves.launch(target, 0.45f);
    }
}
