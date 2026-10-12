package com.reevil.entity.re4relgigante;

import com.reevil.entity.elgigante.ElGiganteEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake El Gigante: shaggy hair and beard, back tumour mass, club. */
public class Re4rElGiganteEntity extends ElGiganteEntity {
    public Re4rElGiganteEntity(EntityType<Re4rElGiganteEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ElGiganteEntity> unchecked(EntityType<Re4rElGiganteEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 504.0).add(Attributes.MOVEMENT_SPEED, 0.216).add(Attributes.ATTACK_DAMAGE, 14.9).add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 6.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "club_sweep", 44, new int[]{24}, 0f, 6.0f, 50, 4).dmg(16f).kb(2.6).face(28);
    private static final AttackMove X31 = new AttackMove(31, "ground_quake", 44, new int[]{24}, 0f, 6.0f, 80, 3).dmg(12f).kb(1.8).face(20);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {

        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        if (!(m.id == 30 || m.id == 31)) super.onMoveHit(m, hitIndex, target);
        if (m.id == 30 && hitIndex == 0) com.reevil.entity.RemakeMoves.aoe(this, 4.5f, 9f, 1.6);
        if (m.id == 31 && hitIndex == 0) com.reevil.entity.RemakeMoves.aoe(this, 6f, 12f, 2.0);
    }
}
