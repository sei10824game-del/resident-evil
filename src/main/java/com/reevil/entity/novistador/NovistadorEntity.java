package com.reevil.entity.novistador;

import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Novistador (RE4): the cloaked insect. Giant Moth flight and attacks (scale burst, ramming dive, proboscis jab, wing buffet), but invisible except while it attacks or is hit. */
public class NovistadorEntity extends GiantMothEntity {
    public NovistadorEntity(EntityType<NovistadorEntity> type, Level level) { super(unchecked(type), level); xpReward = 20; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GiantMothEntity> unchecked(EntityType<NovistadorEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FLYING_SPEED, 0.65).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 30.0);
    }

    /** Invisible except for the instant it strikes or is struck. */
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        setInvisible(isAlive() && !isMoving() && hurtTime <= 0);
    }
}
