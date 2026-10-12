package com.reevil.entity.marguerite;

import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Marguerite Baker (RE7): frail-looking woman with demonic eyes who controls insects; mutated, she grows huge limbs and an insect-hive abdomen. */
public class MargueriteEntity extends MrXEntity {
    public MargueriteEntity(EntityType<MargueriteEntity> type, Level level) { super(unchecked(type), level); xpReward = 120; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<MargueriteEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 100.0).add(Attributes.MOVEMENT_SPEED, 0.32).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
