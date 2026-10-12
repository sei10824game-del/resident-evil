package com.reevil.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** Helpers for the remake creatures' extra moves. */
public final class RemakeMoves {
    private static final Map<List<AttackMove>, Map<List<AttackMove>, List<AttackMove>>> CACHE = new IdentityHashMap<>();

    /** parent moves + extras; single-move forms (crawling, collapse, rise) stay untouched. */
    public static synchronized List<AttackMove> merge(List<AttackMove> base, List<AttackMove> extra) {
        if (base.size() < 2) return base;
        return CACHE.computeIfAbsent(extra, e -> new IdentityHashMap<>()).computeIfAbsent(base, b -> { List<AttackMove> l = new ArrayList<>(b); l.addAll(extra); return List.copyOf(l); });
    }

    public static void launch(LivingEntity t, double vy) {
        if (t == null) return;
        t.push(0, vy, 0); t.hurtMarked = true;
    }

    /** damage + knockback to everything hostile around the attacker (radius). */
    public static void aoe(BioMonster self, double radius, float dmg, double kb) {
        for (LivingEntity e : self.level().getEntitiesOfClass(LivingEntity.class, self.getBoundingBox().inflate(radius), x -> x != self && x.isAlive() && Factions.hostile(self, x))) {
            if (self.distanceTo(e) > radius + e.getBbWidth() * 0.5) continue;
            if (e.hurt(self.damageSources().mobAttack(self), dmg)) {
                Vec3 d = e.position().subtract(self.position()).multiply(1, 0, 1).normalize().scale(kb);
                e.push(d.x, 0.35, d.z); e.hurtMarked = true;
            }
        }
    }

    private RemakeMoves() {}
}
