package com.reevil.client.brainsucker;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.brainsucker.BrainSuckerEntity;
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
 * Brain Sucker. Faces -Z. Texture 128x64 (tools/tex_brain_sucker.py). body > head > prob1 > prob2 > prob3; body > wingL/R > wingOutL/R; body > legL/R (claws).
 * Wing flap = zRot (left: negative up). Impacts: stab .46, swoop .47, screech .44, cling .23/.42/.62/.81.
 */
public class BrainSuckerModel extends BioModel<BrainSuckerEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "brain_sucker"), "main");
    private static final AnimClip C_STAB = stab(), C_SWOOP = swoop(), C_SCREECH = screech(), C_CLING = cling();

    public BrainSuckerModel(ModelPart root) {
        super(root);
        reg("body", "body"); reg("head", "body/head"); reg("prob1", "body/head/prob1"); reg("prob2", "body/head/prob1/prob2"); reg("prob3", "body/head/prob1/prob2/prob3");
        for (String s : new String[]{"L", "R"}) { reg("wing" + s, "body/wing" + s); reg("wingOut" + s, "body/wing" + s + "/wingOut" + s); reg("leg" + s, "body/leg" + s); }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2, -2, -4, 4, 4, 8), PartPose.offset(0, 14, 0));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(28, 0).addBox(-2, -2, -5, 4, 4, 5)
            .texOffs(92, 0).addBox(-2, -4.5f, -3, 1, 3, 1).texOffs(92, 0).addBox(1, -4.5f, -3, 1, 3, 1), PartPose.offset(0, -0.5f, -4));
        PartDefinition p1 = head.addOrReplaceChild("prob1", CubeListBuilder.create().texOffs(48, 0).addBox(-0.5f, -0.5f, -5, 1, 1, 5), PartPose.offsetAndRotation(0, 1, -5, 0.7f, 0, 0));
        PartDefinition p2 = p1.addOrReplaceChild("prob2", CubeListBuilder.create().texOffs(48, 0).addBox(-0.5f, -0.5f, -5, 1, 1, 5), PartPose.offsetAndRotation(0, 0, -5, 0.5f, 0, 0));
        p2.addOrReplaceChild("prob3", CubeListBuilder.create().texOffs(48, 8).addBox(-0.5f, -0.5f, -4, 1, 1, 4), PartPose.offsetAndRotation(0, 0, -5, 0.4f, 0, 0));
        for (int s = 0; s < 2; s++) {
            float sg = s == 0 ? 1 : -1; String n = s == 0 ? "L" : "R";
            PartDefinition w = body.addOrReplaceChild("wing" + n, CubeListBuilder.create().texOffs(0, 14).addBox(sg > 0 ? 0 : -10, -0.5f, -4, 10, 1, 8), PartPose.offsetAndRotation(sg * 2, -1.5f, -0.5f, 0, 0, -sg * 0.1f));
            w.addOrReplaceChild("wingOut" + n, CubeListBuilder.create().texOffs(38, 14).addBox(sg > 0 ? 0 : -12, -0.5f, -4, 12, 1, 10), PartPose.offsetAndRotation(sg * 10, 0, 0, 0, 0, -sg * 0.1f));
            body.addOrReplaceChild("leg" + n, CubeListBuilder.create().texOffs(84, 0).addBox(-0.5f, 0, -0.5f, 1, 5, 1), PartPose.offsetAndRotation(sg * 1.2f, 2, 2.5f, 0.3f, 0, sg * 0.2f));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    protected void baseAnim(BrainSuckerEntity e, float ls, float amt, float age, float yaw, float pitch) {
        float beat = Mth.sin(age * 1.0f);
        for (String s : new String[]{"L", "R"}) {
            float sg = s.equals("L") ? 1 : -1;
            bone("wing" + s).zRot += -sg * (beat * 0.8f - 0.05f);
            bone("wingOut" + s).zRot += -sg * Mth.sin(age * 1.0f - 0.8f) * 0.7f;
            bone("leg" + s).xRot += Mth.sin(age * 0.3f) * 0.1f;
        }
        bone("body").y += -beat * 1.1f;
        bone("body").xRot += -0.1f + Mth.sin(age * 0.07f) * 0.04f;
        bone("head").yRot += Mth.clamp(yaw, -40f, 40f) * Mth.DEG_TO_RAD * 0.6f;
        bone("head").xRot += Mth.clamp(pitch, -25f, 25f) * Mth.DEG_TO_RAD * 0.5f;
        bone("prob1").xRot += Mth.sin(age * 0.13f) * 0.12f; bone("prob2").xRot += Mth.sin(age * 0.13f + 1) * 0.15f; bone("prob3").xRot += Mth.sin(age * 0.13f + 2) * 0.2f;
        if (e.hurtTime > 0) { float h = e.hurtTime / (float) Math.max(1, e.hurtDuration); bone("body").xRot -= h * 0.4f; }
    }

    @Override
    protected void extraAnim(BrainSuckerEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("body").zRot += s * 2.8f; bone("body").y += s * 6f;
        for (String n : new String[]{"L", "R"}) { float sg = n.equals("L") ? 1 : -1; bone("wing" + n).zRot += sg * s; bone("wingOut" + n).zRot += sg * s * 0.6f; }
    }

    @Override
    protected AnimClip clip(BrainSuckerEntity e, int id) {
        switch (id) {
            case BrainSuckerEntity.STAB: return C_STAB;
            case BrainSuckerEntity.SWOOP: return C_SWOOP;
            case BrainSuckerEntity.SCREECH: return C_SCREECH;
            case BrainSuckerEntity.CLING: return C_CLING;
            default: return null;
        }
    }

    private static void wings(AnimClip.Builder b, float[]... rows) { // {t, inner, outer} positive = down stroke
        float[][] i = new float[rows.length][], o = new float[rows.length][];
        for (int r = 0; r < rows.length; r++) { i[r] = k(rows[r][0], 0, 0, rows[r][1]); o[r] = k(rows[r][0], 0, 0, rows[r][2]); }
        pair(b, "wing", i); pair(b, "wingOut", o);
    }

    private static void prob(AnimClip.Builder b, float[]... rows) { // {t, a} same pitch on all three segments (curl negative = extend)
        float[][] a = new float[rows.length][], c = new float[rows.length][], d = new float[rows.length][];
        for (int r = 0; r < rows.length; r++) { a[r] = k(rows[r][0], rows[r][1], 0, 0); c[r] = k(rows[r][0], rows[r][1] * 0.8f, 0, 0); d[r] = k(rows[r][0], rows[r][1] * 0.6f, 0, 0); }
        rot(b, "prob1", a); rot(b, "prob2", c); rot(b, "prob3", d);
    }

    /** 26t, stab @12 (.46): proboscis unfurls, head cocks back, then the needle is driven forward and held, body lunging behind it. */
    private static AnimClip stab() {
        AnimClip.Builder b = AnimClip.create();
        prob(b, k(0, 0), k(0.3f, -40), k(0.46f, -52, SNAP), k(0.75f, -20), k(1, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -28, 0, 0), k(0.46f, 22, 0, 0, SNAP), k(0.75f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -16, 0, 0), k(0.46f, 20, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.3f, 0, 0, 3), k(0.46f, 0, 1, -6, SNAP), k(0.8f, 0, 0, -1), k(1, 0, 0, 0));
        wings(b, k(0, 0, 0), k(0.3f, -50, -30), k(0.46f, 40, 30), k(0.75f, -20, 0), k(1, 0, 0));
        pair(b, "leg", k(0, 0, 0, 0), k(0.46f, -30, 0, 10), k(1, 0, 0, 0));
        return b.build();
    }

    /** 34t, rake @16 (.47): climbs, folds the wings, plunges with both clawed feet thrown forward, then a hard wing-snap to pull out. */
    private static AnimClip swoop() {
        AnimClip.Builder b = AnimClip.create();
        wings(b, k(0, 0, 0), k(0.2f, -70, -40), k(0.3f, -80, -50), k(0.4f, -85, -60, 12), k(0.47f, -86, -62), k(0.6f, 60, 50, SNAP), k(0.8f, 20, 10), k(1, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.25f, -34, 0, 0), k(0.35f, 46, 0, 0, SNAP), k(0.47f, 50, 0, 0), k(0.7f, -16, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.25f, 0, -2, 3), k(0.47f, 0, 4, -8), k(0.8f, 0, 0, -2), k(1, 0, 0, 0));
        pair(b, "leg", k(0, 0, 0, 0), k(0.3f, 20, 0, 0), k(0.47f, -95, 0, 25, SNAP), k(0.7f, -30, 0, 10), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -16, 0, 0), k(0.47f, 14, 0, 0), k(1, 0, 0, 0));
        prob(b, k(0, 0), k(0.35f, -20), k(0.47f, -20), k(1, 0));
        return b.build();
    }

    /** 36t, screech @16 (.44): body rears up vertically, wings flung wide and shuddering, head thrown back with the proboscis flared. */
    private static AnimClip screech() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -50, 0, 0), k(0.44f, -60, 0, 0, SNAP), k(0.8f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -30, 0, 0), k(0.44f, -50, 0, 0, SNAP), k(0.5f, -44, 8, 0), k(0.56f, -50, -8, 0), k(0.62f, -44, 8, 0), k(0.9f, -10, 0, 0), k(1, 0, 0, 0));
        wings(b, k(0, 0, 0), k(0.3f, -40, -30), k(0.44f, -10, 0), k(0.5f, 10, 10), k(0.56f, -10, 0), k(0.62f, 10, 10), k(0.68f, -10, 0), k(0.9f, -20, -10), k(1, 0, 0));
        prob(b, k(0, 0), k(0.3f, -10), k(0.44f, 40, SNAP), k(0.8f, 20), k(1, 0));
        scale(b, "body", k(0, 1, 1, 1), k(0.44f, 1.15f, 1.15f, 1), k(0.6f, 1.05f, 1.05f, 1), k(1, 1, 1, 1));
        return b.build();
    }

    /** 52t: lunges onto the head (@12), then repeated deep stabs (@22/32/42) with the proboscis plunging and the body pulsing as it drinks. */
    private static AnimClip cling() {
        AnimClip.Builder b = AnimClip.create();
        prob(b, k(0, 0), k(0.1f, -45), k(0.23f, -55, SNAP), k(0.3f, -20), k(0.42f, -56, SNAP), k(0.5f, -20), k(0.62f, -56, SNAP), k(0.7f, -20), k(0.81f, -56, SNAP), k(0.92f, -10), k(1, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.1f, -24, 0, 0), k(0.23f, 30, 0, 0, SNAP), k(0.3f, 10, 0, 0), k(0.42f, 30, 0, 0, SNAP), k(0.5f, 10, 0, 0), k(0.62f, 30, 0, 0, SNAP), k(0.7f, 10, 0, 0), k(0.81f, 30, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.1f, -20, 0, 0), k(0.23f, 40, 0, 0, SNAP), k(0.85f, 44, 0, 0), k(1, 0, 0, 0));
        scale(b, "body", k(0, 1, 1, 1), k(0.3f, 1.1f, 1.1f, 1.05f), k(0.45f, 1.0f, 1.0f, 1), k(0.6f, 1.18f, 1.18f, 1.08f), k(0.75f, 1.0f, 1.0f, 1), k(0.9f, 1.25f, 1.25f, 1.1f), k(1, 1, 1, 1));
        wings(b, k(0, 0, 0), k(0.15f, -50, -30), k(0.23f, 30, 20), k(0.4f, -10, 0), k(0.6f, 10, 0), k(0.85f, -10, 0), k(1, 0, 0));
        pair(b, "leg", k(0, 0, 0, 0), k(0.23f, -70, 0, 20, SNAP), k(0.85f, -70, 0, 20), k(1, 0, 0, 0));
        return b.build();
    }
}
