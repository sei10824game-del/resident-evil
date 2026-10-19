package com.reevil.entity.bugadult;

import com.reevil.entity.larva.LarvaEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 adult cannibal bug. */
public class BugAdultEntity extends LarvaEntity {
    public BugAdultEntity(EntityType<BugAdultEntity> type, Level level) { super(unchecked(type), level); xpReward = 8; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<LarvaEntity> unchecked(EntityType<BugAdultEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 45.0).add(Attributes.MOVEMENT_SPEED, 0.264).add(Attributes.ATTACK_DAMAGE, 6.5).add(Attributes.FOLLOW_RANGE, 20);
    }
}
