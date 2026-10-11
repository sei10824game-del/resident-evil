package com.reevil.entity.ivy;

import com.reevil.entity.AttackMove;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Ivy (RE2): the small rooted poison plant. Same anatomy and animation set as Plant 42 (the model is shared and rendered at ~60% size) but with
 * shorter reach, less health, no boss bar and a lower weak point. Moves: tentacle whip, vine snare-and-devour, spore cloud, maw bite, thrash flurry
 * once it withers below half health. Weak point = the base.
 */
public class IvyEntity extends Plant42Entity {
    private static final AttackMove M_WHIP = new AttackMove(WHIP, "ivy_whip", 24, new int[]{12}, 0.5, 4.2, 18, 5).dmg(5f).kb(0.5).face(11);
    private static final AttackMove M_GRAB = new AttackMove(GRAB, "ivy_snare", 60, new int[]{14, 30, 46}, 2, 5.0, 70, 3).dmg(2f).kb(0).face(14);
    private static final AttackMove M_GAS = new AttackMove(GAS, "ivy_spores", 50, new int[]{25}, 0, 6, 200, 2).face(0);
    private static final AttackMove M_BITE = new AttackMove(DEVOUR, "ivy_bite", 36, new int[]{20}, 0, 2.2, 26, 6).dmg(8f).kb(0.3).face(18);
    private static final AttackMove M_FLURRY = new AttackMove(FLURRY, "ivy_thrash", 44, new int[]{10, 20, 30}, 0.5, 4.8, 36, 5).dmg(3.5f).kb(0.4).face(28);
    private static final List<AttackMove> MOVES_0 = List.of(M_WHIP, M_GRAB, M_GAS, M_BITE);
    private static final List<AttackMove> MOVES_1 = List.of(M_WHIP, M_GRAB, M_GAS, M_BITE, M_FLURRY);

    public IvyEntity(EntityType<IvyEntity> type, Level level) {
        super(unchecked(type), level);
        xpReward = 20;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<Plant42Entity> unchecked(EntityType<IvyEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 70.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
            .add(Attributes.FOLLOW_RANGE, 12.0)
            .add(Attributes.ATTACK_DAMAGE, 5.0)
            .add(Attributes.ARMOR, 1.0);
    }

    @Override public List<AttackMove> moves() { return getForm() == 1 ? MOVES_1 : MOVES_0; }
    @Override protected boolean bossBar() { return false; }
    @Override protected double weakHeight() { return 0.95; }
    @Override protected double mouthScale() { return 0.6; }
}
