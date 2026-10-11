package com.reevil.entity.krauser;

import com.reevil.entity.AttackMove;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Jack Krauser (RE4): the mercenary. A bow-wielding marksman who closes with the knife - fast slashes, a lunging thrust, and arrows fired at long range with the bow-draw animation. */
public class KrauserEntity extends GanadoEntity {
    private static final AttackMove M_SLASH = new AttackMove(SWING, "knife_slash", 26, new int[]{12}, 0, 2.8, 10, 7).dmg(7f).kb(0.5).face(11);
    private static final AttackMove M_LUNGE = new AttackMove(STAB, "knife_lunge", 28, new int[]{14}, 1.5, 4.5, 20, 4).dmg(8f).kb(0.4).face(10).move();
    private static final AttackMove M_SHOOT = new AttackMove(SHOOT, "bow_shot", 34, new int[]{16}, 5, 26, 30, 7).face(15);
    private static final List<AttackMove> MOVES = List.of(M_SLASH, M_LUNGE, M_SHOOT);

    public KrauserEntity(EntityType<KrauserEntity> type, Level level) { super(type, level); xpReward = 100; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 200.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.FOLLOW_RANGE, 36.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
            .add(Attributes.ARMOR, 4.0);
    }

    @Override protected int defaultWeapon() { return W_BOW; }
    @Override protected boolean canBurst() { return false; }
    @Override public boolean hasHat() { return false; }
    @Override public List<AttackMove> moves() { return MOVES; }
    @Override public String getTextureSuffix() { return ""; }

    @Override
    public boolean canStartMove(AttackMove m) { return m.id != SHOOT || (getTarget() != null && hasLineOfSight(getTarget())); }

    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, LivingEntity target) {
        if (m.id != SHOOT) { super.onMoveHit(m, hitIndex, target); return; }
        if (target == null) return;
        Arrow a = new Arrow(level(), this);
        a.setPos(getX(), getEyeY() - 0.15, getZ());
        Vec3 d = target.getEyePosition().subtract(a.position());
        double h = Math.sqrt(d.x * d.x + d.z * d.z);
        a.shoot(d.x, d.y + h * 0.16, d.z, 2.4f, 1.5f);
        a.setBaseDamage(5.0);
        a.pickup = AbstractArrow.Pickup.DISALLOWED;
        level().addFreshEntity(a);
        playSound(SoundEvents.ARROW_SHOOT, 1.2f, 0.8f);
    }
}
