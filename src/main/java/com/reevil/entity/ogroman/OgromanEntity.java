package com.reevil.entity.ogroman;

import com.reevil.entity.elgigante.ElGiganteEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Ogroman (RE6): the mutated giant - El Gigante moves on a far bigger, tougher body. */
public class OgromanEntity extends ElGiganteEntity {
    public OgromanEntity(EntityType<OgromanEntity> type, Level level) { super(unchecked(type), level); xpReward = 250; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ElGiganteEntity> unchecked(EntityType<OgromanEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 480.0).add(Attributes.MOVEMENT_SPEED, 0.21).add(Attributes.ATTACK_DAMAGE, 14.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 7.0);
    }

    @Override public String getTextureSuffix() { return ""; }
}
