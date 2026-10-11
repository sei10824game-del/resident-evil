package com.reevil.entity.napad;

import com.reevil.entity.reaper.ReaperEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Napad (RE6): the scuttling insectoid - a fast, light Reaper-type crawler with the tongue lash / snare / claw / pounce set. */
public class NapadEntity extends ReaperEntity {
    public NapadEntity(EntityType<NapadEntity> type, Level level) { super(unchecked(type), level); xpReward = 30; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ReaperEntity> unchecked(EntityType<NapadEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 60.0).add(Attributes.MOVEMENT_SPEED, 0.42).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 30.0);
    }
}
