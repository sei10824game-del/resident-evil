package com.reevil.entity.executionermajini;

import com.reevil.entity.elgigante.ElGiganteEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Executioner Majini (RE5): the towering masked axeman - El Gigante reach, throws and stomps, weak back and all. */
public class ExecutionerMajiniEntity extends ElGiganteEntity {
    public ExecutionerMajiniEntity(EntityType<ExecutionerMajiniEntity> type, Level level) { super(unchecked(type), level); xpReward = 100; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ElGiganteEntity> unchecked(EntityType<ExecutionerMajiniEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 330.0).add(Attributes.MOVEMENT_SPEED, 0.22).add(Attributes.ATTACK_DAMAGE, 13.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 6.0);
    }

    @Override public String getTextureSuffix() { return ""; }
}
