package com.reevil.entity.quickmolded;

import com.reevil.entity.ganado.GanadoEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 Quick Molded: ripper-type, low HP, very fast blade arms. */
public class QuickMoldedEntity extends GanadoEntity {
    public QuickMoldedEntity(EntityType<QuickMoldedEntity> type, Level level) { super(unchecked(type), level); xpReward = 10; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<QuickMoldedEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 14.0).add(Attributes.MOVEMENT_SPEED, 0.408).add(Attributes.ATTACK_DAMAGE, 3.6).add(Attributes.FOLLOW_RANGE, 26);
    }

    { setForm(2); }
    @Override protected boolean canBurst() { return false; }
}
