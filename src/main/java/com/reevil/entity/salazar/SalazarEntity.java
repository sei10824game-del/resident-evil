package com.reevil.entity.salazar;

import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Ramon Salazar (RE4, final form): the immense rooted mass - Plant 42 behaviour (tentacle whip, snare-and-devour, spore cloud, ground slam, weak base, withering second phase) on a larger body. */
public class SalazarEntity extends Plant42Entity {
    public SalazarEntity(EntityType<SalazarEntity> type, Level level) { super(unchecked(type), level); xpReward = 250; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<Plant42Entity> unchecked(EntityType<SalazarEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 360.0).add(Attributes.MOVEMENT_SPEED, 0.0).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 5.0);
    }
}
