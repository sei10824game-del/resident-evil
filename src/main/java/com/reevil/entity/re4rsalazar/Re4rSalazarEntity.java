package com.reevil.entity.re4rsalazar;

import com.reevil.entity.salazar.SalazarEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Salazar: robed lord with a crown. */
public class Re4rSalazarEntity extends SalazarEntity {
    public Re4rSalazarEntity(EntityType<Re4rSalazarEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<SalazarEntity> unchecked(EntityType<Re4rSalazarEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 432.0).add(Attributes.MOVEMENT_SPEED, 0.000).add(Attributes.ATTACK_DAMAGE, 9.2).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 5.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "royal_crush", 40, new int[]{22}, 0f, 6.5f, 60, 4).dmg(14f).kb(1.6).face(24);
    private static final AttackMove X31 = new AttackMove(31, "sweeping_tendrils", 44, new int[]{14, 28}, 0f, 6.5f, 50, 3).dmg(8f).kb(1.4).face(30);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {

        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        if (!(m.id == 30 || m.id == 31)) super.onMoveHit(m, hitIndex, target);
        if (m.id == 30 && hitIndex == 0) com.reevil.entity.RemakeMoves.aoe(this, 4.5f, 8f, 1.4);
        if (m.id == 31 && hitIndex == 0) com.reevil.entity.RemakeMoves.aoe(this, 5.5f, 6f, 1.2);
        if (m.id == 31 && hitIndex == 1) com.reevil.entity.RemakeMoves.aoe(this, 5.5f, 6f, 1.2);
    }
}
