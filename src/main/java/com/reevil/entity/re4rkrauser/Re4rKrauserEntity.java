package com.reevil.entity.re4rkrauser;

import com.reevil.entity.krauser.KrauserEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Krauser: bandoliers, scarred face, blade arm. */
public class Re4rKrauserEntity extends KrauserEntity {
    public Re4rKrauserEntity(EntityType<Re4rKrauserEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<KrauserEntity> unchecked(EntityType<Re4rKrauserEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 240.0).add(Attributes.MOVEMENT_SPEED, 0.324).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 0.5).add(Attributes.ARMOR, 4.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "blade_flurry", 36, new int[]{10, 18, 26}, 0f, 3.0f, 36, 4).dmg(6.5f).kb(0.8).face(32);
    private static final AttackMove X31 = new AttackMove(31, "knife_lunge", 30, new int[]{14}, 2f, 6.2f, 44, 3).dmg(11f).kb(1.2).face(20).move();
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 6 && t <= 9) lunge(0.3f, 0f);
        if (m.id == 30 && t >= 14 && t <= 17) lunge(0.25f, 0f);
        if (m.id == 30 && t >= 22 && t <= 25) lunge(0.25f, 0f);
        if (m.id == 31 && t >= 6 && t <= 13) lunge(0.7f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
