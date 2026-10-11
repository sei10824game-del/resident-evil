package com.reevil.entity.elgigante;

import com.reevil.entity.AttackMove;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * El Gigante (RE4): the giant troll. Built on the stalker rig and behaviour (boss bar, door smashing, kneeling stagger when hurt enough, grab-and-throw) with long-reach club swings and a
 * huge crushing stomp; its weak point is the Plaga on its BACK (x2.5 from behind, x0.5 from the front). Kneels when staggered, exposing it. Never transforms.
 */
public class ElGiganteEntity extends MrXEntity {
    private static final AttackMove M_CLUB = new AttackMove(HAYMAKER, "club_swing", 30, new int[]{15}, 0, 5.5, 20, 6).dmg(13f).kb(2.0).face(14);
    private static final AttackMove M_BACK = new AttackMove(JAB_HOOK, "double_swing", 28, new int[]{9, 19}, 0, 5.0, 16, 5).dmg(9f).kb(1.2).face(18);
    private static final AttackMove M_GRAB = new AttackMove(CHOKE, "pick_up_throw", 72, new int[]{14, 30, 44, 60}, 0, 4.0, 150, 3).dmg(4f).kb(0).face(14);
    private static final AttackMove M_STOMP = new AttackMove(STOMP, "stomp", 24, new int[]{12}, 0, 4.0, 18, 5).dmg(12f).kb(2.4).face(11);
    private static final List<AttackMove> MOVES = List.of(M_CLUB, M_BACK, M_GRAB, M_STOMP);

    public ElGiganteEntity(EntityType<ElGiganteEntity> type, Level level) { super(unchecked(type), level); xpReward = 150; setMaxUpStep(1.8f); }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<MrXEntity> unchecked(EntityType<ElGiganteEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 420.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.ATTACK_DAMAGE, 13.0)
            .add(Attributes.FOLLOW_RANGE, 40.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
            .add(Attributes.ARMOR, 6.0);
    }

    @Override public List<AttackMove> moves() { return MOVES; }
    @Override protected boolean canTransform() { return false; }
    @Override public String getTextureSuffix() { return ""; }

    @Override
    public boolean hurt(DamageSource src, float amount) {
        Entity a = src.getEntity();
        if (a != null) {
            Vec3 face = Vec3.directionFromRotation(0, yBodyRot);
            Vec3 to = a.position().subtract(position()).multiply(1, 0, 1).normalize();
            amount *= face.dot(to) < -0.25 || isKneeling() ? 2.5f : 0.5f;
        }
        return super.hurt(src, amount);
    }
}
