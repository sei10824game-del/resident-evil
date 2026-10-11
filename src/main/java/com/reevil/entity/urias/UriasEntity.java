package com.reevil.entity.urias;

import com.reevil.entity.ustanak.UstanakEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Urias (RE8): the clawed war-machine - the Ustanak claw set on a bulkier body. */
public class UriasEntity extends UstanakEntity {
    public UriasEntity(EntityType<UriasEntity> type, Level level) { super(unchecked(type), level); xpReward = 280; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<UstanakEntity> unchecked(EntityType<UriasEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 600.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 14.0).add(Attributes.FOLLOW_RANGE, 52).add(Attributes.KNOCKBACK_RESISTANCE, 0.95).add(Attributes.ARMOR, 8.0);
    }
}
