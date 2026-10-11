package com.reevil.entity.gnezdo;

import com.reevil.entity.larva.LarvaEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Gnezdo (RE6): the C-virus grub - Larva bites, rearing lunges and acid spit with a tougher body. */
public class GnezdoEntity extends LarvaEntity {
    public GnezdoEntity(EntityType<GnezdoEntity> type, Level level) { super(unchecked(type), level); xpReward = 20; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<LarvaEntity> unchecked(EntityType<GnezdoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 24);
    }
}
