package com.reevil.entity.re3rnemesis;

import com.reevil.entity.nemesis.NemesisEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE3 Remake Nemesis: skinless bulk, no coat, stitched mouth. */
public class Re3rNemesisEntity extends NemesisEntity {
    public Re3rNemesisEntity(EntityType<Re3rNemesisEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<NemesisEntity> unchecked(EntityType<Re3rNemesisEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 480.0).add(Attributes.MOVEMENT_SPEED, 0.270).add(Attributes.ATTACK_DAMAGE, 11.5).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 0.9).add(Attributes.ARMOR, 10.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "crushing_grab_slam", 50, new int[]{18, 34}, 0f, 2.8f, 60, 3).dmg(15f).kb(1.2).face(40);
    private static final AttackMove X31 = new AttackMove(31, "barrage_swing", 40, new int[]{14, 26}, 0f, 3.4f, 40, 4).dmg(11f).kb(1.8).face(30);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 10 && t <= 17) lunge(0.2f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        if (!(m.id == 31)) super.onMoveHit(m, hitIndex, target);
        if (m.id == 30 && hitIndex == 1) com.reevil.entity.RemakeMoves.launch(target, -0.6f);
        if (m.id == 31 && hitIndex == 1) com.reevil.entity.RemakeMoves.aoe(this, 3.2f, 6f, 1.4);
    }
}
