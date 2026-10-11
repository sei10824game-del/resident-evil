package com.reevil.client.birkin;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.birkin.BirkinEntity;
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
 * G-Birkin on one rig; the three mutations differ by posture/scale/visibility in baseAnim (+ texture suffix, + renderer scale).
 * Faces -Z. Texture 128x128 (tools/tex_birkin.py). R = the huge claw arm. Eye = shoulder eyeball. Tendrils tL/tR (4 segments each) only show in G3.
 * Impacts: slam .53, backhand .5, grab .222/.472/.694/.861, flurry .25/.47/.69, leap .553, charge .33/.52/.71, whip .39/.64, stomp .548, bite .5, mutate .5.
 */
public class BirkinModel extends BioModel<BirkinEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "birkin"), "main");
    private static final float D = Mth.DEG_TO_RAD;
    private static final String[] CL = {"clawA", "clawB", "clawC"};
    private static final AnimClip SLAM = slam(), BACK = backhand(), GRAB = grab(), FLURRY = flurry(), LEAP = leap(), CHARGE = charge(), WHIP = whip(), STOMP = stomp(), BITE = bite(), MUTATE = mutate();

    public BirkinModel(ModelPart root) {
        super(root);
        reg("base", "base"); reg("torso", "base/torso"); reg("neck", "base/torso/neck"); reg("head", "base/torso/neck/head"); reg("jaw", "base/torso/neck/head/jaw");
        reg("eye", "base/torso/eye");
        reg("armR", "base/torso/armR"); reg("foreR", "base/torso/armR/foreR"); reg("handR", "base/torso/armR/foreR/handR");
        for (String c : CL) reg(c, "base/torso/armR/foreR/handR/" + c);
        reg("armL", "base/torso/armL"); reg("foreL", "base/torso/armL/foreL"); reg("handL", "base/torso/armL/foreL/handL");
        for (String s : new String[]{"L", "R"}) {
            reg("thigh" + s, "base/thigh" + s); reg("shin" + s, "base/thigh" + s + "/shin" + s); reg("foot" + s, "base/thigh" + s + "/shin" + s + "/foot" + s);
            String p = "base/torso/t" + s; reg("t" + s, p);
            for (int i = 1; i <= 4; i++) { p += "/t" + s + i; reg("t" + s + i, p); }
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5, -2.5f, -3, 10, 5, 6), PartPose.offset(0, -1.5f, 0));
        PartDefinition torso = base.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 12).addBox(-7, -13, -3.5f, 14, 13, 7), PartPose.offset(0, -2.5f, 0));
        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(44, 0).addBox(-2, -3, -2, 4, 3, 4), PartPose.offset(0, -13, 0));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(44, 8).addBox(-3, -7, -3, 6, 7, 6), PartPose.offset(0, -3, 0));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(70, 0).addBox(-2.5f, 0, -5, 5, 2, 5), PartPose.offset(0, -1, -2.5f));
        torso.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(70, 8).addBox(-3.5f, -3.5f, -3.5f, 7, 7, 7), PartPose.offset(8.5f, -11, -1));
        PartDefinition armR = torso.addOrReplaceChild("armR", CubeListBuilder.create().texOffs(0, 34).addBox(-4, 0, -4, 8, 13, 8), PartPose.offsetAndRotation(10, -11, 0, 0, 0, 0.05f));
        PartDefinition foreR = armR.addOrReplaceChild("foreR", CubeListBuilder.create().texOffs(34, 34).addBox(-4.5f, 0, -4.5f, 9, 13, 9), PartPose.offsetAndRotation(0, 13, 0, -0.12f, 0, 0));
        PartDefinition handR = foreR.addOrReplaceChild("handR", CubeListBuilder.create().texOffs(72, 34).addBox(-4, 0, -3.5f, 8, 5, 7), PartPose.offset(0, 13, 0));
        for (int c = 0; c < 3; c++) handR.addOrReplaceChild(CL[c], CubeListBuilder.create().texOffs(72, 48).addBox(-1, 0, -1, 2, 10, 2), PartPose.offsetAndRotation(-3 + c * 3, 5, -2, 0.18f, 0, (c - 1) * 0.1f));
        PartDefinition armL = torso.addOrReplaceChild("armL", CubeListBuilder.create().texOffs(0, 58).addBox(-2.5f, 0, -2.5f, 5, 12, 5), PartPose.offsetAndRotation(-9, -11, 0, 0, 0, -0.05f));
        PartDefinition foreL = armL.addOrReplaceChild("foreL", CubeListBuilder.create().texOffs(22, 58).addBox(-2.5f, 0, -2.5f, 5, 11, 5), PartPose.offsetAndRotation(0, 12, 0, -0.1f, 0, 0));
        PartDefinition handL = foreL.addOrReplaceChild("handL", CubeListBuilder.create().texOffs(44, 58).addBox(-2, 0, -2, 4, 4, 4)
            .texOffs(62, 58).addBox(-1.5f, 4, -1.5f, 1, 5, 1).texOffs(62, 58).addBox(0, 4, -1.5f, 1, 5, 1).texOffs(62, 58).addBox(-0.75f, 4, 0.5f, 1, 4, 1), PartPose.offset(0, 11, 0));
        for (int s = 0; s < 2; s++) {
            String n = s == 0 ? "L" : "R"; float sg = s == 0 ? 1 : -1;
            PartDefinition th = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create().texOffs(0, 78).addBox(-3, 0, -3, 6, 11, 6), PartPose.offset(sg * 3.5f, 2.5f, 0));
            PartDefinition sh = th.addOrReplaceChild("shin" + n, CubeListBuilder.create().texOffs(26, 78).addBox(-2.5f, 0, -2.5f, 5, 10, 5), PartPose.offset(0, 11, 0));
            sh.addOrReplaceChild("foot" + n, CubeListBuilder.create().texOffs(48, 78).addBox(-2.5f, 0, -6, 5, 2, 8), PartPose.offset(0, 10, 0));
            PartDefinition t = torso.addOrReplaceChild("t" + n, CubeListBuilder.create(), PartPose.offsetAndRotation(sg * 4, -10, 3.5f, -0.5f, sg * 0.3f, 0));
            for (int i = 1; i <= 4; i++) t = t.addOrReplaceChild("t" + n + i, CubeListBuilder.create().texOffs(0, 98).addBox(-1.5f, -1.5f, 0, 3, 3, 9), PartPose.offsetAndRotation(0, 0, i == 1 ? 0 : 9, -0.3f, 0, 0));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    protected void baseAnim(BirkinEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        int st = e.stage();
        float spd = 0.55f + st * 0.12f;
        float p = ls * spd, s = Mth.sin(p), c = Mth.cos(p);
        ModelPart base = bone("base");
        base.y += -Math.abs(c) * (1.0f + st * 0.3f) * amt + Mth.sin(age * 0.08f) * 0.12f;
        base.xRot += st == 0 ? 0.03f : st == 1 ? 0.28f : 0.18f;
        base.zRot += s * 0.04f * amt;
        bone("torso").yRot += -s * 0.1f * amt;
        bone("torso").xRot += st == 0 ? 0.08f : st == 1 ? 0.45f : 0.25f;
        bone("neck").xRot += st == 1 ? -0.4f : -0.1f;
        bone("head").xRot += Mth.clamp(pitch, -25f, 25f) * D * 0.4f - (st == 1 ? 0.2f : 0f);
        bone("head").yRot += Mth.clamp(yaw, -50f, 50f) * D * 0.6f;
        bone("jaw").xRot += (st == 2 ? 0.55f : 0.12f) + Mth.sin(age * 0.1f) * (st == 2 ? 0.12f : 0.05f);
        for (int k = 0; k < 2; k++) {
            String n = k == 0 ? "L" : "R";
            float ph = k == 0 ? p : p + Mth.PI, sw = Mth.sin(ph), lf = Math.max(0, Mth.cos(ph));
            bone("thigh" + n).xRot += sw * 0.5f * amt;
            bone("shin" + n).xRot += lf * 0.6f * amt;
            bone("foot" + n).xRot += -lf * 0.3f * amt;
        }
        bone("armL").xRot += -s * 0.25f * amt + Mth.sin(age * 0.07f) * 0.03f;
        bone("armR").xRot += s * 0.12f * amt + Mth.sin(age * 0.06f + 1) * 0.03f;
        // mutation look: the claw arm and eye swell with each stage, G2 knuckles low, G3 jaw agape with tendrils writhing
        float arm = 1f + st * 0.12f;
        for (String n : new String[]{"armR", "foreR", "handR"}) { ModelPart m = bone(n); m.xScale *= arm; m.zScale *= arm; }
        float eye = (1f + st * 0.3f) * (1f + Mth.sin(age * 0.22f) * 0.06f);
        bone("eye").xScale *= eye; bone("eye").yScale *= eye; bone("eye").zScale *= eye;
        bone("eye").x += st == 2 ? 2f : 0f;
        if (st >= 1) { bone("armR").xRot += 0.35f; bone("armL").xRot += 0.35f; bone("foreR").xRot += -0.2f; }
        float tv = st == 2 ? 1f : 0.001f;
        for (String sd : new String[]{"L", "R"}) {
            for (int i = 1; i <= 4; i++) {
                ModelPart m = bone("t" + sd + i);
                m.xScale *= tv; m.yScale *= tv; m.zScale *= tv;
                m.xRot += Mth.sin(age * 0.12f + i * 0.7f + (sd.equals("L") ? 0 : 2f)) * 0.2f;
                m.yRot += Mth.sin(age * 0.09f + i * 0.5f) * 0.18f;
            }
            ModelPart r = bone("t" + sd); r.xScale *= tv; r.yScale *= tv; r.zScale *= tv;
        }
        bone("head").yScale *= st == 2 ? 1.5f : 1f; bone("head").xScale *= st == 2 ? 1.5f : 1f; bone("head").zScale *= st == 2 ? 1.5f : 1f;
        if (e.hurtTime > 0) {
            float h = e.hurtTime / (float) Math.max(1, e.hurtDuration);
            bone("head").xRot -= h * 0.3f; bone("torso").xRot -= h * 0.12f;
        }
    }

    @Override
    protected void extraAnim(BirkinEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("base").xRot += s * 0.9f; bone("base").y += s * 11f;
        bone("thighL").xRot += -s * 1.3f; bone("thighR").xRot += -s * 1.0f; bone("shinL").xRot += s * 1.4f; bone("shinR").xRot += s * 1.2f;
        bone("head").xRot += s * 0.8f; bone("armR").xRot += s * 0.5f; bone("armL").xRot += s * 0.5f;
        bone("jaw").xRot += s * 0.7f;
    }

    @Override
    protected AnimClip clip(BirkinEntity e, int id) {
        switch (id) {
            case BirkinEntity.SLAM: return SLAM;
            case BirkinEntity.BACKHAND: return BACK;
            case BirkinEntity.GRAB: return GRAB;
            case BirkinEntity.FLURRY: return FLURRY;
            case BirkinEntity.LEAP: return LEAP;
            case BirkinEntity.CHARGE: return CHARGE;
            case BirkinEntity.WHIP: return WHIP;
            case BirkinEntity.STOMP: return STOMP;
            case BirkinEntity.BITE: return BITE;
            case BirkinEntity.MUTATE: return MUTATE;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ clips
    private static void legsSet(AnimClip.Builder b, float[][] left, float[][] right) { rot(b, "thighL", left); rot(b, "thighR", right); }

    /** 34t, slam @18 (.53): claw arm hauled overhead, torso arched back, then brought down with the whole body behind it; claws dig in. */
    private static AnimClip slam() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.35f, -172, 0, 14), k(0.46f, -178, 0, 10), k(0.53f, -20, 0, 0, SNAP), k(0.7f, -8, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.35f, -25, 0, 0), k(0.53f, -8, 0, 0), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.46f, -30, 0, 0), k(0.53f, 25, 0, 0, SNAP), k(1, 0, 0, 0));
        for (int i = 0; i < 3; i++) rot(b, CL[i], k(0, 0, 0, 0), k(0.46f, 0, 0, (i - 1) * 14), k(0.53f, 12, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.35f, -60, 0, -20), k(0.53f, 15, 0, -10), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.35f, -26, 8, 0), k(0.46f, -30, 8, 0), k(0.53f, 38, -6, 0, SNAP), k(0.75f, 24, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.46f, -22, 0, 0), k(0.53f, 18, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.46f, 40, 0, 0), k(0.53f, 10, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.46f, 0, -1, 2), k(0.53f, 0, 3.5f, -4, SNAP), k(0.75f, 0, 2, -2), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.53f, -24, 0, 0, SNAP), k(0.75f, -16, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.53f, 30, 0, 0, SNAP), k(0.75f, 20, 0, 0), k(1, 0, 0, 0));
        rot(b, "eye", k(0, 0, 0, 0), k(0.46f, -20, 0, 0), k(0.53f, 10, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 28t, impact @14 (.5): claw arm coiled across the chest, then a flat sweeping backhand that spins the torso right through. */
    private static AnimClip backhand() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.38f, -55, -70, -40), k(0.5f, -85, 85, 25, SNAP), k(0.7f, -60, 60, 10), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.38f, -85, 0, 0), k(0.5f, -6, 0, 0, SNAP), k(1, 0, 0, 0));
        for (int i = 0; i < 3; i++) rot(b, CL[i], k(0, 0, 0, 0), k(0.5f, 0, 0, (i - 1) * 16, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.38f, 6, 52, 0), k(0.5f, 14, -50, 0, SNAP), k(0.7f, 10, -28, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.38f, 0, 24, 0), k(0.5f, 6, -26, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.38f, 0, 28, 0), k(0.5f, 8, -30, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.38f, -35, 0, -20), k(0.5f, 30, -30, -30), k(1, 0, 0, 0));
        legsSet(b, new float[][]{k(0, 0, 0, 0), k(0.38f, 8, 0, 0), k(0.5f, -14, 0, 0), k(1, 0, 0, 0)}, new float[][]{k(0, 0, 0, 0), k(0.38f, -14, 0, 0), k(0.5f, 12, 0, 0), k(1, 0, 0, 0)});
        pos(b, "base", k(0, 0, 0, 0), k(0.5f, 0, 1, -2), k(1, 0, 0, 0));
        return b.build();
    }

    /** 72t: the claw hand closes round the prey @16 (.222), hoists and squeezes (@34, @50), shakes, then smashes and flings it @62 (.861). */
    private static AnimClip grab() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.12f, -50, 0, 14), k(0.222f, -88, -8, 4, SNAP), k(0.5f, -118, 0, 0), k(0.72f, -135, 0, 0), k(0.861f, -22, 0, 0, SNAP), k(0.94f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.222f, -8, 0, 0), k(0.5f, -40, 0, 0), k(0.72f, -50, 0, 0), k(0.861f, -4, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.222f, -25, 0, 0), k(0.72f, -45, 0, 0), k(0.861f, 18, 0, 0), k(1, 0, 0, 0));
        for (int i = 0; i < 3; i++) rot(b, CL[i], k(0, 0, 0, 0), k(0.15f, 0, 0, (i - 1) * 14), k(0.222f, 0, 0, -(i - 1) * 10, SNAP), k(0.72f, 0, 0, -(i - 1) * 22), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.2f, -50, 0, -14), k(0.72f, -70, 0, -10), k(0.861f, 20, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.12f, 8, 0, 0), k(0.222f, 18, 0, 0, SNAP), k(0.5f, 10, 5, 0), k(0.6f, 10, -5, 0), k(0.694f, 10, 5, 0), k(0.8f, -6, 0, 0), k(0.861f, 30, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.222f, 16, 0, 0), k(0.8f, 12, 0, 0), k(0.861f, -8, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.4f, 30, 0, 0), k(0.694f, 55, 0, 0), k(0.861f, 8, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.222f, 0, 1, -3), k(0.8f, 0, 0, -1), k(0.861f, 0, 2.5f, -5, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 32t, rakes @8/@15/@22: right claw, left claw, then both together; hips and head whip into each cut, constant forward drive. */
    private static AnimClip flurry() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.1f, -110, 40, 30), k(0.25f, -40, -55, -10, SNAP), k(0.36f, -70, 10, 20), k(0.47f, -100, -20, 10), k(0.69f, -118, 0, 20, SNAP), k(0.9f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.25f, -70, 0, -16), k(0.36f, -115, -30, -12), k(0.47f, -35, 60, 10, SNAP), k(0.58f, -80, 0, -10), k(0.69f, -118, 0, -16, SNAP), k(0.9f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.1f, -50, 0, 0), k(0.25f, -8, 0, 0, SNAP), k(0.69f, -10, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.36f, -60, 0, 0), k(0.47f, -8, 0, 0, SNAP), k(0.69f, -10, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.1f, 6, 30, 0), k(0.25f, 14, -34, 0, SNAP), k(0.36f, 8, -8, 0), k(0.47f, 14, 36, 0, SNAP), k(0.58f, 8, 0, 0), k(0.69f, 24, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, 8, -24, 0), k(0.47f, 10, 26, 0), k(0.69f, 18, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.1f, 30, 0, 0), k(0.25f, 8, 0, 0), k(0.47f, 8, 0, 0), k(0.69f, 40, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.25f, 0, 1, -2.5f), k(0.47f, 0, 1, -3.5f), k(0.69f, 0, 2.2f, -5), k(1, 0, 0, 0));
        legsSet(b, new float[][]{k(0, 0, 0, 0), k(0.25f, -18, 0, 0), k(0.47f, 14, 0, 0), k(0.69f, -20, 0, 0), k(1, 0, 0, 0)}, new float[][]{k(0, 0, 0, 0), k(0.25f, 14, 0, 0), k(0.47f, -18, 0, 0), k(0.69f, 8, 0, 0), k(1, 0, 0, 0)});
        return b.build();
    }

    /** 38t, landing @21 (.553): deep coil, thundering spring with arms thrown back, flight stretched, then a two-clawed crash and shockwave landing. */
    private static AnimClip leap() {
        AnimClip.Builder b = AnimClip.create();
        pos(b, "base", k(0, 0, 0, 0), k(0.2f, 0, 5, 3), k(0.3f, 0, -2, -3, SNAP), k(0.42f, 0, -9, -8), k(0.553f, 0, 3.5f, -4, SNAP), k(0.7f, 0, 3, -2), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.2f, 16, 0, 0), k(0.3f, -26, 0, 0, SNAP), k(0.42f, -16, 0, 0), k(0.553f, 18, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.2f, 20, 0, 0), k(0.3f, -30, 0, 0, SNAP), k(0.42f, -22, 0, 0), k(0.553f, 40, 0, 0, SNAP), k(0.8f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.2f, 40, 0, 20), k(0.3f, 60, 0, 40, SNAP), k(0.42f, -140, 0, 20), k(0.553f, -30, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.2f, 35, 0, -16), k(0.3f, 55, 0, -34), k(0.42f, -120, 0, -16), k(0.553f, -30, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.2f, -16, 0, 0), k(0.553f, 20, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 40, 0, 0), k(0.42f, 60, 0, 0), k(0.553f, 10, 0, 0, SNAP), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.2f, 40, 0, 0), k(0.3f, -50, 0, 0, SNAP), k(0.42f, -42, 0, 0), k(0.553f, 20, 0, 0, SNAP), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.2f, 50, 0, 0), k(0.3f, -30, 0, 0), k(0.42f, 40, 0, 0), k(0.553f, 40, 0, 0, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 42t, strikes @14/22/30: head-down gallop, shoulders rolling, arms swinging wide to bowl over anything; pulls up short. */
    private static AnimClip charge() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "base", k(0, 0, 0, 0), k(0.12f, 14, 0, 0), k(0.8f, 18, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.12f, 24, 0, 0), k(0.8f, 30, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.12f, 20, 0, 0), k(0.8f, 22, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.12f, 45, 0, 0), k(0.8f, 50, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.12f, 55, 0, 30), k(0.24f, -70, 20, 30), k(0.33f, -50, 40, 40), k(0.52f, -62, -30, 30), k(0.71f, -60, 40, 40), k(0.9f, 20, 0, 10), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.12f, 40, 0, -20), k(0.24f, -50, -20, -26), k(0.43f, -40, -40, -34), k(0.62f, -55, 30, -26), k(0.9f, 10, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.12f, 0, 2, 2), k(0.8f, 0, 2, 2), k(1, 0, 0, 0));
        legsSet(b, new float[][]{k(0, 0, 0, 0), k(0.2f, -50, 0, 0), k(0.3f, 40, 0, 0), k(0.4f, -50, 0, 0), k(0.5f, 40, 0, 0), k(0.6f, -50, 0, 0), k(0.7f, 40, 0, 0), k(0.8f, -30, 0, 0), k(1, 0, 0, 0)},
            new float[][]{k(0, 0, 0, 0), k(0.2f, 40, 0, 0), k(0.3f, -50, 0, 0), k(0.4f, 40, 0, 0), k(0.5f, -50, 0, 0), k(0.6f, 40, 0, 0), k(0.7f, -50, 0, 0), k(0.8f, 30, 0, 0), k(1, 0, 0, 0)});
        pair(b, "shin", k(0, 0, 0, 0), k(0.3f, 40, 0, 0), k(0.5f, 10, 0, 0), k(0.7f, 40, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 36t, lashes @14 (.39) & @23 (.64): both back tendrils rear overhead, whip down-and-across in a travelling wave, then cross-lash back. */
    private static AnimClip whip() {
        AnimClip.Builder b = AnimClip.create();
        for (String sd : new String[]{"L", "R"}) {
            float sg = sd.equals("L") ? 1 : -1;
            rot(b, "t" + sd, k(0, 0, 0, 0), k(0.25f, -50, sg * 20, 0), k(0.39f, 70, -sg * 30, 0, SNAP), k(0.5f, 20, sg * 20, 0), k(0.64f, 62, sg * 40, 0, SNAP), k(0.85f, 10, 0, 0), k(1, 0, 0, 0));
            for (int i = 1; i <= 4; i++) {
                float d = i * 0.02f;
                rot(b, "t" + sd + i, k(0, 0, 0, 0), k(0.25f + d, -34, sg * 16, 0), k(0.39f + d, 40, -sg * 36, 0, SNAP), k(0.5f + d, -20, sg * 14, 0), k(0.64f + d, 44, sg * 44, 0, SNAP), k(0.9f, 0, 0, 0), k(1, 0, 0, 0));
            }
        }
        rot(b, "torso", k(0, 0, 0, 0), k(0.25f, -18, 14, 0), k(0.39f, 18, -18, 0, SNAP), k(0.5f, 6, 12, 0), k(0.64f, 18, 18, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -24, 0, 0), k(0.39f, 14, 0, 0), k(0.64f, 14, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.25f, 55, 0, 0), k(0.64f, 55, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.25f, -60, 0, 30), k(0.39f, -30, 0, 20), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.25f, -60, 0, -30), k(0.39f, -30, 0, -20), k(1, 0, 0, 0));
        return b.build();
    }

    /** 42t, shockwave @23 (.548): rears up on one leg with both arms high, then stamps down with a crushing foot while the arms drop. */
    private static AnimClip stomp() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "thighR", k(0, 0, 0, 0), k(0.3f, -70, 0, 0), k(0.45f, -86, 0, 0), k(0.548f, 8, 0, 0, SNAP), k(0.75f, 0, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinR", k(0, 0, 0, 0), k(0.3f, 80, 0, 0), k(0.45f, 90, 0, 0), k(0.548f, 6, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.45f, 8, 0, 0), k(0.548f, -8, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -110, 0, 40), k(0.45f, -150, 0, 50), k(0.548f, -10, 0, 8, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -100, 0, -40), k(0.45f, -140, 0, -50), k(0.548f, -10, 0, -8, SNAP), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.45f, -20, 0, 0), k(0.548f, 26, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.45f, -26, 0, 0), k(0.548f, 20, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.45f, 60, 0, 0), k(0.548f, 12, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.45f, 0, -3, 2), k(0.548f, 0, 3, -2, SNAP), k(1, 0, 0, 0));
        for (String sd : new String[]{"L", "R"}) for (int i = 1; i <= 4; i++) rot(b, "t" + sd + i, k(0, 0, 0, 0), k(0.45f, -25, 0, 0), k(0.548f, 22, 0, 0, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 34t, bite @17 (.5): the vast jaw cranks open, head rears back, then a lunging snap that closes with the neck fully extended; shakes the prey. */
    private static AnimClip bite() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 70, 0, 0, OUT), k(0.45f, 80, 0, 0), k(0.5f, -10, 0, 0, SNAP), k(0.6f, 14, 0, 0), k(0.7f, -4, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -40, 0, 0), k(0.5f, 28, 0, 0, SNAP), k(0.58f, 22, 20, 0), k(0.68f, 24, -20, 0), k(0.8f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "neck", k(0, 0, 0, 0), k(0.3f, -26, 0, 0), k(0.5f, 24, 0, 0, SNAP), k(0.8f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -20, 0, 0), k(0.5f, 28, 0, 0, SNAP), k(0.8f, 12, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 0, 3), k(0.5f, 0, 2, -6, SNAP), k(0.8f, 0, 1, -2), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, 30, 0, 30), k(0.5f, -60, 0, 24, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, 30, 0, -30), k(0.5f, -50, 0, -24, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 80t, change @40 (.5): head wrenched back, back arching, all limbs flung wide while the flesh heaves; settles into the new, bigger stance. */
    private static AnimClip mutate() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 14, 0, 0), k(0.3f, -34, 4, 0), k(0.36f, -36, -4, 0), k(0.42f, -34, 4, 0), k(0.5f, -42, 0, 0, SNAP), k(0.62f, -30, 0, 0), k(0.85f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.15f, 24, 0, 0), k(0.3f, -56, 0, 0), k(0.5f, -70, 0, 0, SNAP), k(0.62f, -50, 8, 0), k(0.74f, -46, -8, 0), k(0.88f, 14, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 50, 0, 0), k(0.5f, 85, 0, 0, SNAP), k(0.85f, 60, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -20, 0, 44), k(0.5f, -30, 0, 84, SNAP), k(0.7f, -26, 0, 64), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -20, 0, -44), k(0.5f, -30, 0, -84, SNAP), k(0.7f, -26, 0, -64), k(1, 0, 0, 0));
        scale(b, "eye", k(0, 1, 1, 1), k(0.3f, 1.3f, 1.3f, 1.3f), k(0.5f, 1.7f, 1.7f, 1.7f), k(0.7f, 1.3f, 1.3f, 1.3f), k(1, 1, 1, 1));
        scale(b, "torso", k(0, 1, 1, 1), k(0.4f, 1.08f, 1.06f, 1.08f), k(0.5f, 0.94f, 0.96f, 0.94f, SNAP), k(0.7f, 1.04f, 1.03f, 1.04f), k(1, 1, 1, 1));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, 1, 2), k(0.5f, 0, 0, 3), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.3f, -4, 0, 10), k(0.5f, -8, 0, 16), k(1, 0, 0, 0));
        return b.build();
    }
}
