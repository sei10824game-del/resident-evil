package com.reevil.entity.buglarge;

import com.reevil.entity.giantmoth.GiantMothEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 large flying insect. */
public class BugLargeEntity extends GiantMothEntity {
    public BugLargeEntity(EntityType<BugLargeEntity> type, Level level) { super(unchecked(type), level); xpReward = 8; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GiantMothEntity> unchecked(EntityType<BugLargeEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 32.4).add(Attributes.MOVEMENT_SPEED, 0.270).add(Attributes.ATTACK_DAMAGE, 6.9).add(Attributes.FOLLOW_RANGE, 28).add(Attributes.FLYING_SPEED, 0.5);
    }
}
