package com.reevil.entity.bugsmall;

import com.reevil.entity.giantmoth.GiantMothEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 small flying insect. */
public class BugSmallEntity extends GiantMothEntity {
    public BugSmallEntity(EntityType<BugSmallEntity> type, Level level) { super(unchecked(type), level); xpReward = 1; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GiantMothEntity> unchecked(EntityType<BugSmallEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 3.6).add(Attributes.MOVEMENT_SPEED, 0.325).add(Attributes.ATTACK_DAMAGE, 1.8).add(Attributes.FOLLOW_RANGE, 28).add(Attributes.FLYING_SPEED, 0.5);
    }
}
