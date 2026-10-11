package com.reevil.entity.gravedigger;

import com.reevil.entity.yawn.YawnEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Grave Digger (RE3): the giant burrowing worm. Built on the Yawn serpent behaviour (bite, constrict, tail whip, rear-up crash, acid spit, burrow retreat, enraged second form), with a heavier, slower body. */
public class GraveDiggerEntity extends YawnEntity {
    public GraveDiggerEntity(EntityType<GraveDiggerEntity> type, Level level) {
        super(unchecked(type), level);
        xpReward = 80;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<YawnEntity> unchecked(EntityType<GraveDiggerEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 260.0)
            .add(Attributes.MOVEMENT_SPEED, 0.24)
            .add(Attributes.ATTACK_DAMAGE, 9.0)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.85)
            .add(Attributes.ARMOR, 5.0);
    }
}
