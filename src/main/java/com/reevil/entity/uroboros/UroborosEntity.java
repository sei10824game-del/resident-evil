package com.reevil.entity.uroboros;

import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Uroboros (RE5): the colossal tentacle mass - Plant 42 behaviour at an enormous scale. */
public class UroborosEntity extends Plant42Entity {
    public UroborosEntity(EntityType<UroborosEntity> type, Level level) { super(unchecked(type), level); xpReward = 300; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<Plant42Entity> unchecked(EntityType<UroborosEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 450.0).add(Attributes.MOVEMENT_SPEED, 0.0).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.FOLLOW_RANGE, 28).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 6.0);
    }
}
