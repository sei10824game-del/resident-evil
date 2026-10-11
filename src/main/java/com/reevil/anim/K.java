package com.reevil.anim;

/** Terse keyframe row helpers: rows are {t, x, y, z[, easeCode]} in degrees (rot) or pixels (pos) / multiplier (scale). */
public final class K {
    public static final float LIN = 0, SM = 1, IN = 2, OUT = 3, SNAP = 4;
    private static final AnimClip.Ease[] EASES = AnimClip.Ease.values();

    private K() {}

    public static float[] k(float... v) { return v; }

    private static AnimClip.Ease ease(float[] r) { return r.length > 4 ? EASES[(int) r[4]] : AnimClip.Ease.SMOOTH; }

    public static void rot(AnimClip.Builder b, String bone, float[]... rows) {
        AnimClip.BoneBuilder bb = b.bone(bone);
        for (float[] r : rows) bb.rot(r[0], r[1], r[2], r[3], ease(r));
    }

    public static void pos(AnimClip.Builder b, String bone, float[]... rows) {
        AnimClip.BoneBuilder bb = b.bone(bone);
        for (float[] r : rows) bb.pos(r[0], r[1], r[2], r[3], ease(r));
    }

    public static void scale(AnimClip.Builder b, String bone, float[]... rows) {
        AnimClip.BoneBuilder bb = b.bone(bone);
        for (float[] r : rows) bb.scale(r[0], r[1], r[2], r[3], ease(r));
    }

    /** Same rows on bones prefix+"L" and prefix+"R"; y and z are negated for R (mirror). */
    public static void pair(AnimClip.Builder b, String prefix, float[]... rows) {
        for (String s : new String[]{"L", "R"}) {
            float sg = s.equals("L") ? 1 : -1;
            AnimClip.BoneBuilder bb = b.bone(prefix + s);
            for (float[] r : rows) bb.rot(r[0], r[1], sg * r[2], sg * r[3], ease(r));
        }
    }
}
