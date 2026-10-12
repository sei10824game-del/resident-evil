package com.reevil.entity.re4rregenerador;

import com.reevil.entity.regenerador.RegeneradorEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE4 Remake Regenerador: breath mask, chest maw. */
public class Re4rRegeneradorEntity extends RegeneradorEntity {
    public Re4rRegeneradorEntity(EntityType<Re4rRegeneradorEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<RegeneradorEntity> unchecked(EntityType<Re4rRegeneradorEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 132.0).add(Attributes.MOVEMENT_SPEED, 0.173).add(Attributes.ATTACK_DAMAGE, 8.0).add(Attributes.FOLLOW_RANGE, 22).add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "maw_chomp", 32, new int[]{14}, 0f, 2.2f, 34, 4).dmg(8f).kb(0.6).face(24);
    private static final AttackMove X31 = new AttackMove(31, "iron_shove", 30, new int[]{13}, 0f, 2.2f, 32, 3).dmg(5f).kb(2.2).face(18);
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30, X31);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 8 && t <= 13) lunge(0.2f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);
        if (m.id == 30 && hitIndex == 0 && target != null && distanceTo(target) < m.maxRange + 1.2) target.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 40, 1), this);
    }
}
