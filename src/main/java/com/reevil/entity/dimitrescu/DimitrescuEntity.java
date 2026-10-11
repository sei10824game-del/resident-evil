package com.reevil.entity.dimitrescu;

import com.reevil.entity.ustanak.UstanakEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Lady Dimitrescu (RE8): the towering matriarch with bladed claws - the clawed stalker set (punch, stab, sweep, ground pound, claw grab) on a very tall, very tough body. */
public class DimitrescuEntity extends UstanakEntity {
    public DimitrescuEntity(EntityType<DimitrescuEntity> type, Level level) { super(unchecked(type), level); xpReward = 300; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<UstanakEntity> unchecked(EntityType<DimitrescuEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 700.0).add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.ATTACK_DAMAGE, 13.0).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.95).add(Attributes.ARMOR, 6.0);
    }
}
