package com.reevil.entity.gnezdo;

import com.reevil.entity.mrx.MrXEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Gnezdo (RE6): a swarm of insects merged into a humanoid female figure; a Queen insect hides inside and is exposed once the swarm is torn apart. */
public class GnezdoEntity extends MrXEntity {
    public GnezdoEntity(EntityType<GnezdoEntity> type, Level level) { super(unchecked(type), level); xpReward = 40; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<GnezdoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 70.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 28);
    }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
