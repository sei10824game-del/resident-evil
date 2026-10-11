package com.reevil.entity.draindeimos;

import com.reevil.entity.AttackMove;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Drain Deimos (RE3): the blood-drinking flying insect. Moth behaviour (spore cloud, diving ram, buffet) but its proboscis jab drains: it heals by the damage dealt and saps the victim with Weakness. */
public class DrainDeimosEntity extends GiantMothEntity {
    public DrainDeimosEntity(EntityType<DrainDeimosEntity> type, Level level) {
        super(unchecked(type), level);
        xpReward = 20;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GiantMothEntity> unchecked(EntityType<DrainDeimosEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 44.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28)
            .add(Attributes.FLYING_SPEED, 0.6)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.FOLLOW_RANGE, 30.0);
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        float before = target != null ? target.getHealth() : 0;
        super.onMoveHit(m, hitIndex, target);
        if (m.id == STING && target != null && target.getHealth() < before) {
            heal((before - target.getHealth()) * 1.5f);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
        }
    }
}
