package com.reevil.entity.re1rzombie;

import com.reevil.entity.zombie.ZombieEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** REmake zombie: gaunt grey-green, ragged, exposed ribs. */
public class Re1rZombieEntity extends ZombieEntity {
    public Re1rZombieEntity(EntityType<Re1rZombieEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<Re1rZombieEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 28.8).add(Attributes.MOVEMENT_SPEED, 0.184).add(Attributes.ATTACK_DAMAGE, 3.4).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "lunge_grapple", 36, new int[]{18}, 0.4f, 3.2f, 40, 4).dmg(4.5f).kb(0.6).face(30);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 12 && t <= 17) lunge(0.32f, 0.0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
