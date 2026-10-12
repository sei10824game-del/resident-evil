package com.reevil.entity.adjule;

import com.reevil.entity.colmillos.ColmillosEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Adjule (RE5): a dog parasitised by Type-2 Plagas; its head splits open so the parasite can devour prey. */
public class AdjuleEntity extends ColmillosEntity {
    public AdjuleEntity(EntityType<AdjuleEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ColmillosEntity> unchecked(EntityType<AdjuleEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 70.0).add(Attributes.MOVEMENT_SPEED, 0.34).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 30).add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }
}
