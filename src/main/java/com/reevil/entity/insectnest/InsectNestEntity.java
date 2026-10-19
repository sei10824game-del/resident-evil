package com.reevil.entity.insectnest;

import com.reevil.entity.ivy.IvyEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 insect nest: releases bugs until destroyed. */
public class InsectNestEntity extends IvyEntity {
    public InsectNestEntity(EntityType<InsectNestEntity> type, Level level) { super(unchecked(type), level); xpReward = 20; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<IvyEntity> unchecked(EntityType<InsectNestEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 63.0).add(Attributes.MOVEMENT_SPEED, 0.000).add(Attributes.ATTACK_DAMAGE, 5.8).add(Attributes.FOLLOW_RANGE, 12).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 1.0);
    }
}
