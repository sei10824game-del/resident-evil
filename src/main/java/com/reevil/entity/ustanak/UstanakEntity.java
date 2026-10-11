package com.reevil.entity.ustanak;

import com.reevil.entity.mrx.MrXEntity;
import com.reevil.entity.AttackMove;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** Ustanak (RE6): the relentless bounty hunter with the great claw. Born in the clawed Super Tyrant frame - punch, claw stab, claw sweep, ground pound and claw grab - and never stops hunting. */
public class UstanakEntity extends MrXEntity {
    public UstanakEntity(EntityType<UstanakEntity> type, Level level) { super(unchecked(type), level); xpReward = 200; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<UstanakEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 420.0).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ATTACK_DAMAGE, 12.0).add(Attributes.FOLLOW_RANGE, 52).add(Attributes.KNOCKBACK_RESISTANCE, 0.95).add(Attributes.ARMOR, 8.0);
    }

    private static final AttackMove U_PUNCH = new AttackMove(HAYMAKER, "punch", 30, new int[]{15}, 0, 3.0, 16, 5).dmg(11f).kb(1.4).face(14);
    private static final AttackMove U_STAB = new AttackMove(CLAW_STAB, "claw_stab", 30, new int[]{15}, 1.5, 4.8, 22, 6).dmg(15f).kb(0.8).face(12).move();
    private static final AttackMove U_SWEEP = new AttackMove(CLAW_SWEEP, "claw_sweep", 36, new int[]{11, 24}, 0, 4.2, 20, 5).dmg(10f).kb(1.0).face(24);
    private static final AttackMove U_POUND = new AttackMove(POUND, "ground_pound", 42, new int[]{23}, 0, 5.5, 80, 3).dmg(11f).kb(1.4).face(12);
    private static final AttackMove U_CHOKE = new AttackMove(CHOKE, "claw_grab", 72, new int[]{14, 30, 44, 60}, 0, 2.6, 140, 3).dmg(3f).kb(0).face(14);
    private static final List<AttackMove> MOVES = List.of(U_PUNCH, U_STAB, U_SWEEP, U_POUND, U_CHOKE);

    @Override public void aiStep() { if (getForm() != 1 && !level().isClientSide) setForm(1); super.aiStep(); }   // always the clawed Super Tyrant frame
    @Override public List<AttackMove> moves() { return MOVES; }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }
}
