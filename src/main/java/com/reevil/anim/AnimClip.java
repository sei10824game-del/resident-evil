package com.reevil.anim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keyframed additive animation. Values are OFFSETS from the model's base pose, rotations in DEGREES,
 * positions in model units (pixels). Time t is normalized 0..1 over the whole move.
 * Author like: AnimClip.create().bone("rightArm").rot(0f,0,0,0).rot(0.3f,-120,0,20).rot(0.45f,40,0,-10,Ease.OUT).rot(1f,0,0,0).done().build()
 * Between two keys the ease of the LATER key is used.
 */
public final class AnimClip {
    public enum Ease { LINEAR, SMOOTH, IN, OUT, SNAP }

    public static final class Key {
        final float t; final float x, y, z; final Ease ease;
        Key(float t, float x, float y, float z, Ease ease) { this.t = t; this.x = x; this.y = y; this.z = z; this.ease = ease; }
    }

    public static final class Track {
        final List<Key> rot = new ArrayList<>();
        final List<Key> pos = new ArrayList<>();
        final List<Key> scale = new ArrayList<>();
    }

    private final Map<String, Track> tracks = new HashMap<>();

    private AnimClip() {}

    public static Builder create() { return new Builder(); }

    public boolean has(String bone) { return tracks.containsKey(bone); }

    /** @return {rotX,rotY,rotZ (radians), posX,posY,posZ, scaleX,scaleY,scaleZ (multiplier, 1 = none)} or null if bone not animated. */
    public float[] sample(String bone, float t) {
        Track tr = tracks.get(bone);
        if (tr == null) return null;
        float[] r = eval(tr.rot, t, 0f);
        float[] p = eval(tr.pos, t, 0f);
        float[] s = eval(tr.scale, t, 1f);
        return new float[]{(float) Math.toRadians(r[0]), (float) Math.toRadians(r[1]), (float) Math.toRadians(r[2]), p[0], p[1], p[2], s[0], s[1], s[2]};
    }

    public Iterable<String> bones() { return tracks.keySet(); }

    private static float[] eval(List<Key> keys, float t, float rest) {
        if (keys.isEmpty()) return new float[]{rest, rest, rest};
        Key first = keys.get(0);
        if (t <= first.t) return new float[]{first.x, first.y, first.z};
        for (int i = 1; i < keys.size(); i++) {
            Key b = keys.get(i);
            if (t <= b.t) {
                Key a = keys.get(i - 1);
                float span = b.t - a.t;
                float u = span <= 1e-6f ? 1f : (t - a.t) / span;
                u = ease(b.ease, u);
                return new float[]{a.x + (b.x - a.x) * u, a.y + (b.y - a.y) * u, a.z + (b.z - a.z) * u};
            }
        }
        Key last = keys.get(keys.size() - 1);
        return new float[]{last.x, last.y, last.z};
    }

    private static float ease(Ease e, float u) {
        switch (e) {
            case SMOOTH: return u * u * (3 - 2 * u);
            case IN: return u * u;
            case OUT: return 1 - (1 - u) * (1 - u);
            case SNAP: return u * u * u * u;
            default: return u;
        }
    }

    public static final class Builder {
        private final AnimClip clip = new AnimClip();
        public BoneBuilder bone(String name) { return new BoneBuilder(this, clip.tracks.computeIfAbsent(name, k -> new Track())); }
        public AnimClip build() { return clip; }
    }

    public static final class BoneBuilder {
        private final Builder parent; private final Track track;
        BoneBuilder(Builder p, Track t) { parent = p; track = t; }
        public BoneBuilder rot(float t, float x, float y, float z) { return rot(t, x, y, z, Ease.SMOOTH); }
        public BoneBuilder rot(float t, float x, float y, float z, Ease e) { track.rot.add(new Key(t, x, y, z, e)); return this; }
        public BoneBuilder pos(float t, float x, float y, float z) { return pos(t, x, y, z, Ease.SMOOTH); }
        public BoneBuilder pos(float t, float x, float y, float z, Ease e) { track.pos.add(new Key(t, x, y, z, e)); return this; }
        public BoneBuilder scale(float t, float x, float y, float z) { return scale(t, x, y, z, Ease.SMOOTH); }
        public BoneBuilder scale(float t, float x, float y, float z, Ease e) { track.scale.add(new Key(t, x, y, z, e)); return this; }
        public Builder done() { return parent; }
    }
}
