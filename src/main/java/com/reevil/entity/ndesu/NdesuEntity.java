package com.reevil.entity.ndesu;

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

/** Ndesu (RE5): the hulking spore-carrier - a slow giant on the stalker moves, each blow dosing the victim with poison and nausea. */
public class NdesuEntity extends MrXEntity {
    public NdesuEntity(EntityType<NdesuEntity> type, Level level) { super(unchecked(type), level); xpReward = 90; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<NdesuEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 280.0).add(Attributes.MOVEMENT_SPEED, 0.2).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 1.0).add(Attributes.ARMOR, 6.0);
    }

    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (target != null && target.isAlive()) { target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0)); target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0)); }
    }
}
