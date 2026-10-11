package com.reevil.entity.sturm;

import com.reevil.entity.ogroman.OgromanEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Sturm (RE8): the colossus - Ogroman reach and weight with a faster stride. */
public class SturmEntity extends OgromanEntity {
    public SturmEntity(EntityType<SturmEntity> type, Level level) { super(unchecked(type), level); xpReward = 300; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<OgromanEntity> unchecked(EntityType<SturmEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 620.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.ATTACK_DAMAGE, 14.0).add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 7.0);
    }
}
