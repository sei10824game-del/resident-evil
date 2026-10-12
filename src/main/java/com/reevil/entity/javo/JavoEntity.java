package com.reevil.entity.javo;

import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Javo (RE6): the C-virus soldier. Ganado moves and grabs, but faster, and its limbs mutate (whip / scythe / spider) soon after it is hurt. */
public class JavoEntity extends GanadoEntity {
    public JavoEntity(EntityType<JavoEntity> type, Level level) { super(unchecked(type), level); xpReward = 15; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<GanadoEntity> unchecked(EntityType<JavoEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 30);
    }

    /** J'avo mutate by themselves (CanonMoves.multiMutate): arm / leg / head / torso slots, see CANON_NOTES.md */
    @Override public boolean hasHat() { return false; }
}
