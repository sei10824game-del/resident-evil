package com.reevil.entity.re4rregenerador;

import com.reevil.entity.regenerador.RegeneradorEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Regenerador: breath mask, chest maw. */
public class Re4rRegeneradorEntity extends RegeneradorEntity {
    public Re4rRegeneradorEntity(EntityType<Re4rRegeneradorEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<RegeneradorEntity> unchecked(EntityType<Re4rRegeneradorEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 132.0).add(Attributes.MOVEMENT_SPEED, 0.173).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 22).add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }
}
