package com.reevil.entity.colmillos;

import com.reevil.entity.cerberus.CerberusEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Colmillos (RE4): the infected wolf pack. Cerberus behaviour (leap bite, snap, shake-bite, howl that rallies the pack) with a heavier, faster body. */
public class ColmillosEntity extends CerberusEntity {
    public ColmillosEntity(EntityType<ColmillosEntity> type, Level level) { super(unchecked(type), level); xpReward = 20; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<CerberusEntity> unchecked(EntityType<ColmillosEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 22.0).add(Attributes.MOVEMENT_SPEED, 0.36).add(Attributes.ATTACK_DAMAGE, 4.0).add(Attributes.FOLLOW_RANGE, 28.0);
    }
}
