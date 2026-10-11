package com.reevil.entity.dellago;

import com.reevil.entity.neptune.NeptuneEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Del Lago (RE4): the colossal lake monster. Neptune's water-bound ambush behaviour (lunge bite, breach-and-snap, death roll, tail slap, circling feints, retreat to heal) on a far bigger, tougher body. */
public class DelLagoEntity extends NeptuneEntity {
    public DelLagoEntity(EntityType<DelLagoEntity> type, Level level) { super(unchecked(type), level); xpReward = 200; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<NeptuneEntity> unchecked(EntityType<DelLagoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 500.0)
            .add(Attributes.MOVEMENT_SPEED, 0.45)
            .add(Attributes.ATTACK_DAMAGE, 14.0)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
            .add(Attributes.ARMOR, 6.0);
    }
}
