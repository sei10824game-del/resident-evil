package com.reevil.entity.wesker;

import com.reevil.entity.mrx.MrXEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Albert Wesker (RE5): the super-speed fighter - punch flurries, dash-in strikes, snap kicks and a grab-and-throw at triple the Mr. X pace. */
public class WeskerEntity extends MrXEntity {
    public WeskerEntity(EntityType<WeskerEntity> type, Level level) { super(unchecked(type), level); xpReward = 200; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<WeskerEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 320.0).add(Attributes.MOVEMENT_SPEED, 0.42).add(Attributes.ATTACK_DAMAGE, 11.0).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.9).add(Attributes.ARMOR, 8.0);
    }

    private static final AttackMove M_STRIKE = new AttackMove(HAYMAKER, "power_strike", 30, new int[]{15}, 0, 3.2, 8, 6).dmg(11f).kb(1.6).face(14);
    private static final AttackMove M_FLURRY = new AttackMove(JAB_HOOK, "super_speed_flurry", 28, new int[]{9, 19}, 0, 3.0, 6, 8).dmg(7f).kb(0.5).face(18);
    private static final AttackMove M_KICK = new AttackMove(STOMP, "snap_kick", 24, new int[]{12}, 0, 2.8, 10, 4).dmg(9f).kb(2.0).face(11);
    private static final AttackMove M_THROW = new AttackMove(CHOKE, "grab_and_throw", 72, new int[]{14, 30, 44, 60}, 0, 2.8, 110, 3).dmg(4f).kb(0).face(14);
    private static final List<AttackMove> MOVES = List.of(M_STRIKE, M_FLURRY, M_KICK, M_THROW);

    @Override public List<AttackMove> moves() { return MOVES; }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    /** super speed: the flurry and the strike close the gap in a blink. */
    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m.id == JAB_HOOK && (t == 3 || t == 8 || t == 18)) lunge(0.55, 0);
        else if (m.id == HAYMAKER && t == 8) lunge(0.8, 0);
        else super.onMoveTick(m, t, target);
    }
}
