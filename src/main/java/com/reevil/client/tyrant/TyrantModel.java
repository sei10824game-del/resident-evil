package com.reevil.client.tyrant;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.tyrant.TyrantEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

import static com.reevil.anim.AnimClip.Ease.OUT;
import static com.reevil.anim.AnimClip.Ease.SNAP;

/**
 * T-002 Tyrant: ~3 blocks tall. Huge right arm (upper/forearm/hand + 5 dagger claws), smaller left arm with 4 fingers + thumb,
 * hips -> abdomen -> chest spine chain, trapezius mass, neck/head/jaw/exposed brain, heart sac (form >= 1), coat tails (form 0).
 * Texture 128x64 (tools/tex_tyrant.py parses the texOffs/addBox calls below, so UVs stay in sync).
 */
public class TyrantModel extends BioModel<TyrantEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "tyrant"), "main");

    private static final String[] CLAWS = {"rClaw1", "rClaw2", "rClaw3", "rClaw4", "rClaw5"};
    private static final String[] FINGERS = {"lFinger1", "lFinger2", "lFinger3", "lFinger4"};
    private static final float DEG = (float) Math.PI / 180f;

    private static final Map<Integer, AnimClip> CLIPS = new HashMap<>();
    static {
        CLIPS.put(TyrantEntity.CLAW_THRUST, clawThrust());
        CLIPS.put(TyrantEntity.DECAPITATE, decapitate());
        CLIPS.put(TyrantEntity.GRAB_THROW, grabThrow());
        CLIPS.put(TyrantEntity.GROUND_SLAM, groundSlam());
        CLIPS.put(TyrantEntity.BACKHAND, backhand());
        CLIPS.put(TyrantEntity.SHOULDER_CHARGE, shoulderCharge());
        CLIPS.put(TyrantEntity.LUNGE_STAB, lungeStab());
        CLIPS.put(TyrantEntity.AWAKEN, awaken());
        CLIPS.put(TyrantEntity.MUTATE, mutate());
        CLIPS.put(TyrantEntity.ENRAGE, enrage());
    }

    private final ModelPart hips, abdomen, chest, heart, rTrap, coatL, coatR, neck, head, jaw;
    private final ModelPart rArm, rForearm, rHand, lArm, lForearm, lHand;
    private final ModelPart rThigh, rShin, rFoot, lThigh, lShin, lFoot;
    private final ModelPart[] claws = new ModelPart[5];
    private final ModelPart[] fingers = new ModelPart[4];

    public TyrantModel(ModelPart root) {
        super(root);
        hips = reg("hips", "hips");
        abdomen = reg("abdomen", "hips/abdomen");
        chest = reg("chest", "hips/abdomen/chest");
        heart = reg("heart", "hips/abdomen/chest/heart");
        rTrap = reg("rTrap", "hips/abdomen/chest/rTrap");
        neck = reg("neck", "hips/abdomen/chest/neck");
        head = reg("head", "hips/abdomen/chest/neck/head");
        reg("brain", "hips/abdomen/chest/neck/head/brain");
        jaw = reg("jaw", "hips/abdomen/chest/neck/head/jaw");
        rArm = reg("rArm", "hips/abdomen/chest/rArm");
        rForearm = reg("rForearm", "hips/abdomen/chest/rArm/rForearm");
        rHand = reg("rHand", "hips/abdomen/chest/rArm/rForearm/rHand");
        for (int i = 0; i < 5; i++) claws[i] = reg(CLAWS[i], "hips/abdomen/chest/rArm/rForearm/rHand/" + CLAWS[i]);
        lArm = reg("lArm", "hips/abdomen/chest/lArm");
        lForearm = reg("lForearm", "hips/abdomen/chest/lArm/lForearm");
        lHand = reg("lHand", "hips/abdomen/chest/lArm/lForearm/lHand");
        for (int i = 0; i < 4; i++) fingers[i] = reg(FINGERS[i], "hips/abdomen/chest/lArm/lForearm/lHand/" + FINGERS[i]);
        reg("lThumb", "hips/abdomen/chest/lArm/lForearm/lHand/lThumb");
        coatL = reg("coatL", "hips/coatL");
        coatR = reg("coatR", "hips/coatR");
        rThigh = reg("rThigh", "hips/rThigh");
        rShin = reg("rShin", "hips/rThigh/rShin");
        rFoot = reg("rFoot", "hips/rThigh/rShin/rFoot");
        lThigh = reg("lThigh", "hips/lThigh");
        lShin = reg("lShin", "hips/lThigh/lShin");
        lFoot = reg("lFoot", "hips/lThigh/lShin/lFoot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition hips = root.addOrReplaceChild("hips", CubeListBuilder.create().texOffs(50, 0).addBox(-6.0F, -2.0F, -3.5F, 12, 5, 7), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition abdomen = hips.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(50, 12).addBox(-5.5F, -7.0F, -3.0F, 11, 7, 6), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition chest = abdomen.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -10.0F, -4.5F, 16, 10, 9), PartPose.offset(0.0F, -7.0F, 0.0F));
        PartDefinition heart = chest.addOrReplaceChild("heart", CubeListBuilder.create().texOffs(88, 0).addBox(-2.0F, -2.0F, -1.5F, 4, 4, 2), PartPose.offset(2.5F, -6.0F, -4.5F));
        PartDefinition rTrap = chest.addOrReplaceChild("rTrap", CubeListBuilder.create().texOffs(84, 44).addBox(-4.0F, -2.0F, -3.5F, 8, 4, 7), PartPose.offset(-5.0F, -10.0F, 0.0F));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(108, 0).addBox(-2.5F, -3.0F, -2.5F, 5, 3, 5), PartPose.offset(0.0F, -10.0F, -0.5F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, -7.0F, -4.0F, 8, 7, 8), PartPose.offset(0.0F, -2.5F, -0.5F));
        PartDefinition brain = head.addOrReplaceChild("brain", CubeListBuilder.create().texOffs(84, 15).addBox(-3.5F, -2.0F, -3.0F, 7, 3, 6), PartPose.offset(0.5F, -6.5F, 0.5F));
        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(106, 8).addBox(-3.0F, 0.0F, -5.2F, 6, 2, 5), PartPose.offset(0.0F, -1.5F, 1.0F));

        PartDefinition rArm = chest.addOrReplaceChild("rArm", CubeListBuilder.create().texOffs(32, 25).addBox(-5.0F, -1.0F, -3.0F, 6, 13, 6), PartPose.offset(-8.5F, -8.0F, 0.0F));
        PartDefinition rForearm = rArm.addOrReplaceChild("rForearm", CubeListBuilder.create().texOffs(56, 25).addBox(-3.5F, 0.0F, -3.5F, 7, 11, 7), PartPose.offset(-2.0F, 11.5F, 0.0F));
        PartDefinition rHand = rForearm.addOrReplaceChild("rHand", CubeListBuilder.create().texOffs(84, 25).addBox(-3.0F, 0.0F, -2.5F, 6, 4, 5), PartPose.offset(0.0F, 10.5F, 0.0F));
        float[] clawX = {-2.4F, -1.2F, 0.0F, 1.2F, 2.4F};
        for (int i = 0; i < 5; i++) {
            PartDefinition claw = rHand.addOrReplaceChild(CLAWS[i], CubeListBuilder.create().texOffs(100, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 9, 1), PartPose.offset(clawX[i], 3.5F, -1.2F));
        }

        PartDefinition lArm = chest.addOrReplaceChild("lArm", CubeListBuilder.create().texOffs(44, 44).addBox(-1.0F, -1.0F, -2.5F, 5, 12, 5), PartPose.offset(8.0F, -8.0F, 0.0F));
        PartDefinition lForearm = lArm.addOrReplaceChild("lForearm", CubeListBuilder.create().texOffs(64, 44).addBox(-2.5F, 0.0F, -2.5F, 5, 10, 5), PartPose.offset(1.5F, 11.0F, 0.0F));
        PartDefinition lHand = lForearm.addOrReplaceChild("lHand", CubeListBuilder.create().texOffs(106, 25).addBox(-2.5F, 0.0F, -2.0F, 5, 3, 4), PartPose.offset(0.0F, 9.5F, 0.0F));
        float[] fingerX = {-1.8F, -0.6F, 0.6F, 1.8F};
        for (int i = 0; i < 4; i++) {
            PartDefinition finger = lHand.addOrReplaceChild(FINGERS[i], CubeListBuilder.create().texOffs(104, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1), PartPose.offset(fingerX[i], 2.5F, -1.0F));
        }
        PartDefinition lThumb = lHand.addOrReplaceChild("lThumb", CubeListBuilder.create().texOffs(104, 0).addBox(-0.5F, 0.0F, -0.5F, 1, 4, 1), PartPose.offsetAndRotation(-2.2F, 1.5F, -1.3F, -0.3F, 0.0F, 0.5F));

        PartDefinition coatL = hips.addOrReplaceChild("coatL", CubeListBuilder.create().texOffs(114, 44).addBox(-2.5F, 0.0F, 0.0F, 5, 12, 1), PartPose.offset(3.0F, -1.0F, 3.6F));
        PartDefinition coatR = hips.addOrReplaceChild("coatR", CubeListBuilder.create().texOffs(114, 44).mirror().addBox(-2.5F, 0.0F, 0.0F, 5, 12, 1), PartPose.offset(-3.0F, -1.0F, 3.6F));

        PartDefinition rThigh = hips.addOrReplaceChild("rThigh", CubeListBuilder.create().texOffs(0, 44).addBox(-3.0F, 0.0F, -3.0F, 6, 10, 6), PartPose.offset(-3.2F, 1.0F, 0.0F));
        PartDefinition rShin = rThigh.addOrReplaceChild("rShin", CubeListBuilder.create().texOffs(24, 44).addBox(-2.5F, 0.0F, -2.5F, 5, 7, 5), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition rFoot = rShin.addOrReplaceChild("rFoot", CubeListBuilder.create().texOffs(0, 34).addBox(-2.5F, 0.0F, -4.5F, 5, 2, 7), PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition lThigh = hips.addOrReplaceChild("lThigh", CubeListBuilder.create().texOffs(0, 44).mirror().addBox(-3.0F, 0.0F, -3.0F, 6, 10, 6), PartPose.offset(3.2F, 1.0F, 0.0F));
        PartDefinition lShin = lThigh.addOrReplaceChild("lShin", CubeListBuilder.create().texOffs(24, 44).mirror().addBox(-2.5F, 0.0F, -2.5F, 5, 7, 5), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition lFoot = lShin.addOrReplaceChild("lFoot", CubeListBuilder.create().texOffs(0, 34).mirror().addBox(-2.5F, 0.0F, -4.5F, 5, 2, 7), PartPose.offset(0.0F, 7.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    // ================================================================== idle / heavy gait
    @Override
    protected void baseAnim(TyrantEntity e, float ls, float la, float age, float yaw, float pitch) {
        int form = e.getForm();
        float amt = Math.min(la, 1f);
        float ph = ls * (form == 0 ? 0.42f : 0.5f);
        float s = Mth.sin(ph), c = Mth.cos(ph), drop = Math.abs(s);
        float breath = Mth.sin(age * 0.07f);

        // legs: long stride, knee lifts on the swing, foot rolls heel-to-toe
        rThigh.xRot += s * 0.6f * amt;
        lThigh.xRot -= s * 0.6f * amt;
        rShin.xRot += Math.max(0f, -c) * 0.8f * amt;
        lShin.xRot += Math.max(0f, c) * 0.8f * amt;
        rFoot.xRot -= s * 0.2f * amt;
        lFoot.xRot += s * 0.2f * amt;
        // pelvis: drops hard on every heel strike, rolls over the stance leg, yaws with the stride
        hips.y += drop * 1.4f * amt;
        hips.zRot += s * 0.07f * amt;
        hips.yRot += s * 0.1f * amt;
        abdomen.xRot += 0.06f * amt;
        abdomen.zRot -= s * 0.04f * amt;
        // torso counter-twist, absorbs each footfall
        chest.yRot -= s * 0.14f * amt;
        chest.xRot += 0.08f * amt + drop * 0.04f * amt - breath * 0.02f;
        chest.y += breath * 0.2f;
        rTrap.zRot += s * 0.05f * amt;
        rTrap.y -= breath * 0.15f;
        // massive right arm hangs out from the body and lags; left arm swings freely
        rArm.xRot -= s * 0.28f * amt;
        rArm.zRot += 0.1f;
        rForearm.xRot += -0.3f - Math.max(0f, s) * 0.2f * amt;
        rHand.xRot -= 0.25f;
        lArm.xRot += s * 0.5f * amt;
        lArm.zRot -= 0.06f;
        lForearm.xRot += -0.2f - Math.max(0f, -s) * 0.35f * amt;
        // head tracking + stride bob
        neck.yRot += yaw * DEG * 0.25f;
        head.yRot += yaw * DEG * 0.75f;
        head.xRot += pitch * DEG * 0.7f + drop * 0.06f * amt;
        head.zRot += s * 0.04f * amt;
        jaw.xRot += 0.06f + Math.max(0f, breath) * 0.06f;
        // claws twitch individually, fingers rest half-curled
        float clawLen = form == TyrantEntity.FORM_ENRAGED ? 1.5f : form == TyrantEntity.FORM_SUPER ? 1.25f : 1f;
        for (int i = 0; i < 5; i++) {
            claws[i].xRot += Mth.sin(age * 0.13f + i * 1.7f) * 0.06f;
            claws[i].zRot += (i - 2) * 0.05f;
            claws[i].yScale = clawLen;
        }
        for (int i = 0; i < 4; i++) fingers[i].xRot += -0.35f + Mth.sin(age * 0.1f + i) * 0.05f;
        // coat tails flap with the stride
        coatL.xRot += 0.08f + drop * 0.25f * amt;
        coatR.xRot += 0.08f + drop * 0.25f * amt;
        coatL.zRot += s * 0.05f * amt;
        coatR.zRot += s * 0.05f * amt;

        coatL.visible = coatR.visible = form == TyrantEntity.FORM_TYRANT;
        heart.visible = form >= TyrantEntity.FORM_SUPER;
        if (form >= TyrantEntity.FORM_SUPER) {
            float beat = (float) Math.pow(Math.max(0f, Mth.sin(age * (form == 2 ? 0.9f : 0.6f))), 6);
            float hs = 1f + 0.3f * beat;
            heart.xScale = hs; heart.yScale = hs; heart.zScale = hs;
            chest.zScale *= 1f + 0.015f * beat;
        }
        if (form == TyrantEntity.FORM_ENRAGED) {
            // hunched predator posture, head craned up to keep eyes on prey
            chest.xRot += 0.22f;
            abdomen.xRot += 0.1f;
            head.xRot -= 0.28f;
            rArm.zRot += 0.08f;
            lArm.zRot -= 0.08f;
            rForearm.xScale = 1.08f; rForearm.zScale = 1.08f;
        }
    }

    @Override
    protected void extraAnim(TyrantEntity e, float ls, float la, float age, float yaw, float pitch) {
        // dormant: frozen in the first frame of the awakening clip (kneeling, head bowed); only slow breathing
        if (e.isDormant() && e.getMoveId() == 0) {
            applyClip(CLIPS.get(TyrantEntity.AWAKEN), 0f);
            chest.xRot += Mth.sin(age * 0.04f) * 0.02f;
        }
    }

    @Override
    protected AnimClip clip(TyrantEntity e, int moveId) { return CLIPS.get(moveId); }

    // ================================================================== clip helpers
    /** Per-digit keys (t, curl, spread)...; each digit lags the previous by 0.008, spread scales with its offset from the
     *  hand center, and the key exactly at {@code impact} snaps. */
    private static void digits(AnimClip.Builder b, String[] names, float impact, float... d) {
        for (int i = 0; i < names.length; i++) {
            float side = i - (names.length - 1) * 0.5f;
            AnimClip.BoneBuilder bb = b.bone(names[i]);
            for (int k = 0; k + 2 < d.length; k += 3) {
                float t = d[k];
                float ts = (t <= 0f || t >= 1f) ? t : Math.min(0.999f, t + i * 0.008f);
                AnimClip.Ease ease = Math.abs(t - impact) < 1.0E-4f ? SNAP : AnimClip.Ease.SMOOTH;
                bb.rot(ts, d[k + 1] * (1f - 0.05f * i), 0, d[k + 2] * side, ease);
            }
        }
    }

    // ================================================================== attack clips
    /** (1) iconic forward claw thrust: right arm coiled far back with a spine twist, explosive thrust, over-extension. */
    private static AnimClip clawThrust() {
        float a = 0.40f, k = 0.47f, h = 18f / 34f, f = 0.70f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").rot(0, 0, 0, 0).rot(a, 0, 20, -3).rot(h, 0, -25, 4, SNAP).rot(f, 0, -30, 4, OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(a, 0, 1.5f, 1.5f).pos(h, 0, 1.2f, -3, SNAP).pos(f, 0, 1.6f, -3.5f, OUT).pos(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(a, -5, 12, 0).rot(h, 14, -18, 0, SNAP).rot(f, 16, -20, -2, OUT).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(0.15f, -2, 6, 0).rot(a, -8, 25, 6).rot(h, 12, -30, -6, SNAP).rot(f, 14, -32, -5, OUT).rot(0.85f, 5, -10, -2).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(a, 1.04f, 1.03f, 1.05f).scale(h, 1, 1, 1, SNAP).scale(1, 1, 1, 1);
        b.bone("rTrap").rot(0, 0, 0, 0).rot(a, 0, 0, 12).rot(h, 0, 0, -6, SNAP).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(a, 0, -1, 1).pos(h, 0, 0.5f, -1, SNAP).pos(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(a, 0, -8, 0).rot(h, 8, 10, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(0.15f, 5, -5, 0).rot(a, -10, -18, 0).rot(h, 8, 15, 0, SNAP).rot(f, 4, 14, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(a, 12, 0, 0).rot(h, 28, 0, 0, SNAP).rot(f, 22, 0, 0).rot(0.85f, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(0.15f, 10, 0, 8).rot(a, 50, 20, 35).rot(k, 55, 22, 38).rot(h, -95, -10, 5, SNAP).rot(f, -102, -15, 0, OUT).rot(0.85f, -40, -5, 5).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(a, -85, 0, 0).rot(k, -90, 0, 0).rot(h, -5, 0, 0, SNAP).rot(f, 0, 0, 0).rot(0.85f, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(a, -35, 0, 10).rot(h, 12, 0, 0, SNAP).rot(f, 5, 0, 0).rot(1, 0, 0, 0);
        digits(b, CLAWS, h, 0, 0, 0, 0.15f, 15, 4, a, 25, 12, k, 28, 14, h, -5, 0, f, -12, 1, 0.85f, 8, 3, 1, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(0.15f, -10, 0, -5).rot(a, -35, 0, -18).rot(h, 40, 0, -22, SNAP).rot(f, 45, 0, -20, OUT).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(a, -45, 0, 0).rot(h, -20, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lHand").rot(0, 0, 0, 0).rot(a, -15, 0, 0).rot(h, 10, 0, 0).rot(1, 0, 0, 0);
        digits(b, FINGERS, h, 0, 0, 0, a, -70, 0, h, -40, 6, 0.85f, -20, 2, 1, 0, 0);
        b.bone("lThumb").rot(0, 0, 0, 0).rot(a, -40, 0, -20).rot(h, -20, 0, -10).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(a, -12, 0, -3).rot(h, -38, 0, -4, SNAP).rot(f, -40, 0, -4).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(a, 18, 0, 0).rot(h, 36, 0, 0, SNAP).rot(f, 40, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(a, -6, 0, 0).rot(h, 2, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(a, -15, 0, 4).rot(h, 28, 0, 6, SNAP).rot(f, 30, 0, 6).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(a, 30, 0, 0).rot(h, 12, 0, 0, SNAP).rot(f, 14, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(a, -15, 0, 0).rot(h, -38, 0, 0, SNAP).rot(f, -36, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(a, 8, 0, 0).rot(h, 5, 0, 0).rot(f, 30, 0, 6).rot(0.85f, -8, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(a, 10, 0, 0).rot(h, 3, 0, 0).rot(f, 26, 0, -4).rot(0.87f, -6, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (1b) decapitation: arm raised horizontally far to the right, trembling hold, then a neck-height sweep across the front. */
    private static AnimClip decapitate() {
        float p = 0.12f, a = 0.50f, k = 0.54f, q = 0.57f, h = 26f / 42f, f = 0.78f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").rot(0, 0, 0, 0).rot(p, 0, 4, 0).rot(a, 0, 32, -4).rot(q, 0, 34, -4).rot(h, 0, -30, 5, SNAP).rot(f, 0, -40, 5, OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(a, 0, 2.5f, 1).pos(h, 0, 2.8f, -1.5f, SNAP).pos(f, 0, 2.5f, -2).pos(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(a, -6, 15, 0).rot(h, 10, -18, 0, SNAP).rot(f, 12, -22, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(p, -3, 5, 0).rot(a, -10, 40, 8).rot(k, -11, 42, 9).rot(q, -10, 41, 8).rot(h, 8, -50, -8, SNAP).rot(f, 10, -60, -8, OUT).rot(0.9f, 4, -20, -2).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(a, 1.05f, 1.04f, 1.06f).scale(h, 1, 1, 1, SNAP).scale(1, 1, 1, 1);
        b.bone("rTrap").rot(0, 0, 0, 0).rot(a, 0, 0, 18).rot(h, 0, 0, -10, SNAP).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(a, 0, -15, 0).rot(h, 6, 20, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(p, -4, 0, 0).rot(a, -6, -30, -4).rot(h, 5, 25, 4, SNAP).rot(f, 5, 30, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(a, 10, 0, 0).rot(h, 38, 0, 0, SNAP).rot(f, 30, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(p, -5, 0, 6).rot(a, -88, 70, 0).rot(k, -90, 72, 2).rot(q, -88, 71, 0).rot(h, -92, -60, 0, SNAP).rot(f, -80, -90, -5, OUT).rot(0.9f, -30, -30, 0).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(a, -35, 0, 0).rot(h, -2, 0, 0, SNAP).rot(f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(a, -20, 0, -30).rot(h, 0, 0, 25, SNAP).rot(f, 0, 0, 30).rot(1, 0, 0, 0);
        digits(b, CLAWS, h, 0, 0, 0, p, 20, 6, a, -10, 16, q, -12, 18, h, -8, 2, f, 10, 4, 1, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(a, -50, -30, -25).rot(h, 20, 0, -45, SNAP).rot(f, 25, 0, -50).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(a, -50, 0, 0).rot(h, -15, 0, 0).rot(1, 0, 0, 0);
        digits(b, FINGERS, h, 0, 0, 0, a, -20, 10, h, -75, 0, 0.85f, -30, 0, 1, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(a, -20, 10, 18).rot(h, -20, -10, 16, SNAP).rot(f, -22, -12, 16).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(a, 38, 0, 0).rot(h, 40, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(a, -18, 0, -16).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(a, -22, 10, -18).rot(h, -24, -15, -16, SNAP).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(a, 40, 0, 0).rot(h, 44, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(a, -18, 0, 16).rot(1, 0, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(a, 10, 0, -8).rot(h, 4, 0, 0, SNAP).rot(f, 28, 0, 15).rot(0.9f, -6, 0, -4).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(a, 12, 0, -10).rot(h, 2, 0, 0, SNAP).rot(f, 24, 0, 18).rot(0.92f, -5, 0, -3).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (2) left-hand grab: reach, fingers clamp, hoist overhead, struggle shake, carry over shoulder, hurl. */
    private static AnimClip grabThrow() {
        float r = 0.22f, g = 16f / 52f, l = 0.45f, s1 = 0.52f, s2 = 0.58f, w = 0.63f, th = 38f / 52f, f = 0.85f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").rot(0, 0, 0, 0).rot(r, 0, -15, 0).rot(g, 0, -18, 0, SNAP).rot(l, 0, -5, 0).rot(w, 0, 25, -3).rot(th, 0, -35, 4, SNAP).rot(f, 0, -40, 4, OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(r, 0, 1.5f, -1.5f).pos(g, 0, 1.8f, -2).pos(l, 0, 0.5f, 0.5f).pos(w, 0, 1.8f, 1.5f).pos(th, 0, 1.5f, -2.5f, SNAP).pos(f, 0, 1.8f, -3).pos(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(r, 12, -10, 0).rot(g, 14, -12, 0).rot(l, -10, 0, 0).rot(w, -12, 15, 0).rot(th, 18, -20, 0, SNAP).rot(f, 20, -22, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(r, 14, -20, 0).rot(g, 16, -22, 2, SNAP).rot(l, -15, -5, -3).rot(s1, -14, -8, 3).rot(s2, -16, -2, -3).rot(w, -18, 30, -5)
            .rot(th, 22, -40, 6, SNAP).rot(f, 24, -45, 6, OUT).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(l, -10, 0, 0).rot(th, 10, -10, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(r, -8, 15, 0).rot(g, -6, 18, 0).rot(l, -30, 5, 0).rot(s1, -28, 10, 5).rot(s2, -30, 0, -5).rot(w, -20, -20, 0).rot(th, 15, 30, 0, SNAP).rot(f, 10, 30, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(r, 8, 0, 0).rot(g, 20, 0, 0).rot(l, 10, 0, 0).rot(s2, 30, 0, 0).rot(th, 35, 0, 0).rot(f, 25, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(r, -80, -10, -5).rot(g, -88, -12, -5, SNAP).rot(l, -165, 0, -12).rot(s1, -160, 6, -14).rot(s2, -168, -6, -10).rot(w, -175, 35, 0)
            .rot(th, -45, -25, -8, SNAP).rot(f, -30, -30, -10, OUT).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(r, -10, 0, 0).rot(g, -25, 0, 0, SNAP).rot(l, -20, 0, 0).rot(w, -50, 0, 0).rot(th, -5, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lHand").rot(0, 0, 0, 0).rot(r, 20, 0, 0).rot(g, -10, 0, 0, SNAP).rot(th, 25, 0, 0, SNAP).rot(1, 0, 0, 0);
        digits(b, FINGERS, g, 0, 0, 0, r, 25, 14, g, -85, 2, l, -90, 0, w, -92, 0, th, 30, 16, f, 15, 8, 1, 0, 0);
        b.bone("lThumb").rot(0, 0, 0, 0).rot(r, 10, 0, 30).rot(g, -60, 0, -10, SNAP).rot(w, -65, 0, -10).rot(th, 10, 0, 30, SNAP).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(r, 25, 0, 12).rot(g, 28, 0, 14).rot(l, 10, 0, 30).rot(w, 35, 0, 25).rot(th, -35, 0, 10, SNAP).rot(f, -40, 0, 8).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(l, -30, 0, 0).rot(th, -15, 0, 0).rot(1, 0, 0, 0);
        digits(b, CLAWS, th, 0, 0, 0, l, 25, 8, w, 30, 10, th, -5, 2, 1, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(r, -30, 0, -3).rot(g, -32, 0, -3).rot(l, -8, 0, -6).rot(w, -6, 0, -8).rot(th, -40, 0, -4, SNAP).rot(f, -42, 0, -4).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(r, 35, 0, 0).rot(l, 14, 0, 0).rot(w, 20, 0, 0).rot(th, 38, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(r, -5, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(r, 15, 0, 4).rot(l, -5, 0, 8).rot(w, -25, 0, 8).rot(th, 25, 0, 6, SNAP).rot(f, 26, 0, 6).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(l, 10, 0, 0).rot(w, 40, 0, 0).rot(th, 15, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(w, -15, 0, 0).rot(th, -35, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(l, -10, 0, 0).rot(th, 5, 0, 0).rot(f, 25, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(l, -8, 0, 0).rot(th, 3, 0, 0).rot(f, 22, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (3) overhead double-fist slam: rise onto the toes with fists behind the head, roar, drop into a deep squat. */
    private static AnimClip groundSlam() {
        float p = 0.15f, up = 0.42f, k = 0.52f, h = 28f / 46f, bo = 0.66f, st = 0.72f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(p, 0, 1.5f, 0).pos(up, 0, -1.5f, 0.5f).pos(k, 0, -2, 1).pos(h, 0, 4, -2, SNAP).pos(bo, 0, 4.6f, -2).pos(st, 0, 4, -2).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(up, -4, 0, 0).rot(h, 10, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(p, 8, 0, 0).rot(up, -12, 0, 0).rot(k, -15, 0, 0).rot(h, 22, 0, 0, SNAP).rot(st, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(p, 10, 0, 0).rot(up, -18, 0, 0).rot(k, -22, 0, 0).rot(h, 40, 0, 0, SNAP).rot(bo, 44, 0, 0).rot(st, 40, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(up, 1.06f, 1.05f, 1.06f).scale(h, 0.97f, 0.98f, 0.97f, SNAP).scale(st, 1, 1, 1).scale(1, 1, 1, 1);
        b.bone("rTrap").rot(0, 0, 0, 0).rot(up, 0, 0, -12).rot(h, 0, 0, 6, SNAP).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(up, 0, -1.5f, 0).pos(h, 0, 0.5f, 0, SNAP).pos(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(up, -12, 0, 0).rot(h, 15, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(p, 10, 0, 0).rot(up, -30, 0, 0).rot(k, -34, 0, 0).rot(h, 10, 0, 0, SNAP).rot(bo, 14, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(up, 35, 0, 0).rot(k, 40, 0, 0).rot(h, 20, 0, 0).rot(st, 8, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(p, 15, 0, 10).rot(up, -170, 0, -15).rot(k, -175, 0, -12).rot(h, -38, 0, 8, SNAP).rot(bo, -35, 0, 8).rot(st, -38, 0, 8).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(up, -55, 0, 0).rot(k, -60, 0, 0).rot(h, -12, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(up, -30, 0, 0).rot(h, -55, 0, 0, SNAP).rot(st, -50, 0, 0).rot(1, 0, 0, 0);
        digits(b, CLAWS, h, 0, 0, 0, up, 40, 0, h, 30, 0, st, 30, 2, 1, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(p, 15, 0, -10).rot(up, -170, 0, 15).rot(k, -175, 0, 12).rot(h, -38, 0, -8, SNAP).rot(bo, -35, 0, -8).rot(st, -38, 0, -8).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(up, -55, 0, 0).rot(k, -60, 0, 0).rot(h, -12, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lHand").rot(0, 0, 0, 0).rot(up, -20, 0, 0).rot(h, -40, 0, 0).rot(1, 0, 0, 0);
        digits(b, FINGERS, h, 0, 0, 0, p, -40, 0, up, -90, 0, h, -95, 0, 0.75f, -90, 0, 1, 0, 0);
        b.bone("lThumb").rot(0, 0, 0, 0).rot(up, -60, 0, -20).rot(0.75f, -60, 0, -20).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(p, -23, 0, 4).rot(up, 4, 0, 4).rot(k, 6, 0, 4).rot(h, -40, 0, 6, SNAP).rot(bo, -44, 0, 6).rot(st, -40, 0, 6).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(p, 46, 0, 0).rot(up, 0, 0, 0).rot(h, 76, 0, 0, SNAP).rot(bo, 82, 0, 0).rot(st, 76, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(p, -23, 0, 0).rot(up, -14, 0, 0).rot(h, -36, 0, 0, SNAP).rot(bo, -38, 0, 0).rot(st, -36, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(p, -23, 0, -4).rot(up, 4, 0, -4).rot(k, 6, 0, -4).rot(h, -40, 0, -6, SNAP).rot(bo, -44, 0, -6).rot(st, -40, 0, -6).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(p, 46, 0, 0).rot(up, 0, 0, 0).rot(h, 76, 0, 0, SNAP).rot(bo, 82, 0, 0).rot(st, 76, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(p, -23, 0, 0).rot(up, -14, 0, 0).rot(h, -36, 0, 0, SNAP).rot(bo, -38, 0, 0).rot(st, -36, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(up, -5, 0, 0).rot(h, -20, 0, 0, SNAP).rot(st, 35, 0, 0).rot(0.85f, -5, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(up, -5, 0, 0).rot(h, -18, 0, 0, SNAP).rot(st, 32, 0, 0).rot(0.87f, -4, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (4) backhand: arm crossed to the left with torso coiled, whips out to the right, wrist flick. */
    private static AnimClip backhand() {
        float p = 0.12f, a = 0.38f, h = 15f / 30f, f = 0.66f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").rot(0, 0, 0, 0).rot(p, 0, -5, 2).rot(a, 0, -22, 4).rot(h, 0, 25, -4, SNAP).rot(f, 0, 32, -5, OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(a, 1, 1.2f, 0).pos(h, -1, 1.2f, -1, SNAP).pos(f, -1.2f, 1.4f, -1).pos(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(a, 5, -15, 0).rot(h, 6, 18, 0, SNAP).rot(f, 6, 22, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(p, 0, -8, 0).rot(a, 8, -40, 5).rot(h, 4, 45, -6, SNAP).rot(f, 2, 55, -8, OUT).rot(0.85f, 0, 15, -2).rot(1, 0, 0, 0);
        b.bone("rTrap").rot(0, 0, 0, 0).rot(a, 0, 0, -6).rot(h, 0, 0, 14, SNAP).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(a, 0, 15, 0).rot(h, 0, -12, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(a, -5, 25, 0).rot(h, 5, -15, 0, SNAP).rot(f, 5, -20, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(a, 5, 0, 0).rot(h, 25, 0, 0).rot(f, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(p, -20, -15, -5).rot(a, -70, -60, -10).rot(h, -80, 75, 10, SNAP).rot(f, -50, 95, 25, OUT).rot(0.85f, -15, 30, 10).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(a, -65, 0, 0).rot(h, -5, 0, 0, SNAP).rot(f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(a, 0, 0, 25).rot(h, 0, 0, -25, SNAP).rot(f, 0, 0, -30).rot(1, 0, 0, 0);
        digits(b, CLAWS, h, 0, 0, 0, a, 30, 4, h, -8, 14, f, -5, 16, 1, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(a, 20, 0, -30).rot(h, -30, 0, -12, SNAP).rot(f, -35, 0, -10).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(a, -20, 0, 0).rot(h, -50, 0, 0).rot(1, 0, 0, 0);
        digits(b, FINGERS, h, 0, 0, 0, a, -30, 4, h, -70, 0, 0.85f, -20, 0, 1, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(a, -5, -10, 4).rot(h, -8, 15, 8, SNAP).rot(f, -8, 18, 8).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(a, 14, 0, 0).rot(h, 18, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(h, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(a, -14, -10, -6).rot(h, -6, 15, -4, SNAP).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(a, 26, 0, 0).rot(h, 12, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(a, -12, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(a, 5, 0, 10).rot(h, 5, 0, -6).rot(f, 20, 0, -20).rot(0.85f, -5, 0, 5).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(a, 4, 0, 12).rot(h, 6, 0, -4).rot(f, 18, 0, -22).rot(0.87f, -4, 0, 6).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (5) shoulder charge: drops the right shoulder, sprint cycle with pumping left arm, skids to a braced stop. */
    private static AnimClip shoulderCharge() {
        float st = 0.18f, end = 0.74f, sk = 0.86f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(st, 0, 2, 0).pos(0.30f, 0, 1, 0).pos(0.36f, 0, 2.2f, 0).pos(0.42f, 0, 1, 0).pos(0.48f, 0, 2.2f, 0).pos(0.54f, 0, 1, 0)
            .pos(0.60f, 0, 2.2f, 0).pos(0.66f, 0, 1, 0).pos(end, 0, 2, 0).pos(sk, 0, 2.5f, 2).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(st, 10, -15, 0).rot(0.36f, 10, -12, 4).rot(0.48f, 10, -18, -4).rot(0.60f, 10, -12, 4).rot(end, 10, -15, 0).rot(sk, -5, -5, 0).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(st, 15, -10, 0).rot(end, 15, -12, 0).rot(sk, -6, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(st, 28, -30, -10).rot(0.30f, 30, -34, -12).rot(0.42f, 28, -28, -8).rot(0.54f, 30, -34, -12).rot(0.66f, 28, -28, -8)
            .rot(end, 30, -32, -10).rot(sk, -12, -5, 0).rot(1, 0, 0, 0);
        b.bone("rTrap").rot(0, 0, 0, 0).rot(st, 0, 0, 20).rot(end, 0, 0, 20).rot(sk, 0, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(st, 0, -1.5f, -1).pos(end, 0, -1.5f, -1).pos(sk, 0, 0, 0).pos(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(st, 20, 10, 0).rot(end, 20, 10, 0).rot(sk, -5, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(st, -25, 20, 0).rot(end, -25, 20, 0).rot(sk, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(st, 15, 0, 0).rot(0.30f, 30, 0, 0).rot(end, 30, 0, 0).rot(sk, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(st, -25, 0, -15).rot(end, -25, 0, -15).rot(sk, -60, 0, 10).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(st, -95, 0, 0).rot(end, -95, 0, 0).rot(sk, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(st, -10, 0, 0).rot(end, -10, 0, 0).rot(1, 0, 0, 0);
        digits(b, CLAWS, -1, 0, 0, 0, st, 20, 4, end, 20, 4, sk, -10, 10, 1, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(st, 20, 0, -10).rot(0.30f, -40, 0, -10).rot(0.42f, 45, 0, -10).rot(0.54f, -40, 0, -10).rot(0.66f, 45, 0, -10)
            .rot(end, 0, 0, -15).rot(sk, -60, 0, -25).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(st, -70, 0, 0).rot(end, -70, 0, 0).rot(sk, -30, 0, 0).rot(1, 0, 0, 0);
        digits(b, FINGERS, -1, 0, 0, 0, st, -85, 0, end, -85, 0, sk, 10, 10, 1, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(st, -20, 0, 0).rot(0.30f, -55, 0, 0).rot(0.42f, 35, 0, 0).rot(0.54f, -55, 0, 0).rot(0.66f, 35, 0, 0)
            .rot(end, -30, 0, 0).rot(sk, -45, 0, 4).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(st, 30, 0, 0).rot(0.30f, 40, 0, 0).rot(0.36f, 15, 0, 0).rot(0.42f, 60, 0, 0).rot(0.48f, 80, 0, 0).rot(0.54f, 40, 0, 0)
            .rot(0.60f, 15, 0, 0).rot(0.66f, 60, 0, 0).rot(end, 30, 0, 0).rot(sk, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(end, 0, 0, 0).rot(sk, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(st, 10, 0, 0).rot(0.30f, 35, 0, 0).rot(0.42f, -55, 0, 0).rot(0.54f, 35, 0, 0).rot(0.66f, -55, 0, 0)
            .rot(end, 10, 0, 0).rot(sk, 20, 0, -4).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(0.30f, 60, 0, 0).rot(0.36f, 80, 0, 0).rot(0.42f, 40, 0, 0).rot(0.48f, 15, 0, 0).rot(0.54f, 60, 0, 0)
            .rot(0.60f, 80, 0, 0).rot(0.66f, 40, 0, 0).rot(end, 20, 0, 0).rot(sk, 35, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(end, 0, 0, 0).rot(sk, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(st, 20, 0, 0).rot(0.30f, 50, 0, 5).rot(0.48f, 45, 0, -5).rot(end, 55, 0, -5).rot(sk, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(st, 18, 0, 0).rot(0.36f, 52, 0, -5).rot(0.54f, 44, 0, 5).rot(end, 50, 0, 5).rot(sk, -12, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (6) lunge stab gap-closer: deep coil, push off the back leg, body near horizontal behind a straight-arm stab. */
    private static AnimClip lungeStab() {
        float c = 0.30f, k = 0.42f, fl = 0.50f, h = 22f / 36f, sl = 0.75f, rc = 0.88f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(c, 0, 3, 1.5f).pos(k, 0, 2.5f, 0).pos(fl, 0, 0.5f, -3).pos(h, 0, 2, -4, SNAP).pos(sl, 0, 2.5f, -4).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(c, 12, 15, 0).rot(fl, 18, -5, 0).rot(h, 20, -18, 0).rot(sl, 15, -15, 0).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(c, 15, 10, 0).rot(h, 20, -12, 0, SNAP).rot(sl, 15, -10, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(c, 20, 25, 4).rot(k, 18, 28, 4).rot(fl, 25, 10, 0).rot(h, 30, -25, -4, SNAP).rot(sl, 25, -20, -3).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(c, -10, -5, 0).rot(h, -10, 5, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(c, -25, -20, 0).rot(h, -15, 15, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(c, 10, 0, 0).rot(h, 35, 0, 0, SNAP).rot(sl, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("rTrap").rot(0, 0, 0, 0).rot(c, 0, 0, 12).rot(h, 0, 0, -8, SNAP).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(c, 45, 20, 25).rot(k, 50, 22, 28).rot(h, -105, -5, 0, SNAP).rot(sl, -100, -8, 0).rot(rc, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(c, -95, 0, 0).rot(k, -100, 0, 0).rot(h, 0, 0, 0, SNAP).rot(sl, -5, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(c, -30, 0, 0).rot(h, 10, 0, 0, SNAP).rot(1, 0, 0, 0);
        digits(b, CLAWS, h, 0, 0, 0, c, 30, 12, k, 32, 14, h, -6, 0, sl, -4, 0, 1, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(c, -40, 0, -20).rot(fl, -60, 0, -30).rot(h, 35, 0, -25, SNAP).rot(sl, 30, 0, -20).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(c, -40, 0, 0).rot(h, -15, 0, 0).rot(1, 0, 0, 0);
        digits(b, FINGERS, h, 0, 0, 0, c, -20, 8, h, -60, 0, 1, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(c, -35, 0, 4).rot(k, -30, 0, 4).rot(fl, 35, 0, 4).rot(h, 30, 0, 4).rot(sl, 25, 0, 4).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(c, 70, 0, 0).rot(k, 65, 0, 0).rot(fl, 5, 0, 0).rot(h, 15, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(c, -35, 0, 0).rot(fl, -40, 0, 0).rot(h, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(c, -35, 0, -4).rot(k, -40, 0, -4).rot(fl, -70, 0, -4).rot(h, -55, 0, -4, SNAP).rot(sl, -50, 0, -4).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(c, 70, 0, 0).rot(fl, 40, 0, 0).rot(h, 55, 0, 0).rot(sl, 55, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(c, -35, 0, 0).rot(fl, 20, 0, 0).rot(h, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(c, -5, 0, 0).rot(fl, 40, 0, 0).rot(h, 50, 0, 0).rot(sl, 20, 0, 0).rot(rc, -8, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(c, -4, 0, 0).rot(fl, 36, 0, 0).rot(h, 48, 0, 0).rot(sl, 24, 0, 0).rot(rc, -6, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    // ================================================================== transformation clips
    /** dormant kneel (t=0) -> twitch -> heartbeat jolt -> rises -> shakes its head -> roar with arms flung wide. */
    private static AnimClip awaken() {
        float hold = 0.12f, tw = 0.18f, g = 20f / 90f, look = 0.30f, push = 0.36f, mid = 0.42f, up = 0.55f, in = 0.74f, r = 70f / 90f, rh = 0.92f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 9.5f, 1).pos(hold, 0, 9.5f, 1).pos(g, 0, 9, 1, SNAP).pos(look, 0, 9.2f, 1).pos(mid, 0, 6, 0).pos(up, 0, 0, 0).pos(in, 0, 0.5f, 0.5f).pos(r, 0, 0, 0).pos(1, 0, 0, 0)
            .rot(0, 5, 0, 0).rot(push, 5, 0, 0).rot(up, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 5, 0, 4).rot(push, 5, 0, 4).rot(mid, -30, 0, 4).rot(up, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 85, 0, 0).rot(push, 85, 0, 0).rot(mid, 60, 0, 0).rot(up, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, -50, 0, 0).rot(push, -50, 0, 0).rot(mid, -30, 0, 0).rot(up, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, -80, 0, -6).rot(push, -80, 0, -6).rot(mid, -60, 0, -4).rot(up, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 80, 0, 0).rot(push, 80, 0, 0).rot(mid, 60, 0, 0).rot(up, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 20, 0, 0).rot(push, 20, 0, 0).rot(up, 12, 0, 0).rot(in, -6, 0, 0).rot(r, -10, 0, 0).rot(rh, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 25, 0, 5).rot(hold, 25, 0, 5).rot(g, 18, 0, 3, SNAP).rot(look, 22, 0, 3).rot(up, 15, 0, 0).rot(in, -10, 0, 0).rot(r, -22, 0, 0, SNAP)
            .rot(0.86f, -24, 0, 0).rot(rh, -20, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(in, 1.07f, 1.05f, 1.07f).scale(r, 1, 1, 1, SNAP).scale(1, 1, 1, 1);
        b.bone("neck").rot(0, 15, 0, 0).rot(g, 5, 0, 0).rot(up, 8, 0, 0).rot(r, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 30, 0, -5).rot(hold, 30, 0, -5).rot(g, 10, 0, 0, SNAP).rot(look, 15, -15, 0).rot(up, 20, 0, 0).rot(0.58f, 15, -25, 5).rot(0.62f, 15, 25, -5)
            .rot(0.66f, 15, -20, 4).rot(0.70f, 15, 15, -3).rot(in, -5, 0, 0).rot(r, -35, 0, 0, SNAP).rot(0.86f, -38, 5, 0).rot(rh, -35, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 3, 0, 0).rot(0.62f, 15, 0, 0).rot(in, 10, 0, 0).rot(r, 45, 0, 0, SNAP).rot(rh, 42, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, -25, 0, 8).rot(push, -25, 0, 8).rot(mid, -10, 0, 15).rot(up, 0, 0, 10).rot(in, 10, 0, 20).rot(r, -20, 0, 55, SNAP).rot(rh, -22, 0, 58).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, -25, 0, 0).rot(mid, -10, 0, 0).rot(in, -40, 0, 0).rot(r, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, -20, 0, 0).rot(r, -15, 0, 0).rot(1, 0, 0, 0);
        digits(b, CLAWS, r, 0, 30, 0, hold, 30, 0, tw, 10, 8, g, 35, 0, look, 25, 4, in, 30, 2, r, -10, 18, rh, -12, 18, 1, 0, 0);
        b.bone("lArm").rot(0, -30, 0, -12).rot(push, -30, 0, -12).rot(mid, -15, 0, -20).rot(up, 0, 0, -10).rot(in, 10, 0, -20).rot(r, -20, 0, -55, SNAP).rot(rh, -22, 0, -58).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, -55, 0, 0).rot(push, -55, 0, 0).rot(mid, -30, 0, 0).rot(in, -40, 0, 0).rot(r, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lHand").rot(0, 10, 0, 0).rot(up, 0, 0, 0).rot(1, 0, 0, 0);
        digits(b, FINGERS, r, 0, -30, 0, hold, -30, 0, tw, 0, 10, g, -60, 0, look, -40, 0, in, -80, 0, r, 20, 14, rh, 20, 14, 1, 0, 0);
        b.bone("coatL").rot(0, 30, 0, 0).rot(push, 30, 0, 0).rot(up, 5, 0, 0).rot(r, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 30, 0, 0).rot(push, 30, 0, 0).rot(up, 5, 0, 0).rot(r, 10, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** Tyrant -> Super Tyrant: convulsions clutching the chest, then arches back as the heart bursts out, roar. */
    private static AnimClip mutate() {
        float hn = 0.44f, h = 30f / 60f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("chest").rot(0, 0, 0, 0).rot(0.08f, 15, 0, 6, SNAP).rot(0.14f, 20, 0, -6).rot(0.20f, 25, 0, 7).rot(0.26f, 28, 0, -7).rot(0.32f, 30, 0, 6).rot(0.38f, 32, 0, -5)
            .rot(hn, 35, 0, 0).rot(h, -35, 0, 0, SNAP).rot(0.60f, -38, 0, 0).rot(0.75f, -30, 0, 0).rot(0.88f, -10, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(hn, 0.97f, 1, 0.97f).scale(h, 1.1f, 1.05f, 1.1f, SNAP).scale(0.75f, 1.04f, 1.02f, 1.04f).scale(1, 1, 1, 1);
        b.bone("heart").scale(0, 1, 1, 1).scale(hn, 1, 1, 1).scale(h, 1.8f, 1.8f, 1.8f, SNAP).scale(0.60f, 1.3f, 1.3f, 1.3f).scale(0.70f, 1.6f, 1.6f, 1.6f).scale(0.80f, 1.2f, 1.2f, 1.2f).scale(1, 1, 1, 1);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(hn, 15, 0, 0).rot(h, -12, 0, 0, SNAP).rot(0.75f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("hips").pos(0, 0, 0, 0).pos(hn, 0, 2.5f, 0).pos(h, 0, 1, 1, SNAP).pos(0.75f, 0, 1, 1).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(hn, 8, 0, 0).rot(h, -5, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(hn, -35, 0, 6).rot(h, -15, 0, 12, SNAP).rot(0.75f, -15, 0, 12).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(hn, -35, 0, -6).rot(h, -15, 0, -12, SNAP).rot(0.75f, -15, 0, -12).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(hn, 70, 0, 0).rot(h, 30, 0, 0, SNAP).rot(0.75f, 30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(hn, 70, 0, 0).rot(h, 30, 0, 0, SNAP).rot(0.75f, 30, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(hn, -35, 0, 0).rot(h, -15, 0, 0, SNAP).rot(0.75f, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(hn, -35, 0, 0).rot(h, -15, 0, 0, SNAP).rot(0.75f, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("neck").rot(0, 0, 0, 0).rot(hn, 20, 0, 0).rot(h, -20, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(0.08f, 20, 0, 10, SNAP).rot(0.20f, 25, 0, -10).rot(0.32f, 30, 0, 10).rot(hn, 35, 0, 0).rot(h, -45, 0, 0, SNAP)
            .rot(0.60f, -48, 8, 0).rot(0.70f, -45, -8, 0).rot(0.80f, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(0.08f, 20, 0, 0).rot(hn, 10, 0, 0).rot(h, 50, 0, 0, SNAP).rot(0.80f, 45, 0, 0).rot(1, 0, 0, 0);
        b.bone("rTrap").rot(0, 0, 0, 0).rot(hn, 0, 0, 10).rot(h, 0, 0, -15, SNAP).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(0.15f, -60, 0, 30).rot(hn, -70, 0, 35).rot(h, -10, 0, -85, SNAP).rot(0.75f, -12, 0, -88).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(0.15f, -90, 0, 0).rot(hn, -95, 0, 0).rot(h, -10, 0, 0, SNAP).rot(1, 0, 0, 0);
        digits(b, FINGERS, h, 0, 0, 0, 0.15f, -60, 0, hn, -80, 0, h, 25, 16, 0.80f, 20, 14, 1, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(0.15f, -30, 0, -20).rot(hn, -35, 0, -25).rot(h, -10, 0, 85, SNAP).rot(0.75f, -12, 0, 88).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(0.15f, -60, 0, 0).rot(h, -10, 0, 0, SNAP).rot(1, 0, 0, 0);
        digits(b, CLAWS, h, 0, 0, 0, 0.08f, 40, 0, 0.20f, 10, 10, 0.32f, 40, 0, hn, 35, 2, h, -15, 20, 0.80f, -12, 18, 1, 0, 0);
        b.bone("coatL").rot(0, 0, 0, 0).rot(hn, 10, 0, 0).rot(h, 60, 0, -20, SNAP).rot(1, 0, 0, 0);
        b.bone("coatR").rot(0, 0, 0, 0).rot(hn, 10, 0, 0).rot(h, 60, 0, 20, SNAP).rot(1, 0, 0, 0);
        return b.build();
    }

    /** Super -> enraged: drops into a crouch, explodes upward into a flexing roar, trembling while it regenerates. */
    private static AnimClip enrage() {
        float c = 0.30f, h = 20f / 50f, e = 0.80f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(c, 0, 4, 1).pos(h, 0, 0.5f, 0, SNAP).pos(e, 0, 0.5f, 0).pos(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(c, -38, 0, 6).rot(h, -8, 0, 10, SNAP).rot(e, -8, 0, 10).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(c, -38, 0, -6).rot(h, -8, 0, -10, SNAP).rot(e, -8, 0, -10).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(c, 76, 0, 0).rot(h, 16, 0, 0, SNAP).rot(e, 16, 0, 0).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(c, 76, 0, 0).rot(h, 16, 0, 0, SNAP).rot(e, 16, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(c, -38, 0, 0).rot(h, -8, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(c, -38, 0, 0).rot(h, -8, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("abdomen").rot(0, 0, 0, 0).rot(c, 25, 0, 0).rot(h, -10, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(c, 40, 0, 0).rot(h, -25, 0, 0, SNAP).rot(0.50f, -27, 0, 3).rot(0.60f, -25, 0, -3).rot(0.70f, -27, 0, 3).rot(e, -25, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(c, 1, 1, 1).scale(h, 1.08f, 1.06f, 1.08f, SNAP).scale(e, 1.05f, 1.04f, 1.05f).scale(1, 1, 1, 1);
        b.bone("heart").scale(0, 1, 1, 1).scale(h, 1.7f, 1.7f, 1.7f, SNAP).scale(0.55f, 1.2f, 1.2f, 1.2f).scale(0.70f, 1.6f, 1.6f, 1.6f).scale(1, 1, 1, 1);
        b.bone("rTrap").pos(0, 0, 0, 0).pos(h, 0, -1.5f, 0, SNAP).pos(e, 0, -1.5f, 0).pos(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(c, 30, 0, 0).rot(h, -40, 0, 0, SNAP).rot(0.55f, -42, 6, 0).rot(0.70f, -42, -6, 0).rot(e, -38, 0, 0).rot(1, 0, 0, 0);
        b.bone("jaw").rot(0, 0, 0, 0).rot(c, 10, 0, 0).rot(h, 50, 0, 0, SNAP).rot(e, 48, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(c, 10, 0, 15).rot(h, -150, 0, -30, SNAP).rot(e, -150, 0, -28).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(h, -40, 0, 0, SNAP).rot(e, -42, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(c, 10, 0, -15).rot(h, -150, 0, 30, SNAP).rot(e, -150, 0, 28).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(h, -40, 0, 0, SNAP).rot(e, -42, 0, 0).rot(1, 0, 0, 0);
        digits(b, CLAWS, h, 0, 0, 0, c, 40, 0, h, -15, 22, e, -15, 22, 1, 0, 0);
        digits(b, FINGERS, h, 0, 0, 0, c, -80, 0, h, 25, 16, e, 25, 16, 1, 0, 0);
        return b.build();
    }
}
