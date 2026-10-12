package com.reevil.entity.re4rironmaiden;

import com.reevil.entity.ironmaiden.IronMaidenEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Iron Maiden: spiked cage on both sides. */
public class Re4rIronMaidenEntity extends IronMaidenEntity {
    public Re4rIronMaidenEntity(EntityType<Re4rIronMaidenEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<IronMaidenEntity> unchecked(EntityType<Re4rIronMaidenEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 192.0).add(Attributes.MOVEMENT_SPEED, 0.162).add(Attributes.ATTACK_DAMAGE, 10.3).add(Attributes.FOLLOW_RANGE, 22).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 6.0);
    }
}
