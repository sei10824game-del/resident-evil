package com.reevil.entity.re4relgigante;

import com.reevil.entity.elgigante.ElGiganteEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake El Gigante: shaggy hair and beard, back tumour mass, club. */
public class Re4rElGiganteEntity extends ElGiganteEntity {
    public Re4rElGiganteEntity(EntityType<Re4rElGiganteEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ElGiganteEntity> unchecked(EntityType<Re4rElGiganteEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 504.0).add(Attributes.MOVEMENT_SPEED, 0.216).add(Attributes.ATTACK_DAMAGE, 14.9).add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 6.0);
    }
}
