package com.reevil.entity.re3rzombie;

import com.reevil.entity.zombie.ZombieEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE3 Remake zombie: torn civilian clothes, long hair. */
public class Re3rZombieEntity extends ZombieEntity {
    public Re3rZombieEntity(EntityType<Re3rZombieEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<Re3rZombieEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 28.8).add(Attributes.MOVEMENT_SPEED, 0.184).add(Attributes.ATTACK_DAMAGE, 3.4).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X32 = new AttackMove(32, "frenzied_flail", 40, new int[]{10, 18, 26}, 0f, 1.9f, 36, 3).dmg(2.8f).kb(0.5).face(30);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X32);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {

        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
