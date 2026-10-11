package com.reevil.entity.adjule;

import com.reevil.entity.mrx.MrXEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Adjule (RE5): the tall, mouth-chested mutant - stalker move set with a poisonous touch. */
public class AdjuleEntity extends MrXEntity {
    public AdjuleEntity(EntityType<AdjuleEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<AdjuleEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 160.0).add(Attributes.MOVEMENT_SPEED, 0.27).add(Attributes.ATTACK_DAMAGE, 9.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 0.6).add(Attributes.ARMOR, 4.0);
    }

    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (target != null && target.isAlive() && random.nextFloat() < 0.35f) target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0));
    }
}
