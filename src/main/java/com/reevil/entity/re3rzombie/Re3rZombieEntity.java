package com.reevil.entity.re3rzombie;

import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE3 Remake zombie: torn civilian clothes, long hair. */
public class Re3rZombieEntity extends ZombieEntity {
    public Re3rZombieEntity(EntityType<Re3rZombieEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<Re3rZombieEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 28.8).add(Attributes.MOVEMENT_SPEED, 0.184).add(Attributes.ATTACK_DAMAGE, 3.4).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }
}
