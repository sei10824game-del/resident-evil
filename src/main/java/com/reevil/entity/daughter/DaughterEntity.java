package com.reevil.entity.daughter;

import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Dimitrescu daughters (RE8: Bela/Cassandra/Daniela): human + blowfly; swarm form shrugs off damage and lunges at the neck; in the cold they fall back to a sickle frenzy. */
public class DaughterEntity extends MrXEntity {
    public DaughterEntity(EntityType<DaughterEntity> type, Level level) { super(unchecked(type), level); xpReward = 80; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<DaughterEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 90.0).add(Attributes.MOVEMENT_SPEED, 0.36).add(Attributes.ATTACK_DAMAGE, 7.0).add(Attributes.FOLLOW_RANGE, 32).add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return new String[]{"_bela", "_cassandra", "_daniela"}[Math.max(0, Math.min(2, getMut()))]; }
    /** one-time random variant, kept in getMut() (synced to the client) */
    @Override
    public void aiStep() {
        if (!level().isClientSide && getPersistentStage() == 0) { setPersistentStage(1); initVariant(); }
        super.aiStep();
    }
    private void initVariant() { setMut(random.nextInt(3)); }
}
