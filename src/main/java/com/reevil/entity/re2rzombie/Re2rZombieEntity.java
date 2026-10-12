package com.reevil.entity.re2rzombie;

import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE2 Remake zombie: RPD cap and vest, torn flesh. */
public class Re2rZombieEntity extends ZombieEntity {
    public Re2rZombieEntity(EntityType<Re2rZombieEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<Re2rZombieEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 28.8).add(Attributes.MOVEMENT_SPEED, 0.184).add(Attributes.ATTACK_DAMAGE, 3.4).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }
}
