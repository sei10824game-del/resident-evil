package com.reevil.client.garrador;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.garrador.GarradorEntity;
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
 * Garrador. Faces -Z. Texture 128x64 (tools/tex_garrador.py). base > torso > (neck > head > cage), plaga (back parasite), armL/R > foreL/R > handL/R (> clawA..C on the LEFT),
 * thighL/R > shinL/R > footL/R. Impacts: slash .34/.66, rush .4/.6, crush .25/.5/.75, slam .55.
 */
public class GarradorModel extends BioModel<GarradorEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "garrador"), "main");
    private static final float D = Mth.DEG_TO_RAD;
    private static final String[] CLAWS = {"clawA", "clawB", "clawC"};
    private static final AnimClip C_SLASH = slash(), C_RUSH = rush(), C_CRUSH = crush(), C_SLAM = slam();

    public GarradorModel(ModelPart root) {
        super(root);
        reg("base", "base"); reg("torso", "base/torso"); reg("neck", "base/torso/neck"); reg("head", "base/torso/neck/head"); reg("plaga", "base/torso/plaga");
        for (String s : new String[]{"L", "R"}) {
            reg("arm" + s, "base/torso/arm" + s); reg("fore" + s, "base/torso/arm" + s + "/fore" + s); reg("hand" + s, "base/torso/arm" + s + "/fore" + s + "/hand" + s);
            reg("thigh" + s, "base/thigh" + s); reg("shin" + s, "base/thigh" + s + "/shin" + s); reg("foot" + s, "base/thigh" + s + "/shin" + s + "/foot" + s);
        }
        for (String c : CLAWS) reg(c, "base/torso/armL/foreL/handL/" + c);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -2, -3, 10, 4, 6), PartPose.offset(0, 9, 0));
        PartDefinition torso = base.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 10).addBox(-6, -12, -3.5f, 12, 12, 7), PartPose.offset(0, -2, 0));
        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(40, 0).addBox(-2, -2, -2, 4, 2, 4), PartPose.offset(0, -12, 0));
        neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 6).addBox(-3.5f, -7, -3.5f, 7, 7, 7).texOffs(70, 0).addBox(-4, -7.5f, -4, 8, 8, 8), PartPose.offset(0, -1.5f, 0));
        torso.addOrReplaceChild("plaga", CubeListBuilder.create().texOffs(94, 0).addBox(-3, -3, 0, 6, 6, 4), PartPose.offset(0, -6, 3.5f));
        for (int s = 0; s < 2; s++) {
            boolean l = s == 0; float sg = l ? 1 : -1; String n = l ? "L" : "R";
            PartDefinition arm = torso.addOrReplaceChild("arm" + n, CubeListBuilder.create().texOffs(0, 30).addBox(-2.5f, 0, -2.5f, 5, 11, 5), PartPose.offsetAndRotation(sg * 8.5f, -10, 0, 0, 0, sg * 0.1f));
            PartDefinition fore = arm.addOrReplaceChild("fore" + n, CubeListBuilder.create().texOffs(22, 30).addBox(-3, 0, -3, 6, 10, 6), PartPose.offsetAndRotation(0, 11, 0, -0.2f, 0, 0));
            PartDefinition hand = fore.addOrReplaceChild("hand" + n, CubeListBuilder.create().texOffs(48, 30).addBox(-2.5f, 0, -2.5f, 5, 4, 5), PartPose.offset(0, 10, 0));
            if (l) for (int c = 0; c < 3; c++) hand.addOrReplaceChild(CLAWS[c], CubeListBuilder.create().texOffs(70, 30).addBox(-0.5f, 0, -1, 1, 14, 2), PartPose.offsetAndRotation(-1.5f + c * 1.5f, 3, -0.5f, 0.1f, 0, (c - 1) * 0.15f));
            PartDefinition th = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create().texOffs(0, 48).addBox(-2.5f, 0, -2.5f, 5, 8, 5), PartPose.offset(sg * 3, 1.5f, 0));
            PartDefinition sh = th.addOrReplaceChild("shin" + n, CubeListBuilder.create().texOffs(22, 48).addBox(-2.5f, 0, -2.5f, 5, 8, 5), PartPose.offset(0, 8, 0));
            sh.addOrReplaceChild("foot" + n, CubeListBuilder.create().texOffs(44, 48).addBox(-3, 0, -5, 6, 2, 7), PartPose.offset(0, 8, 0));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    protected void baseAnim(GarradorEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        float p = ls * 0.55f, s = Mth.sin(p), c = Mth.cos(p);
        bone("base").y += -Math.abs(c) * 1.0f * amt + Mth.sin(age * 0.07f) * 0.12f;
        bone("base").xRot += 0.12f + 0.04f * amt;
        bone("torso").yRot += -s * 0.14f * amt; bone("torso").xRot += 0.18f;
        // blind: head weaves side to side listening
        bone("head").yRot += Mth.sin(age * 0.06f) * 0.4f * (e.getTarget() == null ? 1f : 0.3f) + Mth.clamp(yaw, -35f, 35f) * D * 0.3f;
        bone("head").xRot += 0.15f + Mth.sin(age * 0.045f) * 0.08f;
        for (int k = 0; k < 2; k++) {
            String n = k == 0 ? "L" : "R"; float ph = k == 0 ? p : p + Mth.PI, sw = Mth.sin(ph), lf = Math.max(0, Mth.cos(ph));
            bone("thigh" + n).xRot += sw * 0.45f * amt; bone("shin" + n).xRot += lf * 0.5f * amt; bone("foot" + n).xRot += -lf * 0.25f * amt;
            bone("arm" + n).xRot += -sw * 0.25f * amt + 0.1f; bone("fore" + n).xRot += -0.3f;
        }
        bone("plaga").xScale *= 1f + Mth.sin(age * 0.35f) * 0.1f; bone("plaga").yScale *= 1f + Mth.sin(age * 0.35f) * 0.12f;
        for (int i = 0; i < 3; i++) bone(CLAWS[i]).xRot += Mth.sin(age * 0.2f + i) * 0.04f;
        if (e.hurtTime > 0) { float h = e.hurtTime / (float) Math.max(1, e.hurtDuration); bone("torso").xRot -= h * 0.2f; bone("head").xRot -= h * 0.3f; }
    }

    @Override
    protected void extraAnim(GarradorEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("base").xRot += s * 1.2f; bone("base").y += s * 8f;
        bone("thighL").xRot += -s * 1.2f; bone("thighR").xRot += -s * 0.8f; bone("shinL").xRot += s * 1.4f; bone("shinR").xRot += s * 1.0f;
        bone("head").xRot += s * 0.6f; bone("armL").xRot += s * 0.5f; bone("armR").xRot += s * 0.4f;
    }

    @Override
    protected AnimClip clip(GarradorEntity e, int id) {
        switch (id) {
            case GarradorEntity.SLASH: return C_SLASH;
            case GarradorEntity.RUSH: return C_RUSH;
            case GarradorEntity.CRUSH: return C_CRUSH;
            case GarradorEntity.SLAM: return C_SLAM;
            default: return null;
        }
    }

    private static void pair2(AnimClip.Builder b, String prefix, float[]... rows) {
        java.util.List<float[]> l = new java.util.ArrayList<>(), r = new java.util.ArrayList<>();
        java.util.List<float[]> cur = l; int zeros = 0;
        for (float[] row : rows) { if (row[0] == 0f && ++zeros == 2) cur = r; cur.add(row); }
        rot(b, prefix + "L", l.toArray(new float[0][])); rot(b, prefix + "R", r.toArray(new float[0][]));
    }

    /** 32t, @11 (.34) and @21 (.66): blade arm hauled across, ripped through in a horizontal slash, then back-handed through the opposite way; torso swinging with it. */
    private static AnimClip slash() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armL", k(0, 0, 0, 0), k(0.18f, -80, 60, -30), k(0.34f, -70, -70, -20, SNAP), k(0.5f, -60, -50, -10), k(0.66f, -70, 80, -30, SNAP), k(0.85f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.18f, -50, 0, 0), k(0.34f, -10, 0, 0, SNAP), k(0.66f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.18f, -40, 0, 20), k(0.34f, -20, 0, 30), k(0.66f, -50, 0, 20), k(1, 0, 0, 0));
        for (int i = 0; i < 3; i++) rot(b, CLAWS[i], k(0, 0, 0, 0), k(0.34f, 0, 0, (i - 1) * 12, SNAP), k(0.66f, 0, 0, -(i - 1) * 12, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.18f, 6, 38, 0), k(0.34f, 16, -40, 0, SNAP), k(0.5f, 10, -10, 0), k(0.66f, 16, 42, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.34f, 8, -20, 0), k(0.66f, 8, 22, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.34f, 0, 1, -2), k(0.66f, 0, 1, -2), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.34f, 12, 0, 0), k(0.66f, -14, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.34f, -14, 0, 0), k(0.66f, 12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 40t, rams @16 (.4) and @24 (.6): head down, shoulders hunched, arms trailing back, then a thundering run that bowls through the prey. */
    private static AnimClip rush() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.15f, 18, 0, 0), k(0.85f, 22, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 26, 0, 0), k(0.85f, 32, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.15f, 20, 0, 0), k(0.85f, 24, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, 70, 0, 24), k(0.4f, 40, 0, 36), k(0.85f, 50, 0, 30), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, 70, 0, -24), k(0.4f, 40, 0, -36), k(0.85f, 50, 0, -30), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.15f, 0, 2, 2), k(0.85f, 0, 2, 2), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.2f, -50, 0, 0), k(0.3f, 40, 0, 0), k(0.4f, -50, 0, 0), k(0.5f, 40, 0, 0), k(0.6f, -50, 0, 0), k(0.7f, 40, 0, 0), k(0.8f, -30, 0, 0), k(1, 0, 0, 0),
            k(0, 0, 0, 0), k(0.2f, 40, 0, 0), k(0.3f, -50, 0, 0), k(0.4f, 40, 0, 0), k(0.5f, -50, 0, 0), k(0.6f, 40, 0, 0), k(0.7f, -50, 0, 0), k(0.8f, 30, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.3f, 40, 0, 0), k(0.5f, 10, 0, 0), k(0.7f, 40, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 64t: both hands seize (@16), lift and squeeze (@32), then it hurls the prey aside (@48): torso heaving, head tilting blind toward its victim. */
    private static AnimClip crush() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -70, 0, 14), k(0.25f, -92, -16, 4, SNAP), k(0.5f, -118, -6, 0), k(0.7f, -126, 0, 0), k(0.75f, -30, 40, 0, SNAP), k(0.9f, -30, 20, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.25f, -20, 0, 0), k(0.5f, -50, 0, 0), k(0.75f, -6, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, -50, 0, -14), k(0.25f, -80, 14, -6), k(0.7f, -100, 0, 0), k(0.8f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 6, 0, 0), k(0.25f, 20, 0, 0, SNAP), k(0.4f, 12, 6, 0), k(0.5f, 12, -6, 0), k(0.6f, 12, 6, 0), k(0.7f, -8, 0, 0), k(0.75f, 28, 30, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, 14, 0, 0), k(0.7f, 10, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.25f, 0, 1, -3), k(0.75f, 0, 2, -4, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 40t, slam @22 (.55): both fists up over the head, spine arched, then driven into the ground; the plaga on its back flares. */
    private static AnimClip slam() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -165, 0, 20), k(0.45f, -176, 0, 14), k(0.55f, -25, 0, 0, SNAP), k(0.75f, -12, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -160, 0, -20), k(0.45f, -172, 0, -14), k(0.55f, -25, 0, 0, SNAP), k(0.75f, -12, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -28, 0, 0), k(0.45f, -36, 0, 0), k(0.55f, 44, 0, 0, SNAP), k(0.8f, 30, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.45f, -20, 0, 0), k(0.55f, 22, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.45f, 0, -1, 2), k(0.55f, 0, 3.5f, -3, SNAP), k(0.8f, 0, 2.5f, -2), k(1, 0, 0, 0));
        scale(b, "plaga", k(0, 1, 1, 1), k(0.55f, 1.6f, 1.6f, 1.4f, SNAP), k(0.8f, 1.1f, 1.1f, 1.1f), k(1, 1, 1, 1));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.55f, -24, 0, 0, SNAP), k(0.8f, -16, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.55f, 30, 0, 0, SNAP), k(0.8f, 20, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }
}
