package com.reevil.entity.palehead;

import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Pale Head (RE3 remake): bloated-skulled, slow, very tough zombie with heavy blows. Zombie behaviour (swipe, grab-bite, vomit, crawl) with far more health; it does not rise again. */
public class PaleHeadEntity extends ZombieEntity {
    public PaleHeadEntity(EntityType<PaleHeadEntity> type, Level level) {
        super(unchecked(type), level);
        xpReward = 15;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<ZombieEntity> unchecked(EntityType<PaleHeadEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 90.0)
            .add(Attributes.MOVEMENT_SPEED, 0.15)
            .add(Attributes.ATTACK_DAMAGE, 6.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
            .add(Attributes.ARMOR, 3.0);
    }

    @Override protected boolean revives() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
