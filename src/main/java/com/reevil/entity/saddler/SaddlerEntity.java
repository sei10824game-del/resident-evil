package com.reevil.entity.saddler;

import com.reevil.entity.nemesis.NemesisEntity;
import com.reevil.entity.AttackMove;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Osmund Saddler (RE4): the robed cult leader. The Nemesis frame without the rocket launcher - staff blows, tentacle lashes, snare-and-drag and (final form) the all-round tentacle barrage, mutating twice as he is hurt. */
public class SaddlerEntity extends NemesisEntity {
    public SaddlerEntity(EntityType<SaddlerEntity> type, Level level) { super(unchecked(type), level); xpReward = 200; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<NemesisEntity> unchecked(EntityType<SaddlerEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 300.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 9.0).add(Attributes.FOLLOW_RANGE, 48).add(Attributes.KNOCKBACK_RESISTANCE, 0.8).add(Attributes.ARMOR, 6.0);
    }

    protected static final AttackMove S_PUNCH = new AttackMove(PUNCH, "staff_punch", 30, new int[]{15}, 0, 3.0, 18, 6).dmg(9f).kb(1.2).face(14);
    protected static final AttackMove S_KICK = new AttackMove(KICK, "kick", 24, new int[]{12}, 0, 2.5, 16, 4).dmg(7f).kb(1.6).face(11);
    protected static final AttackMove S_WHIP = new AttackMove(WHIP, "tentacle_lash", 28, new int[]{13}, 2.5, 9.5, 20, 6).dmg(9f).kb(1.0).face(12);
    protected static final AttackMove S_SNARE = new AttackMove(SNARE, "tentacle_snare", 58, new int[]{14, 40}, 3.5, 12, 70, 4).dmg(4f).kb(0).face(14);
    protected static final AttackMove S_BARRAGE = new AttackMove(BARRAGE, "tentacle_barrage", 50, new int[]{14, 22, 30, 38}, 0, 9, 50, 5).dmg(8f).kb(1.0).face(10);
    private static final List<AttackMove> SF0 = List.of(S_PUNCH, S_WHIP, S_KICK);
    private static final List<AttackMove> SF1 = List.of(S_PUNCH, S_WHIP, S_SNARE, S_KICK);
    private static final List<AttackMove> SF2 = List.of(S_WHIP, S_SNARE, S_BARRAGE, S_PUNCH);
    @Override public List<AttackMove> moves() { return getForm() == 0 ? SF0 : getForm() == 1 ? SF1 : SF2; }
}
