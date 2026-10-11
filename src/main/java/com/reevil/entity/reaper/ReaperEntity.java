package com.reevil.entity.reaper;

import com.reevil.entity.huntergamma.HunterGammaEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Reaper (RE5): the lanky, fast, long-tongued stalker - a sighted tongue-lash / snare / claw / pounce crawler on the Hunter Gamma behaviour. */
public class ReaperEntity extends HunterGammaEntity {
    public ReaperEntity(EntityType<ReaperEntity> type, Level level) { super(unchecked(type), level); xpReward = 40; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<HunterGammaEntity> unchecked(EntityType<ReaperEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 110.0).add(Attributes.MOVEMENT_SPEED, 0.36).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 30.0).add(Attributes.ARMOR, 4.0);
    }
}
