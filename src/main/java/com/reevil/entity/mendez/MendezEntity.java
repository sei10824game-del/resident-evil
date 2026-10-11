package com.reevil.entity.mendez;

import com.reevil.entity.elgigante.ElGiganteEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Bitores Mendez (RE4): the village chief - a huge brute with El Gigante reach, throws and stomps. */
public class MendezEntity extends ElGiganteEntity {
    public MendezEntity(EntityType<MendezEntity> type, Level level) { super(unchecked(type), level); xpReward = 150; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ElGiganteEntity> unchecked(EntityType<MendezEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 300.0).add(Attributes.MOVEMENT_SPEED, 0.23).add(Attributes.ATTACK_DAMAGE, 11.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 5.0);
    }

    @Override public String getTextureSuffix() { return ""; }
}
