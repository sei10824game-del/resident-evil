package com.reevil.entity.re2rlicker;

import com.reevil.entity.licker.LickerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE2 Remake Licker: skinless bulk, back spikes, long claws. */
public class Re2rLickerEntity extends LickerEntity {
    public Re2rLickerEntity(EntityType<Re2rLickerEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<LickerEntity> unchecked(EntityType<Re2rLickerEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 57.2).add(Attributes.MOVEMENT_SPEED, 0.324).add(Attributes.ATTACK_DAMAGE, 5.8).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.ARMOR, 1.0);
    }
}
