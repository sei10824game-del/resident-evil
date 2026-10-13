package com.reevil.entity.re2rnemesis;

import com.reevil.entity.nemesis.NemesisEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Nemesis-T in the RE2 Remake roster: rocket launcher plus a machine gun. */
public class Re2rNemesisEntity extends NemesisEntity {
    public Re2rNemesisEntity(EntityType<Re2rNemesisEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<NemesisEntity> unchecked(EntityType<Re2rNemesisEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 440.0).add(Attributes.MOVEMENT_SPEED, 0.270).add(Attributes.ATTACK_DAMAGE, 11.5).add(Attributes.FOLLOW_RANGE, 64).add(Attributes.KNOCKBACK_RESISTANCE, 0.9).add(Attributes.ARMOR, 10.0);
    }
}
