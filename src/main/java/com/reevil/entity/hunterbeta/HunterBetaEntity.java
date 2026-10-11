package com.reevil.entity.hunterbeta;

import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Hunter Beta (RE3 remake): the tougher, quicker, sandy-scaled Hunter - same full move set (leap slash, double slash, decapitation, pin, dodge-hops, vault, red-hot rage form). */
public class HunterBetaEntity extends HunterEntity {
    public HunterBetaEntity(EntityType<HunterBetaEntity> type, Level level) { super(unchecked(type), level); xpReward = 20; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<HunterEntity> unchecked(EntityType<HunterBetaEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 90.0)
            .add(Attributes.MOVEMENT_SPEED, 0.34)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
            .add(Attributes.ARMOR, 4.0);
    }

    @Override public String getTextureSuffix() { return getForm() == 1 ? "_red" : ""; }
}
