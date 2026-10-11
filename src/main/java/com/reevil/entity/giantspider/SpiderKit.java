package com.reevil.entity.giantspider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** Shared spider helpers (Giant Spider + Web Spinner): ceiling probe, simulated projectile, area splash. */
public final class SpiderKit {
    private SpiderKit() {}

    /** Y of the underside of the first block above the mob (searching 1..max blocks up), or NaN if open / too low / not sturdy. */
    public static double findCeiling(Mob mob, int min, int max) {
        Level lvl = mob.level();
        BlockPos base = mob.blockPosition();
        for (int dy = 1; dy <= max; dy++) {
            BlockPos p = base.above(dy);
            BlockState s = lvl.getBlockState(p);
            if (s.isAir()) continue;
            if (dy < min || !s.isFaceSturdy(lvl, p, Direction.DOWN)) return Double.NaN;
            return p.getY();
        }
        return Double.NaN;
    }

    /** Damage + effect every non-monster living entity within r of 'at', with a particle burst. */
    public static void splash(Mob owner, Vec3 at, double r, float dmg, Supplier<MobEffectInstance> effect, ParticleOptions particle, int count) {
        Level lvl = owner.level();
        if (lvl instanceof ServerLevel sl) sl.sendParticles(particle, at.x, at.y, at.z, count, r * 0.4, 0.2, r * 0.4, 0.05);
        for (LivingEntity e : lvl.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(r))) {
            if (e == owner || e instanceof Monster || !e.isAlive()) continue;
            if (dmg > 0) e.hurt(owner.damageSources().mobAttack(owner), dmg);
            if (effect != null) e.addEffect(effect.get(), owner);
        }
    }

    /**
     * Server-side simulated projectile (venom glob / silk strand): moves, falls, leaves a particle trail and calls
     * 'impact' on the first block or living non-monster it touches. No entity type needed.
     */
    public static final class Shot {
        private Vec3 pos, vel;
        private final double gravity;
        private final ParticleOptions trail;
        private final Consumer<Vec3> impact;
        private int life;

        public Shot(Vec3 pos, Vec3 vel, double gravity, ParticleOptions trail, Consumer<Vec3> impact) {
            this.pos = pos; this.vel = vel; this.gravity = gravity; this.trail = trail; this.impact = impact;
        }

        /** Initial velocity that reaches 'to' after exactly 'ticks' ticks with per-tick 'gravity'. */
        public static Vec3 aim(Vec3 from, Vec3 to, int ticks, double gravity) {
            double vy = (to.y - from.y + gravity * ticks * (ticks - 1) / 2.0) / ticks;
            return new Vec3((to.x - from.x) / ticks, vy, (to.z - from.z) / ticks);
        }

        /** @return true when finished (impact or expired). */
        public boolean tick(Mob owner) {
            Level lvl = owner.level();
            Vec3 next = pos.add(vel);
            vel = vel.add(0, -gravity, 0);
            if (lvl instanceof ServerLevel sl) {
                for (int i = 0; i < 3; i++) {
                    Vec3 p = pos.lerp(next, i / 3.0);
                    sl.sendParticles(trail, p.x, p.y, p.z, 1, 0.03, 0.03, 0.03, 0.0);
                }
            }
            pos = next;
            if (++life > 80) return true;
            BlockPos bp = BlockPos.containing(pos);
            if (!lvl.getBlockState(bp).getCollisionShape(lvl, bp).isEmpty()) { impact.accept(pos); return true; }
            AABB box = new AABB(pos.x - 0.4, pos.y - 0.4, pos.z - 0.4, pos.x + 0.4, pos.y + 0.4, pos.z + 0.4);
            for (LivingEntity e : lvl.getEntitiesOfClass(LivingEntity.class, box)) {
                if (e != owner && e.isAlive() && !(e instanceof Monster)) { impact.accept(pos); return true; }
            }
            return false;
        }
    }
}
