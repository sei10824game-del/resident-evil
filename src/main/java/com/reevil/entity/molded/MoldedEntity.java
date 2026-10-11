package com.reevil.entity.molded;

import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Molded (RE7): the black-goo humanoid. Ganado grabs and strikes, and its body erupts into tendril, blade-arm or many-legged forms once wounded. */
public class MoldedEntity extends GanadoEntity {
    public MoldedEntity(EntityType<MoldedEntity> type, Level level) { super(unchecked(type), level); xpReward = 12; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<MoldedEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 42.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 30);
    }

    @Override protected int defaultWeapon() { return W_NONE; }
    @Override public boolean hasHat() { return false; }
    /** the mould is unstable: it can erupt into whip, blade-arm or spider forms soon after being hurt. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && getForm() == 0 && getHealth() < getMaxHealth() * 0.8f && random.nextInt(80) == 0) triggerBurst();
    }
}
