package com.reevil.entity.jill;

import com.reevil.entity.wesker.WeskerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Jill Valentine (RE5, brainwashed): Wesker-style fast fighter - flurries, dash strikes, snap kicks and a throw - lighter and a little slower. */
public class JillEntity extends WeskerEntity {
    public JillEntity(EntityType<JillEntity> type, Level level) { super(unchecked(type), level); xpReward = 150; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<WeskerEntity> unchecked(EntityType<JillEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 240.0).add(Attributes.MOVEMENT_SPEED, 0.38).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 44).add(Attributes.KNOCKBACK_RESISTANCE, 0.7).add(Attributes.ARMOR, 5.0);
    }
}
