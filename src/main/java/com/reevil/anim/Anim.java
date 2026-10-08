package com.reevil.anim;

public final class Anim {
    private Anim() {}
    public static float lerp(float a, float b, float t) { return a + (b - a) * t; }
    public static float clamp01(float t) { return t < 0 ? 0 : Math.min(t, 1); }
    /** Progress of a window [from,to] inside t (0..1 clamped). */
    public static float window(float t, float from, float to) { return clamp01((t - from) / (to - from)); }
    public static float smooth(float u) { u = clamp01(u); return u * u * (3 - 2 * u); }
}
