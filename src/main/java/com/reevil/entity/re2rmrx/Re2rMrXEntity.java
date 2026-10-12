package com.reevil.entity.re2rmrx;

import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE2 Remake Tyrant T-103: long leather coat, wide fedora, scarred face. */
public class Re2rMrXEntity extends MrXEntity {
    public Re2rMrXEntity(EntityType<Re2rMrXEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<Re2rMrXEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 288.0).add(Attributes.MOVEMENT_SPEED, 0.248).add(Attributes.ATTACK_DAMAGE, 10.3).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.85).add(Attributes.ARMOR, 8.0);
    }
}
