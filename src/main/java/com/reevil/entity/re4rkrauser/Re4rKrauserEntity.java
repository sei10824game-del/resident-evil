package com.reevil.entity.re4rkrauser;

import com.reevil.entity.krauser.KrauserEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Krauser: bandoliers, scarred face, blade arm. */
public class Re4rKrauserEntity extends KrauserEntity {
    public Re4rKrauserEntity(EntityType<Re4rKrauserEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<KrauserEntity> unchecked(EntityType<Re4rKrauserEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 240.0).add(Attributes.MOVEMENT_SPEED, 0.324).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 0.5).add(Attributes.ARMOR, 4.0);
    }
}
