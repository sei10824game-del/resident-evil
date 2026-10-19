package com.reevil.entity.blademolded;

import com.reevil.entity.ganado.GanadoEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE7 Blade Molded: both arms are blades. */
public class BladeMoldedEntity extends GanadoEntity {
    public BladeMoldedEntity(EntityType<BladeMoldedEntity> type, Level level) { super(unchecked(type), level); xpReward = 18; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<BladeMoldedEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 42.0).add(Attributes.MOVEMENT_SPEED, 0.240).add(Attributes.ATTACK_DAMAGE, 5.2).add(Attributes.FOLLOW_RANGE, 26);
    }

    { setForm(2); }
    @Override protected boolean canBurst() { return false; }
}
