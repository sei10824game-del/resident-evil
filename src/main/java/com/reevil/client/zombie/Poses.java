package com.reevil.client.zombie;

import com.reevil.anim.AnimClip;
import net.minecraft.client.model.geom.ModelPart;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Static pose tables (bone -> {rx,ry,rz degrees, px,py,pz pixels}) shared by the zombie and crimson head models.
 * Lying / crawling poses are tables so that transition clips (fall, collapse, rise) start or end EXACTLY on them.
 */
public final class Poses {
    public static final float R = (float) (Math.PI / 180.0);
    private static final float[] ZERO = new float[6];

    private Poses() {}

    public static float[] v(float... f) { return f; }

    /** map("bone", v(rx,ry,rz[,px,py,pz]), ...) */
    public static Map<String, float[]> map(Object... kv) {
        Map<String, float[]> m = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            float[] val = (float[]) kv[i + 1];
            m.put((String) kv[i], val.length >= 6 ? val : Arrays.copyOf(val, 6));
        }
        return m;
    }

    /** keyframe {t, rx, ry, rz} */
    public static float[] k(float t, float rx, float ry, float rz) { return new float[]{t, rx, ry, rz}; }

    /** keyframe {t, rx, ry, rz, px, py, pz} */
    public static float[] k(float t, float rx, float ry, float rz, float px, float py, float pz) {
        return new float[]{t, rx, ry, rz, px, py, pz};
    }

    /** Add a pose table scaled by k (0..1) on top of the current bone values. */
    public static void applyPose(Function<String, ModelPart> bones, Map<String, float[]> pose, float k) {
        if (k == 0f) return;
        for (Map.Entry<String, float[]> en : pose.entrySet()) {
            ModelPart p = bones.apply(en.getKey());
            if (p == null) continue;
            float[] v = en.getValue();
            p.xRot += v[0] * R * k; p.yRot += v[1] * R * k; p.zRot += v[2] * R * k;
            p.x += v[3] * k; p.y += v[4] * k; p.z += v[5] * k;
        }
    }

    /** Clip track for one bone: key 0 = from-pose (or zero), mids (k(...)), key 1 = to-pose (or zero). */
    public static void track(AnimClip.Builder c, String bone, Map<String, float[]> from, Map<String, float[]> to, float[]... mids) {
        float[] a = get(from, bone), b = get(to, bone);
        AnimClip.BoneBuilder bb = c.bone(bone);
        bb.rot(0f, a[0], a[1], a[2]);
        for (float[] m : mids) bb.rot(m[0], m[1], m[2], m[3]);
        bb.rot(1f, b[0], b[1], b[2]);
        boolean pos = a[3] != 0 || a[4] != 0 || a[5] != 0 || b[3] != 0 || b[4] != 0 || b[5] != 0;
        for (float[] m : mids) if (m.length >= 7) pos = true;
        if (pos) {
            bb.pos(0f, a[3], a[4], a[5]);
            for (float[] m : mids) if (m.length >= 7) bb.pos(m[0], m[4], m[5], m[6]);
            bb.pos(1f, b[3], b[4], b[5]);
        }
    }

    private static float[] get(Map<String, float[]> m, String bone) {
        if (m == null) return ZERO;
        float[] v = m.get(bone);
        return v == null ? ZERO : v;
    }
}
