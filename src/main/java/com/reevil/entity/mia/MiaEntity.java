package com.reevil.entity.mia;

import com.reevil.entity.jill.JillEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 Mia: hostile when infected by Eveline. */
public class MiaEntity extends JillEntity {
    public MiaEntity(EntityType<MiaEntity> type, Level level) { super(unchecked(type), level); xpReward = 40; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<JillEntity> unchecked(EntityType<MiaEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 120.0).add(Attributes.MOVEMENT_SPEED, 0.304).add(Attributes.ATTACK_DAMAGE, 6.4).add(Attributes.FOLLOW_RANGE, 44).add(Attributes.KNOCKBACK_RESISTANCE, 0.7).add(Attributes.ARMOR, 5.0);
    }
}
