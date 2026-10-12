package com.reevil.entity.re4rsalazar;

import com.reevil.entity.salazar.SalazarEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Salazar: robed lord with a crown. */
public class Re4rSalazarEntity extends SalazarEntity {
    public Re4rSalazarEntity(EntityType<Re4rSalazarEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<SalazarEntity> unchecked(EntityType<Re4rSalazarEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 432.0).add(Attributes.MOVEMENT_SPEED, 0.000).add(Attributes.ATTACK_DAMAGE, 9.2).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 5.0);
    }
}
