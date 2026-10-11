package com.reevil.entity.daughter;

import com.reevil.entity.reaper.ReaperEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Dimitrescu daughter (RE8): the pale clawed hunters - sighted tongue / claw / pounce crawling attacks. */
public class DaughterEntity extends ReaperEntity {
    public DaughterEntity(EntityType<DaughterEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ReaperEntity> unchecked(EntityType<DaughterEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 80.0).add(Attributes.MOVEMENT_SPEED, 0.38).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 30).add(Attributes.KNOCKBACK_RESISTANCE, 0.1).add(Attributes.ARMOR, 2.0);
    }
}
