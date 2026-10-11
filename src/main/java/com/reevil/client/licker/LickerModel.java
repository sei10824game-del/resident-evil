package com.reevil.client.licker;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.licker.LickerEntity;
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
 * Licker. Faces -Z, +xRot = forward/down. Texture 128x64 (tools/tex_licker.py parses these calls).
 * base(pelvis) > torso > neck1 > neck2 > head > jaw > t1..t6 (tongue chain); torso > armL/R > foreL/R > handL/R;
 * base > thighL/R > shinL/R > footL/R. Impacts: lash .467, slash .364/.636, pounce .5, snare .333/.583/.833, drop .46, shriek .5.
 */
public class LickerModel extends BioModel<LickerEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "licker"), "main");
    private static final float D = Mth.DEG_TO_RAD;
    private static final float[] TONGUE_REST = {0.12f, 0.16f, 0.2f, 0.2f, 0.24f, 0.3f};
    private static final AnimClip LASH = lash(), SLASH = slash(), POUNCE = pounce(), GRAB = grab(), DROP = drop(), SHRIEK = shriek(), ASCEND = ascend();

    public LickerModel(ModelPart root) {
        super(root);
        reg("base", "base");
        reg("torso", "base/torso");
        reg("neck1", "base/torso/neck1");
        reg("neck2", "base/torso/neck1/neck2");
        reg("head", "base/torso/neck1/neck2/head");
        reg("jaw", "base/torso/neck1/neck2/head/jaw");
        String p = "base/torso/neck1/neck2/head/jaw";
        for (int i = 1; i <= 6; i++) { p += "/t" + i; reg("t" + i, p); }
        for (String s : new String[]{"L", "R"}) {
            reg("arm" + s, "base/torso/arm" + s);
            reg("fore" + s, "base/torso/arm" + s + "/fore" + s);
            reg("hand" + s, "base/torso/arm" + s + "/fore" + s + "/hand" + s);
            reg("thigh" + s, "base/thigh" + s);
            reg("shin" + s, "base/thigh" + s + "/shin" + s);
            reg("foot" + s, "base/thigh" + s + "/shin" + s + "/foot" + s);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -3, -3, 6, 6, 6), PartPose.offset(0, 14, 3));
        PartDefinition torso = base.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 18).addBox(-4, -4, -10, 8, 7, 10), PartPose.offsetAndRotation(0, -1, -3, 0.12f, 0, 0));
        PartDefinition n1 = torso.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(38, 18).addBox(-2, -2, -5, 4, 4, 5), PartPose.offsetAndRotation(0, -1, -10, -0.3f, 0, 0));
        PartDefinition n2 = n1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(58, 18).addBox(-2, -2, -4, 4, 4, 4), PartPose.offsetAndRotation(0, 0, -5, -0.1f, 0, 0));
        PartDefinition head = n2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 0).addBox(-4, -4, -8, 8, 6, 8)
            .texOffs(60, 0).addBox(-3, -6, -6, 6, 2, 5), PartPose.offsetAndRotation(0, 0, -4, 0.15f, 0, 0));
        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(84, 0).addBox(-3, 0, -9, 6, 2, 9)
            .texOffs(116, 0).addBox(-2.5f, -1, -8, 5, 1, 1).texOffs(116, 0).addBox(-2.5f, -1, -4, 5, 1, 1), PartPose.offsetAndRotation(0, 2, -1, 0.1f, 0, 0));
        PartDefinition t = jaw;
        for (int i = 0; i < 6; i++) {
            t = t.addOrReplaceChild("t" + (i + 1), CubeListBuilder.create().texOffs(46, 38).addBox(-1, -1, -6, 2, 2, 6), PartPose.offsetAndRotation(0, 1, i == 0 ? -8 : -6, TONGUE_REST[i], 0, 0));
        }
        for (int s = 0; s < 2; s++) {
            boolean l = s == 0; float sg = l ? 1 : -1; String n = l ? "L" : "R";
            CubeListBuilder up = CubeListBuilder.create().texOffs(78, 18).addBox(-1.5f, 0, -1.5f, 3, 8, 3);
            CubeListBuilder fo = CubeListBuilder.create().texOffs(92, 18).addBox(-1.5f, 0, -1.5f, 3, 8, 3);
            CubeListBuilder ha = CubeListBuilder.create().texOffs(106, 18).addBox(-2, 0, -2, 4, 2, 4)
                .texOffs(106, 26).addBox(-1.5f, 2, -2, 1, 5, 1).texOffs(106, 26).addBox(0.5f, 2, -2, 1, 5, 1).texOffs(106, 26).addBox(-0.5f, 2, 1, 1, 4, 1);
            PartDefinition arm = torso.addOrReplaceChild("arm" + n, up, PartPose.offsetAndRotation(sg * 5, 0, -8, -0.45f, 0, sg * 0.15f));
            PartDefinition fore = arm.addOrReplaceChild("fore" + n, fo, PartPose.offsetAndRotation(0, 8, 0, 0.35f, 0, 0));
            fore.addOrReplaceChild("hand" + n, ha, PartPose.offsetAndRotation(0, 8, 0, 0.5f, 0, 0));
            PartDefinition th = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create().texOffs(0, 38).addBox(-2, 0, -2, 4, 7, 4), PartPose.offsetAndRotation(sg * 3.5f, 0, 1, -0.8f, 0, sg * 0.1f));
            PartDefinition sh = th.addOrReplaceChild("shin" + n, CubeListBuilder.create().texOffs(18, 38).addBox(-1.5f, 0, -1.5f, 3, 8, 3), PartPose.offsetAndRotation(0, 7, 0, 1.3f, 0, 0));
            sh.addOrReplaceChild("foot" + n, CubeListBuilder.create().texOffs(32, 38).addBox(-2, 0, -4, 4, 2, 5), PartPose.offsetAndRotation(0, 8, 0, -0.5f, 0, 0));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    protected void baseAnim(LickerEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        float partial = age - e.tickCount;
        float ceil = Mth.lerp(partial, e.ceilAnimO, e.ceilAnim);
        float climb = Mth.lerp(partial, e.climbAnimO, e.climbAnim);
        float p = ls * 1.2f, s = Mth.sin(p), c = Mth.cos(p);
        ModelPart base = bone("base");
        base.zRot += Mth.PI * ceil;                          // upside-down on the ceiling (mirror about the bbox centre)
        base.y += 2f * ceil;
        base.xRot += -70f * D * climb;
        base.y += -Math.abs(s) * 0.9f * amt + Mth.sin(age * 0.12f) * 0.2f;
        bone("torso").xRot += c * 0.07f * amt;
        bone("torso").yRot += s * 0.09f * amt;
        // crawl gait: diagonal arm/leg pairs
        for (int k = 0; k < 2; k++) {
            String n = k == 0 ? "L" : "R";
            float ph = k == 0 ? p : p + Mth.PI;
            float sw = Mth.sin(ph), lf = Math.max(0, Mth.cos(ph));
            bone("arm" + n).xRot += sw * 0.7f * amt - lf * 0.3f * amt;
            bone("fore" + n).xRot += lf * 0.6f * amt;
            bone("hand" + n).xRot += lf * 0.3f * amt;
            float sw2 = Mth.sin(ph + Mth.PI);
            bone("thigh" + n).xRot += sw2 * 0.6f * amt;
            bone("shin" + n).xRot += Math.max(0, Mth.cos(ph + Mth.PI)) * 0.8f * amt;
            bone("foot" + n).xRot += -Math.max(0, Mth.cos(ph + Mth.PI)) * 0.4f * amt;
        }
        if (ceil > 0.5f) { // clutching the ceiling
            for (String n : new String[]{"L", "R"}) { bone("arm" + n).xRot += -0.6f; bone("thigh" + n).xRot += -0.3f; bone("hand" + n).xRot += -0.4f; }
        }
        // blind head: sweeps side to side sniffing for sound, jaw slack, brain pulsing
        bone("head").yRot += Mth.sin(age * 0.07f) * 0.35f * (e.getTarget() == null ? 1f : 0.35f) + Mth.clamp(yaw, -35f, 35f) * D * 0.3f;
        bone("head").xRot += Mth.sin(age * 0.05f + 1) * 0.1f + Mth.clamp(pitch, -25f, 25f) * D * 0.3f;
        bone("neck1").yRot += Mth.sin(age * 0.07f - 0.4f) * 0.15f;
        bone("jaw").xRot += 0.18f + Mth.sin(age * 0.11f) * 0.1f;
        for (int i = 1; i <= 6; i++) {
            bone("t" + i).xRot += Mth.sin(age * 0.15f - i * 0.6f) * 0.08f;
            bone("t" + i).yRot += Mth.sin(age * 0.11f - i * 0.5f) * 0.1f;
        }
        if (e.hurtTime > 0) {
            float h = e.hurtTime / (float) Math.max(1, e.hurtDuration);
            bone("head").xRot -= h * 0.5f; bone("jaw").xRot += h * 0.7f; bone("torso").xRot -= h * 0.2f;
        }
    }

    @Override
    protected void extraAnim(LickerEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("base").zRot += s * 1.45f;
        bone("base").y += s * 4f;
        bone("head").xRot += s * 0.5f; bone("jaw").xRot += s * 0.9f;
        for (int i = 1; i <= 6; i++) bone("t" + i).xRot += s * 0.3f;
        for (String n : new String[]{"L", "R"}) { bone("arm" + n).xRot += s * 0.6f; bone("thigh" + n).xRot += s * 0.4f; }
    }

    @Override
    protected AnimClip clip(LickerEntity e, int id) {
        switch (id) {
            case LickerEntity.LASH: return LASH;
            case LickerEntity.SLASH: return SLASH;
            case LickerEntity.POUNCE: return POUNCE;
            case LickerEntity.GRAB: return GRAB;
            case LickerEntity.DROP: return DROP;
            case LickerEntity.SHRIEK: return SHRIEK;
            case LickerEntity.ASCEND: return ASCEND;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ clip helpers
    /** Tongue as a travelling wave: rows {t, x, y[, ease]} (degrees) with per-segment lag. */
    private static void tongue(AnimClip.Builder b, float delay, float[]... rows) {
        for (int i = 1; i <= 6; i++) {
            float[][] r = new float[rows.length][];
            for (int j = 0; j < rows.length; j++) {
                float t = Math.min(1f, rows[j][0] * (1f - 5f * delay) + (i - 1) * delay);
                float w = 1f - (i - 1) * 0.06f;
                r[j] = rows[j].length > 3 ? new float[]{t, rows[j][1] * w, rows[j][2] * (0.5f + i * 0.15f), 0, rows[j][3]} : new float[]{t, rows[j][1] * w, rows[j][2] * (0.5f + i * 0.15f), 0};
            }
            rot(b, "t" + i, r);
        }
    }

    /** 30t, impact @14 (.467): rears and coils the tongue with jaws gaping, whips it out in a sweeping lash, snaps back. */
    private static AnimClip lash() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.3f, -14, 0, 0), k(0.467f, 10, 0, 0, SNAP), k(0.7f, 4, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 1.5f, 3), k(0.467f, 0, 0.5f, -4, SNAP), k(0.8f, 0, 0, -1), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -22, 18, 0), k(0.467f, 12, -22, 0, SNAP), k(0.7f, 4, -6, 0), k(1, 0, 0, 0));
        rot(b, "neck1", k(0, 0, 0, 0), k(0.3f, -20, 16, 0), k(0.467f, 18, -18, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -28, 10, 0), k(0.467f, 14, -14, 0, SNAP), k(0.7f, 6, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.25f, 55, 0, 0, OUT), k(0.55f, 60, 0, 0), k(0.8f, 20, 0, 0), k(1, 0, 0, 0));
        tongue(b, 0.014f, k(0, 0, 0), k(0.3f, -35, 24), k(0.467f, -16, -34, SNAP), k(0.58f, 4, 18), k(0.72f, -20, -6), k(0.88f, -40, 0), k(1, 0, 0));
        pair(b, "arm", k(0, 0, 0, 0), k(0.3f, 20, 0, 0), k(0.467f, -25, 0, 10, SNAP), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.3f, 14, 0, 0), k(0.467f, -10, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 22t, claws @8 (.364) & @14 (.636): reared on the hind legs, right arm rakes across, left rakes back, torso twisting through. */
    private static AnimClip slash() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.2f, -22, 0, 0), k(0.364f, -18, 0, 0), k(0.636f, -14, 0, 0), k(0.85f, -8, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.2f, 0, 1, 3), k(0.8f, 0, 0.5f, 1), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.2f, -20, 26, 0), k(0.364f, -8, -26, 0, SNAP), k(0.5f, -14, 0, 0), k(0.636f, -8, 28, 0, SNAP), k(0.85f, -4, 4, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.2f, -16, 12, 0), k(0.364f, 10, -16, 0), k(0.636f, 10, 18, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.2f, 40, 0, 0), k(0.364f, 10, 0, 0, SNAP), k(0.5f, 45, 0, 0), k(0.636f, 8, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.2f, -125, 0, 10), k(0.364f, -30, 55, -12, SNAP), k(0.5f, -40, 30, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.2f, -45, 0, 0), k(0.364f, -8, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.2f, -30, 0, 0), k(0.364f, 25, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -100, 0, -10), k(0.5f, -118, -20, 0), k(0.636f, -30, -55, 12, SNAP), k(0.85f, -20, -20, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.5f, -45, 0, 0), k(0.636f, -8, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handL", k(0, 0, 0, 0), k(0.5f, -30, 0, 0), k(0.636f, 25, 0, 0, SNAP), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.2f, -12, 0, 0), k(0.8f, -8, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.2f, 14, 0, 0), k(0.8f, 8, 0, 0), k(1, 0, 0, 0));
        tongue(b, 0.01f, k(0, 0, 0), k(0.364f, -10, 12), k(0.636f, -10, -12), k(1, 0, 0));
        return b.build();
    }

    /** 28t, impact @14 (.5): deep coil, explosive spring with the body stretched flat, claws and jaws spread at the hit, rolling landing. */
    private static AnimClip pounce() {
        AnimClip.Builder b = AnimClip.create();
        pos(b, "base", k(0, 0, 0, 0), k(0.25f, 0, 4, 3), k(0.36f, 0, -3, -5, SNAP), k(0.5f, 0, -6, -10), k(0.68f, 0, -1, -7), k(0.82f, 0, 3, -2), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.25f, 14, 0, 0), k(0.36f, -20, 0, 0, SNAP), k(0.5f, -12, 0, 0), k(0.68f, 12, 0, 0), k(0.82f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.25f, 18, 0, 0), k(0.4f, -22, 0, 0, SNAP), k(0.5f, -14, 0, 0), k(0.82f, 6, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -20, 0, 0), k(0.5f, 18, 0, 0, SNAP), k(0.7f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.25f, 30, 0, 0), k(0.5f, 62, 0, 0, SNAP), k(0.7f, 20, 0, 0), k(1, 0, 0, 0));
        pair(b, "arm", k(0, 0, 0, 0), k(0.25f, 40, 0, -10), k(0.4f, -70, 0, 18, SNAP), k(0.5f, -85, 0, 25), k(0.7f, -30, 0, 8), k(0.85f, 10, 0, 0), k(1, 0, 0, 0));
        pair(b, "fore", k(0, 0, 0, 0), k(0.25f, 20, 0, 0), k(0.5f, -30, 0, 0), k(0.8f, 10, 0, 0), k(1, 0, 0, 0));
        pair(b, "hand", k(0, 0, 0, 0), k(0.5f, -30, 0, 0), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.25f, 35, 0, 6), k(0.36f, -45, 0, -8, SNAP), k(0.5f, -60, 0, 0), k(0.7f, -10, 0, 0), k(0.85f, 12, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.25f, 30, 0, 0), k(0.36f, -50, 0, 0, SNAP), k(0.7f, 10, 0, 0), k(1, 0, 0, 0));
        tongue(b, 0.012f, k(0, 0, 0), k(0.36f, 20, 8), k(0.5f, -25, 0), k(0.75f, 10, -6), k(1, 0, 0));
        return b.build();
    }

    /** 48t: tongue shoots out and wraps (@16 .333), the Licker leans back reeling it in (squeeze @28), lurches forward and bites (@40 .833). */
    private static AnimClip grab() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.25f, -10, 0, 0), k(0.333f, 6, 0, 0, SNAP), k(0.45f, -4, 0, 0), k(0.72f, -14, 0, 0), k(0.833f, 14, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.25f, -18, 0, 0), k(0.333f, 12, 0, 0, SNAP), k(0.72f, -20, 0, 0), k(0.833f, 14, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "neck1", k(0, 0, 0, 0), k(0.25f, -18, 0, 0), k(0.333f, 14, 0, 0, SNAP), k(0.72f, -14, 0, 0), k(0.833f, 20, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -24, 0, 0), k(0.333f, 10, 0, 0, SNAP), k(0.72f, -12, 0, 0), k(0.833f, 16, 0, 0, SNAP), k(0.9f, 10, 8, 0), k(0.95f, 12, -8, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.22f, 60, 0, 0, OUT), k(0.72f, 60, 0, 0), k(0.833f, -4, 0, 0, SNAP), k(0.95f, 14, 0, 0), k(1, 0, 0, 0));
        tongue(b, 0.012f, k(0, 0, 0), k(0.25f, -38, 12), k(0.333f, -8, 0, SNAP), k(0.4f, -2, 26), // wraps the prey
            k(0.72f, -4, 8), k(0.833f, -30, 0), k(0.95f, -45, 0), k(1, 0, 0));
        pair(b, "arm", k(0, 0, 0, 0), k(0.25f, 30, 0, 0), k(0.72f, 55, 0, 0), k(0.833f, -35, 0, 6, SNAP), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.25f, 15, 0, 0), k(0.72f, 28, 0, 0), k(0.833f, -22, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.72f, -14, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.72f, 0, 1.5f, 3), k(0.833f, 0, 0, -5, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 26t, impact @12 (.46): upside-down clutch lets go, body whips around, claws and jaws splayed as it falls onto the prey. */
    private static AnimClip drop() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.2f, -20, 0, 0), k(0.46f, 18, 0, 0, SNAP), k(0.7f, 10, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.46f, 0, 0, -2), k(0.6f, 0, 3, 0), k(0.8f, 0, 2, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.2f, -16, 0, 0), k(0.46f, 14, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -18, 0, 0), k(0.46f, 16, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 62, 0, 0), k(0.46f, 66, 0, 0), k(0.6f, 8, 0, 0, SNAP), k(1, 0, 0, 0));
        pair(b, "arm", k(0, 0, 0, 0), k(0.3f, -80, 0, 28), k(0.46f, -95, 0, 20, SNAP), k(0.7f, -30, 0, 6), k(1, 0, 0, 0));
        pair(b, "hand", k(0, 0, 0, 0), k(0.46f, -35, 0, 0), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.3f, -30, 0, 14), k(0.46f, -50, 0, 6), k(0.7f, 16, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.46f, -30, 0, 0), k(0.7f, 20, 0, 0), k(1, 0, 0, 0));
        tongue(b, 0.01f, k(0, 0, 0), k(0.46f, -30, 10), k(0.7f, 10, -6), k(1, 0, 0));
        return b.build();
    }

    /** 36t, cry @18 (.5): rears on the hind legs, head thrown back, jaws and tongue flung open, whole body trembling. */
    private static AnimClip shriek() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.3f, -28, 0, 0), k(0.5f, -32, 0, 0), k(0.85f, -14, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -30, 0, 0), k(0.5f, -34, 0, 3), k(0.56f, -32, 0, -3), k(0.62f, -34, 0, 3), k(0.68f, -32, 0, -3), k(0.75f, -34, 0, 3), k(0.9f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "neck1", k(0, 0, 0, 0), k(0.3f, -26, 0, 0), k(0.5f, -34, 0, 0), k(0.85f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -40, 0, 0), k(0.5f, -56, 0, 0, SNAP), k(0.56f, -50, 8, 0), k(0.62f, -56, -8, 0), k(0.68f, -50, 8, 0), k(0.85f, -16, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 50, 0, 0), k(0.5f, 78, 0, 0, SNAP), k(0.8f, 70, 0, 0), k(1, 0, 0, 0));
        tongue(b, 0.01f, k(0, 0, 0), k(0.3f, -20, 10), k(0.5f, -42, 22), k(0.6f, -36, -22), k(0.7f, -42, 22), k(0.85f, -10, 0), k(1, 0, 0));
        pair(b, "arm", k(0, 0, 0, 0), k(0.3f, -70, 0, 30), k(0.5f, -95, 0, 45), k(0.85f, -40, 0, 12), k(1, 0, 0, 0));
        pair(b, "fore", k(0, 0, 0, 0), k(0.5f, -40, 0, 0), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.3f, -20, 0, 6), k(0.5f, -28, 0, 6), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.3f, 20, 0, 0), k(0.5f, 30, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 2, 3), k(0.5f, 0, 2.5f, 3), k(1, 0, 0, 0));
        return b.build();
    }

    /** 18t: crouch, then spring straight up with arms and legs reaching for the ceiling. */
    private static AnimClip ascend() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.3f, 14, 0, 0), k(0.45f, -62, 0, 0, SNAP), k(0.8f, -48, 0, 0), k(1, -30, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 3, 2), k(0.45f, 0, -2, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, 12, 0, 0), k(0.45f, -25, 0, 0, SNAP), k(1, -10, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -10, 0, 0), k(0.45f, -22, 0, 0), k(1, -10, 0, 0));
        pair(b, "arm", k(0, 0, 0, 0), k(0.3f, 25, 0, -8), k(0.45f, -130, 0, 14, SNAP), k(1, -110, 0, 10));
        pair(b, "hand", k(0, 0, 0, 0), k(0.45f, -40, 0, 0), k(1, -30, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.3f, 32, 0, 0), k(0.45f, -20, 0, 0, SNAP), k(1, -10, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.3f, 30, 0, 0), k(0.45f, -40, 0, 0), k(1, -10, 0, 0));
        return b.build();
    }
}
