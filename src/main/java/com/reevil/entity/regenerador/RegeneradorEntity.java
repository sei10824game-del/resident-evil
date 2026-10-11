package com.reevil.entity.regenerador;

import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Regenerador (RE4): the wheezing, constantly self-healing brute. Zombie moves with far more health, a fast regeneration, and no reviving - kill it quickly. */
public class RegeneradorEntity extends ZombieEntity {
    public RegeneradorEntity(EntityType<RegeneradorEntity> type, Level level) { super(unchecked(type), level); xpReward = 20; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<RegeneradorEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 110.0).add(Attributes.MOVEMENT_SPEED, 0.16).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 22.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    /** wheezes and knits back together: 2 HP per second (4 HP when left alone for 3 seconds). */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && tickCount % 20 == 0 && getHealth() < getMaxHealth()) heal(tickCount - getLastHurtByMobTimestamp() > 60 ? 4f : 1.5f);
    }

    @Override protected boolean revives() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
