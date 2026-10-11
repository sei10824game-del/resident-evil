package com.reevil.entity.verdugo;

import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Verdugo (RE4): the nigh-unstoppable executioner - fast stalker with the Mr. X move set, weak to cold. */
public class VerdugoEntity extends MrXEntity {
    public VerdugoEntity(EntityType<VerdugoEntity> type, Level level) { super(unchecked(type), level); xpReward = 120; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<VerdugoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 230.0).add(Attributes.MOVEMENT_SPEED, 0.34).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 6.0);
    }

    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    /** the executioner is brittle against cold: freezing damage and snowballs hit 4x. */
    @Override
    public boolean hurt(DamageSource src, float amount) {
        if (src.is(DamageTypeTags.IS_FREEZING) || src.getDirectEntity() instanceof Snowball) amount = Math.max(amount, 1f) * 4f;
        return super.hurt(src, amount);
    }
}
