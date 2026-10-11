package com.reevil.entity.ironmaiden;

import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Iron Maiden (RE4): the spike-studded Regenerador. Regenerates, and tears the flesh of anyone who strikes it in melee. */
public class IronMaidenEntity extends ZombieEntity {
    public IronMaidenEntity(EntityType<IronMaidenEntity> type, Level level) { super(unchecked(type), level); xpReward = 20; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<IronMaidenEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 160.0).add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.ATTACK_DAMAGE, 9.0).add(Attributes.FOLLOW_RANGE, 22.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 6.0);
    }

    /** barbed body: anyone who hits it in melee is torn for 2 damage. */
    @Override
    public boolean hurt(DamageSource src, float amount) {
        boolean r = super.hurt(src, amount);
        if (r && src.getDirectEntity() instanceof LivingEntity a && distanceToSqr(a) < 9.0) a.hurt(damageSources().thorns(this), 2f);
        return r;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isAlive() && tickCount % 20 == 0 && getHealth() < getMaxHealth()) heal(2f);
    }

    @Override protected boolean revives() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
