package com.reevil.entity.gatlingmajini;

import com.reevil.entity.krauser.KrauserEntity;
import com.reevil.entity.AttackMove;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Gatling Majini (RE5): the heavy gunner - seven-round arrow bursts at range (drawn through the bow animation) and a rifle-butt bash up close. */
public class GatlingMajiniEntity extends KrauserEntity {
    public GatlingMajiniEntity(EntityType<GatlingMajiniEntity> type, Level level) { super(unchecked(type), level); xpReward = 40; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<KrauserEntity> unchecked(EntityType<GatlingMajiniEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 70.0).add(Attributes.MOVEMENT_SPEED, 0.22).add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.FOLLOW_RANGE, 34).add(Attributes.KNOCKBACK_RESISTANCE, 0.6).add(Attributes.ARMOR, 3.0);
    }

    private static final AttackMove G_BASH = new AttackMove(SWING, "butt_strike", 26, new int[]{12}, 0, 2.8, 12, 5).dmg(6f).kb(0.8).face(11);
    private static final AttackMove G_SPRAY = new AttackMove(SHOOT, "gatling_spray", 34, new int[]{12, 15, 18, 21, 24, 27, 30}, 4, 26, 50, 8).face(30);
    private static final List<AttackMove> MOVES = List.of(G_BASH, G_SPRAY);
    @Override public List<AttackMove> moves() { return MOVES; }
}
