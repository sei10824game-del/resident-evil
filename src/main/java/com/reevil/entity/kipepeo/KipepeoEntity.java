package com.reevil.entity.kipepeo;

import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Kipepeo (RE5): the hovering poison-dust moth - Giant Moth behaviour. */
public class KipepeoEntity extends GiantMothEntity {
    public KipepeoEntity(EntityType<KipepeoEntity> type, Level level) { super(unchecked(type), level); xpReward = 15; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GiantMothEntity> unchecked(EntityType<KipepeoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 38.0).add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.FLYING_SPEED, 0.6).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 30.0);
    }
}
