package com.reevil.entity.re4rverdugo;

import com.reevil.entity.verdugo.VerdugoEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Verdugo: hooded exposed-bone frame. */
public class Re4rVerdugoEntity extends VerdugoEntity {
    public Re4rVerdugoEntity(EntityType<Re4rVerdugoEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<VerdugoEntity> unchecked(EntityType<Re4rVerdugoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 276.0).add(Attributes.MOVEMENT_SPEED, 0.367).add(Attributes.ATTACK_DAMAGE, 11.5).add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 6.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "blade_cross_slash", 34, new int[]{12, 22}, 0f, 3.4f, 36, 4).dmg(9f).kb(1.0).face(26);
    private static final AttackMove X31 = new AttackMove(31, "phantom_dash", 34, new int[]{16}, 3f, 8.5f, 60, 3).dmg(10f).kb(1.6).face(20).move();
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 7 && t <= 11) lunge(0.3f, 0f);
        if (m.id == 30 && t >= 17 && t <= 21) lunge(0.3f, 0f);
        if (m.id == 31 && t >= 6 && t <= 15) lunge(0.7f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
