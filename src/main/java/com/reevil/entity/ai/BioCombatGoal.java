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
        double dist = Math.max(0.0, Math.sqrt(mob.distanceToSqr(t)) - t.getBbWidth() * 0.5 - mob.getBbWidth() * 0.5); // overlapping boxes count as point-blank, not negative
        mob.getLookControl().setLookAt(t, 30f, 30f);
        if (--repath <= 0) { mob.getNavigation().moveTo(t, speed); repath = 6 + mob.getRandom().nextInt(6); }
        if (mob.getMoveCooldown() > 0) return;
        List<AttackMove> ok = new ArrayList<>();
        int total = 0;
        for (AttackMove m : mob.allMoves()) {
            if (m.weight > 0 && dist >= m.minRange && dist <= m.maxRange && mob.hasLineOfSight(t) && mob.canStart(m)) { ok.add(m); total += m.weight; }
        }
        if (ok.isEmpty()) { // point-blank with only longer-range moves available: use the shortest-range ones anyway
            double best = Double.MAX_VALUE;
            for (AttackMove m : mob.allMoves()) if (m.weight > 0 && dist < m.minRange && mob.canStart(m)) best = Math.min(best, m.minRange);
            if (best == Double.MAX_VALUE) return;
            total = 0;
            for (AttackMove m : mob.allMoves()) if (m.weight > 0 && m.minRange <= best + 1.0e-6 && dist < m.minRange && mob.canStart(m)) { ok.add(m); total += m.weight; }
            if (ok.isEmpty() || total <= 0) return;
        }
        int r = mob.getRandom().nextInt(total);
        for (AttackMove m : ok) { r -= m.weight; if (r < 0) { mob.startMove(m); return; } }
    }
}
