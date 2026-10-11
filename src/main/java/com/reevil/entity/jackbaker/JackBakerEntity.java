package com.reevil.entity.jackbaker;

import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Jack Baker (RE7): the regenerating, relentless patriarch - the stalker move set and door smashing with a hardy self-healing body. */
public class JackBakerEntity extends MrXEntity {
    public JackBakerEntity(EntityType<JackBakerEntity> type, Level level) { super(unchecked(type), level); xpReward = 150; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<JackBakerEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 340.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 11.0).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 4.0);
    }

    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    /** knits itself back together: 2 HP a second when left alone. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && tickCount % 20 == 0 && getHealth() < getMaxHealth()) heal(tickCount - getLastHurtByMobTimestamp() > 60 ? 3f : 1f);
    }
}
