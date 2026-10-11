package com.reevil.entity.fatmolded;

import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Fat Molded (RE7): the bloated brute - slow zombie moves behind a mountain of mould and health; does not rise again. */
public class FatMoldedEntity extends ZombieEntity {
    public FatMoldedEntity(EntityType<FatMoldedEntity> type, Level level) { super(unchecked(type), level); xpReward = 35; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<FatMoldedEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 160.0).add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.ATTACK_DAMAGE, 9.0).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 0.7).add(Attributes.ARMOR, 2.0);
    }

    @Override protected boolean revives() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
