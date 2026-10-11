package com.reevil.entity.lycan;

import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Lycan (RE8): the village werewolf. Hunter behaviour - great leaps, double slashes, decapitation chance, dodge-hops - in a lighter, faster, furred body. */
public class LycanEntity extends HunterEntity {
    public LycanEntity(EntityType<LycanEntity> type, Level level) { super(unchecked(type), level); xpReward = 18; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<HunterEntity> unchecked(EntityType<LycanEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0).add(Attributes.MOVEMENT_SPEED, 0.38).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 0.2).add(Attributes.ARMOR, 1.0);
    }

    @Override public String getTextureSuffix() { return ""; }
}
