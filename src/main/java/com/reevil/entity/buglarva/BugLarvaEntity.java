package com.reevil.entity.buglarva;

import com.reevil.entity.larva.LarvaEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 cannibal bug larva. */
public class BugLarvaEntity extends LarvaEntity {
    public BugLarvaEntity(EntityType<BugLarvaEntity> type, Level level) { super(unchecked(type), level); xpReward = 3; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<LarvaEntity> unchecked(EntityType<BugLarvaEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.238).add(Attributes.ATTACK_DAMAGE, 5.8).add(Attributes.FOLLOW_RANGE, 20);
    }
}
