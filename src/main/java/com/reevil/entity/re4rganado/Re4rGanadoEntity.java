package com.reevil.entity.re4rganado;

import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake villager: bandana, scruffy beard, wicker pack. */
public class Re4rGanadoEntity extends GanadoEntity {
    public Re4rGanadoEntity(EntityType<Re4rGanadoEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<Re4rGanadoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 33.6).add(Attributes.MOVEMENT_SPEED, 0.259).add(Attributes.ATTACK_DAMAGE, 4.6).add(Attributes.FOLLOW_RANGE, 26);
    }
}
