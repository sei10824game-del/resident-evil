package com.reevil.entity.re4rverdugo;

import com.reevil.entity.verdugo.VerdugoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Verdugo: hooded exposed-bone frame. */
public class Re4rVerdugoEntity extends VerdugoEntity {
    public Re4rVerdugoEntity(EntityType<Re4rVerdugoEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<VerdugoEntity> unchecked(EntityType<Re4rVerdugoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 276.0).add(Attributes.MOVEMENT_SPEED, 0.367).add(Attributes.ATTACK_DAMAGE, 11.5).add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 6.0);
    }
}
