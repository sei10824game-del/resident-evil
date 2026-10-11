package com.reevil.client.chimera;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.anim.AnimClip.Ease;
import com.reevil.client.BioModel;
import com.reevil.entity.chimera.ChimeraEntity;
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
 * Chimera: hunched fly-human. Bone tree:
 * base(center, flips 180deg on ceilings) > chest > abdomen (bloated sac), wingL/R, neck > head > eyeL/R, mandL/R
 *                                         chest > armL/R > foreL/R > bladeL/R (scythe) + clawL/R (hook)
 *       base > thighL/R > shinL/R > tarsL/R (elongated insect hind legs)
 * Texture 128x128 (tools/tex_chimera.py).
 */
public class ChimeraModel extends BioModel<ChimeraEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "chimera"), "main");
    private static final float D = (float) Math.PI / 180f;

    public ChimeraModel(ModelPart root) {
        super(root);
        reg("base", "base");
        reg("chest", "base/chest");
        reg("abdomen", "base/chest/abdomen");
        reg("wingL", "base/chest/wingL");
        reg("wingR", "base/chest/wingR");
        reg("neck", "base/chest/neck");
        reg("head", "base/chest/neck/head");
        reg("eyeL", "base/chest/neck/head/eyeL");
        reg("eyeR", "base/chest/neck/head/eyeR");
        reg("mandL", "base/chest/neck/head/mandL");
        reg("mandR", "base/chest/neck/head/mandR");
        for (String s : new String[]{"L", "R"}) {
            reg("arm" + s, "base/chest/arm" + s);
            reg("fore" + s, "base/chest/arm" + s + "/fore" + s);
            reg("blade" + s, "base/chest/arm" + s + "/fore" + s + "/blade" + s);
            reg("claw" + s, "base/chest/arm" + s + "/fore" + s + "/claw" + s);
            reg("thigh" + s, "base/thigh" + s);
            reg("shin" + s, "base/thigh" + s + "/shin" + s);
            reg("tars" + s, "base/thigh" + s + "/shin" + s + "/tars" + s);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create(), PartPose.offset(0f, 12f, 0f));
        PartDefinition chest = base.addOrReplaceChild("chest", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.5f, -5f, -7f, 9, 8, 8), PartPose.offsetAndRotation(0f, -1f, -1f, -0.2f, 0f, 0f));
        chest.addOrReplaceChild("abdomen", CubeListBuilder.create()
            .texOffs(0, 18).addBox(-4f, -3f, 0f, 8, 7, 9), PartPose.offsetAndRotation(0f, -2f, 0f, -0.35f, 0f, 0f));
        chest.addOrReplaceChild("wingL", CubeListBuilder.create()
            .texOffs(36, 18).addBox(0f, -0.5f, 0f, 7, 1, 11), PartPose.offsetAndRotation(2f, -5f, -2f, 0f, -0.6f, -0.25f));
        chest.addOrReplaceChild("wingR", CubeListBuilder.create()
            .texOffs(36, 32).addBox(-7f, -0.5f, 0f, 7, 1, 11), PartPose.offsetAndRotation(-2f, -5f, -2f, 0f, 0.6f, 0.25f));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create()
            .texOffs(36, 0).addBox(-2f, -2f, -3f, 4, 4, 3), PartPose.offsetAndRotation(0f, -3f, -7f, -0.1f, 0f, 0f));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(52, 0).addBox(-3f, -3f, -5f, 6, 6, 5), PartPose.offsetAndRotation(0f, 0f, -3f, 0.3f, 0f, 0f));
        head.addOrReplaceChild("eyeL", CubeListBuilder.create()
            .texOffs(76, 0).addBox(-1f, -1.5f, -1.5f, 3, 3, 3), PartPose.offset(2.5f, -1.5f, -4f));
        head.addOrReplaceChild("eyeR", CubeListBuilder.create()
            .texOffs(76, 8).addBox(-2f, -1.5f, -1.5f, 3, 3, 3), PartPose.offset(-2.5f, -1.5f, -4f));
        head.addOrReplaceChild("mandL", CubeListBuilder.create()
            .texOffs(90, 0).addBox(-0.5f, -0.5f, -3f, 1, 1, 3), PartPose.offsetAndRotation(1.2f, 1.8f, -4.8f, 0.2f, -0.3f, 0f));
        head.addOrReplaceChild("mandR", CubeListBuilder.create()
            .texOffs(90, 6).addBox(-0.5f, -0.5f, -3f, 1, 1, 3), PartPose.offsetAndRotation(-1.2f, 1.8f, -4.8f, 0.2f, 0.3f, 0f));

        for (int s = 0; s < 2; s++) {
            boolean left = s == 0;
            String n = left ? "L" : "R";
            float sx = left ? 1f : -1f;
            int v = left ? 36 : 50;
            PartDefinition arm = chest.addOrReplaceChild("arm" + n, CubeListBuilder.create()
                .texOffs(0, v).addBox(-1f, -1f, -1f, 2, 7, 2), PartPose.offsetAndRotation(4.5f * sx, 0f, -5.5f, -0.4f, 0f, -0.25f * sx));
            PartDefinition fore = arm.addOrReplaceChild("fore" + n, CubeListBuilder.create()
                .texOffs(8, v).addBox(-1f, 0f, -1f, 2, 6, 2), PartPose.offsetAndRotation(0f, 5.5f, 0f, 0.25f, 0f, 0f));
            fore.addOrReplaceChild("blade" + n, CubeListBuilder.create()
                .texOffs(16, v).addBox(-0.5f, 0f, -1f, 1, 11, 2), PartPose.offsetAndRotation(0f, 5.5f, 0f, -0.95f, 0f, 0f));
            fore.addOrReplaceChild("claw" + n, CubeListBuilder.create()
                .texOffs(24, v).addBox(-0.5f, 0f, -0.5f, 1, 3, 1), PartPose.offsetAndRotation(0f, 5.5f, 0.5f, 0.6f, 0f, 0f));

            int lv = left ? 18 : 36;
            PartDefinition thigh = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create()
                .texOffs(76, lv).addBox(-1f, 0f, -1f, 2, 8, 2), PartPose.offsetAndRotation(3.5f * sx, 1f, 3f, 2.4f, 0f, 0.35f * sx));
            PartDefinition shin = thigh.addOrReplaceChild("shin" + n, CubeListBuilder.create()
                .texOffs(84, lv).addBox(-1f, 0f, -1f, 2, 13, 2), PartPose.offsetAndRotation(0f, 8f, 0f, -2.2f, 0f, -0.7f * sx));
            shin.addOrReplaceChild("tars" + n, CubeListBuilder.create()
                .texOffs(92, lv).addBox(-0.5f, 0f, -0.5f, 1, 5, 1), PartPose.offsetAndRotation(0f, 13f, 0f, -0.5f, 0f, 0f));
        }
        return LayerDefinition.create(mesh, 128, 128);
    }

    // ------------------------------------------------------------------ helpers

    private void rot(String b, float x, float y, float z) {
        ModelPart p = bone(b);
        p.xRot += x * D; p.yRot += y * D; p.zRot += z * D;
    }

    private static float[] k(float... v) { return v; }

    /** mandibles, k = {t, open} (+ = spread apart) */
    static void mand(AnimClip.Builder b, float[]... keys) {
        AnimClip.BoneBuilder l = b.bone("mandL"), r = b.bone("mandR");
        for (float[] k : keys) { l.rot(k[0], 0, -k[1], 0); r.rot(k[0], 0, k[1], 0); }
    }

    /** wing membranes, k = {t, flare(raise), spread(unfold sideways)} */
    static void wings(AnimClip.Builder b, float[]... keys) {
        AnimClip.BoneBuilder l = b.bone("wingL"), r = b.bone("wingR");
        for (float[] k : keys) { l.rot(k[0], 0, k[2], -k[1]); r.rot(k[0], 0, -k[2], k[1]); }
    }

    /** hind legs, k = {t, coilL, coilR}: + = fold (knee up, foot under body), - = kick/extend */
    static void rear(AnimClip.Builder b, float[]... keys) {
        String[] side = {"L", "R"};
        for (int s = 0; s < 2; s++) {
            AnimClip.BoneBuilder th = b.bone("thigh" + side[s]), sh = b.bone("shin" + side[s]), ta = b.bone("tars" + side[s]);
            for (float[] k : keys) {
                float c = k[1 + s];
                th.rot(k[0], c, 0, 0);
                sh.rot(k[0], -1.6f * c, 0, 0);
                ta.rot(k[0], 0.6f * c, 0, 0);
            }
        }
    }

    // ------------------------------------------------------------------ clips

    private static final AnimClip FLURRY, LUNGE, CHEW, DROP, RETREAT, WALLRUN, ASCEND, HURT, DEATH;

    static {
        // ---- 1: rear up, scythe L across, scythe R across, double overhead chop (hits t=9,14,19 of 28 -> .32/.5/.68)
        AnimClip.Builder b = AnimClip.create();
        b.bone("base").pos(0, 0, 0, 0).pos(.2f, 0, -1.5f, 1).pos(.32f, 0, -1, -1.2f, Ease.IN).pos(.5f, 0, -1, -1.6f)
            .pos(.68f, 0, 1, -2.2f, Ease.IN).pos(.8f, 0, .8f, -1.6f).pos(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.2f, -35, -20, 0).rot(.32f, -25, 30, 0, Ease.IN).rot(.42f, -30, 24, 0)
            .rot(.5f, -25, -30, 0, Ease.IN).rot(.6f, -45, 0, 0).rot(.68f, 15, 0, 0, Ease.IN).rot(.8f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(.2f, 15, 15, 0).rot(.32f, 10, -20, 0).rot(.5f, 10, 20, 0).rot(.68f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.2f, 15, 15, 0).rot(.32f, 10, -20, 0).rot(.5f, 10, 20, 0).rot(.6f, 25, 0, 0).rot(.68f, -15, 0, 0).rot(1, 0, 0, 0);
        mand(b, k(0, 0), k(.2f, 30), k(.32f, 10), k(.42f, 30), k(.5f, 10), k(.6f, 35), k(.7f, 5), k(1, 0));
        wings(b, k(0, 0, 0), k(.2f, 30, 25), k(.5f, 35, 30), k(.68f, 50, 40), k(.85f, 20, 15), k(1, 0, 0));
        b.bone("armL").rot(0, 0, 0, 0).rot(.2f, -120, -40, -50).rot(.32f, -80, 70, 10, Ease.IN).rot(.42f, -70, 85, 10, Ease.OUT)
            .rot(.5f, -60, 40, 0).rot(.6f, -170, 0, -15).rot(.68f, -25, 0, -10, Ease.IN).rot(.8f, -20, 0, -10).rot(1, 0, 0, 0);
        b.bone("foreL").rot(0, 0, 0, 0).rot(.2f, -40, 0, 0).rot(.32f, 10, 0, 0, Ease.IN).rot(.5f, 0, 0, 0).rot(.6f, -30, 0, 0).rot(.68f, 10, 0, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("bladeL").rot(0, 0, 0, 0).rot(.2f, -30, 0, 0).rot(.32f, 60, 0, 0, Ease.IN).rot(.42f, 50, 0, 0).rot(.6f, -20, 0, 0)
            .rot(.68f, 50, 0, 0, Ease.IN).rot(.85f, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.2f, -30, 0, 10).rot(.32f, -60, 0, 20).rot(.42f, -120, 40, 50).rot(.5f, -80, -70, -10, Ease.IN)
            .rot(.58f, -70, -85, -10, Ease.OUT).rot(.63f, -170, 0, 15).rot(.68f, -25, 0, 10, Ease.IN).rot(.8f, -20, 0, 10).rot(1, 0, 0, 0);
        b.bone("foreR").rot(0, 0, 0, 0).rot(.42f, -40, 0, 0).rot(.5f, 10, 0, 0, Ease.IN).rot(.63f, -30, 0, 0).rot(.68f, 10, 0, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("bladeR").rot(0, 0, 0, 0).rot(.42f, -30, 0, 0).rot(.5f, 60, 0, 0, Ease.IN).rot(.58f, 50, 0, 0).rot(.63f, -20, 0, 0)
            .rot(.68f, 50, 0, 0, Ease.IN).rot(.85f, 20, 0, 0).rot(1, 0, 0, 0);
        rear(b, k(0, 0, 0), k(.2f, -15, -15), k(.32f, -10, 5), k(.5f, 5, -10), k(.68f, 20, 20), k(.85f, 10, 10), k(1, 0, 0));
        FLURRY = b.build();

        // ---- 2: leaping lunge-stab + bite (coil, spring t=8 -> .31, impale/bite t=13 -> .5)
        b = AnimClip.create();
        b.bone("base").pos(0, 0, 0, 0).pos(.25f, 0, 1.5f, 2.5f).pos(.31f, 0, -1, -1, Ease.OUT).pos(.5f, 0, 0, -2.5f).pos(.65f, 0, .5f, -1.5f).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(.25f, 10, 0, 0).rot(.31f, -15, 0, 0).rot(.45f, 5, 0, 0).rot(.5f, 12, 0, 0, Ease.IN).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.25f, 12, 0, 0).rot(.35f, -10, 0, 0).rot(.5f, 10, 0, 0, Ease.IN).rot(.7f, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(.25f, -15, 0, 0).rot(.35f, 20, 0, 0).rot(.55f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(.25f, 15, 0, 0).rot(.4f, -20, 0, 0).rot(.5f, -30, 0, 0, Ease.IN).rot(.7f, -10, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(.25f, 0, 0, 1.5f).pos(.5f, 0, 0, -2.5f, Ease.IN).pos(.7f, 0, 0, -1).pos(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.25f, -20, 0, 0).rot(.45f, -10, 0, 0).rot(.5f, 25, 0, 0, Ease.IN).rot(.65f, 10, 0, 0).rot(1, 0, 0, 0);
        mand(b, k(0, 0), k(.25f, 20), k(.45f, 50), k(.5f, -10), k(.6f, -5), k(.75f, 25), k(1, 0));
        wings(b, k(0, 0, 0), k(.25f, 10, 10), k(.31f, 60, 60), k(.5f, 45, 50), k(.75f, 15, 10), k(1, 0, 0));
        b.bone("armL").rot(0, 0, 0, 0).rot(.25f, 40, 0, -10).rot(.31f, -60, 0, -20).rot(.45f, -110, 15, -10).rot(.5f, -85, 20, 0, Ease.IN).rot(.7f, -60, 10, 0).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.25f, 40, 0, 10).rot(.31f, -60, 0, 20).rot(.45f, -110, -15, 10).rot(.5f, -85, -20, 0, Ease.IN).rot(.7f, -60, -10, 0).rot(1, 0, 0, 0);
        for (String s : new String[]{"L", "R"}) {
            b.bone("fore" + s).rot(0, 0, 0, 0).rot(.25f, -30, 0, 0).rot(.45f, -40, 0, 0).rot(.5f, 10, 0, 0, Ease.IN).rot(1, 0, 0, 0);
            b.bone("blade" + s).rot(0, 0, 0, 0).rot(.25f, -40, 0, 0).rot(.45f, -30, 0, 0).rot(.5f, 65, 0, 0, Ease.IN).rot(.7f, 40, 0, 0).rot(1, 0, 0, 0);
        }
        rear(b, k(0, 0, 0), k(.25f, 25, 25), k(.31f, -25, -25), k(.45f, 30, 30), k(.55f, 15, 15), k(1, 0, 0));
        LUNGE = b.build();

        // ---- 3: grab & chew: pounce, wrap both arms and hook blades into prey, head darts in five times (hits .31/.44/.56/.69/.81)
        b = AnimClip.create();
        b.bone("base").pos(0, 0, 0, 0).pos(.12f, 0, 1, 1.5f).pos(.19f, 0, -1, -2.5f, Ease.OUT).pos(.9f, 0, -1, -2.5f).pos(.95f, 0, 0, 1.5f).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(.19f, -20, 0, 0).rot(.9f, -22, 0, 0).rot(1, 0, 0, 0);
        AnimClip.BoneBuilder chest = b.bone("chest").rot(0, 0, 0, 0).rot(.19f, -15, 0, 0);
        AnimClip.BoneBuilder head = b.bone("head").rot(0, 0, 0, 0).rot(.19f, -20, 0, 0);
        AnimClip.BoneBuilder abd = b.bone("abdomen").scale(0, 1, 1, 1).scale(.19f, 1, 1, 1);
        AnimClip.BoneBuilder eyeL = b.bone("eyeL").scale(0, 1, 1, 1).scale(.19f, 1, 1, 1);
        AnimClip.BoneBuilder eyeR = b.bone("eyeR").scale(0, 1, 1, 1).scale(.19f, 1, 1, 1);
        float[] bite = {.3125f, .4375f, .5625f, .6875f, .8125f};
        float[] mk = new float[2 + bite.length * 4 + 4];
        int mi = 0;
        mk[mi++] = 0; mk[mi++] = 0;
        for (int i = 0; i < bite.length; i++) {
            float bt = bite[i], side = (i % 2 == 0) ? 1f : -1f;
            chest.rot(bt - .0625f, -10, 0, 0).rot(bt, 8, 0, 0, Ease.IN);
            head.rot(bt - .0625f, -20, -6 * side, 0).rot(bt, 30, 10 * side, 0, Ease.IN);
            abd.scale(bt - .0625f, .95f, .96f, .95f).scale(bt, 1.12f, 1.08f, 1.1f, Ease.IN);
            eyeL.scale(bt - .0625f, 1, 1, 1).scale(bt, 1.25f, 1.25f, 1.25f, Ease.IN);
            eyeR.scale(bt - .0625f, 1, 1, 1).scale(bt, 1.25f, 1.25f, 1.25f, Ease.IN);
            mk[mi++] = bt - .0625f; mk[mi++] = 50;
            mk[mi++] = bt; mk[mi++] = -15;
        }
        chest.rot(.9f, -5, 0, 0).rot(1, 0, 0, 0);
        head.rot(.9f, 0, 0, 0).rot(1, 0, 0, 0);
        abd.scale(.9f, 1, 1, 1).scale(1, 1, 1, 1);
        eyeL.scale(1, 1, 1, 1);
        eyeR.scale(1, 1, 1, 1);
        mk[mi++] = .9f; mk[mi++] = 30;
        mk[mi++] = 1; mk[mi++] = 0;
        float[][] mkeys = new float[mi / 2][];
        for (int i = 0; i < mkeys.length; i++) mkeys[i] = new float[]{mk[i * 2], mk[i * 2 + 1]};
        mand(b, mkeys);
        b.bone("armL").rot(0, 0, 0, 0).rot(.12f, -140, -30, -40).rot(.19f, -90, 50, -10, Ease.IN).rot(.3125f, -95, 55, -8).rot(.5625f, -88, 50, -10)
            .rot(.8125f, -95, 55, -8).rot(.9f, -90, 50, -10).rot(.95f, -40, 0, -20).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.12f, -140, 30, 40).rot(.19f, -90, -50, 10, Ease.IN).rot(.3125f, -95, -55, 8).rot(.5625f, -88, -50, 10)
            .rot(.8125f, -95, -55, 8).rot(.9f, -90, -50, 10).rot(.95f, -40, 0, 20).rot(1, 0, 0, 0);
        for (String s : new String[]{"L", "R"}) {
            b.bone("fore" + s).rot(0, 0, 0, 0).rot(.12f, -20, 0, 0).rot(.19f, -50, 0, 0).rot(.9f, -55, 0, 0).rot(1, 0, 0, 0);
            b.bone("blade" + s).rot(0, 0, 0, 0).rot(.12f, 40, 0, 0).rot(.19f, -60, 0, 0, Ease.IN).rot(.9f, -60, 0, 0).rot(.95f, 30, 0, 0).rot(1, 0, 0, 0);
            b.bone("claw" + s).rot(0, 0, 0, 0).rot(.19f, -50, 0, 0).rot(.9f, -50, 0, 0).rot(1, 0, 0, 0);
        }
        wings(b, k(0, 0, 0), k(.12f, 50, 40), k(.19f, 25, 20), k(.3125f, 35, 25), k(.4375f, 25, 20), k(.5625f, 35, 25),
            k(.6875f, 25, 20), k(.8125f, 35, 25), k(.95f, 40, 30), k(1, 0, 0));
        rear(b, k(0, 0, 0), k(.12f, 30, 30), k(.19f, -20, -20), k(.9f, -15, -15), k(.95f, 25, 25), k(1, 0, 0));
        CHEW = b.build();

        // ---- 4: ceiling drop (limbs spread while falling, blades stab down on landing t=13 -> .43, pin & bite)
        b = AnimClip.create();
        b.bone("base").pos(0, 0, 0, 0).pos(.35f, 0, -1, 0).pos(.43f, 0, 2.5f, -1, Ease.IN).pos(.55f, 0, 1.5f, -1.5f).pos(.8f, 0, .5f, -1).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(.2f, 15, 0, 0).rot(.43f, 20, 0, 0, Ease.IN).rot(.6f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.2f, -20, 0, 0).rot(.43f, 15, 0, 0, Ease.IN).rot(.6f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(.1f, 25, 0, 0).rot(.43f, -20, 0, 0).rot(.55f, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.2f, -25, 0, 0).rot(.43f, -10, 0, 0).rot(.55f, 30, 0, 0, Ease.IN).rot(.7f, 0, 0, 0).rot(1, 0, 0, 0);
        mand(b, k(0, 0), k(.2f, 50), k(.5f, 50), k(.55f, -15), k(.7f, 30), k(1, 0));
        wings(b, k(0, 0, 0), k(.08f, 70, 60), k(.35f, 60, 55), k(.43f, 20, 30), k(.7f, 10, 10), k(1, 0, 0));
        b.bone("armL").rot(0, 0, 0, 0).rot(.1f, -140, 0, -60).rot(.35f, -130, 0, -55).rot(.43f, -40, 0, -15, Ease.IN).rot(.75f, -45, 0, -15).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.1f, -140, 0, 60).rot(.35f, -130, 0, 55).rot(.43f, -40, 0, 15, Ease.IN).rot(.75f, -45, 0, 15).rot(1, 0, 0, 0);
        for (String s : new String[]{"L", "R"}) {
            b.bone("fore" + s).rot(0, 0, 0, 0).rot(.1f, -10, 0, 0).rot(.43f, -20, 0, 0).rot(1, 0, 0, 0);
            b.bone("blade" + s).rot(0, 0, 0, 0).rot(.1f, 40, 0, 0).rot(.35f, 50, 0, 0).rot(.43f, -10, 0, 0, Ease.IN).rot(.75f, -15, 0, 0).rot(1, 0, 0, 0);
        }
        rear(b, k(0, 0, 0), k(.1f, -30, -30), k(.35f, -25, -25), k(.43f, 35, 35), k(.6f, 25, 25), k(1, 0, 0));
        DROP = b.build();

        // ---- 5: hit-and-run retreat: low backward skitter, blades raised defensively, hissing
        b = AnimClip.create();
        b.bone("base").pos(0, 0, 0, 0).pos(.15f, 0, -1.2f, .5f).pos(.3f, 0, .8f, 0).pos(.45f, 0, -.6f, 0).pos(.6f, 0, .8f, 0).pos(.75f, 0, -.4f, 0).pos(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.15f, -30, 0, 0).rot(.8f, -25, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.15f, 10, 0, 0).rot(.3f, 5, 12, 0).rot(.5f, 5, -12, 0).rot(.7f, 5, 10, 0).rot(1, 0, 0, 0);
        mand(b, k(0, 0), k(.15f, 45), k(.3f, 20), k(.45f, 45), k(.6f, 20), k(.75f, 45), k(1, 0));
        b.bone("armL").rot(0, 0, 0, 0).rot(.15f, -130, -20, -30).rot(.5f, -120, -15, -35).rot(.85f, -125, -20, -30).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.15f, -130, 20, 30).rot(.5f, -120, 15, 35).rot(.85f, -125, 20, 30).rot(1, 0, 0, 0);
        b.bone("bladeL").rot(0, 0, 0, 0).rot(.15f, -60, 0, 0).rot(.85f, -60, 0, 0).rot(1, 0, 0, 0);
        b.bone("bladeR").rot(0, 0, 0, 0).rot(.15f, -60, 0, 0).rot(.85f, -60, 0, 0).rot(1, 0, 0, 0);
        rear(b, k(0, 0, 0), k(.15f, -25, 20), k(.3f, 20, -25), k(.45f, -25, 20), k(.6f, 20, -25), k(.75f, -25, 20), k(.9f, 15, -15), k(1, 0, 0));
        wings(b, k(0, 0, 0), k(.15f, 30, 20), k(.85f, 30, 20), k(1, 0, 0));
        b.bone("abdomen").rot(0, 0, 0, 0).rot(.3f, 10, 12, 0).rot(.6f, 10, -12, 0).rot(1, 0, 0, 0);
        RETREAT = b.build();

        // ---- 6: wall-run sprint: stretched low body, rapid alternating limbs, wings buzzing
        b = AnimClip.create();
        AnimClip.BoneBuilder bp = b.bone("base").pos(0, 0, 0, 0);
        AnimClip.BoneBuilder al = b.bone("armL").rot(0, 0, 0, 0);
        AnimClip.BoneBuilder ar = b.bone("armR").rot(0, 0, 0, 0);
        AnimClip.BoneBuilder ab = b.bone("abdomen").rot(0, 0, 0, 0);
        float[][] rk = new float[13][];
        float[][] wk = new float[13][];
        rk[0] = k(0, 0, 0);
        wk[0] = k(0, 0, 0);
        for (int i = 1; i <= 11; i++) {
            float t = i / 12f;
            boolean even = i % 2 == 0;
            bp.pos(t, 0, even ? .5f : -1f, -1);
            al.rot(t, even ? 30 : -60, 0, 0);
            ar.rot(t, even ? -60 : 30, 0, 0);
            ab.rot(t, even ? 6 : -4, even ? 8 : -8, 0);
            rk[i] = k(t, even ? 25 : -25, even ? -25 : 25);
            wk[i] = k(t, even ? 55 : 45, even ? 40 : 35);
        }
        bp.pos(1, 0, 0, 0);
        al.rot(1, 0, 0, 0);
        ar.rot(1, 0, 0, 0);
        ab.rot(1, 0, 0, 0);
        rk[12] = k(1, 0, 0);
        wk[12] = k(1, 0, 0);
        rear(b, rk);
        wings(b, wk);
        b.bone("chest").rot(0, 0, 0, 0).rot(.1f, 15, 0, 0).rot(.9f, 15, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.1f, -15, 0, 0).rot(.9f, -15, 0, 0).rot(1, 0, 0, 0);
        mand(b, k(0, 0), k(.1f, 30), k(.9f, 30), k(1, 0));
        WALLRUN = b.build();

        // ---- 7: spring up to the ceiling (coil, launch t=6 -> .33, arms reach up; flip comes from the synced ceiling flag)
        b = AnimClip.create();
        b.bone("base").pos(0, 0, 0, 0).pos(.28f, 0, 2, 1).pos(.35f, 0, -3, 0, Ease.OUT).pos(.6f, 0, -2, 0).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(.28f, 10, 0, 0).rot(.4f, -40, 0, 0).rot(.6f, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.28f, 10, 0, 0).rot(.4f, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.28f, 30, 0, -10).rot(.4f, -170, 0, -25).rot(.7f, -160, 0, -30).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.28f, 30, 0, 10).rot(.4f, -170, 0, 25).rot(.7f, -160, 0, 30).rot(1, 0, 0, 0);
        b.bone("bladeL").rot(0, 0, 0, 0).rot(.4f, 40, 0, 0).rot(.7f, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("bladeR").rot(0, 0, 0, 0).rot(.4f, 40, 0, 0).rot(.7f, -30, 0, 0).rot(1, 0, 0, 0);
        rear(b, k(0, 0, 0), k(.28f, 35, 35), k(.35f, -30, -30), k(.7f, -20, -20), k(1, 0, 0));
        wings(b, k(0, 0, 0), k(.3f, 20, 20), k(.4f, 70, 60), k(.7f, 50, 40), k(1, 0, 0));
        mand(b, k(0, 0), k(.3f, 40), k(1, 0));
        ASCEND = b.build();

        // ---- hurt recoil
        b = AnimClip.create();
        b.bone("base").pos(0, 0, 0, 0).pos(.15f, 0, -.5f, 1.5f, Ease.OUT).pos(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(.15f, -25, 0, 12, Ease.OUT).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(.15f, -30, 0, -15, Ease.OUT).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(.15f, 20, 15, 0).rot(1, 0, 0, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.15f, -50, 0, -30).rot(1, 0, 0, 0);
        b.bone("armR").rot(0, 0, 0, 0).rot(.15f, -50, 0, 30).rot(1, 0, 0, 0);
        mand(b, k(0, 0), k(.15f, 55), k(1, 0));
        wings(b, k(0, 0, 0), k(.15f, 50, 35), k(1, 0, 0));
        HURT = b.build();

        // ---- death: rears, flips onto its back like a dying insect, limbs curl in and twitch, sac deflates
        b = AnimClip.create();
        b.bone("base").rot(0, 0, 0, 0).rot(.2f, -25, 0, 0).rot(.45f, 0, 0, 100, Ease.IN).rot(.6f, 0, 0, 180).rot(.7f, 0, 0, 170).rot(1, 0, 0, 180)
            .pos(0, 0, 0, 0).pos(.2f, 0, -2, 0).pos(.6f, 0, 5, 0).pos(.7f, 0, 4.5f, 0).pos(1, 0, 5, 0);
        b.bone("armL").rot(0, 0, 0, 0).rot(.2f, -60, 0, -30).rot(.6f, -100, 0, 20).rot(.7f, -110, 0, 25).rot(.78f, -95, 0, 18)
            .rot(.86f, -108, 0, 24).rot(.93f, -98, 0, 20).rot(1, -104, 0, 22);
        b.bone("armR").rot(0, 0, 0, 0).rot(.2f, -60, 0, 30).rot(.6f, -100, 0, -20).rot(.7f, -105, 0, -24).rot(.8f, -96, 0, -18)
            .rot(.9f, -110, 0, -25).rot(1, -100, 0, -21);
        for (String s : new String[]{"L", "R"}) {
            b.bone("fore" + s).rot(0, 0, 0, 0).rot(.6f, -70, 0, 0).rot(.75f, -80, 0, 0).rot(.88f, -65, 0, 0).rot(1, -75, 0, 0);
            b.bone("blade" + s).rot(0, 0, 0, 0).rot(.6f, -50, 0, 0).rot(1, -60, 0, 0);
        }
        rear(b, k(0, 0, 0), k(.2f, -20, -20), k(.6f, 40, 40), k(.72f, 50, 35), k(.8f, 38, 48), k(.88f, 50, 40), k(.95f, 42, 46), k(1, 46, 44));
        b.bone("head").rot(0, 0, 0, 0).rot(.2f, -35, 0, 0).rot(.6f, 20, 0, 20).rot(1, 25, 0, 25);
        mand(b, k(0, 0), k(.2f, 60), k(.6f, 30), k(.75f, 45), k(.88f, 20), k(1, 35));
        wings(b, k(0, 0, 0), k(.2f, 70, 50), k(.6f, -10, -20), k(1, -15, -25));
        b.bone("abdomen").rot(0, 0, 0, 0).rot(.6f, 15, 0, 0).rot(1, 20, 0, 0)
            .scale(0, 1, 1, 1).scale(.6f, 1, 1, 1).scale(1, .85f, .8f, .9f);
        b.bone("eyeL").scale(0, 1, 1, 1).scale(1, .85f, .85f, .85f);
        b.bone("eyeR").scale(0, 1, 1, 1).scale(1, .85f, .85f, .85f);
        DEATH = b.build();
    }

    @Override
    protected AnimClip clip(ChimeraEntity e, int moveId) {
        switch (moveId) {
            case ChimeraEntity.FLURRY: return FLURRY;
            case ChimeraEntity.LUNGE: return LUNGE;
            case ChimeraEntity.CHEW: return CHEW;
            case ChimeraEntity.DROP: return DROP;
            case ChimeraEntity.RETREAT: return RETREAT;
            case ChimeraEntity.WALLRUN: return WALLRUN;
            case ChimeraEntity.ASCEND: return ASCEND;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ procedural idle / scuttle

    @Override
    protected void baseAnim(ChimeraEntity e, float ls, float amt, float age, float headYaw, float headPitch) {
        amt = Math.min(amt, 1f);
        float partial = age - e.tickCount;
        float ceil = Mth.lerp(partial, e.ceilAnimO, e.ceilAnim);
        float climb = Mth.lerp(partial, e.climbAnimO, e.climbAnim);
        float p = ls * 1.1f;
        float sinP = Mth.sin(p), cosP = Mth.cos(p);

        ModelPart base = bone("base");
        base.zRot += Mth.PI * ceil;                 // hang upside-down from the ceiling
        base.xRot += -75f * D * climb;              // body pitched up the wall while climbing
        base.y += -Math.abs(sinP) * 0.8f * amt + Mth.sin(age * 0.6f) * 0.15f;

        // hunched, jittery scuttle: diagonal pairs (front L + hind R)
        rot("chest", 6f * amt + Mth.sin(age * 0.11f) * 1.5f, sinP * 6f * amt, sinP * 5f * amt);
        rot("armL", cosP * 28f * amt, 0, 0);
        rot("armR", -cosP * 28f * amt, 0, 0);
        rot("foreL", -Math.max(0f, -sinP) * 25f * amt, 0, 0);
        rot("foreR", -Math.max(0f, sinP) * 25f * amt, 0, 0);
        rot("bladeL", -sinP * 10f * amt + Mth.sin(age * 0.2f) * 2f, 0, 0);
        rot("bladeR", sinP * 10f * amt + Mth.sin(age * 0.2f + 1f) * 2f, 0, 0);
        rot("thighL", -cosP * 18f * amt, 0, 0);
        rot("thighR", cosP * 18f * amt, 0, 0);
        rot("shinL", Math.max(0f, sinP) * 22f * amt, 0, 0);
        rot("shinR", Math.max(0f, -sinP) * 22f * amt, 0, 0);
        rot("tarsL", Mth.sin(p + 0.8f) * 10f * amt, 0, 0);
        rot("tarsR", -Mth.sin(p + 0.8f) * 10f * amt, 0, 0);

        rot("abdomen", Mth.sin(p * 2f) * 4f * amt + Mth.sin(age * 0.07f) * 3f, -sinP * 10f * amt, 0);
        ModelPart abd = bone("abdomen");
        float breathe = 1f + Mth.sin(age * 0.12f) * 0.05f;
        abd.xScale = breathe; abd.yScale = breathe; abd.zScale = 1f + Mth.sin(age * 0.12f + 0.5f) * 0.03f;

        float yaw = Mth.clamp(headYaw, -70f, 70f);
        rot("neck", 0, yaw * 0.3f, 0);
        rot("head", headPitch * 0.6f + Mth.sin(age * 0.5f) * 2f, yaw * 0.7f + Mth.sin(age * 0.37f) * 6f, Mth.sin(age * 0.23f) * 6f);
        float pulse = 1f + Mth.sin(age * 0.25f) * 0.07f;
        for (String s : new String[]{"eyeL", "eyeR"}) {
            ModelPart eye = bone(s);
            eye.xScale = pulse; eye.yScale = pulse; eye.zScale = pulse;
        }
        float chew = Math.max(0f, Mth.sin(age * 0.6f)) * 15f;
        rot("mandL", 0, -chew, 0);
        rot("mandR", 0, chew, 0);
        float buzz = Mth.sin(age * 2.2f) * 4f * amt + Mth.sin(age * 0.1f) * 2f;
        rot("wingL", 0, 0, -buzz);
        rot("wingR", 0, 0, buzz);
    }

    @Override
    protected void extraAnim(ChimeraEntity e, float ls, float amt, float age, float headYaw, float headPitch) {
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
