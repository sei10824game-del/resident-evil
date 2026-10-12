package com.reevil.entity.urias;

import com.reevil.entity.elgigante.ElGiganteEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Urias (RE8): troll-like Lycan with a war-hammer, enormous jumps and a habit of hurling columns. */
public class UriasEntity extends ElGiganteEntity {
    public UriasEntity(EntityType<UriasEntity> type, Level level) { super(unchecked(type), level); xpReward = 280; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ElGiganteEntity> unchecked(EntityType<UriasEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 600.0).add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.ATTACK_DAMAGE, 14.0).add(Attributes.FOLLOW_RANGE, 52).add(Attributes.KNOCKBACK_RESISTANCE, 0.95).add(Attributes.ARMOR, 4.0);
    }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
