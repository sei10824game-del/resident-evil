package com.reevil.entity.re2rlicker;

import com.reevil.entity.licker.LickerEntity;
import com.reevil.entity.AttackMove;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/** RE2 Remake Licker: skinless bulk, back spikes, long claws. */
public class Re2rLickerEntity extends LickerEntity {
    public Re2rLickerEntity(EntityType<Re2rLickerEntity> type, Level level) { super(unchecked(type), level); xpReward = 60; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<LickerEntity> unchecked(EntityType<Re2rLickerEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 57.2).add(Attributes.MOVEMENT_SPEED, 0.324).add(Attributes.ATTACK_DAMAGE, 5.8).add(Attributes.FOLLOW_RANGE, 24).add(Attributes.ARMOR, 1.0);
    }

    // ---- remake-only moves (ids 30+; clips in client/remake/RemakeClips)
    private static final AttackMove X30 = new AttackMove(30, "frenzy_claws", 34, new int[]{8, 14, 20, 26}, 0f, 3.0f, 36, 4).dmg(4.5f).kb(0.4).face(30).move();
    private static final java.util.List<AttackMove> EXTRA = java.util.List.of(X30);
    @Override public java.util.List<AttackMove> moves() { return com.reevil.entity.RemakeMoves.merge(super.moves(), EXTRA); }
    @Override
    protected void onMoveTick(AttackMove m, int t, net.minecraft.world.entity.LivingEntity target) {
        if (m.id == 30 && t >= 4 && t <= 7) lunge(0.4f, 0f);
        if (m.id == 30 && t >= 11 && t <= 13) lunge(0.3f, 0f);
        if (m.id == 30 && t >= 17 && t <= 19) lunge(0.3f, 0f);
        if (m.id == 30 && t >= 23 && t <= 25) lunge(0.3f, 0f);
        super.onMoveTick(m, t, target);
    }
    @Override
    protected void onMoveHit(AttackMove m, int hitIndex, net.minecraft.world.entity.LivingEntity target) {
        super.onMoveHit(m, hitIndex, target);

    }
}
