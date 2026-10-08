package com.reevil.entity;

/**
 * One attack of a creature. id (1..n) is also the key the model uses to pick the animation clip.
 * Ticks are server ticks (20/s). hitTicks = move ticks at which damage/effects are applied (onMoveHit),
 * so the animation's impact keyframe must sit at hitTick/duration.
 */
public final class AttackMove {
    public final int id;
    public final String name;
    public final int duration;
    public final int[] hitTicks;
    public final double minRange, maxRange;   // distance to target (center-to-edge) in which the move may START
    public final int cooldown;                // global cooldown after move ends
    public final int weight;                  // selection weight
    /** ticks during which the entity keeps turning to face the target (from move start). */
    public int faceUntil;
    /** if true the entity cannot walk during the move (lunges are applied via velocity in onMoveTick). */
    public boolean freeze = true;
    public float damage = 4f;
    public double knockback = 0.4;

    public AttackMove(int id, String name, int duration, int[] hitTicks, double minRange, double maxRange, int cooldown, int weight) {
        this.id = id; this.name = name; this.duration = duration; this.hitTicks = hitTicks;
        this.minRange = minRange; this.maxRange = maxRange; this.cooldown = cooldown; this.weight = weight;
        this.faceUntil = hitTicks.length > 0 ? hitTicks[0] : duration;
    }

    public AttackMove dmg(float d) { this.damage = d; return this; }
    public AttackMove kb(double k) { this.knockback = k; return this; }
    public AttackMove face(int ticks) { this.faceUntil = ticks; return this; }
    public AttackMove move() { this.freeze = false; return this; }
}
