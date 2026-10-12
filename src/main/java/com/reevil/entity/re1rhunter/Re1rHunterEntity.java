package com.reevil.entity.re1rhunter;

import com.reevil.entity.hunter.HunterEntity;
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
}
