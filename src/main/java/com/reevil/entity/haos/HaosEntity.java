package com.reevil.entity.haos;

import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Haos (RE6): the shifting mass - Plant 42 behaviour (tentacle whip, snare-devour, spores, slam, weak base, withering phase) at a large scale. */
public class HaosEntity extends Plant42Entity {
    public HaosEntity(EntityType<HaosEntity> type, Level level) { super(unchecked(type), level); xpReward = 250; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<Plant42Entity> unchecked(EntityType<HaosEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 420.0).add(Attributes.MOVEMENT_SPEED, 0.0).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.FOLLOW_RANGE, 28).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 6.0);
    }
}
