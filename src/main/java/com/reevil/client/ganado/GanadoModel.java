package com.reevil.client.ganado;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.ganado.GanadoEntity;
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
 * Ganado villager + Plaga forms on one rig (also used by the chainsaw man). Faces -Z. Texture 128x64 (tools/tex_ganado.py).
 * base > torso > neck > head > hat ; neck > bulb, whipA/B (> 3 segs); torso > armL/R > foreL/R > handL/R (> weapon variants) ; foreL/R > bladeL/R ; torso > sl0..3 (> 3 segs) ; base > thighL/R > shinL/R > footL/R.
 * Weapons shown by entity.getWeapon(); plaga parts by form (1 whips, 2 scythe blades, 3 spider legs). Impacts: swing .46, thrust .5, grab .23/.47/.7, burst .36, lash .43/.7, scythe .33/.63, pierce .47.
 */
public class GanadoModel extends BioModel<GanadoEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "ganado"), "main");
    private static final float D = Mth.DEG_TO_RAD;
    private static final String[] W = {"wPitch", "wAxe", "wSickle", "wTorch", "wSaw"};
    private static final AnimClip C_SWING = swing(), C_STAB = stab(), C_GRAB = grab(), C_BURST = burst(), C_LASH = lash(), C_SCYTHE = scythe(), C_PIERCE = pierce();

    public GanadoModel(ModelPart root) {
        super(root);
        reg("base", "base"); reg("torso", "base/torso"); reg("neck", "base/torso/neck"); reg("head", "base/torso/neck/head"); reg("hat", "base/torso/neck/head/hat"); reg("bulb", "base/torso/neck/bulb");
        for (String s : new String[]{"A", "B"}) { String p = "base/torso/neck/whip" + s; reg("whip" + s, p); for (int i = 1; i <= 3; i++) { p += "/w" + s + i; reg("w" + s + i, p); } }
        for (String s : new String[]{"L", "R"}) {
            reg("arm" + s, "base/torso/arm" + s); reg("fore" + s, "base/torso/arm" + s + "/fore" + s); reg("hand" + s, "base/torso/arm" + s + "/fore" + s + "/hand" + s);
            reg("blade" + s, "base/torso/arm" + s + "/fore" + s + "/blade" + s);
            reg("thigh" + s, "base/thigh" + s); reg("shin" + s, "base/thigh" + s + "/shin" + s); reg("foot" + s, "base/thigh" + s + "/shin" + s + "/foot" + s);
        }
        for (String w : W) reg(w, "base/torso/armR/foreR/handR/" + w);
        for (int l = 0; l < 4; l++) { String p = "base/torso/sl" + l; reg("sl" + l, p); for (int i = 1; i <= 3; i++) { p += "/s" + l + i; reg("s" + l + i, p); } }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-4, -1.5f, -2, 8, 3, 4), PartPose.offset(0, 11.5f, 0));
        PartDefinition torso = base.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 8).addBox(-4, -10, -2, 8, 10, 4), PartPose.offset(0, -1.5f, 0));
        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(26, 0).addBox(-1.5f, -2, -1.5f, 3, 2, 3), PartPose.offset(0, -10, 0));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 6).addBox(-3.5f, -7, -3.5f, 7, 7, 7), PartPose.offset(0, -1.5f, 0));
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(56, 0).addBox(-5, -1, -5, 10, 1, 10).texOffs(56, 12).addBox(-3, -4, -3, 6, 3, 6), PartPose.offset(0, -6.5f, 0));
        neck.addOrReplaceChild("bulb", CubeListBuilder.create().texOffs(100, 0).addBox(-2.5f, -5, -2.5f, 5, 5, 5), PartPose.offset(0, -1.5f, 0));
        for (int a = 0; a < 2; a++) {
            float sg = a == 0 ? 1 : -1; String n = a == 0 ? "A" : "B";
            PartDefinition w = neck.addOrReplaceChild("whip" + n, CubeListBuilder.create(), PartPose.offsetAndRotation(sg * 1.5f, -2, 0, -0.9f, sg * 0.5f, 0));
            for (int i = 1; i <= 3; i++) w = w.addOrReplaceChild("w" + n + i, CubeListBuilder.create().texOffs(0, 50).addBox(-1, -1, -7, 2, 2, 7), PartPose.offsetAndRotation(0, 0, i == 1 ? 0 : -7, -0.2f, 0, 0));
        }
        for (int s = 0; s < 2; s++) {
            boolean l = s == 0; float sg = l ? 1 : -1; String n = l ? "L" : "R";
            PartDefinition arm = torso.addOrReplaceChild("arm" + n, CubeListBuilder.create().texOffs(0, 24).addBox(-1.5f, 0, -1.5f, 3, 9, 3), PartPose.offsetAndRotation(sg * 5.5f, -9, 0, 0, 0, sg * 0.05f));
            PartDefinition fore = arm.addOrReplaceChild("fore" + n, CubeListBuilder.create().texOffs(14, 24).addBox(-1.5f, 0, -1.5f, 3, 9, 3), PartPose.offsetAndRotation(0, 9, 0, -0.15f, 0, 0));
            PartDefinition hand = fore.addOrReplaceChild("hand" + n, CubeListBuilder.create().texOffs(28, 24).addBox(-1.5f, 0, -1.5f, 3, 3, 3), PartPose.offset(0, 9, 0));
            fore.addOrReplaceChild("blade" + n, CubeListBuilder.create().texOffs(22, 50).addBox(-0.5f, 2, -2.5f, 1, 13, 3), PartPose.offset(0, 4, 0));
            if (!l) {
                hand.addOrReplaceChild("wPitch", CubeListBuilder.create().texOffs(54, 24).addBox(-0.5f, -6, -0.5f, 1, 22, 1).texOffs(60, 24).addBox(-2.5f, 16, -0.5f, 1, 6, 1).texOffs(60, 24).addBox(-0.5f, 16, -0.5f, 1, 7, 1).texOffs(60, 24).addBox(1.5f, 16, -0.5f, 1, 6, 1), PartPose.offsetAndRotation(0, 2, 0, -1.3f, 0, 0));
                hand.addOrReplaceChild("wAxe", CubeListBuilder.create().texOffs(54, 24).addBox(-0.5f, -4, -0.5f, 1, 16, 1).texOffs(66, 24).addBox(-0.5f, 9, -3.5f, 1, 5, 6), PartPose.offsetAndRotation(0, 2, 0, -1.3f, 0, 0));
                hand.addOrReplaceChild("wSickle", CubeListBuilder.create().texOffs(54, 24).addBox(-0.5f, -3, -0.5f, 1, 10, 1).texOffs(82, 24).addBox(-0.5f, 6, -7, 1, 2, 8), PartPose.offsetAndRotation(0, 2, 0, -1.3f, 0, 0));
                hand.addOrReplaceChild("wTorch", CubeListBuilder.create().texOffs(54, 24).addBox(-0.5f, -3, -0.5f, 1, 12, 1).texOffs(66, 32).addBox(-1.5f, 9, -1.5f, 3, 3, 3), PartPose.offsetAndRotation(0, 2, 0, -1.3f, 0, 0));
                hand.addOrReplaceChild("wSaw", CubeListBuilder.create().texOffs(84, 38).addBox(-1.5f, -2, -2, 3, 4, 12).texOffs(100, 24).addBox(-0.5f, -1.5f, -14, 1, 3, 12), PartPose.offsetAndRotation(0, 2, 0, -0.3f, 0, 0));
            }
            PartDefinition th = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create().texOffs(0, 38).addBox(-2, 0, -2, 4, 8, 4), PartPose.offset(sg * 2, 1.5f, 0));
            PartDefinition sh = th.addOrReplaceChild("shin" + n, CubeListBuilder.create().texOffs(18, 38).addBox(-2, 0, -2, 4, 8, 4), PartPose.offset(0, 8, 0));
            sh.addOrReplaceChild("foot" + n, CubeListBuilder.create().texOffs(36, 38).addBox(-2, 0, -4, 4, 2, 5), PartPose.offset(0, 8, 0));
        }
        float[][] sp = {{3, -8, 2.5f, 0.6f, 0.9f}, {-3, -8, 2.5f, 0.6f, -0.9f}, {4, -4, 2.5f, 0.9f, 1.5f}, {-4, -4, 2.5f, 0.9f, -1.5f}};
        for (int l = 0; l < 4; l++) {
            PartDefinition s = torso.addOrReplaceChild("sl" + l, CubeListBuilder.create(), PartPose.offsetAndRotation(sp[l][0], sp[l][1], sp[l][2], -sp[l][3], 3.14159f + sp[l][4] * 0.5f, 0));
            for (int i = 1; i <= 3; i++) s = s.addOrReplaceChild("s" + l + i, CubeListBuilder.create().texOffs(34, 50).addBox(-1, -1, -9, 2, 2, 9), PartPose.offsetAndRotation(0, 0, i == 1 ? 0 : -9, i == 1 ? 0 : 0.7f, 0, 0));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    private static void vis(ModelPart p, boolean on) { float v = on ? 1f : 0.001f; p.xScale *= v; p.yScale *= v; p.zScale *= v; }

    @Override
    protected void baseAnim(GanadoEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        int f = e.getForm(), wp = e.getWeapon();
        float p = ls * (0.75f + f * 0.1f), s = Mth.sin(p), c = Mth.cos(p);
        ModelPart base = bone("base");
        base.y += -Math.abs(c) * 0.8f * amt + Mth.sin(age * 0.09f) * 0.1f;
        base.xRot += 0.05f + 0.06f * amt + (f == 3 ? 0.25f : 0f);
        bone("torso").yRot += -s * 0.14f * amt;
        bone("torso").xRot += 0.08f + (f >= 1 ? 0.15f : 0f);
        bone("head").yRot += Mth.clamp(yaw, -50f, 50f) * D * 0.6f + (f == 0 ? Mth.sin(age * 0.05f) * 0.06f : 0f);
        bone("head").xRot += Mth.clamp(pitch, -25f, 25f) * D * 0.4f + (f == 0 ? 0.1f : 0f);
        bone("head").zRot += f == 0 ? Mth.sin(age * 0.04f) * 0.05f : 0f;
        for (int k = 0; k < 2; k++) {
            String n = k == 0 ? "L" : "R";
            float ph = k == 0 ? p : p + Mth.PI, sw = Mth.sin(ph), lf = Math.max(0, Mth.cos(ph));
            bone("thigh" + n).xRot += sw * 0.6f * amt; bone("shin" + n).xRot += lf * 0.7f * amt; bone("foot" + n).xRot += -lf * 0.3f * amt;
            if (f != 2) { bone("arm" + n).xRot += -sw * 0.45f * amt + (e.getTarget() != null ? -0.5f : 0f); bone("fore" + n).xRot += -0.25f * amt - (e.getTarget() != null ? 0.5f : 0f); }
        }
        // held weapon variants
        for (int i = 0; i < W.length; i++) vis(bone(W[i]), f == 0 && wp == i + 1);
        bone("wTorch").zRot += Mth.sin(age * 0.8f) * 0.02f;
        bone("handL").xRot += 0.2f;
        if (f == 0) bone("armR").xRot += wp != 0 ? -0.6f : 0f;
        // plaga: head gone, bulb + limbs by form
        vis(bone("head"), f == 0);
        vis(bone("bulb"), f >= 1);
        if (f >= 1) { bone("bulb").xRot += Mth.sin(age * 0.3f) * 0.12f; bone("bulb").yScale *= 1f + Mth.sin(age * 0.4f) * 0.1f; }
        for (String sd : new String[]{"A", "B"}) {
            vis(bone("whip" + sd), f == 1 || f == 2 && false);
            for (int i = 1; i <= 3; i++) { vis(bone("w" + sd + i), f == 1); bone("w" + sd + i).xRot += Mth.sin(age * 0.2f + i * 0.8f + (sd.equals("A") ? 0 : 2)) * 0.25f; bone("w" + sd + i).yRot += Mth.sin(age * 0.15f + i) * 0.2f; }
        }
        for (String sd : new String[]{"L", "R"}) { vis(bone("blade" + sd), f == 2); if (f == 2) { bone("hand" + sd).xScale *= 0.001f; bone("hand" + sd).yScale *= 0.001f; bone("hand" + sd).zScale *= 0.001f; bone("arm" + sd).zRot += (sd.equals("L") ? 1 : -1) * 0.4f; bone("fore" + sd).xRot += -0.7f; } }
        for (int l = 0; l < 4; l++) {
            vis(bone("sl" + l), f == 3);
            for (int i = 1; i <= 3; i++) { vis(bone("s" + l + i), f == 3); bone("s" + l + i).xRot += Mth.sin(age * 0.18f + l * 1.1f + i * 0.6f) * 0.12f; }
            if (f == 3) bone("sl" + l).xRot += Math.max(0, Mth.sin(p + l * 1.57f)) * 0.3f * amt;
        }
        if (e.hurtTime > 0) { float h = e.hurtTime / (float) Math.max(1, e.hurtDuration); bone("torso").xRot -= h * 0.25f; bone("head").xRot -= h * 0.4f; bone("neck").xRot -= h * 0.2f; }
    }

    @Override
    protected void extraAnim(GanadoEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("base").xRot += s * 1.4f; bone("base").y += s * 9f;
        bone("thighL").xRot += -s * 0.8f; bone("thighR").xRot += -s * 0.5f; bone("shinL").xRot += s * 0.9f; bone("shinR").xRot += s * 0.6f;
        bone("armL").xRot += s * 1.2f; bone("armR").xRot += s * 0.8f;
    }

    @Override
    protected AnimClip clip(GanadoEntity e, int id) {
        switch (id) {
            case GanadoEntity.SWING: return C_SWING;
            case GanadoEntity.STAB: return C_STAB;
            case GanadoEntity.GRAB: return C_GRAB;
            case GanadoEntity.BURST: return C_BURST;
            case GanadoEntity.LASH: return C_LASH;
            case GanadoEntity.SCYTHE: return C_SCYTHE;
            case GanadoEntity.PIERCE: return C_PIERCE;
            default: return null;
        }
    }

    private static void chain(AnimClip.Builder b, String prefix, int n, float delay, float[]... rows) { // rows {t, x, y[, ease]}
        for (int i = 1; i <= n; i++) {
            float[][] r = new float[rows.length][];
            for (int j = 0; j < rows.length; j++) {
                float t = Math.min(1f, rows[j][0] * (1f - n * delay) + (i - 1) * delay);
                r[j] = rows[j].length > 3 ? new float[]{t, rows[j][1] * (1.1f - i * 0.15f), rows[j][2] * (0.5f + i * 0.2f), 0, rows[j][3]} : new float[]{t, rows[j][1] * (1.1f - i * 0.15f), rows[j][2] * (0.5f + i * 0.2f), 0};
            }
            rot(b, prefix + i, r);
        }
    }

    /** 26t, hit @12 (.46): tool hoisted over the shoulder, torso coiled, then a downward diagonal chop with the whole body. */
    private static AnimClip swing() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -150, 10, 18), k(0.46f, -30, -18, 0, SNAP), k(0.7f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.3f, -50, 0, 0), k(0.46f, -10, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -70, 0, -20), k(0.46f, -50, 0, -10), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -14, 28, 0), k(0.46f, 26, -26, 0, SNAP), k(0.8f, 12, -8, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -10, 12, 0), k(0.46f, 18, -12, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 1, 1.5f), k(0.46f, 0, 1.5f, -2.5f, SNAP), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.3f, 14, 0, 0), k(0.46f, -20, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.3f, -10, 0, 0), k(0.46f, 12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    private static void pair2(AnimClip.Builder b, String prefix, float[]... rows) {
        java.util.List<float[]> l = new java.util.ArrayList<>(), r = new java.util.ArrayList<>();
        java.util.List<float[]> cur = l; int zeros = 0;
        for (float[] row : rows) { if (row[0] == 0f && ++zeros == 2) cur = r; cur.add(row); }
        rot(b, prefix + "L", l.toArray(new float[0][])); rot(b, prefix + "R", r.toArray(new float[0][]));
    }

    /** 28t, thrust @14 (.5): tool drawn back to the hip, a lunging two-handed jab straight ahead, then pulled free. */
    private static AnimClip stab() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.35f, 30, 0, 20), k(0.5f, -85, -14, -4, SNAP), k(0.75f, -70, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.35f, -70, 0, 0), k(0.5f, -8, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.35f, -40, 0, -16), k(0.5f, -80, 20, -10), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.35f, -8, 24, 0), k(0.5f, 20, -18, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.35f, 0, 1.5f, 2), k(0.5f, 0, 2, -5, SNAP), k(0.85f, 0, 1, -2), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.35f, 20, 0, 0), k(0.5f, -40, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.35f, -14, 0, 0), k(0.5f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.5f, 12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 60t: seizes (@14), chokes (@28), then savages the neck (@42): both arms clamp, body leans in, head darting. */
    private static AnimClip grab() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armL", k(0, 0, 0, 0), k(0.12f, -70, 0, -12), k(0.23f, -92, 14, -4, SNAP), k(0.7f, -96, 8, 0), k(0.9f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.12f, -70, 0, 12), k(0.23f, -92, -14, 4, SNAP), k(0.7f, -96, -8, 0), k(0.9f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.23f, -30, 0, 0), k(0.7f, -50, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.23f, -30, 0, 0), k(0.7f, -50, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.12f, 6, 0, 0), k(0.23f, 20, 0, 0, SNAP), k(0.7f, 28, 0, 0), k(0.9f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.23f, 16, 0, 0), k(0.4f, 24, 10, 0), k(0.47f, 18, -10, 0), k(0.6f, 24, 10, 0), k(0.7f, 30, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.23f, 0, 1, -2), k(0.7f, 0, 1.5f, -3), k(1, 0, 0, 0));
        return b.build();
    }

    /** 44t, burst @16 (.36): convulses, hands clawing at the throat, spine arching back as the skull splits and the parasite erupts. */
    private static AnimClip burst() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.1f, 12, 0, 0), k(0.2f, -22, 6, 0), k(0.28f, -26, -6, 0), k(0.36f, -34, 0, 0, SNAP), k(0.6f, -20, 0, 0), k(0.85f, 6, 0, 0), k(1, 0, 0, 0));
        rot(b, "neck", k(0, 0, 0, 0), k(0.2f, -20, 8, 0), k(0.36f, -40, 0, 0, SNAP), k(0.7f, -18, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.1f, 20, 0, 0), k(0.2f, -30, 14, 0), k(0.28f, -34, -14, 0), k(0.36f, -50, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.1f, -120, 0, -20), k(0.36f, -60, 0, -50, SNAP), k(0.7f, -40, 0, -30), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.1f, -120, 0, 20), k(0.36f, -60, 0, 50, SNAP), k(0.7f, -40, 0, 30), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.1f, -110, 0, 0), k(0.36f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.1f, -110, 0, 0), k(0.36f, -20, 0, 0), k(1, 0, 0, 0));
        scale(b, "bulb", k(0, 1, 1, 1), k(0.36f, 1.5f, 1.6f, 1.5f), k(0.6f, 1, 1, 1), k(1, 1, 1, 1));
        pos(b, "base", k(0, 0, 0, 0), k(0.2f, 0, 1, 1), k(0.36f, 0, -0.5f, 2), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.2f, 0, 0, 8), k(0.36f, -8, 0, 14), k(1, 0, 0, 0));
        return b.build();
    }

    /** 30t, lashes @13 (.43) and @21 (.7): both neck tentacles rear overhead and whip down/across in turn. */
    private static AnimClip lash() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "whipA", k(0, 0, 0, 0), k(0.25f, -60, 30, 0), k(0.43f, 80, -40, 0, SNAP), k(0.7f, 20, 0, 0), k(1, 0, 0, 0));
        rot(b, "whipB", k(0, 0, 0, 0), k(0.5f, -60, -30, 0), k(0.7f, 80, 40, 0, SNAP), k(0.9f, 20, 0, 0), k(1, 0, 0, 0));
        chain(b, "wA", 3, 0.03f, k(0, 0, 0), k(0.25f, -40, 20), k(0.43f, 50, -40, SNAP), k(0.6f, 10, 10), k(1, 0, 0));
        chain(b, "wB", 3, 0.03f, k(0, 0, 0), k(0.5f, -40, -20), k(0.7f, 50, 40, SNAP), k(0.85f, 10, -10), k(1, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.25f, -14, 14, 0), k(0.43f, 14, -16, 0, SNAP), k(0.5f, 6, 8, 0), k(0.7f, 14, 16, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "bulb", k(0, 0, 0, 0), k(0.25f, -30, 0, 0), k(0.43f, 20, 0, 0), k(0.7f, 20, 0, 0), k(1, 0, 0, 0));
        pair(b, "arm", k(0, 0, 0, 0), k(0.25f, -30, 0, 28), k(0.7f, -20, 0, 20), k(1, 0, 0, 0));
        return b.build();
    }

    /** 30t, slashes @10 (.33) and @19 (.63): blade-arms crossed in front, flung wide in an X-shaped double slash, wings of the torso twisting. */
    private static AnimClip scythe() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -120, 40, 40), k(0.33f, -40, -60, 10, SNAP), k(0.5f, -90, -20, 30), k(0.63f, -20, 70, 20, SNAP), k(0.85f, -20, 0, 10), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, -30, 0, -20), k(0.33f, -20, 0, -20), k(0.5f, -120, -40, -40), k(0.63f, -40, 60, -10, SNAP), k(0.85f, -20, 0, -10), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.15f, -50, 0, 0), k(0.33f, -8, 0, 0, SNAP), k(0.63f, -8, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.5f, -50, 0, 0), k(0.63f, -8, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 6, 32, 0), k(0.33f, 14, -34, 0, SNAP), k(0.5f, 8, -10, 0), k(0.63f, 14, 36, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "bulb", k(0, 0, 0, 0), k(0.33f, 20, -20, 0), k(0.63f, 20, 20, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.33f, 0, 1, -2), k(0.63f, 0, 1, -2), k(1, 0, 0, 0));
        return b.build();
    }

    /** 30t, impact @14 (.47): spider legs fold back, the body rears, then a lunge with one leg driven like a lance. */
    private static AnimClip pierce() {
        AnimClip.Builder b = AnimClip.create();
        for (int l = 0; l < 4; l++) {
            float lead = l < 2 ? 1f : 0.4f;
            rot(b, "sl" + l, k(0, 0, 0, 0), k(0.3f, 60 * lead, 0, 0), k(0.47f, -90 * lead, 0, 0, SNAP), k(0.75f, -30 * lead, 0, 0), k(1, 0, 0, 0));
            chain(b, "s" + l, 3, 0.02f, k(0, 0, 0), k(0.3f, 40, 0), k(0.47f, -50, 0, SNAP), k(0.75f, -10, 0), k(1, 0, 0));
        }
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -20, 0, 0), k(0.47f, 26, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "bulb", k(0, 0, 0, 0), k(0.3f, -26, 0, 0), k(0.47f, 24, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 1.5f, 3), k(0.47f, 0, 2, -6, SNAP), k(0.85f, 0, 1, -2), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.3f, 22, 0, 0), k(0.47f, -30, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.3f, -16, 0, 0), k(0.47f, 12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }
}
