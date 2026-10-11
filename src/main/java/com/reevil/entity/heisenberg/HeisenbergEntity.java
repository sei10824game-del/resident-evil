package com.reevil.entity.heisenberg;

import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Karl Heisenberg (RE8): the factory lord - heavy swings, combos, throws and kicks on the stalker frame. */
public class HeisenbergEntity extends MrXEntity {
    public HeisenbergEntity(EntityType<HeisenbergEntity> type, Level level) { super(unchecked(type), level); xpReward = 250; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<HeisenbergEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 480.0).add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.9).add(Attributes.ARMOR, 6.0);
    }

    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
