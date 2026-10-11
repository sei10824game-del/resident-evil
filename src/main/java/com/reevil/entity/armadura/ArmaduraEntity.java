package com.reevil.entity.armadura;

import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Armadura (RE4): the haunted suit of armour. Slow zombie moves behind extreme armour that blunts everything but heavy hits. */
public class ArmaduraEntity extends ZombieEntity {
    public ArmaduraEntity(EntityType<ArmaduraEntity> type, Level level) { super(unchecked(type), level); xpReward = 25; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<ArmaduraEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 120.0).add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 22).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 14.0);
    }

    @Override protected boolean revives() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
