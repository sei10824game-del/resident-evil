package com.reevil.client.giantspider;

import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.BioMonster;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Shared spider skeleton (Giant Spider + Web Spinner; texture layout in tools/tex_giant_spider.py, 128x128).
 * Model faces -Z. Bones: body > head > chel{R,L} > fang{R,L}; head > palp{R,L} > palp2{R,L};
 * body > abdomen > spinneret [> thread]; body > hip{R,L}{0..3} (yaw) > fem (lift) > tib (knee) > tar (tip).
 * Side convention for clip helpers: R is the reference side, L is mirrored automatically.
 */
public abstract class SpiderRigModel<T extends BioMonster> extends BioModel<T> {
    protected static final float DEG = Mth.DEG_TO_RAD;
    protected static final String[] SIDES = {"R", "L"};
    private static final float[] LEG_Z = {-3.5f, -1.2f, 1.2f, 3.5f};
    private static final float[] LEG_FWD = {0.75f, 0.28f, -0.25f, -0.7f};
    private static final AnimClip.Ease[] EASES = AnimClip.Ease.values();
    /** Ease codes for key rows: LINEAR, SMOOTH, IN, OUT, SNAP. */
    protected static final float LIN = 0, SM = 1, IN = 2, OUT = 3, SNAP = 4;

    protected SpiderRigModel(ModelPart root, boolean thread) {
        super(root);
        reg("body", "body");
        reg("head", "body/head");
        reg("abdomen", "body/abdomen");
        reg("spinneret", "body/abdomen/spinneret");
        if (thread) reg("thread", "body/abdomen/spinneret/thread");
        for (String s : SIDES) {
            reg("chel" + s, "body/head/chel" + s);
            reg("fang" + s, "body/head/chel" + s + "/fang" + s);
            reg("palp" + s, "body/head/palp" + s);
            reg("palp2" + s, "body/head/palp" + s + "/palp2" + s);
            for (int i = 0; i < 4; i++) {
                String n = s + i, p = "body/hip" + n;
                reg("hip" + n, p);
                reg("fem" + n, p + "/fem" + n);
                reg("tib" + n, p + "/fem" + n + "/tib" + n);
                reg("tar" + n, p + "/fem" + n + "/tib" + n + "/tar" + n);
            }
        }
    }

    public static LayerDefinition createRig(boolean thread) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -3, -5, 10, 6, 10), PartPose.offset(0, 15, 0));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 0).addBox(-4, -2.5f, -6, 8, 5, 6), PartPose.offset(0, -0.5f, -5));
        PartDefinition abd = body.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 16).addBox(-6, -5, 0, 12, 10, 14), PartPose.offsetAndRotation(0, -1, 5, 0.15f, 0, 0));
        PartDefinition spin = abd.addOrReplaceChild("spinneret", CubeListBuilder.create().texOffs(52, 16).addBox(-1.5f, -1.5f, 0, 3, 3, 2), PartPose.offset(0, 1, 14));
        if (thread) spin.addOrReplaceChild("thread", CubeListBuilder.create().texOffs(0, 64).addBox(-0.5f, -0.5f, 0, 1, 1, 32), PartPose.offset(0, 0, 2));
        for (int s = 0; s < 2; s++) {
            boolean r = s == 0;
            float sg = r ? 1 : -1;
            String side = SIDES[s];
            PartDefinition chel = head.addOrReplaceChild("chel" + side, CubeListBuilder.create().texOffs(r ? 80 : 68, 0).addBox(-1.5f, 0, -1.5f, 3, 4, 3), PartPose.offsetAndRotation(-sg * 1.6f, 1.5f, -5.5f, 0.25f, 0, 0));
            chel.addOrReplaceChild("fang" + side, CubeListBuilder.create().texOffs(r ? 96 : 92, 0).addBox(-0.5f, 0, -0.5f, 1, 3, 1), PartPose.offsetAndRotation(0, 4, -0.5f, 0, 0, -sg * 0.6f));
            PartDefinition palp = head.addOrReplaceChild("palp" + side, CubeListBuilder.create().texOffs(r ? 112 : 100, 0).addBox(-1, -1, -4, 2, 2, 4), PartPose.offsetAndRotation(-sg * 3.2f, 1, -5, 0.4f, sg * 0.15f, 0));
            palp.addOrReplaceChild("palp2" + side, CubeListBuilder.create().texOffs(r ? 60 : 52, 22).addBox(-0.5f, -0.5f, -3, 1, 1, 3), PartPose.offsetAndRotation(0, 0, -4, 0.6f, 0, 0));
            for (int i = 0; i < 4; i++) {
                String n = side + i;
                PartDefinition hip = body.addOrReplaceChild("hip" + n, CubeListBuilder.create(), PartPose.offsetAndRotation(r ? -4.5f : 4.5f, 0.5f, LEG_Z[i], 0, -sg * LEG_FWD[i], 0));
                PartDefinition fem = hip.addOrReplaceChild("fem" + n, CubeListBuilder.create().texOffs(0, 40).addBox(r ? -10 : 0, -1.5f, -1.5f, 10, 3, 3), PartPose.rotation(0, 0, sg * 0.7f));
                PartDefinition tib = fem.addOrReplaceChild("tib" + n, CubeListBuilder.create().texOffs(0, 46).addBox(r ? -11 : 0, -1, -1, 11, 2, 2), PartPose.offsetAndRotation(r ? -10 : 10, 0, 0, 0, 0, -sg * 1.6f));
                tib.addOrReplaceChild("tar" + n, CubeListBuilder.create().texOffs(0, 50).addBox(r ? -7 : 0, -0.5f, -0.5f, 7, 1, 1), PartPose.offsetAndRotation(r ? -11 : 11, 0, 0, 0, 0, -sg * 0.5f));
            }
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    /**
     * Idle + walk. Alternating tetrapod gait: group A = L0,R1,L2,R3 and group B = R0,L1,R2,L3 in antiphase;
     * each leg swings fore-aft on the hip and lifts (femur up, tip curled) only during its forward swing.
     */
    protected void spiderBase(float ls, float amt, float age, float yaw, float pitch, float freq) {
        amt = Math.min(amt, 1f);
        for (int s = 0; s < 2; s++) {
            float sg = s == 0 ? 1 : -1;
            for (int i = 0; i < 4; i++) {
                String n = SIDES[s] + i;
                boolean groupA = (i % 2 == 0) != (s == 0);
                float ph = ls * freq + (groupA ? 0 : Mth.PI);
                float swing = Mth.sin(ph) * 0.38f * amt;
                float lift = Math.max(0f, Mth.cos(ph)) * 0.55f * amt;
                float breathe = Mth.sin(age * 0.09f + i * 0.7f + s) * 0.025f;
                bone("hip" + n).yRot += -sg * swing;
                bone("fem" + n).zRot += sg * (lift + breathe);
                bone("tib" + n).zRot += sg * lift * 0.25f;
                bone("tar" + n).zRot += sg * lift * 0.35f;
            }
        }
        ModelPart body = bone("body");
        body.y += -Mth.abs(Mth.sin(ls * freq)) * 0.6f * amt + Mth.sin(age * 0.09f) * 0.15f;
        body.zRot += Mth.sin(ls * freq) * 0.04f * amt;
        ModelPart head = bone("head");
        head.yRot += Mth.clamp(yaw, -40f, 40f) * DEG * 0.6f;
        head.xRot += Mth.clamp(pitch, -30f, 30f) * DEG * 0.5f;
        ModelPart abd = bone("abdomen");
        abd.xRot += Mth.sin(age * 0.09f) * 0.04f - Mth.sin(ls * freq * 2) * 0.03f * amt;
        float pulse = 1f + Mth.sin(age * 0.18f) * 0.025f;
        abd.xScale *= pulse;
        abd.yScale *= pulse;
        for (int s = 0; s < 2; s++) {
            float sg = s == 0 ? 1 : -1;
            String side = SIDES[s];
            bone("palp" + side).xRot += Mth.sin(age * 0.3f + s * 1.7f) * 0.12f;
            bone("palp2" + side).xRot += Mth.sin(age * 0.3f + s * 1.7f + 0.8f) * 0.15f;
            bone("chel" + side).zRot += sg * (Mth.sin(age * 0.15f) * 0.04f);
            bone("fang" + side).zRot += sg * Math.max(0f, Mth.sin(age * 0.05f)) * 0.15f;
        }
    }

    /** Fold every leg inward (hanging, dangling). */
    protected void tuckLegs(float lift, float knee) {
        for (int s = 0; s < 2; s++) {
            float sg = s == 0 ? 1 : -1;
            for (int i = 0; i < 4; i++) {
                bone("fem" + SIDES[s] + i).zRot += sg * lift;
                bone("tib" + SIDES[s] + i).zRot += -sg * knee;
            }
        }
    }

    private static AnimClip.Ease ease(float[] k, int idx) { return k.length > idx ? EASES[(int) k[idx]] : AnimClip.Ease.SMOOTH; }

    /**
     * One leg. Rows {t, fwd, lift, knee, tip[, ease]} in degrees: fwd = hip swings forward, lift = femur up,
     * knee = tibia flexes down, tip = tarsus flexes down. Mirrored for L. Call once per leg per clip.
     */
    protected static void leg(AnimClip.Builder b, String side, int i, float[]... rows) {
        float sg = side.equals("R") ? 1 : -1;
        AnimClip.BoneBuilder hip = b.bone("hip" + side + i), fem = b.bone("fem" + side + i), tib = b.bone("tib" + side + i), tar = b.bone("tar" + side + i);
        for (float[] k : rows) {
            AnimClip.Ease e = ease(k, 5);
            hip.rot(k[0], 0, -sg * k[1], 0, e);
            fem.rot(k[0], 0, 0, sg * k[2], e);
            tib.rot(k[0], 0, 0, -sg * k[3], e);
            tar.rot(k[0], 0, 0, -sg * k[4], e);
        }
    }

    /** Same rows on the left and right leg of pair i. */
    protected static void legs(AnimClip.Builder b, int i, float[]... rows) {
        leg(b, "R", i, rows);
        leg(b, "L", i, rows);
    }

    /**
     * Mirrored bone pair ("chel", "fang", "palp", "palp2"). Rows {t, x, y, z[, ease]}: y/z are negated for L, so
     * for chel/fang/palp positive z = spread outward, negative x = swing forward/up.
     */
    protected static void pair(AnimClip.Builder b, String bone, float[]... rows) {
        for (String s : SIDES) {
            float sg = s.equals("R") ? 1 : -1;
            AnimClip.BoneBuilder bb = b.bone(bone + s);
            for (float[] k : rows) bb.rot(k[0], k[1], sg * k[2], sg * k[3], ease(k, 4));
        }
    }

    /** Single bone rotation rows {t, x, y, z[, ease]}. */
    protected static void rot(AnimClip.Builder b, String bone, float[]... rows) {
        AnimClip.BoneBuilder bb = b.bone(bone);
        for (float[] k : rows) bb.rot(k[0], k[1], k[2], k[3], ease(k, 4));
    }

    /** Single bone position rows {t, x, y, z[, ease]} (pixels, +y = down, -z = forward). */
    protected static void pos(AnimClip.Builder b, String bone, float[]... rows) {
        AnimClip.BoneBuilder bb = b.bone(bone);
        for (float[] k : rows) bb.pos(k[0], k[1], k[2], k[3], ease(k, 4));
    }

    /** Single bone scale rows {t, x, y, z[, ease]}. */
    protected static void scale(AnimClip.Builder b, String bone, float[]... rows) {
        AnimClip.BoneBuilder bb = b.bone(bone);
        for (float[] k : rows) bb.scale(k[0], k[1], k[2], k[3], ease(k, 4));
    }

    protected static float[] k(float... v) { return v; }
}
