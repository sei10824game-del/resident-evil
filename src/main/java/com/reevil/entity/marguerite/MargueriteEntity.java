package com.reevil.entity.marguerite;

import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Marguerite Baker (RE7): the insect matriarch - a large hovering swarm-lady with the Giant Moth dust cloud, ramming dive, jab and wing buffet. */
public class MargueriteEntity extends GiantMothEntity {
    public MargueriteEntity(EntityType<MargueriteEntity> type, Level level) { super(unchecked(type), level); xpReward = 100; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GiantMothEntity> unchecked(EntityType<MargueriteEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 90.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FLYING_SPEED, 0.65).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 32.0);
    }
}
