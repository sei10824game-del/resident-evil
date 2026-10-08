package com.reevil.entity.ai;

import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/** Chase the target; when a move's range window fits and cooldown is over, start a weighted-random eligible move. */
public class BioCombatGoal extends Goal {
    private final BioMonster mob;
    private final double speed;
    private int repath;

    public BioCombatGoal(BioMonster mob, double speed) {
        this.mob = mob; this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean canUse() { LivingEntity t = mob.getTarget(); return t != null && t.isAlive(); }
    @Override public boolean canContinueToUse() { return canUse() || mob.isMoving(); }
    @Override public void stop() { mob.getNavigation().stop(); }

    @Override
    public void tick() {
        LivingEntity t = mob.getTarget();
        if (t == null) return;
        if (mob.isMoving()) return; // move runs inside BioMonster.aiStep
        double dist = Math.sqrt(mob.distanceToSqr(t)) - t.getBbWidth() * 0.5 - mob.getBbWidth() * 0.5;
        mob.getLookControl().setLookAt(t, 30f, 30f);
        if (--repath <= 0) { mob.getNavigation().moveTo(t, speed); repath = 6 + mob.getRandom().nextInt(6); }
        if (mob.getMoveCooldown() > 0) return;
        List<AttackMove> ok = new ArrayList<>();
        int total = 0;
        for (AttackMove m : mob.moves()) {
            if (dist >= m.minRange && dist <= m.maxRange && mob.hasLineOfSight(t) && mob.canStartMove(m)) { ok.add(m); total += m.weight; }
        }
        if (ok.isEmpty()) return;
        int r = mob.getRandom().nextInt(total);
        for (AttackMove m : ok) { r -= m.weight; if (r < 0) { mob.startMove(m); return; } }
    }
}
