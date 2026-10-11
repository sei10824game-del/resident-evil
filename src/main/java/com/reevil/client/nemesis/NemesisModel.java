package com.reevil.client.nemesis;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.reevil.anim.K.*;

/**
 * Nemesis on the Mr. X skeleton plus: a shoulder-fired launcher (only shown while ROCKET plays) and tentacle chains tA..tD (5 segments each; tA shows during
 * lash/snare and from form 1, tB..tD from form 2 or during the barrage). Texture 128x64 (tools/tex_nemesis.py).
 * Impacts: punch .5, combo .321/.679, choke .194/.417/.611/.833, kick .5, lash .464, snare .241/.69, rocket .464 (fires), barrage .28/.44/.6/.76, mutate .489.
 */
public class NemesisModel extends BioModel<NemesisEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "nemesis"), "main");
    private static final float D = Mth.DEG_TO_RAD;
    private static final String[] TN = {"A", "B", "C", "D"};
    private static final AnimClip C_PUNCH = haymaker(), C_COMBO = jabHook(), C_CHOKE = choke(), C_KICK = stomp(), C_WHIP = whip(), C_SNARE = snare(), C_ROCKET = rocket(),
        C_BARRAGE = barrage(), C_MUTATE = transform(), C_STAGGER = stagger();

    public NemesisModel(ModelPart root) {
        super(root);
        reg("base", "base"); reg("torso", "base/torso"); reg("neck", "base/torso/neck"); reg("head", "base/torso/neck/head"); reg("hatBrim", "base/torso/neck/head/hatBrim");
        reg("launcher", "base/torso/armR/foreR/handR/launcher");
        for (String s : new String[]{"L", "R"}) {
            reg("arm" + s, "base/torso/arm" + s); reg("fore" + s, "base/torso/arm" + s + "/fore" + s); reg("hand" + s, "base/torso/arm" + s + "/fore" + s + "/hand" + s);
            reg("thigh" + s, "base/thigh" + s); reg("coat" + s, "base/thigh" + s + "/coat" + s); reg("shin" + s, "base/thigh" + s + "/shin" + s); reg("foot" + s, "base/thigh" + s + "/shin" + s + "/foot" + s);
        }
        for (String t : TN) { String p = "base/torso/t" + t; reg("t" + t, p); for (int i = 1; i <= 5; i++) { p += "/t" + t + i; reg("t" + t + i, p); } }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5f, -1.5f, -2, 9, 3, 4), PartPose.offset(0, 6, 0));
        PartDefinition torso = base.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 8).addBox(-5, -11, -2.5f, 10, 11, 5), PartPose.offset(0, -1.5f, 0));
        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(30, 0).addBox(-1.5f, -1, -1.5f, 3, 1, 3), PartPose.offset(0, -11, 0));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 6).addBox(-3.5f, -7, -3.5f, 7, 7, 7), PartPose.offset(0, -1, 0));
        head.addOrReplaceChild("hatBrim", CubeListBuilder.create().texOffs(60, 0).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1), PartPose.offset(0, -8, 0)); // bald: placeholder bone so reg path exists
        for (int i = 0; i < 2; i++) {
            boolean l = i == 0; float sg = l ? 1 : -1; String n = l ? "L" : "R";
            PartDefinition arm = torso.addOrReplaceChild("arm" + n, CubeListBuilder.create().texOffs(0, 24).addBox(-2, 0, -2, 4, 10, 4), PartPose.offsetAndRotation(sg * 7, -10, 0, 0, 0, sg * 0.04f));
            PartDefinition fore = arm.addOrReplaceChild("fore" + n, CubeListBuilder.create().texOffs(18, 24).addBox(-2, 0, -2, 4, 10, 4), PartPose.offsetAndRotation(0, 10, 0, -0.1f, 0, 0));
            PartDefinition hand = fore.addOrReplaceChild("hand" + n, CubeListBuilder.create().texOffs(36, 24).addBox(-2, 0, -2, 4, 3, 4), PartPose.offset(0, 10, 0));
            if (!l) hand.addOrReplaceChild("launcher", CubeListBuilder.create().texOffs(54, 24).addBox(-2, -2, -14, 4, 4, 14), PartPose.offsetAndRotation(0, 3, 0, -1.4f, 0, 0));
            PartDefinition th = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create().texOffs(0, 40).addBox(-2, 0, -2, 4, 7, 4), PartPose.offset(sg * 2, 1.5f, 0));
            th.addOrReplaceChild("coat" + n, CubeListBuilder.create().texOffs(58, 40).addBox(-2.5f, -1, -3, 5, 11, 6), PartPose.ZERO);
            PartDefinition sh = th.addOrReplaceChild("shin" + n, CubeListBuilder.create().texOffs(18, 40).addBox(-2, 0, -2, 4, 7, 4), PartPose.offset(0, 7, 0));
            sh.addOrReplaceChild("foot" + n, CubeListBuilder.create().texOffs(36, 40).addBox(-2, 0, -5, 4, 2, 6), PartPose.offset(0, 7, 0));
        }
        float[][] tp = {{6.5f, -9, 2.5f, -0.6f, 0.5f}, {-6.5f, -9, 2.5f, -0.6f, -0.5f}, {3, -4, 3, -0.2f, 0.25f}, {-3, -4, 3, -0.2f, -0.25f}};
        for (int c = 0; c < 4; c++) {
            PartDefinition t = torso.addOrReplaceChild("t" + TN[c], CubeListBuilder.create(), PartPose.offsetAndRotation(tp[c][0], tp[c][1], tp[c][2], tp[c][3], 3.14159f + tp[c][4], 0));
            for (int i = 1; i <= 5; i++) t = t.addOrReplaceChild("t" + TN[c] + i, CubeListBuilder.create().texOffs(82, 24).addBox(-1.5f, -1.5f, -9, 3, 3, 9), PartPose.offsetAndRotation(0, 0, i == 1 ? 0 : -9, -0.25f, 0, 0));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    protected void baseAnim(NemesisEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        int f = e.getForm();
        float p = ls * (0.55f + f * 0.08f), s = Mth.sin(p), c = Mth.cos(p);
        ModelPart base = bone("base");
        base.y += -Math.abs(c) * 0.9f * amt + Mth.sin(age * 0.07f) * 0.1f;
        base.xRot += 0.04f + f * 0.1f + 0.04f * amt;
        bone("torso").yRot += -s * 0.12f * amt;
        bone("torso").xRot += 0.06f + f * 0.12f;
        bone("head").xRot += 0.05f - f * 0.1f + Mth.clamp(pitch, -25f, 25f) * D * 0.4f;
        bone("head").yRot += Mth.clamp(yaw, -50f, 50f) * D * 0.6f;
        for (int k = 0; k < 2; k++) {
            String n = k == 0 ? "L" : "R";
            float ph = k == 0 ? p : p + Mth.PI, sw = Mth.sin(ph), lf = Math.max(0, Mth.cos(ph));
            bone("thigh" + n).xRot += sw * 0.5f * amt; bone("shin" + n).xRot += lf * 0.55f * amt; bone("foot" + n).xRot += -lf * 0.3f * amt;
            bone("coat" + n).xRot += -sw * 0.18f * amt - Math.abs(sw) * 0.05f;
            bone("arm" + n).xRot += -sw * 0.28f * amt + Mth.sin(age * 0.06f + k) * 0.03f;
            bone("fore" + n).xRot += -0.18f * amt;
        }
        int mv = e.getMoveId();
        bone("launcher").xScale *= mv == NemesisEntity.ROCKET ? 1f : 0.001f; bone("launcher").yScale *= mv == NemesisEntity.ROCKET ? 1f : 0.001f; bone("launcher").zScale *= mv == NemesisEntity.ROCKET ? 1f : 0.001f;
        for (int q = 0; q < 4; q++) {
            boolean show = q == 0 ? (f >= 1 || mv == NemesisEntity.WHIP || mv == NemesisEntity.SNARE || mv == NemesisEntity.BARRAGE) : (f >= 2 || mv == NemesisEntity.BARRAGE);
            float v = show ? 1f : 0.001f;
            ModelPart r = bone("t" + TN[q]); r.xScale *= v; r.yScale *= v; r.zScale *= v;
            for (int i = 1; i <= 5; i++) {
                ModelPart m = bone("t" + TN[q] + i);
                m.xScale *= v; m.yScale *= v; m.zScale *= v;
                m.xRot += Mth.sin(age * 0.13f + i * 0.7f + q * 1.7f) * 0.18f;
                m.yRot += Mth.sin(age * 0.09f + i * 0.5f + q) * 0.16f;
            }
        }
        if (f >= 2) { bone("armR").zRot += 0.2f; bone("armL").zRot -= 0.2f; bone("coatL").zScale *= 0.5f; bone("coatR").zScale *= 0.5f; }
        else if (f == 1) { bone("coatL").zScale *= 0.8f; bone("coatR").zScale *= 0.8f; }
        if (e.hurtTime > 0) { float h = e.hurtTime / (float) Math.max(1, e.hurtDuration); bone("head").xRot -= h * 0.25f; bone("torso").xRot -= h * 0.12f; }
    }

    @Override
    protected void extraAnim(NemesisEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("base").xRot += s * 1.0f; bone("base").y += s * 12f;
        bone("thighL").xRot += -s * 1.3f; bone("thighR").xRot += -s * 0.9f; bone("shinL").xRot += s * 1.5f; bone("shinR").xRot += s * 1.2f;
        bone("head").xRot += s * 0.8f; bone("armL").xRot += s * 0.5f; bone("armR").xRot += s * 0.4f;
    }

    @Override
    protected AnimClip clip(NemesisEntity e, int id) {
        switch (id) {
            case NemesisEntity.PUNCH: return C_PUNCH;
            case NemesisEntity.COMBO: return C_COMBO;
            case NemesisEntity.CHOKE: return C_CHOKE;
            case NemesisEntity.KICK: return C_KICK;
            case NemesisEntity.WHIP: return C_WHIP;
            case NemesisEntity.SNARE: return C_SNARE;
            case NemesisEntity.ROCKET: return C_ROCKET;
            case NemesisEntity.BARRAGE: return C_BARRAGE;
            case NemesisEntity.MUTATE: return C_MUTATE;
            case NemesisEntity.STAGGER: return C_STAGGER;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ new clips
    /** Tentacle chain as a travelling wave: rows {t, x, y[, ease]}; tentacles point backwards at rest so +x lifts/lashes forward-over. */
    private static void tent(AnimClip.Builder b, String c, float delay, float[]... rows) {
        for (int i = 1; i <= 5; i++) {
            float[][] r = new float[rows.length][];
            for (int j = 0; j < rows.length; j++) {
                float t = Math.min(1f, rows[j][0] * (1f - 5f * delay) + (i - 1) * delay);
                r[j] = rows[j].length > 3 ? new float[]{t, rows[j][1] * (1.1f - i * 0.1f), rows[j][2] * (0.5f + i * 0.12f), 0, rows[j][3]} : new float[]{t, rows[j][1] * (1.1f - i * 0.1f), rows[j][2] * (0.5f + i * 0.12f), 0};
            }
            rot(b, "t" + c + i, r);
        }
    }

    /** 28t, lash @13 (.464): the shoulder tentacle rears back over his head, then whips forward; torso twists behind it. */
    private static AnimClip whip() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "tA", k(0, 0, 0, 0), k(0.3f, 55, 30, 0), k(0.464f, -150, -20, 0, SNAP), k(0.7f, -110, 10, 0), k(1, 0, 0, 0));
        tent(b, "A", 0.014f, k(0, 0, 0), k(0.3f, 50, 40), k(0.464f, -60, -50, SNAP), k(0.6f, -30, 20), k(0.8f, -10, 0), k(1, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -8, 24, 0), k(0.464f, 12, -26, 0, SNAP), k(0.75f, 6, -6, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -6, 18, 0), k(0.464f, 8, -18, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, 30, 0, 24), k(0.464f, -60, 0, 14, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -20, 0, -20), k(0.464f, 10, 0, -10), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 1, 2), k(0.464f, 0, 1, -3, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 58t: tentacle shoots out and wraps the prey (@14), he leans back hauling it in (to @40), then buries a fist in it. */
    private static AnimClip snare() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "tA", k(0, 0, 0, 0), k(0.15f, 40, 10, 0), k(0.241f, -140, 0, 0, SNAP), k(0.7f, -140, 0, 0), k(1, 0, 0, 0));
        tent(b, "A", 0.012f, k(0, 0, 0), k(0.15f, 40, 20), k(0.241f, -52, 0, SNAP), k(0.32f, -52, 36), k(0.5f, -40, 20), k(0.69f, -20, 10), k(0.8f, -20, 0), k(1, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, -6, 10, 0), k(0.241f, 12, 0, 0, SNAP), k(0.55f, -22, 0, 0), k(0.69f, 24, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.55f, -12, 0, 0), k(0.69f, 14, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.5f, 40, 0, 20), k(0.69f, -92, 0, 4, SNAP), k(0.9f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.5f, -60, 0, 0), k(0.69f, -4, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.55f, 0, 1.5f, 3), k(0.69f, 0, 1.5f, -4, SNAP), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.55f, 22, 0, 0), k(0.69f, -14, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.55f, -16, 0, 0), k(0.69f, 10, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 56t, launch @26 (.464): drops to a braced stance, hoists the launcher onto the shoulder, aims, fires - the recoil rocks the whole body - then lowers it. */
    private static AnimClip rocket() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.2f, -100, -20, 10), k(0.3f, -108, -26, 6), k(0.464f, -110, -26, 6), k(0.5f, -102, -24, 6, SNAP), k(0.8f, -60, -6, 6), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.2f, -70, 0, 0), k(0.464f, -74, 0, 0), k(0.5f, -62, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.3f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.2f, -76, 22, -8), k(0.464f, -80, 24, -8), k(0.8f, -40, 8, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.2f, -40, 0, 0), k(0.464f, -44, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.2f, -4, -16, 0), k(0.464f, -2, -18, 0), k(0.5f, -12, -14, 0, SNAP), k(0.7f, -4, -12, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.2f, 8, 14, 0), k(0.464f, 10, 14, 0), k(0.5f, 2, 12, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.2f, 0, 2, 0), k(0.464f, 0, 2, 0), k(0.5f, 0, 2, 3, SNAP), k(0.7f, 0, 1.5f, 1), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.2f, -22, 0, 0), k(0.5f, -22, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.2f, 18, 0, 0), k(0.5f, 18, 0, 0), k(1, 0, 0, 0));
        pair2(b, "shin", k(0, 0, 0, 0), k(0.2f, 30, 0, 0), k(0.5f, 30, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.2f, 14, 0, 0), k(0.5f, 14, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 50t, lashes @14/22/30/38: all four tentacles flail in turn around the body while he spins on the spot, arms flung wide. */
    private static AnimClip barrage() {
        AnimClip.Builder b = AnimClip.create();
        String[] c = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            float hit = 0.28f + i * 0.16f, sg = i % 2 == 0 ? 1 : -1;
            rot(b, "t" + c[i], k(0, 0, 0, 0), k(hit - 0.12f, 60, sg * 40, 0), k(hit, -140, -sg * 50, 0, SNAP), k(hit + 0.14f, -80, sg * 10, 0), k(1, 0, 0, 0));
            tent(b, c[i], 0.01f, k(0, 0, 0), k(hit - 0.12f, 50, sg * 40), k(hit, -50, -sg * 60, SNAP), k(hit + 0.14f, -20, sg * 20), k(Math.min(1f, hit + 0.3f), 0, 0), k(1, 0, 0));
        }
        rot(b, "torso", k(0, 0, 0, 0), k(0.16f, -6, 24, 0), k(0.28f, 8, -30, 0, SNAP), k(0.44f, 8, 30, 0, SNAP), k(0.6f, 8, -30, 0, SNAP), k(0.76f, 8, 30, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.28f, 0, -22, 0), k(0.44f, 0, 22, 0), k(0.6f, 0, -22, 0), k(0.76f, 0, 22, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.16f, -30, 0, -50), k(0.76f, -34, 0, -60), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.16f, -30, 0, 50), k(0.76f, -34, 0, 60), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.16f, 0, 1, 1), k(0.76f, 0, 1, 1), k(1, 0, 0, 0));
        return b.build();
    }

    /** Different rows for L and R: rowsL..., rowsR... split at the second t=0 key. */
    private static void pair2(AnimClip.Builder b, String prefix, float[]... rows) {
        java.util.List<float[]> l = new java.util.ArrayList<>(), r = new java.util.ArrayList<>();
        java.util.List<float[]> cur = l; int zeros = 0;
        for (float[] row : rows) { if (row[0] == 0f && ++zeros == 2) cur = r; cur.add(row); }
        rot(b, prefix + "L", l.toArray(new float[0][])); rot(b, prefix + "R", r.toArray(new float[0][]));
    }

    // ------------------------------------------------------------------ clips (right arm = punching arm)
    /** 30t, impact @15 (.5): shoulders wind away, fist drawn way back and low, weight onto the back foot, then a huge arcing haymaker with the whole torso. */
    private static AnimClip haymaker() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.4f, 6, 38, 4), k(0.5f, 14, -42, -3, SNAP), k(0.7f, 10, -22, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.4f, -4, 18, 0), k(0.5f, 8, -22, 0, SNAP), k(0.7f, 4, -8, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.4f, 0, 1.5f, 2.5f), k(0.5f, 0, 1f, -3f, SNAP), k(0.8f, 0, 0.5f, -1), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, -4, 20, 0), k(0.5f, 8, -26, 0, SNAP), k(0.7f, 6, -12, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.4f, 55, -10, 55), k(0.46f, -40, 20, 75, SNAP), k(0.5f, -95, 10, 35, SNAP), k(0.62f, -60, 0, 10), k(0.85f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.4f, -50, 0, 0), k(0.5f, -6, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.4f, 10, 0, 0), k(0.5f, 0, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.4f, -45, 0, -10), k(0.5f, -20, -10, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.4f, -80, 0, 0), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.4f, 12, 0, 0), k(0.5f, -18, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.4f, -16, 0, 0), k(0.5f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinL", k(0, 0, 0, 0), k(0.4f, 10, 0, 0), k(0.5f, 24, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }


    /** 28t, jab @9 (.321) then hooking cross @19 (.679): quick straight jab, retract, hips rotate into a looping hook. */
    private static AnimClip jabHook() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.18f, 4, 12, 0), k(0.321f, 8, -20, 0, SNAP), k(0.45f, 4, -4, 0), k(0.55f, 4, -28, 0), k(0.679f, 12, 34, 0, SNAP), k(0.85f, 6, 10, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.321f, 4, -8, 0), k(0.55f, 2, -20, 0), k(0.679f, 6, 22, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.321f, 0, 0.5f, -2), k(0.679f, 0, 0.8f, -3), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.321f, 4, -10, 0), k(0.679f, 6, 18, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.18f, -50, 0, 12), k(0.321f, -88, -10, 4, SNAP), k(0.45f, -50, 0, 12), k(0.55f, -40, 0, 30), k(0.679f, -60, 55, 40, SNAP), k(0.85f, -20, 10, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.18f, -85, 0, 0), k(0.321f, -4, 0, 0, SNAP), k(0.45f, -85, 0, 0), k(0.679f, -60, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.18f, -55, 0, -10), k(0.321f, -50, 0, -10), k(0.55f, -85, -10, 0), k(0.679f, -100, 0, 0, SNAP), k(0.85f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.18f, -90, 0, 0), k(0.55f, -95, 0, 0), k(0.679f, -20, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.321f, -10, 0, 0), k(0.679f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighR", k(0, 0, 0, 0), k(0.321f, 12, 0, 0), k(0.679f, -12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 72t: left hand seizes the throat @14 (.194), lifts him, chokes (@30,@44), shakes, then slam-throws @60 (.833). */
    private static AnimClip choke() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.1f, -40, 0, 12), k(0.194f, -92, -14, 4, SNAP), k(0.42f, -105, -6, 0), k(0.68f, -118, 0, 0), k(0.833f, -35, 0, 0, SNAP), k(0.92f, -50, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.194f, -10, 0, 0), k(0.42f, -35, 0, 0), k(0.68f, -45, 0, 0), k(0.833f, -5, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.194f, -30, 0, 0), k(0.68f, -50, 0, 0), k(0.833f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.1f, -35, 0, -10), k(0.194f, -70, 10, -4), k(0.42f, -84, 8, 0), k(0.68f, -96, 0, 0), k(0.833f, -25, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.42f, -30, 0, 0), k(0.68f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.1f, 10, 0, 0), k(0.194f, 18, 0, 0, SNAP), k(0.42f, 12, 5, 0), k(0.5f, 12, -5, 0), k(0.611f, 12, 5, 0), k(0.7f, 6, 0, 0), k(0.833f, 26, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.194f, 22, 0, 0), k(0.7f, 18, 0, 0), k(0.833f, -6, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.194f, 0, 0.5f, -2), k(0.7f, 0, 0, -1), k(0.833f, 0, 1.5f, -4, SNAP), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.194f, -10, 0, 0), k(0.833f, 8, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.194f, 14, 0, 0), k(0.833f, -18, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 24t, front kick @12 (.5): knee drives up, then a piston stomp straight out; supporting leg locked, torso leaning back for balance. */
    private static AnimClip stomp() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "thighR", k(0, 0, 0, 0), k(0.3f, -85, 0, 0), k(0.5f, -90, 0, 0, SNAP), k(0.75f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinR", k(0, 0, 0, 0), k(0.3f, 100, 0, 0), k(0.5f, 6, 0, 0, SNAP), k(0.75f, 40, 0, 0), k(1, 0, 0, 0));
        rot(b, "footR", k(0, 0, 0, 0), k(0.3f, 20, 0, 0), k(0.5f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.3f, 6, 0, 0), k(0.5f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -8, 0, 0), k(0.5f, -18, 0, 0, SNAP), k(0.8f, -6, 0, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.5f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -30, 0, -20), k(0.5f, -20, 0, -35), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, 25, 0, 20), k(0.5f, 40, 0, 30), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.5f, 12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 90t, change @44 (.489): head thrown back, arms flung wide, torso heaving as the coat bursts; calms into the hunched Super Tyrant stance. */
    private static AnimClip transform() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, -10, 0, 0), k(0.3f, -28, 4, 0), k(0.36f, -30, -4, 0), k(0.42f, -28, 4, 0), k(0.489f, -34, 0, 0, SNAP), k(0.6f, -26, 0, 0), k(0.85f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.15f, 20, 0, 0), k(0.3f, -50, 0, 0), k(0.489f, -62, 0, 0, SNAP), k(0.6f, -44, 6, 0), k(0.72f, -40, -6, 0), k(0.88f, 14, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -20, 0, -40), k(0.489f, -30, 0, -78, SNAP), k(0.7f, -25, 0, -60), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -20, 0, 40), k(0.489f, -30, 0, 82, SNAP), k(0.7f, -25, 0, 62), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.489f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.489f, -20, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, -1, 1), k(0.489f, 0, -1.5f, 1.5f), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.3f, -4, 0, 6), k(0.489f, -10, 0, 12), k(1, 0, 0, 0));
        return b.build();
    }

    /** 90t kneel: the legs give way onto one knee, head and shoulders slumped, fist planted, holds, then pushes back up to full height. */
    private static AnimClip stagger() {
        AnimClip.Builder b = AnimClip.create();
        pos(b, "base", k(0, 0, 0, 0), k(0.12f, 0, 5, 3), k(0.22f, 0, 8.5f, 4), k(0.7f, 0, 8.5f, 4), k(0.85f, 0, 4, 2), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.22f, 18, 0, 0), k(0.7f, 20, 0, 0), k(0.85f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.22f, 26, 0, 0), k(0.7f, 30, 0, 0), k(0.85f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.22f, 30, 0, 0), k(0.7f, 34, 0, 0), k(0.85f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.22f, -88, 0, 6), k(0.7f, -88, 0, 6), k(0.85f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinL", k(0, 0, 0, 0), k(0.22f, 90, 0, 0), k(0.7f, 90, 0, 0), k(0.85f, 40, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighR", k(0, 0, 0, 0), k(0.22f, -20, 0, -6), k(0.7f, -20, 0, -6), k(0.85f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinR", k(0, 0, 0, 0), k(0.22f, 105, 0, 0), k(0.7f, 105, 0, 0), k(0.85f, 40, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.22f, -10, 0, 8), k(0.7f, -10, 0, 8), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.22f, -62, 0, -6), k(0.7f, -62, 0, -6), k(0.85f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.22f, -30, 0, 0), k(0.7f, -30, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }
}
