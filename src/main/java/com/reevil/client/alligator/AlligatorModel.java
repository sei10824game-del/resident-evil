package com.reevil.client.alligator;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.alligator.AlligatorEntity;
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
 * Giant Alligator. Faces -Z, +xRot = nose DOWN. Texture 128x64 (tools/tex_alligator.py).
 * body > head > jaw ; body > t1 > t2 > t3 ; body > flL/flR (> flowL/R > ftL/R), hlL/hlR (> hlowL/R > htL/R).
 * Impacts: snap .4545, lunge .467, roll .1875 + every 8t, sweep .5, ambush .5.
 */
public class AlligatorModel extends BioModel<AlligatorEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "alligator"), "main");
    private static final AnimClip C_SNAP = snap(), C_LUNGE = lunge(), C_ROLL = roll(), C_SWEEP = sweep(), C_AMBUSH = ambush();

    public AlligatorModel(ModelPart root) {
        super(root);
        reg("body", "body"); reg("head", "body/head"); reg("jaw", "body/head/jaw");
        reg("t1", "body/t1"); reg("t2", "body/t1/t2"); reg("t3", "body/t1/t2/t3");
        for (String s : new String[]{"L", "R"}) for (String g : new String[]{"f", "h"}) {
            String a = g + "l" + s;
            reg(a, "body/" + a); reg(g + "low" + s, "body/" + a + "/" + g + "low" + s); reg(g + "t" + s, "body/" + a + "/" + g + "low" + s + "/" + g + "t" + s);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeListBuilder bodyC = CubeListBuilder.create().texOffs(0, 0).addBox(-5, -3, -12, 10, 6, 24);
        for (int i = 0; i < 5; i++) bodyC.texOffs(46, 50).addBox(-0.5f, -5, -9 + i * 4.5f, 1, 2, 2);
        PartDefinition body = root.addOrReplaceChild("body", bodyC, PartPose.offset(0, 18.5f, 0));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(70, 0).addBox(-3.5f, -2.5f, -14, 7, 4, 14)
            .texOffs(110, 24).addBox(-3.5f, -4.5f, -4, 2, 2, 2).texOffs(110, 24).addBox(1.5f, -4.5f, -4, 2, 2, 2)
            .texOffs(110, 20).addBox(-3, 1.5f, -13, 6, 1, 1).texOffs(110, 20).addBox(-3, 1.5f, -9, 6, 1, 1).texOffs(110, 20).addBox(-3, 1.5f, -5, 6, 1, 1), PartPose.offsetAndRotation(0, -0.5f, -12, 0.05f, 0, 0));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(70, 20).addBox(-3, 0, -13, 6, 2, 13)
            .texOffs(110, 20).addBox(-2.5f, -1, -12, 5, 1, 1).texOffs(110, 20).addBox(-2.5f, -1, -8, 5, 1, 1).texOffs(110, 20).addBox(-2.5f, -1, -4, 5, 1, 1), PartPose.offset(0, 1.5f, -1));
        PartDefinition t1 = body.addOrReplaceChild("t1", CubeListBuilder.create().texOffs(0, 32).addBox(-4, -2.5f, 0, 8, 5, 10).texOffs(46, 50).addBox(-0.5f, -4.5f, 2, 1, 2, 2), PartPose.offset(0, 0, 12));
        PartDefinition t2 = t1.addOrReplaceChild("t2", CubeListBuilder.create().texOffs(38, 32).addBox(-3, -2, 0, 6, 4, 10).texOffs(46, 50).addBox(-0.5f, -4, 3, 1, 2, 2), PartPose.offset(0, 0, 10));
        t2.addOrReplaceChild("t3", CubeListBuilder.create().texOffs(72, 38).addBox(-1.5f, -1.5f, 0, 3, 3, 10), PartPose.offset(0, 0, 10));
        for (int s = 0; s < 2; s++) for (int g = 0; g < 2; g++) {
            float sg = s == 0 ? 1 : -1; String n = s == 0 ? "L" : "R"; String p = g == 0 ? "f" : "h"; float z = g == 0 ? -8 : 8;
            PartDefinition up = body.addOrReplaceChild(p + "l" + n, CubeListBuilder.create().texOffs(0, 50).addBox(-1.5f, 0, -1.5f, 3, 4, 3), PartPose.offsetAndRotation(sg * 5.5f, 1.5f, z, 0, 0, sg * 0.9f));
            PartDefinition lo = up.addOrReplaceChild(p + "low" + n, CubeListBuilder.create().texOffs(14, 50).addBox(-1.5f, 0, -1.5f, 3, 3, 3), PartPose.offsetAndRotation(0, 4, 0, 0, 0, -sg * 0.9f));
            lo.addOrReplaceChild(p + "t" + n, CubeListBuilder.create().texOffs(28, 50).addBox(-2, 0, -3, 4, 1, 4), PartPose.offset(0, 3, 0));
        }
        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    protected void baseAnim(AlligatorEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        boolean wet = e.isInWater();
        float p = ls * (wet ? 0.5f : 0.9f), s = Mth.sin(p);
        // sinuous swim / waddle: the whole spine S-curves
        float sw = (wet ? 0.35f : 0.18f) * (0.3f + amt);
        float sa = age * (wet ? 0.2f : 0.1f);
        bone("body").yRot += -s * sw * 0.3f * (amt > 0.05f ? 1f : 0f);
        bone("t1").yRot += Mth.sin(p - 0.6f) * sw + Mth.sin(sa) * 0.05f;
        bone("t2").yRot += Mth.sin(p - 1.4f) * sw * 1.3f;
        bone("t3").yRot += Mth.sin(p - 2.2f) * sw * 1.5f;
        bone("head").yRot += Mth.sin(p) * sw * 0.5f + Mth.clamp(yaw, -35f, 35f) * Mth.DEG_TO_RAD * 0.5f;
        bone("head").xRot += Mth.clamp(pitch, -20f, 20f) * Mth.DEG_TO_RAD * 0.4f;
        bone("jaw").xRot += 0.04f + (e.getTarget() != null ? 0.14f : 0f) + Mth.sin(age * 0.07f) * 0.03f;
        if (!wet) bone("body").y += -Math.abs(s) * 0.4f * amt;
        for (int k = 0; k < 2; k++) {
            String n = k == 0 ? "L" : "R";
            float ph = k == 0 ? p : p + Mth.PI;
            // diagonal gait: front-left with hind-right
            float swF = Mth.sin(ph), swH = Mth.sin(ph + Mth.PI);
            bone("fl" + n).xRot += swF * 0.6f * amt; bone("flow" + n).xRot += Math.max(0, Mth.cos(ph)) * 0.5f * amt;
            bone("hl" + n).xRot += swH * 0.6f * amt; bone("hlow" + n).xRot += Math.max(0, Mth.cos(ph + Mth.PI)) * 0.5f * amt;
            if (wet) { bone("fl" + n).zRot += (k == 0 ? -1 : 1) * 0.5f; bone("hl" + n).zRot += (k == 0 ? -1 : 1) * 0.5f; } // legs tucked while swimming
        }
        if (e.hurtTime > 0) { float h = e.hurtTime / (float) Math.max(1, e.hurtDuration); bone("head").xRot -= h * 0.3f; bone("jaw").xRot += h * 0.6f; bone("t1").yRot += h * 0.3f; }
    }

    @Override
    protected void extraAnim(AlligatorEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("body").zRot += s * Mth.PI; bone("body").y += s * 5f;
        bone("jaw").xRot += s * 0.8f;
        for (String n : new String[]{"L", "R"}) { bone("fl" + n).zRot += s * 1.0f; bone("hl" + n).zRot += s * 1.0f; }
    }

    @Override
    protected AnimClip clip(AlligatorEntity e, int id) {
        switch (id) {
            case AlligatorEntity.SNAP: return C_SNAP;
            case AlligatorEntity.LUNGE: return C_LUNGE;
            case AlligatorEntity.ROLL: return C_ROLL;
            case AlligatorEntity.SWEEP: return C_SWEEP;
            case AlligatorEntity.AMBUSH: return C_AMBUSH;
            default: return null;
        }
    }

    private static void tail(AnimClip.Builder b, float[]... rows) { // rows {t, y1, y2, y3}
        float[][] r1 = new float[rows.length][], r2 = new float[rows.length][], r3 = new float[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            r1[i] = k(rows[i][0], 0, rows[i][1], 0); r2[i] = k(rows[i][0], 0, rows[i][2], 0); r3[i] = k(rows[i][0], 0, rows[i][3], 0);
        }
        rot(b, "t1", r1); rot(b, "t2", r2); rot(b, "t3", r3);
    }

    /** 22t, snap @10 (.4545): neck cocks back, jaws gape, head shoots forward and slams shut, then a flick of the head. */
    private static AnimClip snap() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -26, 0, 0), k(0.4545f, 14, 0, 0, SNAP), k(0.6f, 10, 14, 0), k(0.75f, 6, -10, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 60, 0, 0, OUT), k(0.42f, 66, 0, 0), k(0.4545f, -6, 0, 0, SNAP), k(0.7f, 6, 0, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -4, 0, 0), k(0.4545f, 6, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.3f, 0, 0, 2), k(0.4545f, 0, 0, -4, SNAP), k(0.8f, 0, 0, -1), k(1, 0, 0, 0));
        tail(b, k(0, 0, 0, 0), k(0.3f, 14, 18, 20), k(0.4545f, -10, -14, -18), k(0.8f, 4, 6, 8), k(1, 0, 0, 0));
        pair(b, "fl", k(0, 0, 0, 0), k(0.3f, 0, 0, 10), k(0.4545f, -20, 0, -8), k(1, 0, 0, 0));
        return b.build();
    }

    /** 30t, impact @14 (.467): coils the S-shape, drives off all four legs and tail, flies flat with jaws agape, lands with a clamp. */
    private static AnimClip lunge() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.25f, 0, 14, 0), k(0.35f, -10, -8, 0, SNAP), k(0.467f, 6, 0, 0, SNAP), k(0.7f, 2, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.25f, 0, 2, 3), k(0.35f, 0, -2, -3, SNAP), k(0.467f, 0, 0, -6), k(0.75f, 0, 1.5f, -2), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -20, 20, 0), k(0.4f, -12, -6, 0), k(0.467f, 18, 0, 0, SNAP), k(0.7f, 6, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.25f, 50, 0, 0), k(0.4f, 72, 0, 0), k(0.467f, -8, 0, 0, SNAP), k(0.7f, 8, 0, 0), k(1, 0, 0, 0));
        tail(b, k(0, 0, 0, 0), k(0.25f, -22, -28, -34), k(0.35f, 24, 30, 34, SNAP), k(0.6f, -8, -10, -12), k(1, 0, 0, 0));
        pair(b, "fl", k(0, 0, 0, 0), k(0.25f, 20, 0, 0), k(0.35f, -50, 0, -10), k(0.467f, -40, 0, -10), k(1, 0, 0, 0));
        pair(b, "hl", k(0, 0, 0, 0), k(0.25f, 25, 0, 0), k(0.35f, 40, 0, 8), k(1, 0, 0, 0));
        return b.build();
    }

    /** 64t: jaws clamp (@12), then the body corkscrews through two full rolls (a hit every 8t) with the tail thrashing and head whipping. */
    private static AnimClip roll() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.1f, 6, 0, 0), k(0.2f, 4, 0, 0), k(0.4f, 4, 0, 360, LIN), k(0.6f, 4, 0, 720, LIN), k(0.8f, 4, 0, 1080, LIN), k(0.9f, 0, 0, 1080), k(1, 0, 0, 1080));
        rot(b, "head", k(0, 0, 0, 0), k(0.1f, 14, 0, 0), k(0.25f, 10, 18, 0), k(0.35f, 12, -20, 0), k(0.45f, 10, 20, 0), k(0.55f, 12, -20, 0), k(0.65f, 10, 18, 0), k(0.75f, 12, -16, 0), k(0.9f, 0, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.06f, 55, 0, 0, OUT), k(0.1875f, -6, 0, 0, SNAP), k(0.83f, -4, 0, 0), k(0.92f, 30, 0, 0), k(1, 0, 0, 0));
        tail(b, k(0, 0, 0, 0), k(0.15f, 26, 32, 40), k(0.25f, -28, -34, -42), k(0.35f, 28, 36, 44), k(0.45f, -28, -36, -44), k(0.55f, 28, 36, 44), k(0.65f, -28, -36, -44), k(0.8f, 20, 24, 30), k(1, 0, 0, 0));
        pair(b, "fl", k(0, 0, 0, 0), k(0.15f, 0, 0, -30), k(0.85f, 0, 0, -30), k(1, 0, 0, 0));
        pair(b, "hl", k(0, 0, 0, 0), k(0.15f, 0, 0, -30), k(0.85f, 0, 0, -30), k(1, 0, 0, 0));
        return b.build();
    }

    /** 28t, impact @14 (.5): body bends into a C round the prey, tail cocked, then the whole rear whips through 180 degrees. */
    private static AnimClip sweep() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.35f, 0, 22, 0), k(0.5f, 0, -26, 0, SNAP), k(0.75f, 0, -10, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.35f, 0, -26, 0), k(0.5f, 0, 24, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.35f, 30, 0, 0), k(0.5f, 8, 0, 0), k(1, 0, 0, 0));
        tail(b, k(0, 0, 0, 0), k(0.35f, 36, 44, 50), k(0.5f, -50, -60, -70, SNAP), k(0.62f, -20, -30, -34), k(0.8f, 6, 8, 10), k(1, 0, 0, 0));
        pair(b, "fl", k(0, 0, 0, 0), k(0.35f, 14, 0, 6), k(0.5f, -16, 0, -6), k(1, 0, 0, 0));
        pair(b, "hl", k(0, 0, 0, 0), k(0.35f, -20, 0, 6), k(0.5f, 24, 0, -6), k(1, 0, 0, 0));
        return b.build();
    }

    /** 36t, impact @18 (.5): rockets up out of the water at a steep angle (nose high), jaws agape, belly arched, crashes down on the bank. */
    private static AnimClip ambush() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.2f, 10, 0, 0), k(0.35f, -34, 0, 0, SNAP), k(0.5f, -24, 0, 0), k(0.7f, 14, 0, 0, IN), k(0.85f, 6, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.2f, 0, 3, 2), k(0.35f, 0, -4, -3), k(0.5f, 0, -8, -6), k(0.7f, 0, 1, -3), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -10, 0, 0), k(0.5f, 18, 0, 0, SNAP), k(0.7f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 66, 0, 0, OUT), k(0.46f, 74, 0, 0), k(0.5f, -8, 0, 0, SNAP), k(0.8f, 6, 0, 0), k(1, 0, 0, 0));
        tail(b, k(0, 0, 0, 0), k(0.2f, 20, 24, 28), k(0.35f, -20, -24, -28), k(0.6f, 10, 12, 14), k(1, 0, 0, 0));
        pair(b, "fl", k(0, 0, 0, 0), k(0.35f, -60, 0, -20), k(0.5f, -40, 0, -10), k(1, 0, 0, 0));
        return b.build();
    }
}
