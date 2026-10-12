package com.reevil.entity.re1rhunter;

import com.reevil.entity.hunter.HunterEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** REmake Hunter: dorsal plates, bone claws. */
public class Re1rHunterEntity extends HunterEntity {
    public Re1rHunterEntity(EntityType<Re1rHunterEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<HunterEntity> unchecked(EntityType<Re1rHunterEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 72.0).add(Attributes.MOVEMENT_SPEED, 0.356).add(Attributes.ATTACK_DAMAGE, 6.9).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 0.4).add(Attributes.ARMOR, 4.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "pounce_rake", 34, new int[]{16, 22}, 2f, 6.5f, 50, 4).dmg(8f).kb(1.2).face(24).move();
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 8 && t <= 9) lunge(1.05f, 0.55f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
