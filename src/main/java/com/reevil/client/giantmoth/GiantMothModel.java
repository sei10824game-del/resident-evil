package com.reevil.client.giantmoth;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.giantmoth.GiantMothEntity;
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
 * Giant Moth. Faces -Z, +xRot = nose down. Texture 128x64 (tools/tex_giant_moth.py).
 * body(thorax) > abdomen, head > (antL/R > ant2L/R, prob > prob2), wingL/R > wingOutL/R, legs legL/R0..2.
 * Wing flap = zRot (left wing: negative = up, right wing mirrored). Impacts: burst .5, ram .5, jab .458, buffet .5.
 */
public class GiantMothModel extends BioModel<GiantMothEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "giant_moth"), "main");
    private static final AnimClip C_DUST = dust(), C_RAM = ram(), C_STING = sting(), C_BUFFET = buffet();

    public GiantMothModel(ModelPart root) {
        super(root);
        reg("body", "body"); reg("abdomen", "body/abdomen"); reg("head", "body/head");
        reg("prob", "body/head/prob"); reg("prob2", "body/head/prob/prob2");
        for (String s : new String[]{"L", "R"}) {
            reg("ant" + s, "body/head/ant" + s); reg("ant2" + s, "body/head/ant" + s + "/ant2" + s);
            reg("wing" + s, "body/wing" + s); reg("wingOut" + s, "body/wing" + s + "/wingOut" + s);
            for (int i = 0; i < 3; i++) reg("leg" + s + i, "body/leg" + s + i);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(38, 0).addBox(-2.5f, -2.5f, -3, 5, 5, 6), PartPose.offset(0, 14, 0));
        body.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -3, 0, 6, 6, 12), PartPose.offsetAndRotation(0, 0.5f, 2.5f, 0.35f, 0, 0));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(62, 0).addBox(-2, -2, -4, 4, 4, 4)
            .texOffs(80, 0).addBox(-2.5f, -2.5f, -3.5f, 1, 2, 2).texOffs(80, 0).addBox(1.5f, -2.5f, -3.5f, 1, 2, 2), PartPose.offset(0, -0.5f, -3));
        PartDefinition prob = head.addOrReplaceChild("prob", CubeListBuilder.create().texOffs(104, 0).addBox(-0.5f, -0.5f, -4, 1, 1, 4), PartPose.offsetAndRotation(0, 1.5f, -4, 0.9f, 0, 0));
        prob.addOrReplaceChild("prob2", CubeListBuilder.create().texOffs(104, 0).addBox(-0.5f, -0.5f, -4, 1, 1, 4), PartPose.offsetAndRotation(0, 0, -4, 0.6f, 0, 0));
        for (int s = 0; s < 2; s++) {
            float sg = s == 0 ? 1 : -1; String n = s == 0 ? "L" : "R";
            PartDefinition ant = head.addOrReplaceChild("ant" + n, CubeListBuilder.create().texOffs(88, 0).addBox(-0.5f, -0.5f, -6, 1, 1, 6), PartPose.offsetAndRotation(sg * 1.2f, -2, -3.5f, -0.5f, sg * -0.35f, 0));
            ant.addOrReplaceChild("ant2" + n, CubeListBuilder.create().texOffs(88, 8).addBox(-0.5f, -0.5f, -5, 1, 1, 5), PartPose.offsetAndRotation(0, 0, -6, 0.5f, sg * -0.3f, 0));
            PartDefinition w = body.addOrReplaceChild("wing" + n, CubeListBuilder.create().texOffs(0, 20).addBox(sg > 0 ? 0 : -12, -0.5f, -5, 12, 1, 10), PartPose.offsetAndRotation(sg * 2.5f, -2, 0, 0, 0, -sg * 0.15f));
            w.addOrReplaceChild("wingOut" + n, CubeListBuilder.create().texOffs(46, 20).addBox(sg > 0 ? 0 : -12, -0.5f, -6, 12, 1, 12), PartPose.offsetAndRotation(sg * 12, 0, 0, 0, 0, -sg * 0.1f));
            for (int i = 0; i < 3; i++) body.addOrReplaceChild("leg" + n + i, CubeListBuilder.create().texOffs(96, 20).addBox(-0.5f, 0, -0.5f, 1, 5, 1), PartPose.offsetAndRotation(sg * 1.5f, 2.5f, -2 + i * 2, 0.2f * (i - 1), 0, sg * 0.35f));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    protected void baseAnim(GiantMothEntity e, float ls, float amt, float age, float yaw, float pitch) {
        float beat = Mth.sin(age * 0.85f);
        float amp = 0.75f;
        for (String s : new String[]{"L", "R"}) {
            float sg = s.equals("L") ? 1 : -1;
            bone("wing" + s).zRot += -sg * (beat * amp - 0.1f);
            bone("wingOut" + s).zRot += -sg * Mth.sin(age * 0.85f - 0.7f) * 0.5f;
            bone("wing" + s).yRot += sg * (-0.1f + beat * 0.1f);
            bone("ant" + s).xRot += Mth.sin(age * 0.2f + sg) * 0.1f;
            bone("ant2" + s).xRot += Mth.sin(age * 0.2f + sg + 1) * 0.15f;
            for (int i = 0; i < 3; i++) bone("leg" + s + i).xRot += Mth.sin(age * 0.3f + i) * 0.08f + 0.3f;   // legs dangle
        }
        bone("body").y += -beat * 1.2f;
        bone("body").xRot += 0.1f + Mth.sin(age * 0.07f) * 0.04f;
        bone("abdomen").xRot += Mth.sin(age * 0.85f - 1f) * 0.12f;
        bone("abdomen").yRot += Mth.sin(age * 0.11f) * 0.08f;
        bone("head").yRot += Mth.clamp(yaw, -40f, 40f) * Mth.DEG_TO_RAD * 0.6f;
        bone("head").xRot += Mth.clamp(pitch, -25f, 25f) * Mth.DEG_TO_RAD * 0.5f;
        bone("prob").xRot += Mth.sin(age * 0.13f) * 0.1f;
        if (e.hurtTime > 0) { float h = e.hurtTime / (float) Math.max(1, e.hurtDuration); bone("body").xRot -= h * 0.4f; bone("head").xRot -= h * 0.3f; }
    }

    @Override
    protected void extraAnim(GiantMothEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("body").zRot += s * 2.6f; bone("body").y += s * 6f;
        for (String n : new String[]{"L", "R"}) { float sg = n.equals("L") ? 1 : -1; bone("wing" + n).zRot += sg * s * 0.9f; bone("wingOut" + n).zRot += sg * s * 0.5f; }
    }

    @Override
    protected AnimClip clip(GiantMothEntity e, int id) {
        switch (id) {
            case GiantMothEntity.DUST: return C_DUST;
            case GiantMothEntity.RAM: return C_RAM;
            case GiantMothEntity.STING: return C_STING;
            case GiantMothEntity.BUFFET: return C_BUFFET;
            default: return null;
        }
    }

    private static void wings(AnimClip.Builder b, float[]... rows) { // rows {t, wingZ, outZ, yaw}; positive = DOWN stroke, mirrored on the right
        float[][] i = new float[rows.length][], o = new float[rows.length][];
        for (int r = 0; r < rows.length; r++) { i[r] = k(rows[r][0], 0, rows[r][3], rows[r][1]); o[r] = k(rows[r][0], 0, 0, rows[r][2]); }
        pair(b, "wing", i); pair(b, "wingOut", o);
    }

    /** 40t, burst @20 (.5): wings beat faster and faster while the abdomen swells, then one huge down-stroke shakes the scales loose. */
    private static AnimClip dust() {
        AnimClip.Builder b = AnimClip.create();
        wings(b, k(0, 0, 0, 0), k(0.1f, -60, -30, 0), k(0.2f, 50, 30, 0), k(0.3f, -70, -35, 0), k(0.4f, 55, 30, 0), k(0.5f, 70, 40, 0, SNAP), k(0.6f, -50, -20, 0), k(0.75f, 20, 10, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -22, 0, 0), k(0.5f, 12, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.3f, -30, 0, 0), k(0.5f, 20, 0, 0, SNAP), k(1, 0, 0, 0));
        scale(b, "abdomen", k(0, 1, 1, 1), k(0.3f, 1.25f, 1.25f, 1.05f), k(0.45f, 1.4f, 1.4f, 1.1f), k(0.5f, 0.85f, 0.85f, 1f, SNAP), k(0.7f, 1.05f, 1.05f, 1f), k(1, 1, 1, 1));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -14, 0, 0), k(0.5f, 8, 0, 0), k(1, 0, 0, 0));
        pair(b, "ant", k(0, 0, 0, 0), k(0.3f, -20, 0, 20), k(0.5f, 10, 0, 30), k(1, 0, 0, 0));
        return b.build();
    }

    /** 34t, impact @17 (.5): flares up and back with wings spread, folds them, plummets beak-first into the target, wings snapping open to brake. */
    private static AnimClip ram() {
        AnimClip.Builder b = AnimClip.create();
        wings(b, k(0, 0, 0, 0), k(0.15f, -60, -30, 0), k(0.3f, -85, -40, 0), k(0.4f, -88, -50, 18), k(0.5f, -86, -55, 20, SNAP), k(0.62f, -80, -40, 10), k(0.72f, 45, 40, -10, SNAP), k(0.9f, 20, 10, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -35, 0, 0), k(0.4f, 40, 0, 0, SNAP), k(0.5f, 52, 0, 0), k(0.65f, 38, 0, 0), k(0.8f, -14, 0, 0), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.3f, 22, 0, 0), k(0.5f, -40, 0, 0), k(0.8f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -20, 0, 0), k(0.5f, 16, 0, 0), k(1, 0, 0, 0));
        rot(b, "prob", k(0, 0, 0, 0), k(0.4f, -50, 0, 0), k(0.5f, -52, 0, 0), k(1, 0, 0, 0));
        rot(b, "prob2", k(0, 0, 0, 0), k(0.4f, -34, 0, 0), k(0.5f, -34, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.3f, 0, -2, 3), k(0.5f, 0, 5, -8), k(0.8f, 0, 0, -3), k(1, 0, 0, 0));
        return b.build();
    }

    /** 24t, jab @11 (.458): proboscis unrolls, head cocks back, then the whole body lunges and stabs; abdomen curls for balance. */
    private static AnimClip sting() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "prob", k(0, 0, 0, 0), k(0.25f, -50, 0, 0), k(0.4f, -52, 0, 0), k(0.458f, -58, 0, 0, SNAP), k(0.75f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "prob2", k(0, 0, 0, 0), k(0.25f, -34, 0, 0), k(0.458f, -38, 0, 0, SNAP), k(0.75f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -26, 0, 0), k(0.458f, 24, 0, 0, SNAP), k(0.7f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -18, 0, 0), k(0.458f, 20, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.3f, 0, 0, 3), k(0.458f, 0, 1, -6, SNAP), k(0.8f, 0, 0, -1), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.3f, 25, 0, 0), k(0.458f, -30, 0, 0, SNAP), k(1, 0, 0, 0));
        wings(b, k(0, 0, 0, 0), k(0.3f, -40, -20, 0), k(0.458f, 30, 20, 0), k(0.7f, -20, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 28t, gust @14 (.5): wings cocked high and forward, then slammed back and down in one enormous sweep, throwing the body backwards. */
    private static AnimClip buffet() {
        AnimClip.Builder b = AnimClip.create();
        wings(b, k(0, 0, 0, 0), k(0.25f, -88, -50, -40), k(0.4f, -90, -55, -45), k(0.5f, 70, 50, 45, SNAP), k(0.62f, 50, 20, 30), k(0.8f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.25f, 14, 0, 0), k(0.5f, -26, 0, 0, SNAP), k(0.8f, -6, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.4f, 0, 3, -2), k(0.5f, 0, -4, 5, SNAP), k(0.8f, 0, 0, 2), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, 16, 0, 0), k(0.5f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.4f, -22, 0, 0), k(0.5f, 28, 0, 0), k(1, 0, 0, 0));
        pair(b, "ant", k(0, 0, 0, 0), k(0.5f, 28, 0, 10), k(1, 0, 0, 0));
        return b.build();
    }
}
