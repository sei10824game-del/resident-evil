package com.reevil.entity.miranda;

import com.reevil.entity.saddler.SaddlerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Mother Miranda (RE8): the cloaked matriarch. Saddler behaviour (lashes, snares, tentacle barrage) with a sturdier, faster frame; mutates twice. */
public class MirandaEntity extends SaddlerEntity {
    public MirandaEntity(EntityType<MirandaEntity> type, Level level) { super(unchecked(type), level); xpReward = 300; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<SaddlerEntity> unchecked(EntityType<MirandaEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 420.0).add(Attributes.MOVEMENT_SPEED, 0.28).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.FOLLOW_RANGE, 52).add(Attributes.KNOCKBACK_RESISTANCE, 0.85).add(Attributes.ARMOR, 6.0);
    }
}
