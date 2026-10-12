package com.reevil.entity.re3rnemesis;

import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE3 Remake Nemesis: skinless bulk, no coat, stitched mouth. */
public class Re3rNemesisEntity extends NemesisEntity {
    public Re3rNemesisEntity(EntityType<Re3rNemesisEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<NemesisEntity> unchecked(EntityType<Re3rNemesisEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 480.0).add(Attributes.MOVEMENT_SPEED, 0.270).add(Attributes.ATTACK_DAMAGE, 11.5).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 0.9).add(Attributes.ARMOR, 10.0);
    }
}
