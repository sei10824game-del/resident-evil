package com.reevil.entity.chainsawmajini;

import com.reevil.entity.salvador.SalvadorEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Chainsaw Majini (RE5): the Salvador sweep / rush / beheading with a hooded head instead of a sack. */
public class ChainsawMajiniEntity extends SalvadorEntity {
    public ChainsawMajiniEntity(EntityType<ChainsawMajiniEntity> type, Level level) { super(unchecked(type), level); xpReward = 35; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<SalvadorEntity> unchecked(EntityType<ChainsawMajiniEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 80.0).add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 28).add(Attributes.KNOCKBACK_RESISTANCE, 0.5).add(Attributes.ARMOR, 3.0);
    }
}
