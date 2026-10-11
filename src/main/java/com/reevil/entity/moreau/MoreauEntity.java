package com.reevil.entity.moreau;

import com.reevil.entity.dellago.DelLagoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Salvatore Moreau (RE8): the reservoir fish-horror - Neptune ambush, breach, death-roll and tail-slap behaviour. */
public class MoreauEntity extends DelLagoEntity {
    public MoreauEntity(EntityType<MoreauEntity> type, Level level) { super(unchecked(type), level); xpReward = 200; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<DelLagoEntity> unchecked(EntityType<MoreauEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 320.0).add(Attributes.MOVEMENT_SPEED, 1.1).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.FOLLOW_RANGE, 40).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 4.0);
    }
}
