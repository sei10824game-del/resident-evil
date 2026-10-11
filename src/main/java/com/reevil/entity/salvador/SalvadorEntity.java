package com.reevil.entity.salvador;

import com.reevil.entity.AttackMove;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import java.util.List;

/** Dr. Salvador / chainsaw Ganado (RE4): sack-headed brute with a chainsaw. Wide sweeping cut, rushing thrust, and the dreaded overhead beheading (huge damage, much worse on a low-health victim). Never bursts into a Plaga. */
public class SalvadorEntity extends GanadoEntity {
    private static final AttackMove M_SWEEP = new AttackMove(SWING, "saw_sweep", 26, new int[]{12}, 0, 3.2, 16, 6).dmg(8f).kb(1.0).face(11);
    private static final AttackMove M_RUSH = new AttackMove(RUSH, "saw_rush", 30, new int[]{15}, 3, 9, 40, 4).dmg(9f).kb(0.8).face(8).move();
    private static final AttackMove M_DECAP = new AttackMove(DECAP, "beheading", 40, new int[]{20}, 0, 2.6, 80, 3).dmg(22f).kb(0.4).face(18);
    private static final List<AttackMove> MOVES = List.of(M_SWEEP, M_RUSH, M_DECAP);

    public SalvadorEntity(EntityType<SalvadorEntity> type, Level level) { super(type, level); xpReward = 40; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 90.0)
            .add(Attributes.MOVEMENT_SPEED, 0.26)
            .add(Attributes.ATTACK_DAMAGE, 8.0)
            .add(Attributes.FOLLOW_RANGE, 28.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
            .add(Attributes.ARMOR, 3.0);
    }

    @Override protected int defaultWeapon() { return W_CHAINSAW; }
    @Override protected boolean canBurst() { return false; }
    @Override public boolean hasHat() { return false; }
    @Override public List<AttackMove> moves() { return MOVES; }
    @Override public String getTextureSuffix() { return ""; }

    @Override
    protected void onMoveStart(AttackMove m) {
        super.onMoveStart(m);
        playSound(SoundEvents.GRINDSTONE_USE, 1.6f, 0.5f);
    }

    @Override
    protected void onMoveTick(AttackMove m, int t, LivingEntity target) {
        if (m.id == RUSH && t >= 6 && t <= 16) lunge(0.5, 0);
        else if (m.id == DECAP && t >= 17 && t <= 20) lunge(0.12, 0);
        else super.onMoveTick(m, t, target);
    }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        if (m.id == DECAP && target != null && distanceTo(target) <= m.maxRange + 0.9) {
            float dmg = m.damage * (target.getHealth() < target.getMaxHealth() * 0.4f ? 2.5f : 1f);
            if (strike(target, dmg, m.knockback)) playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.6f, 0.5f);
        } else if (m.id == SWING || m.id == RUSH) {
            if (target != null && distanceTo(target) <= m.maxRange + 1.0 && strike(target, m.damage, m.knockback)) playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.3f, 0.5f);
        } else super.onMoveHit(m, hitIndex, target);
    }
}
