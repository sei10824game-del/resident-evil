package com.reevil.entity.majini;

import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Majini (RE5): the African-village infected. Ganado behaviour - random tool, grab-and-choke, and the Plaga (Cephalo) bursting from the neck into whip / scythe / spider forms. */
public class MajiniEntity extends GanadoEntity {
    public MajiniEntity(EntityType<MajiniEntity> type, Level level) { super(unchecked(type), level); xpReward = 8; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<MajiniEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 34.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.ATTACK_DAMAGE, 5.0).add(Attributes.FOLLOW_RANGE, 28);
    }
}
