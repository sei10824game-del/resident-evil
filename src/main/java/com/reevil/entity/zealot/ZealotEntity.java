package com.reevil.entity.zealot;

import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Zealot (RE4): hooded Los Illuminados cultist - Ganado melee (torch or bare hands), never bursts into a Plaga. */
public class ZealotEntity extends GanadoEntity {
    public ZealotEntity(EntityType<ZealotEntity> type, Level level) { super(unchecked(type), level); xpReward = 8; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<ZealotEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.ATTACK_DAMAGE, 5.0).add(Attributes.FOLLOW_RANGE, 28);
    }

    @Override protected int defaultWeapon() { return random.nextBoolean() ? W_TORCH : W_NONE; }
    @Override public boolean hasHat() { return false; }
    @Override protected boolean canBurst() { return false; }
}
