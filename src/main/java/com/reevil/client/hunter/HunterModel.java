package com.reevil.client.hunter;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.anim.AnimClip.Ease;
import com.reevil.client.BioModel;
import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Hunter: hunched digitigrade reptile. Bone tree:
 * base(center of mass) > hips > chest(+back ridge) > neck > head(skull,snout,crest) > jaw
 *                               chest > armL/R > foreL/R(+elbow spur) > handL/R > clawL1..3 / clawR1..3
 *                        hips > tail > tail2
 *        base > thighL/R > shinL/R > footL/R (metatarsus) > toesL/R
 * Texture 128x128 (tools/tex_hunter.py).
 */
public class HunterModel extends BioModel<HunterEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "hunter"), "main");
    private static final float D = (float) Math.PI / 180f;

    public HunterModel(ModelPart root) {
        super(root);
        reg("base", "base");
        reg("hips", "base/hips");
        reg("chest", "base/hips/chest");
        reg("neck", "base/hips/chest/neck");
        reg("head", "base/hips/chest/neck/head");
        reg("jaw", "base/hips/chest/neck/head/jaw");
        reg("tail", "base/hips/tail");
        reg("tail2", "base/hips/tail/tail2");
        for (String s : new String[]{"L", "R"}) {
            String arm = "base/hips/chest/arm" + s;
            reg("arm" + s, arm);
            reg("fore" + s, arm + "/fore" + s);
            reg("hand" + s, arm + "/fore" + s + "/hand" + s);
            for (int i = 1; i <= 3; i++) reg("claw" + s + i, arm + "/fore" + s + "/hand" + s + "/claw" + s + i);
            String leg = "base/thigh" + s;
            reg("thigh" + s, leg);
            reg("shin" + s, leg + "/shin" + s);
            reg("foot" + s, leg + "/shin" + s + "/foot" + s);
            reg("toes" + s, leg + "/shin" + s + "/foot" + s + "/toes" + s);
        }
    }

    public static LayerDefinition createBodyLayer() { return LayerDefinition.create(mesh(), 128, 128); }
    public static MeshDefinition mesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0f, 12f, 0f));
        PartDefinition hips = base.addOrReplaceChild("hips", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.5f, -2f, -3f, 9, 5, 6), PartPose.offset(0f, -4.5f, 0f));
        PartDefinition chest = hips.addOrReplaceChild("chest", CubeListBuilder.create()
            .texOffs(0, 12).addBox(-5.5f, -13f, -4f, 11, 13, 8)
            .texOffs(40, 0).addBox(-1f, -12f, 4f, 2, 11, 2), PartPose.offsetAndRotation(0f, -1f, 0f, 0.55f, 0f, 0f));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create()
            .texOffs(50, 0).addBox(-2.5f, -4f, -2.5f, 5, 5, 5), PartPose.offsetAndRotation(0f, -12.5f, -1f, -0.25f, 0f, 0f));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(72, 0).addBox(-3.5f, -4f, -5f, 7, 5, 7)
            .texOffs(100, 0).addBox(-2.5f, -2.5f, -10f, 5, 3, 5)
            .texOffs(72, 14).addBox(-1f, -5f, -3f, 2, 1, 6), PartPose.offsetAndRotation(0f, -3.5f, -0.5f, -0.3f, 0f, 0f));
        head.addOrReplaceChild("jaw", CubeListBuilder.create()
            .texOffs(100, 10).addBox(-2.5f, 0f, -5.5f, 5, 2, 6), PartPose.offset(0f, 0.5f, -4.5f));
        PartDefinition tail = hips.addOrReplaceChild("tail", CubeListBuilder.create()
            .texOffs(0, 66).addBox(-1.5f, -1.5f, 0f, 3, 3, 8), PartPose.offsetAndRotation(0f, 0f, 2.5f, -0.6f, 0f, 0f));
        tail.addOrReplaceChild("tail2", CubeListBuilder.create()
            .texOffs(24, 66).addBox(-1f, -1f, 0f, 2, 2, 8), PartPose.offsetAndRotation(0f, 0f, 7.5f, -0.3f, 0f, 0f));

        for (int s = 0; s < 2; s++) {
            boolean left = s == 0;
            String n = left ? "L" : "R";
            float sx = left ? 1f : -1f;
            int v = left ? 36 : 50;
            // arm: shoulder at chest top, hanging long and loose
            PartDefinition arm = chest.addOrReplaceChild("arm" + n, CubeListBuilder.create()
                .texOffs(0, v).addBox(left ? -1f : -2f, -1f, -1.5f, 3, 8, 3),
                PartPose.offsetAndRotation(6.5f * sx, -11f, 0f, -0.72f, 0f, -0.1f * sx));
            PartDefinition fore = arm.addOrReplaceChild("fore" + n, CubeListBuilder.create()
                .texOffs(12, v).addBox(-1.5f, 0f, -1.5f, 3, 8, 3)
                .texOffs(52, v).addBox(-0.5f, -0.5f, 1f, 1, 3, 2),
                PartPose.offsetAndRotation(0.5f * sx, 6.5f, 0f, -0.35f, 0f, 0f));
            PartDefinition hand = fore.addOrReplaceChild("hand" + n, CubeListBuilder.create()
                .texOffs(24, v).addBox(-2f, 0f, -2f, 4, 2, 4), PartPose.offset(0f, 7.5f, 0f));
            for (int i = 1; i <= 3; i++) {
                float cx = (i - 2) * 1.3f * sx;
                hand.addOrReplaceChild("claw" + n + i, CubeListBuilder.create()
                    .texOffs(40 + (i - 1) * 4, v).addBox(-0.5f, 0f, -0.5f, 1, 5, 1),
                    PartPose.offsetAndRotation(cx, 1.5f, i == 2 ? -1.2f : -0.7f, -0.35f, 0f, 0f));
            }
            // digitigrade leg: thigh forward, shin back, long metatarsus, splayed toes
            PartDefinition thigh = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create()
                .texOffs(64, v).addBox(-2f, -1f, -2.5f, 4, 8, 5),
                PartPose.offsetAndRotation(3f * sx, -3.5f, 0f, -0.55f, 0f, 0f));
            PartDefinition shin = thigh.addOrReplaceChild("shin" + n, CubeListBuilder.create()
                .texOffs(82, v).addBox(-1.5f, 0f, -1.5f, 3, 7, 3), PartPose.offsetAndRotation(0f, 6f, 0f, 1.1f, 0f, 0f));
            PartDefinition foot = shin.addOrReplaceChild("foot" + n, CubeListBuilder.create()
                .texOffs(94, v).addBox(-1.5f, 0f, -1.5f, 3, 5, 3), PartPose.offsetAndRotation(0f, 6f, 0f, -0.7f, 0f, 0f));
            foot.addOrReplaceChild("toes" + n, CubeListBuilder.create()
                .texOffs(106, v).addBox(-2f, 0f, -4f, 4, 1, 5), PartPose.offsetAndRotation(0f, 4.5f, 0f, 0.15f, 0f, 0f));
        }
        return mesh;
    }

    // ------------------------------------------------------------------ helpers

    private void rot(String b, float x, float y, float z) {
        ModelPart p = bone(b);
        p.xRot += x * D; p.yRot += y * D; p.zRot += z * D;
    }

    /** Fold both digitigrade legs. k = {t, crouchDeg, swingL, swingR, dy, dz}; swing + = leg forward. Base drops so feet stay planted. */
    static void legs(AnimClip.Builder b, float[]... keys) {
        String[] side = {"L", "R"};
        for (int s = 0; s < 2; s++) {
            AnimClip.BoneBuilder th = b.bone("thigh" + side[s]);
            AnimClip.BoneBuilder sh = b.bone("shin" + side[s]);
            AnimClip.BoneBuilder ft = b.bone("foot" + side[s]);
            for (float[] k : keys) {
                float c = k[1], sw = k[2 + s];
                th.rot(k[0], -c - sw, 0, 0);
                sh.rot(k[0], 2f * c, 0, 0);
                ft.rot(k[0], -c, 0, 0);
            }
        }
        AnimClip.BoneBuilder bs = b.bone("base");
        for (float[] k : keys) bs.pos(k[0], 0, k[1] * 0.14f + k[4], k[5]);
    }

    /** Three claw fingers of one hand. k = {t, curl, spread}; curl + = hook forward, spread + = fingers fan apart. */
    static void claws(AnimClip.Builder b, String side, float[]... keys) {
        AnimClip.BoneBuilder c1 = b.bone("claw" + side + "1");
        AnimClip.BoneBuilder c2 = b.bone("claw" + side + "2");
        AnimClip.BoneBuilder c3 = b.bone("claw" + side + "3");
        float m = side.equals("L") ? 1f : -1f;
        for (float[] k : keys) {
            c1.rot(k[0], -k[1], 0, k[2] * m);
            c2.rot(k[0], -k[1] * 1.1f, 0, 0);
            c3.rot(k[0], -k[1], 0, -k[2] * m);
        }
    }

    private static float[] k(float... v) { return v; }

    // ------------------------------------------------------------------ clips

    private static final AnimClip SLASH, LEAP, DECAP, PIN, BACKFLIP, SIDE_L, SIDE_R, ROAR, VAULT, HURT, DEATH;

    static {
        // ---- 1: standing double-claw combo (hits t=9,15 of 24 -> .375 / .625)
        AnimClip.Builder b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.25f, 22, 0, 0, 0, .6f), k(.375f, 16, 22, -8, 0, -1.5f), k(.53f, 24, 14, -4, 0, -1f),
            k(.625f, 18, -6, 24, 0, -2.5f), k(.75f, 22, -4, 18, 0, -2.2f), k(1, 0, 0, 0, 0, 0));
        b.bone("hips").rot(0, 0, 0, 0).rot(.25f, 0, -10, 0).rot(.375f, 4, 12, 0, Ease.IN).rot(.53f, 0, -8, 0).rot(.625f, 4, -12, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.25f, -8, -28, 4).rot(.375f, 12, 32, -4, Ease.IN).rot(.45f, 14, 36, -5, Ease.OUT)
            .rot(.53f, 6, -26, 4).rot(.625f, 14, -34, 4, Ease.IN).rot(.72f, 16, -40, 5, Ease.OUT).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(.25f, -10, 14, 0).rot(.375f, 6, -16, 0, Ease.IN).rot(.53f, -8, 13, 0).rot(.625f, 6, 17, 0, Ease.IN).rot(.72f, 6, 20, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.25f, -12, 14, -6).rot(.375f, 10, -16, 8, Ease.IN).rot(.53f, -10, 13, -6).rot(.625f, 10, 17, -8, Ease.IN).rot(.72f, 8, 20, -6).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.2f, 30, 0, 0).rot(.375f, 10, 0, 0).rot(.5f, 32, 0, 0).rot(.625f, 8, 0, 0).rot(.8f, 25, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.25f, -120, -40, -50).rot(.375f, -85, 75, 10, Ease.IN).rot(.45f, -60, 95, 15, Ease.OUT)
            .rot(.62f, -25, 30, 0).rot(1, 0, 0, 0);
        b.bone("foreL").rot(0, 0, 0, 0).rot(.25f, -70, 0, 0).rot(.375f, -5, 0, 0, Ease.IN).rot(.45f, -15, 0, 0).rot(.62f, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("handL").rot(0, 0, 0, 0).rot(.25f, 25, 0, 0).rot(.375f, -30, 0, 0, Ease.IN).rot(.45f, -35, 0, 0).rot(1, 0, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.25f, -25, 30), k(.375f, 45, 10), k(.45f, 55, 5), k(.7f, 10, 8), k(1, 0, 0));
        b.bone("armR").rot(0, 0, 0, 0).rot(.25f, 25, 0, 0).rot(.375f, 30, 0, 0).rot(.53f, -125, 40, 50).rot(.625f, -85, -75, -10, Ease.IN)
            .rot(.72f, -60, -95, -15, Ease.OUT).rot(.88f, -20, -20, 0).rot(1, 0, 0, 0);
        b.bone("foreR").rot(0, 0, 0, 0).rot(.25f, -20, 0, 0).rot(.375f, -20, 0, 0).rot(.53f, -70, 0, 0).rot(.625f, -5, 0, 0, Ease.IN).rot(.72f, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("handR").rot(0, 0, 0, 0).rot(.53f, 25, 0, 0).rot(.625f, -30, 0, 0, Ease.IN).rot(.72f, -35, 0, 0).rot(1, 0, 0, 0);
        claws(b, "R", k(0, 0, 0), k(.375f, 5, 5), k(.53f, -25, 30), k(.625f, 45, 10), k(.72f, 55, 5), k(.9f, 10, 5), k(1, 0, 0));
        b.bone("tail").rot(0, 0, 0, 0).rot(.25f, 8, 22, 0).rot(.375f, 4, -26, 0, Ease.IN).rot(.53f, 8, 20, 0).rot(.625f, 4, 26, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.3f, 0, 25, 0).rot(.45f, 0, -30, 0).rot(.6f, 0, 22, 0).rot(.75f, 0, -28, 0).rot(1, 0, 0, 0);
        SLASH = b.build();

        // ---- 2: leaping claw (launch t=9 -> .225, land ~t=25, hits 26/31 -> .65 / .775)
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.18f, 42, 6, 6, 0, 1.2f), k(.225f, -8, -20, -20, -1.5f, 0), k(.32f, 40, 25, 15, -6.6f, 0),
            k(.55f, 25, 35, 30, -3.5f, 0), k(.625f, 40, 10, 6, 0, 0), k(.7f, 30, 4, -6, 0, -1), k(.775f, 32, -6, 10, 0, -1.5f),
            k(.88f, 20, 0, 0, 0, -.5f), k(1, 0, 0, 0, 0, 0));
        b.bone("base").rot(0, 0, 0, 0).rot(.18f, 8, 0, 0).rot(.25f, -12, 0, 0, Ease.OUT).rot(.42f, 0, 0, 0).rot(.58f, 14, 0, 0).rot(.65f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.18f, 22, 0, 0).rot(.225f, -16, 0, 0, Ease.OUT).rot(.4f, -6, 0, 0).rot(.6f, 8, 0, 0)
            .rot(.65f, 18, 22, 0, Ease.IN).rot(.72f, 16, 26, 0).rot(.775f, 18, -24, 0, Ease.IN).rot(.85f, 14, -28, 0).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(.18f, -18, 0, 0).rot(.225f, 10, 0, 0).rot(.4f, 4, 0, 0).rot(.6f, -10, 0, 0).rot(.65f, -6, -10, 0).rot(.775f, -6, 12, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.18f, -24, 0, 0).rot(.225f, 6, 0, 0).rot(.4f, 0, 0, 0).rot(.6f, -14, 0, 0).rot(.65f, -8, -10, 0).rot(.775f, -8, 12, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.15f, 35, 0, 0).rot(.225f, 20, 0, 0).rot(.45f, 40, 0, 0).rot(.62f, 45, 0, 0).rot(.66f, 10, 0, 0)
            .rot(.75f, 35, 0, 0).rot(.8f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.18f, 55, 0, -20).rot(.225f, -60, 0, -35, Ease.OUT).rot(.3f, -150, 0, -55).rot(.55f, -140, -10, -60)
            .rot(.62f, -125, -20, -40).rot(.65f, -55, 60, 5, Ease.IN).rot(.72f, -35, 75, 10, Ease.OUT).rot(.85f, -15, 20, 0).rot(1, 0, 0, 0);
        b.bone("foreL").rot(0, 0, 0, 0).rot(.18f, -20, 0, 0).rot(.3f, -15, 0, 0).rot(.55f, -25, 0, 0).rot(.62f, -50, 0, 0).rot(.65f, -5, 0, 0, Ease.IN).rot(.72f, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("handL").rot(0, 0, 0, 0).rot(.3f, 20, 0, 0).rot(.62f, 25, 0, 0).rot(.65f, -30, 0, 0, Ease.IN).rot(.8f, -20, 0, 0).rot(1, 0, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.18f, 10, 0), k(.3f, -30, 35), k(.6f, -30, 38), k(.65f, 50, 8), k(.75f, 40, 5), k(1, 0, 0));
        b.bone("armR").rot(0, 0, 0, 0).rot(.18f, 55, 0, 20).rot(.225f, -60, 0, 35, Ease.OUT).rot(.3f, -150, 0, 55).rot(.55f, -140, 10, 60)
            .rot(.7f, -130, 25, 45).rot(.775f, -55, -60, -5, Ease.IN).rot(.85f, -35, -75, -10, Ease.OUT).rot(.93f, -15, -20, 0).rot(1, 0, 0, 0);
        b.bone("foreR").rot(0, 0, 0, 0).rot(.18f, -20, 0, 0).rot(.3f, -15, 0, 0).rot(.55f, -25, 0, 0).rot(.7f, -50, 0, 0).rot(.775f, -5, 0, 0, Ease.IN).rot(.85f, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("handR").rot(0, 0, 0, 0).rot(.3f, 20, 0, 0).rot(.7f, 25, 0, 0).rot(.775f, -30, 0, 0, Ease.IN).rot(.9f, -20, 0, 0).rot(1, 0, 0, 0);
        claws(b, "R", k(0, 0, 0), k(.18f, 10, 0), k(.3f, -30, 35), k(.7f, -30, 38), k(.775f, 50, 8), k(.87f, 40, 5), k(1, 0, 0));
        b.bone("tail").rot(0, 0, 0, 0).rot(.18f, 20, 0, 0).rot(.25f, -10, 0, 0).rot(.4f, 25, 0, 0).rot(.6f, 30, 0, 0).rot(.66f, -5, -15, 0).rot(.78f, 0, 18, 0).rot(1, 0, 0, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.2f, 15, 0, 0).rot(.3f, -15, 0, 0).rot(.45f, 20, 0, 0).rot(.66f, -10, 0, 0).rot(.7f, 0, -20, 0).rot(.82f, 0, 22, 0).rot(1, 0, 0, 0);
        LEAP = b.build();

        // ---- 3: decapitation (rear up, cock both claws back at neck height, scissor across; hit t=21 of 36 -> .583)
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.14f, -10, 0, 0, -.5f, .5f), k(.4f, 14, -8, -8, 0, 1f), k(.5f, 22, 20, -10, 0, -1f),
            k(.583f, 30, 26, -14, 0, -3f), k(.66f, 34, 28, -16, 0, -3.4f), k(.82f, 26, 20, -10, 0, -2.8f), k(1, 0, 0, 0, 0, 0));
        b.bone("base").rot(0, 0, 0, 0).rot(.14f, -14, 0, 0).rot(.4f, -6, 0, 0).rot(.583f, 10, 0, 0, Ease.IN).rot(.7f, 14, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.14f, -32, 0, 0).rot(.22f, -30, 2, 0).rot(.3f, -28, -2, 0).rot(.4f, -12, 0, 0)
            .rot(.583f, 20, 0, 0, Ease.IN).rot(.66f, 30, 0, 0, Ease.OUT).rot(.82f, 22, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(.3f, 1.08f, 1.04f, 1.08f).scale(.5f, 1.04f, 1f, 1.04f).scale(.6f, 1, 1, 1).scale(1, 1, 1, 1);
        b.bone("hips").rot(0, 0, 0, 0).rot(.14f, -8, 0, 0).rot(.583f, 6, 0, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(.14f, -25, 0, 0).rot(.4f, -8, 0, 0).rot(.583f, -14, 0, 0, Ease.IN).rot(.66f, -18, 0, 0).rot(.82f, -10, 0, 12).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.14f, -35, 0, 0).rot(.3f, -30, 8, 0).rot(.4f, -6, 0, 0).rot(.583f, -12, 0, 0, Ease.IN)
            .rot(.7f, -10, 0, 0).rot(.82f, -6, 0, 22).rot(.92f, -4, 0, 18).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.12f, 45, 0, 0).rot(.3f, 40, 0, 0).rot(.4f, 15, 0, 0).rot(.583f, 5, 0, 0).rot(.7f, 30, 0, 0).rot(.82f, 35, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.14f, -165, 0, -45).rot(.22f, -160, -4, -52).rot(.3f, -162, -10, -55).rot(.45f, -95, -70, -15)
            .rot(.52f, -92, -80, -12).rot(.583f, -88, 80, 0, Ease.IN).rot(.66f, -85, 100, 4, Ease.OUT).rot(.82f, -70, 70, 0).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.14f, -165, 0, 45).rot(.22f, -160, 4, 52).rot(.3f, -162, 10, 55).rot(.45f, -95, 70, 15)
            .rot(.52f, -92, 80, 12).rot(.583f, -88, -80, 0, Ease.IN).rot(.66f, -85, -100, -4, Ease.OUT).rot(.82f, -70, -70, 0).rot(1, 0, 0, 0);
        for (String s : new String[]{"L", "R"}) {
            b.bone("fore" + s).rot(0, 0, 0, 0).rot(.14f, -20, 0, 0).rot(.45f, -45, 0, 0).rot(.583f, 0, 0, 0, Ease.IN).rot(.66f, -6, 0, 0).rot(.82f, -20, 0, 0).rot(1, 0, 0, 0);
            b.bone("hand" + s).rot(0, 0, 0, 0).rot(.14f, 30, 0, 0).rot(.45f, 30, 0, 0).rot(.583f, -10, 0, 0, Ease.IN).rot(1, 0, 0, 0);
            claws(b, s, k(0, 0, 0), k(.14f, -35, 40), k(.22f, -30, 44), k(.3f, -35, 40), k(.45f, -15, 10), k(.583f, -20, -6),
                k(.7f, 30, 0), k(.85f, 20, 5), k(1, 0, 0));
        }
        b.bone("tail").rot(0, 0, 0, 0).rot(.14f, -10, 0, 0).rot(.4f, 20, 0, 0).rot(.583f, 30, 0, 0).rot(.66f, 25, 10, 0).rot(.82f, 20, -10, 0).rot(1, 0, 0, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.2f, -10, 0, 0).rot(.45f, 25, 0, 0).rot(.583f, 15, 0, 0).rot(.7f, 20, 15, 0).rot(.88f, 10, -15, 0).rot(1, 0, 0, 0);
        DECAP = b.build();

        // ---- 4: pounce & pin maul (launch t=8 -> .143, slam ~.357, alternating stabs + bites, hits .357/.5/.643/.786/.893)
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.12f, 40, 0, 0, 0, 1), k(.143f, -6, -18, -18, -1, 0), k(.22f, 40, 40, 40, -6.6f, 0),
            k(.33f, 30, 45, 45, -4, 0), k(.357f, 44, 30, 30, 1, -2.5f), k(.5f, 40, 28, 24, 1.4f, -2.5f), k(.643f, 42, 24, 30, 1.2f, -2.6f),
            k(.786f, 40, 30, 24, 1.4f, -2.5f), k(.893f, 42, 24, 30, 1.2f, -2.6f), k(.95f, 20, 0, 0, 0, -1), k(1, 0, 0, 0, 0, 0));
        b.bone("base").rot(0, 0, 0, 0).rot(.12f, 8, 0, 0).rot(.143f, -14, 0, 0).rot(.3f, 8, 0, 0).rot(.357f, 32, 0, 0, Ease.IN).rot(.95f, 28, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.12f, 20, 0, 0).rot(.143f, -14, 0, 0).rot(.3f, 0, 0, 0).rot(.357f, 20, 0, 0, Ease.IN)
            .rot(.45f, 6, 14, 0).rot(.5f, 22, -12, 0, Ease.IN).rot(.6f, 6, -14, 0).rot(.643f, 22, 12, 0, Ease.IN).rot(.74f, 6, 14, 0)
            .rot(.786f, 22, -12, 0, Ease.IN).rot(.86f, 6, -14, 0).rot(.893f, 22, 12, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(.143f, 10, 0, 0).rot(.357f, -15, 0, 0).rot(.45f, -25, 0, 0).rot(.55f, 10, 0, 0).rot(.6f, -22, 0, 0)
            .rot(.7f, 10, 0, 0).rot(.75f, -22, 0, 0).rot(.84f, 10, 0, 0).rot(.893f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.143f, -10, 0, 0).rot(.357f, -15, 0, 0).rot(.45f, -15, 0, 0).rot(.55f, 25, 0, 0, Ease.IN)
            .rot(.6f, -15, 0, 0).rot(.7f, 25, 0, 0, Ease.IN).rot(.75f, -15, 0, 0).rot(.84f, 25, 0, 0, Ease.IN).rot(.9f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.143f, 30, 0, 0).rot(.357f, 45, 0, 0).rot(.45f, 45, 0, 0).rot(.55f, 0, 0, 0, Ease.IN).rot(.6f, 45, 0, 0)
            .rot(.7f, 0, 0, 0, Ease.IN).rot(.75f, 45, 0, 0).rot(.84f, 0, 0, 0, Ease.IN).rot(.9f, 30, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.12f, 50, 0, -15).rot(.143f, -90, 0, -30).rot(.22f, -130, 0, -50).rot(.33f, -110, 0, -40)
            .rot(.357f, -60, 0, -10, Ease.IN).rot(.45f, -160, -10, -30).rot(.5f, -55, 10, -5, Ease.IN).rot(.6f, -60, 0, -10)
            .rot(.74f, -160, -10, -30).rot(.786f, -55, 10, -5, Ease.IN).rot(.88f, -60, 0, -10).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.12f, 50, 0, 15).rot(.143f, -90, 0, 30).rot(.22f, -130, 0, 50).rot(.33f, -110, 0, 40)
            .rot(.357f, -60, 0, 10, Ease.IN).rot(.45f, -60, 0, 10).rot(.6f, -160, 10, 30).rot(.643f, -55, -10, 5, Ease.IN).rot(.74f, -60, 0, 10)
            .rot(.86f, -160, 10, 30).rot(.893f, -55, -10, 5, Ease.IN).rot(1, 0, 0, 0);
        b.bone("foreL").rot(0, 0, 0, 0).rot(.143f, -20, 0, 0).rot(.357f, -10, 0, 0).rot(.45f, -50, 0, 0).rot(.5f, 0, 0, 0, Ease.IN)
            .rot(.74f, -50, 0, 0).rot(.786f, 0, 0, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("foreR").rot(0, 0, 0, 0).rot(.143f, -20, 0, 0).rot(.357f, -10, 0, 0).rot(.6f, -50, 0, 0).rot(.643f, 0, 0, 0, Ease.IN)
            .rot(.86f, -50, 0, 0).rot(.893f, 0, 0, 0, Ease.IN).rot(1, 0, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.143f, -25, 35), k(.33f, -30, 38), k(.357f, 60, 5), k(.45f, -10, 25), k(.5f, 50, 0),
            k(.74f, -10, 25), k(.786f, 50, 0), k(.9f, 30, 0), k(1, 0, 0));
        claws(b, "R", k(0, 0, 0), k(.143f, -25, 35), k(.33f, -30, 38), k(.357f, 60, 5), k(.6f, -10, 25), k(.643f, 50, 0),
            k(.86f, -10, 25), k(.893f, 50, 0), k(.95f, 30, 0), k(1, 0, 0));
        b.bone("tail").rot(0, 0, 0, 0).rot(.143f, -15, 0, 0).rot(.3f, 25, 0, 0).rot(.357f, 35, 0, 0).rot(.5f, 30, 15, 0).rot(.643f, 30, -15, 0)
            .rot(.786f, 30, 15, 0).rot(.893f, 30, -15, 0).rot(1, 0, 0, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.2f, -15, 0, 0).rot(.4f, 25, 0, 0).rot(.55f, 15, 20, 0).rot(.7f, 15, -20, 0).rot(.85f, 15, 20, 0).rot(1, 0, 0, 0);
        PIN = b.build();

        // ---- 5: backflip evasion (impulse t=2, full 360 tucked flip, land crouched)
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.1f, 32, 0, 0, 0, 0), k(.15f, -6, -10, -10, -1, 0), k(.3f, 55, 30, 30, -10.7f, 0),
            k(.7f, 55, 30, 30, -10.7f, 0), k(.85f, 34, 6, 6, 0, 0), k(1, 0, 0, 0, 0, 0));
        b.bone("base").rot(0, 0, 0, 0).rot(.12f, 8, 0, 0).rot(.2f, -40, 0, 0, Ease.IN).rot(.45f, -190, 0, 0, Ease.LINEAR)
            .rot(.7f, -330, 0, 0, Ease.LINEAR).rot(.82f, -360, 0, 0, Ease.OUT).rot(1, -360, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.1f, 18, 0, 0).rot(.2f, -25, 0, 0).rot(.4f, 20, 0, 0).rot(.7f, 20, 0, 0).rot(.85f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(.2f, -20, 0, 0).rot(.4f, 15, 0, 0).rot(.75f, 10, 0, 0).rot(.85f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.2f, -20, 0, 0).rot(.45f, 20, 0, 0).rot(.8f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.1f, 25, 0, 0).rot(.4f, 10, 0, 0).rot(.85f, 35, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.1f, -40, 0, -10).rot(.2f, -150, 0, -40).rot(.4f, -60, 0, -20).rot(.7f, -60, 0, -20).rot(.85f, -30, 0, -50).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.1f, -40, 0, 10).rot(.2f, -150, 0, 40).rot(.4f, -60, 0, 20).rot(.7f, -60, 0, 20).rot(.85f, -30, 0, 50).rot(1, 0, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.2f, -25, 30), k(.4f, 40, 5), k(.85f, -20, 35), k(1, 0, 0));
        claws(b, "R", k(0, 0, 0), k(.2f, -25, 30), k(.4f, 40, 5), k(.85f, -20, 35), k(1, 0, 0));
        b.bone("tail").rot(0, 0, 0, 0).rot(.2f, 30, 0, 0).rot(.45f, -20, 0, 0).rot(.75f, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.3f, 25, 0, 0).rot(.55f, -25, 0, 0).rot(.85f, 15, 0, 0).rot(1, 0, 0, 0);
        BACKFLIP = b.build();

        SIDE_L = sideHop(1f);
        SIDE_R = sideHop(-1f);

        // ---- 7: roar / hiss display (rear up, chest swells, arms flung wide with trembling claws, head shakes)
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.15f, -10, 0, 0, 0, .6f), k(.6f, -8, 0, 0, 0, .6f), k(.8f, 24, 10, -6, 0, -1), k(1, 0, 0, 0, 0, 0));
        b.bone("base").rot(0, 0, 0, 0).rot(.15f, -16, 0, 0).rot(.6f, -14, 0, 0).rot(.8f, 8, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.15f, -38, 0, 0).rot(.3f, -36, 0, 0).rot(.45f, -40, 0, 0).rot(.6f, -36, 0, 0).rot(.8f, 18, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(.15f, 1.1f, 1.05f, 1.1f).scale(.6f, 1.08f, 1.04f, 1.08f).scale(.8f, 1, 1, 1).scale(1, 1, 1, 1);
        b.bone("neck").rot(0, 0, 0, 0).rot(.15f, -30, 0, 0).rot(.6f, -28, 0, 0).rot(.8f, 12, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.15f, -40, 0, 0).rot(.25f, -42, 10, 6).rot(.35f, -38, -10, -6).rot(.45f, -42, 10, 6)
            .rot(.55f, -38, -8, -6).rot(.65f, -30, 0, 0).rot(.8f, 18, 0, 0).rot(.9f, 15, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.12f, 55, 0, 0).rot(.6f, 52, 0, 0).rot(.7f, 20, 0, 0).rot(.8f, 40, 0, 0).rot(.92f, 30, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.15f, -60, -30, -75).rot(.25f, -65, -32, -80).rot(.35f, -58, -28, -72).rot(.45f, -66, -32, -80)
            .rot(.6f, -60, -30, -74).rot(.8f, -40, 10, -20).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.15f, -60, 30, 75).rot(.25f, -65, 32, 80).rot(.35f, -58, 28, 72).rot(.45f, -66, 32, 80)
            .rot(.6f, -60, 30, 74).rot(.8f, -40, -10, 20).rot(1, 0, 0, 0);
        for (String s : new String[]{"L", "R"}) {
            b.bone("fore" + s).rot(0, 0, 0, 0).rot(.15f, -30, 0, 0).rot(.6f, -35, 0, 0).rot(.8f, -40, 0, 0).rot(1, 0, 0, 0);
            b.bone("hand" + s).rot(0, 0, 0, 0).rot(.15f, 30, 0, 0).rot(.6f, 30, 0, 0).rot(1, 0, 0, 0);
            claws(b, s, k(0, 0, 0), k(.15f, -30, 40), k(.25f, -20, 30), k(.35f, -32, 42), k(.45f, -20, 30), k(.55f, -32, 42),
                k(.65f, -25, 35), k(.8f, 30, 10), k(1, 0, 0));
        }
        b.bone("tail").rot(0, 0, 0, 0).rot(.15f, 35, 0, 0).rot(.3f, 32, 12, 0).rot(.45f, 35, -12, 0).rot(.6f, 32, 10, 0).rot(.8f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.2f, 20, 0, 0).rot(.35f, 20, 18, 0).rot(.5f, 20, -18, 0).rot(.65f, 15, 12, 0).rot(1, 0, 0, 0);
        ROAR = b.build();

        // ---- 8: obstacle vault (spring t=5 -> .25, reach over, hook claws, pull, land)
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.2f, 40, 0, 0, 0, 0), k(.25f, -8, -20, -20, -1, 0), k(.45f, 55, 40, 40, -7.7f, 0),
            k(.65f, 30, -10, -10, -3, 0), k(.85f, 34, 0, 0, 0, 0), k(1, 0, 0, 0, 0, 0));
        b.bone("base").rot(0, 0, 0, 0).rot(.2f, 10, 0, 0).rot(.3f, -25, 0, 0).rot(.55f, 10, 0, 0).rot(.75f, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.2f, 20, 0, 0).rot(.3f, -20, 0, 0).rot(.55f, 15, 0, 0).rot(.85f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.2f, -20, 0, 0).rot(.4f, -10, 0, 0).rot(.6f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.25f, 30, 0, 0).rot(.6f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.2f, 40, 0, -10).rot(.3f, -170, 0, -20).rot(.45f, -150, 0, -25).rot(.6f, -50, 0, -15, Ease.IN).rot(.85f, -30, 0, -10).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.2f, 40, 0, 10).rot(.3f, -170, 0, 20).rot(.45f, -150, 0, 25).rot(.6f, -50, 0, 15, Ease.IN).rot(.85f, -30, 0, 10).rot(1, 0, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.3f, -25, 30), k(.5f, -25, 30), k(.6f, 50, 5), k(.85f, 20, 0), k(1, 0, 0));
        claws(b, "R", k(0, 0, 0), k(.3f, -25, 30), k(.5f, -25, 30), k(.6f, 50, 5), k(.85f, 20, 0), k(1, 0, 0));
        b.bone("tail").rot(0, 0, 0, 0).rot(.3f, -25, 0, 0).rot(.6f, 25, 0, 0).rot(1, 0, 0, 0);
        VAULT = b.build();

        // ---- hurt flinch (sampled by hurtTime progress)
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.2f, 14, -8, -8, 0, 1.4f), k(1, 0, 0, 0, 0, 0));
        b.bone("chest").rot(0, 0, 0, 0).rot(.15f, -22, 0, 10, Ease.OUT).rot(.5f, -10, 0, 4).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.15f, -28, 0, -12, Ease.OUT).rot(.5f, -10, 0, -4).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.15f, 45, 0, 0).rot(.6f, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.15f, -30, 0, -35, Ease.OUT).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.15f, -30, 0, 35, Ease.OUT).rot(1, 0, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.15f, -30, 35), k(1, 0, 0));
        claws(b, "R", k(0, 0, 0), k(.15f, -30, 35), k(1, 0, 0));
        b.bone("tail").rot(0, 0, 0, 0).rot(.15f, 30, 15, 0).rot(1, 0, 0, 0);
        HURT = b.build();

        // ---- death: shriek, stagger upright, collapse face-down, claws twitch
        b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.2f, -8, 0, 0, 0, 1.2f), k(.45f, 28, -10, 12, 0, 1.5f), k(.75f, 50, -30, 20, 2, -2),
            k(.85f, 46, -28, 18, 2.6f, -2.5f), k(1, 50, -30, 20, 2, -2.5f));
        b.bone("base").rot(0, 0, 0, 0).rot(.2f, -18, 0, 6).rot(.45f, 10, 0, -4).rot(.75f, 82, 0, -10, Ease.IN).rot(.85f, 74, 0, -9, Ease.OUT).rot(1, 80, 0, -10);
        b.bone("chest").rot(0, 0, 0, 0).rot(.2f, -30, 0, 10).rot(.45f, 10, 0, 0).rot(.8f, -10, 0, 0).rot(1, -14, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(.2f, -30, 0, 0).rot(.75f, -20, 0, 25).rot(1, -25, 0, 30);
        b.bone("head").rot(0, 0, 0, 0).rot(.2f, -40, 20, 0).rot(.6f, 0, 0, 0).rot(.8f, -20, 0, 30).rot(1, -20, 0, 35);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.15f, 55, 0, 0).rot(.6f, 30, 0, 0).rot(.85f, 40, 0, 0).rot(1, 35, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.2f, -90, -20, -60).rot(.45f, -30, 0, -30).rot(.75f, -150, 0, -30).rot(1, -160, 10, -40);
        b.bone("armR").rot(0, 0, 0, 0).rot(.2f, -60, 20, 40).rot(.45f, -20, 0, 20).rot(.75f, -80, 0, 60).rot(1, -85, 0, 70);
        b.bone("foreL").rot(0, 0, 0, 0).rot(.75f, -10, 0, 0).rot(1, 10, 0, 0);
        b.bone("foreR").rot(0, 0, 0, 0).rot(.75f, -30, 0, 0).rot(1, -20, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.2f, -35, 40), k(.6f, 20, 10), k(.85f, -10, 20), k(.9f, 30, 15), k(.95f, 5, 18), k(1, 25, 15));
        claws(b, "R", k(0, 0, 0), k(.2f, -35, 40), k(.6f, 20, 10), k(.83f, 35, 15), k(.88f, -5, 25), k(.94f, 30, 10), k(1, 20, 15));
        b.bone("tail").rot(0, 0, 0, 0).rot(.2f, 30, 0, 0).rot(.75f, -10, 0, 0).rot(.85f, -5, 15, 0).rot(.92f, -5, -10, 0).rot(1, -8, 5, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.3f, 20, 0, 0).rot(.8f, -10, 0, 0).rot(.9f, -10, 20, 0).rot(1, -10, -5, 0);
        DEATH = b.build();
    }

    /** side hop; s=+1 hops to the creature's left (+x), s=-1 right. Impulse at t=2 of 14 (.14). */
    private static AnimClip sideHop(float s) {
        AnimClip.Builder b = AnimClip.create();
        legs(b, k(0, 0, 0, 0, 0, 0), k(.12f, 30, 0, 0, 0, 0), k(.2f, -4, -6, -6, -1, 0), k(.4f, 45, 10, 10, -7.8f, 0),
            k(.7f, 40, 8, 8, -5.6f, 0), k(.82f, 34, 0, 0, 0, 0), k(1, 0, 0, 0, 0, 0));
        b.bone("base").rot(0, 0, 0, 0).rot(.12f, 0, 0, -12 * s).rot(.4f, 0, 0, 22 * s).rot(.7f, 0, 0, 18 * s).rot(.82f, 0, 0, -6 * s).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.12f, 15, 0, -8 * s).rot(.4f, 5, 0, 10 * s).rot(.8f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.12f, 0, 0, 12 * s).rot(.4f, -6, 0, -25 * s).rot(.7f, -6, 0, -18 * s).rot(.85f, 0, 0, 4 * s).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(.2f, 30, 0, 0).rot(.8f, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.12f, -20, 0, -10).rot(.4f, -50, 0, -70).rot(.7f, -50, 0, -60).rot(.85f, -20, 0, -20).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.12f, -20, 0, 10).rot(.4f, -50, 0, 70).rot(.7f, -50, 0, 60).rot(.85f, -20, 0, 20).rot(1, 0, 0, 0);
        claws(b, "L", k(0, 0, 0), k(.4f, -20, 30), k(.85f, 10, 10), k(1, 0, 0));
        claws(b, "R", k(0, 0, 0), k(.4f, -20, 30), k(.85f, 10, 10), k(1, 0, 0));
        b.bone("tail").rot(0, 0, 0, 0).rot(.3f, 15, -30 * s, 0).rot(.7f, 10, -20 * s, 0).rot(1, 0, 0, 0);
        b.bone("tail2").rot(0, 0, 0, 0).rot(.4f, 10, -25 * s, 0).rot(.8f, 5, 10 * s, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    @Override
    protected AnimClip clip(HunterEntity e, int moveId) {
        switch (moveId) {
            case HunterEntity.SLASH: return SLASH;
            case HunterEntity.LEAP: return LEAP;
            case HunterEntity.DECAP: return DECAP;
            case HunterEntity.PIN: return PIN;
            case HunterEntity.BACKFLIP: return BACKFLIP;
            case HunterEntity.SIDE_L: return SIDE_L;
            case HunterEntity.SIDE_R: return SIDE_R;
            case HunterEntity.ROAR: return ROAR;
            case HunterEntity.VAULT: return VAULT;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ procedural idle / gait

    @Override
    protected void baseAnim(HunterEntity e, float ls, float amt, float age, float headYaw, float headPitch) {
        amt = Math.min(amt, 1f);
        boolean red = e.getForm() == 1;
        float p = ls * 0.75f;
        float br = Mth.sin(age * 0.09f);
        float sinP = Mth.sin(p), cosP = Mth.cos(p);

        // stalking crouch-run: always low, deeper when running; red form even lower and wider
        float crouch = 8f + 14f * amt + (red ? 5f : 0f);
        float swing = cosP * 38f * amt;
        float liftL = Math.max(0f, -sinP) * amt, liftR = Math.max(0f, sinP) * amt;
        leg("L", crouch, swing, liftL);
        leg("R", crouch, -swing, liftR);
        ModelPart base = bone("base");
        base.y += crouch * 0.14f - Math.abs(sinP) * 1.1f * amt;

        rot("hips", 6f * amt, sinP * 7f * amt, cosP * 3f * amt);
        rot("chest", 10f * amt + br * 2.5f, -sinP * 9f * amt, 0);
        ModelPart chest = bone("chest");
        chest.xScale = 1f + br * 0.02f; chest.zScale = 1f + br * 0.025f;

        float yaw = Mth.clamp(headYaw, -60f, 60f);
        rot("neck", -8f * amt, yaw * 0.4f, 0);
        rot("head", -6f * amt - Math.abs(sinP) * 4f * amt + headPitch * 0.7f, yaw * 0.6f, Mth.sin(age * 0.05f) * 4f * (1f - amt));
        rot("jaw", 4f + Math.max(0f, Mth.sin(age * 0.09f + 1f)) * 6f + Math.max(0f, Mth.sin(p * 2f)) * 8f * amt + (red ? 8f : 0f), 0, 0);

        // long arms hang and swing loosely, lagging behind the stride
        float spread = red ? 6f : 0f;
        rot("armL", -12f * amt + Mth.cos(p + Mth.PI) * 16f * amt + br * 2f, 0, -6f - 4f * amt - spread);
        rot("armR", -12f * amt + cosP * 16f * amt + br * 2f, 0, 6f + 4f * amt + spread);
        rot("foreL", -(10f + 10f * amt) + Mth.sin(p + 1f) * 8f * amt, 0, 0);
        rot("foreR", -(10f + 10f * amt) + Mth.sin(p + 1f + Mth.PI) * 8f * amt, 0, 0);
        rot("handL", Mth.sin(p + 1.6f) * 10f * amt, 0, 0);
        rot("handR", Mth.sin(p + 1.6f + Mth.PI) * 10f * amt, 0, 0);
        for (int i = 1; i <= 3; i++) {
            float tw = Mth.sin(age * 0.25f + i * 1.3f) * 4f;
            rot("clawL" + i, tw + 12f * amt, 0, 0);
            rot("clawR" + i, -tw + 12f * amt, 0, 0);
        }

        rot("tail", 12f * amt + br * 2f, Mth.sin(age * 0.05f) * 10f * (1f - amt) - sinP * 14f * amt, 0);
        rot("tail2", 4f * amt, Mth.sin(age * 0.05f - 0.8f) * 14f * (1f - amt) - Mth.sin(p - 0.7f) * 18f * amt, 0);
    }

    private void leg(String s, float crouch, float swing, float lift) {
        rot("thigh" + s, -crouch - swing - lift * 25f, 0, 0);
        rot("shin" + s, 2f * crouch + lift * 55f, 0, 0);
        rot("foot" + s, -crouch - lift * 30f, 0, 0);
        rot("toes" + s, lift * 20f, 0, 0);
    }

    @Override
    protected void extraAnim(HunterEntity e, float ls, float amt, float age, float headYaw, float headPitch) {
        float partial = age - e.tickCount;
        if (e.deathTime > 0) {
            applyClip(DEATH, Mth.clamp((e.deathTime + partial) / 20f, 0f, 1f));
            return;
        }
        if (e.hurtTime > 0) {
            applyClip(HURT, Mth.clamp(1f - (e.hurtTime - partial) / 10f, 0f, 1f));
        }
    }
}
