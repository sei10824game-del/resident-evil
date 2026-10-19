package com.reevil.entity.moldedfang;

import com.reevil.entity.ganado.GanadoEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 Molded with a fang-filled mouth. */
public class MoldedFangEntity extends GanadoEntity {
    public MoldedFangEntity(EntityType<MoldedFangEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<MoldedFangEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.8).add(Attributes.MOVEMENT_SPEED, 0.264).add(Attributes.ATTACK_DAMAGE, 4.6).add(Attributes.FOLLOW_RANGE, 26);
    }
}
