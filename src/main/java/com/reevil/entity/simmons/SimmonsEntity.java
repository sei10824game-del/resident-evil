package com.reevil.entity.simmons;

import com.reevil.entity.saddler.SaddlerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Derek Simmons (RE6): the mutating senator - Saddler behaviour with more health and a faster, heavier frame. */
public class SimmonsEntity extends SaddlerEntity {
    public SimmonsEntity(EntityType<SimmonsEntity> type, Level level) { super(unchecked(type), level); xpReward = 250; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<SaddlerEntity> unchecked(EntityType<SimmonsEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 380.0).add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.85).add(Attributes.ARMOR, 6.0);
    }
}
