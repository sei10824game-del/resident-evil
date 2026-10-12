package com.reevil.entity.soldat;

import com.reevil.entity.mrx.MrXEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Soldat (RE8): cybernetic Cadou soldier with drill arms - Eins, Zwei, Jet (jetpack dash) and Panzer variants; the chest exhaust is the weak spot. */
public class SoldatEntity extends MrXEntity {
    public SoldatEntity(EntityType<SoldatEntity> type, Level level) { super(unchecked(type), level); xpReward = 40; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<SoldatEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 70.0).add(Attributes.MOVEMENT_SPEED, 0.26).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 36).add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
    /** one-time random variant, kept in getMut() (synced to the client) */
    @Override
    public void aiStep() {
        if (!level().isClientSide && getPersistentStage() == 0) { setPersistentStage(1); initVariant(); }
        super.aiStep();
    }
    /** Eins / Zwei / Jet / Panzer stored in getMut(). */
    private void initVariant() {
        int r = random.nextInt(10); setMut(r < 5 ? 0 : r < 8 ? 1 : r < 9 ? 2 : 3);
        double hp = switch (getMut()) { case 0 -> 70; case 1 -> 90; case 2 -> 80; default -> 260; };
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(hp); setHealth((float) hp);
    }
}
