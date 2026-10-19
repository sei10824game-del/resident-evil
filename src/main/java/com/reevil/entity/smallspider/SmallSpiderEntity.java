package com.reevil.entity.smallspider;

import com.reevil.entity.giantspider.GiantSpiderEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 small spider. */
public class SmallSpiderEntity extends GiantSpiderEntity {
    public SmallSpiderEntity(EntityType<SmallSpiderEntity> type, Level level) { super(unchecked(type), level); xpReward = 3; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GiantSpiderEntity> unchecked(EntityType<SmallSpiderEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 14.0).add(Attributes.MOVEMENT_SPEED, 0.324).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 0.6).add(Attributes.ARMOR, 4);
    }
}
