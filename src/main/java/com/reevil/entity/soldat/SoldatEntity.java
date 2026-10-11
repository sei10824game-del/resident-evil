package com.reevil.entity.soldat;

import com.reevil.entity.krauser.KrauserEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Soldat (RE8): the masked rifleman - Krauser fire-and-melee behaviour (long-range shots, knife lunges). */
public class SoldatEntity extends KrauserEntity {
    public SoldatEntity(EntityType<SoldatEntity> type, Level level) { super(unchecked(type), level); xpReward = 30; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<KrauserEntity> unchecked(EntityType<SoldatEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 60.0).add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 0.3).add(Attributes.ARMOR, 4.0);
    }
}
