package com.reevil.entity.armmolded;

import com.reevil.entity.ganado.GanadoEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 Molded with one giant arm. */
public class ArmMoldedEntity extends GanadoEntity {
    public ArmMoldedEntity(EntityType<ArmMoldedEntity> type, Level level) { super(unchecked(type), level); xpReward = 25; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<ArmMoldedEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 61.6).add(Attributes.MOVEMENT_SPEED, 0.204).add(Attributes.ATTACK_DAMAGE, 6.4).add(Attributes.FOLLOW_RANGE, 26);
    }

    @Override protected boolean canBurst() { return false; }
}
