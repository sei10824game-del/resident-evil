package com.reevil.entity.eveline;

import com.reevil.entity.saddler.SaddlerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Eveline (RE7): the mould-queen. Saddler behaviour - lashes, snares, the all-round tentacle barrage - mutating as she is wounded. */
public class EvelineEntity extends SaddlerEntity {
    public EvelineEntity(EntityType<EvelineEntity> type, Level level) { super(unchecked(type), level); xpReward = 250; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<SaddlerEntity> unchecked(EntityType<EvelineEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 360.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.ATTACK_DAMAGE, 9.0).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 5.0);
    }
}
