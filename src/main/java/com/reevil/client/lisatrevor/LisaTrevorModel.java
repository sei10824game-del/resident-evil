package com.reevil.client.lisatrevor;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.lisatrevor.LisaTrevorEntity;
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
 * Lisa Trevor: hunched emaciated body in a tattered dress, hair over the face, flesh mass on the back,
 * rope-thin arms long enough to drag the fingers on the floor (left upper arm swollen), wrist shackles with dangling chains,
 * and (form 1) two extra thin arms bursting from the ribs. Texture 128x64 (tools/tex_lisa_trevor.py parses this file).
 */
public class LisaTrevorModel extends BioModel<LisaTrevorEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "lisa_trevor"), "main");

    private static final String[] RF = {"rF1", "rF2", "rF3", "rF4"};
    private static final String[] LF = {"lF1", "lF2", "lF3", "lF4"};
    private static final float DEG = (float) Math.PI / 180f;

    private static final Map<Integer, AnimClip> CLIPS = new HashMap<>();
    static {
        CLIPS.put(LisaTrevorEntity.ARM_SWIPE, armSwipe());
        CLIPS.put(LisaTrevorEntity.GRAB_HOLD, grabHold());
        CLIPS.put(LisaTrevorEntity.FLING, fling());
        CLIPS.put(LisaTrevorEntity.POUNCE, pounce());
        CLIPS.put(LisaTrevorEntity.WAIL, wail());
        CLIPS.put(LisaTrevorEntity.CHAIN_SPIN, chainSpin());
        CLIPS.put(LisaTrevorEntity.RECOIL, recoil());
        CLIPS.put(LisaTrevorEntity.QUAD_FLURRY, quadFlurry());
        CLIPS.put(LisaTrevorEntity.MUTATE, mutate());
    }

    private final ModelPart hips, skirt, spine, chest, tumor, tumor2, neck, head, hairBack, hairFront, hairL, hairR;
    private final ModelPart rArm, rForearm, rHand, rChain1, rChain2, lArm, lForearm, lHand, lChain1, lChain2;
    private final ModelPart rArm2, rForearm2, lArm2, lForearm2, rClaw2a, rClaw2b, lClaw2a, lClaw2b;
    private final ModelPart rThigh, rShin, rFoot, lThigh, lShin, lFoot;
    private final ModelPart[] rFingers = new ModelPart[4];
    private final ModelPart[] lFingers = new ModelPart[4];

    public LisaTrevorModel(ModelPart root) {
        super(root);
        hips = reg("hips", "hips");
        skirt = reg("skirt", "hips/skirt");
        spine = reg("spine", "hips/spine");
        chest = reg("chest", "hips/spine/chest");
        tumor = reg("tumor", "hips/spine/chest/tumor");
        tumor2 = reg("tumor2", "hips/spine/chest/tumor/tumor2");
        neck = reg("neck", "hips/spine/chest/neck");
        head = reg("head", "hips/spine/chest/neck/head");
        hairBack = reg("hairBack", "hips/spine/chest/neck/head/hairBack");
        hairFront = reg("hairFront", "hips/spine/chest/neck/head/hairFront");
        hairL = reg("hairL", "hips/spine/chest/neck/head/hairL");
        hairR = reg("hairR", "hips/spine/chest/neck/head/hairR");
        String c = "hips/spine/chest/";
        rArm = reg("rArm", c + "rArm");
        rForearm = reg("rForearm", c + "rArm/rForearm");
        reg("rShackle", c + "rArm/rForearm/rShackle");
        rChain1 = reg("rChain1", c + "rArm/rForearm/rShackle/rChain1");
        rChain2 = reg("rChain2", c + "rArm/rForearm/rShackle/rChain1/rChain2");
        rHand = reg("rHand", c + "rArm/rForearm/rHand");
        for (int i = 0; i < 4; i++) rFingers[i] = reg(RF[i], c + "rArm/rForearm/rHand/" + RF[i]);
        lArm = reg("lArm", c + "lArm");
        lForearm = reg("lForearm", c + "lArm/lForearm");
        reg("lShackle", c + "lArm/lForearm/lShackle");
        lChain1 = reg("lChain1", c + "lArm/lForearm/lShackle/lChain1");
        lChain2 = reg("lChain2", c + "lArm/lForearm/lShackle/lChain1/lChain2");
        lHand = reg("lHand", c + "lArm/lForearm/lHand");
        for (int i = 0; i < 4; i++) lFingers[i] = reg(LF[i], c + "lArm/lForearm/lHand/" + LF[i]);
        rArm2 = reg("rArm2", c + "rArm2");
        rForearm2 = reg("rForearm2", c + "rArm2/rForearm2");
        rClaw2a = reg("rClaw2a", c + "rArm2/rForearm2/rClaw2a");
        rClaw2b = reg("rClaw2b", c + "rArm2/rForearm2/rClaw2b");
        lArm2 = reg("lArm2", c + "lArm2");
        lForearm2 = reg("lForearm2", c + "lArm2/lForearm2");
        lClaw2a = reg("lClaw2a", c + "lArm2/lForearm2/lClaw2a");
        lClaw2b = reg("lClaw2b", c + "lArm2/lForearm2/lClaw2b");
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

        PartDefinition hips = root.addOrReplaceChild("hips", CubeListBuilder.create().texOffs(30, 0).addBox(-3.5F, -2.0F, -2.5F, 7, 4, 5), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition skirt = hips.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 0.0F, -3.0F, 9, 9, 6), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition spine = hips.addOrReplaceChild("spine", CubeListBuilder.create().texOffs(0, 15).addBox(-3.0F, -8.0F, -2.0F, 6, 8, 4), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.35F, 0.0F, 0.0F));
        PartDefinition chest = spine.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(20, 15).addBox(-3.5F, -6.0F, -2.5F, 7, 6, 5), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.21F, 0.0F, 0.0F));
        PartDefinition tumor = chest.addOrReplaceChild("tumor", CubeListBuilder.create().texOffs(44, 15).addBox(-3.0F, -4.0F, 0.0F, 6, 7, 4), PartPose.offsetAndRotation(1.0F, -2.0F, 2.0F, 0.0F, 0.0F, 0.12F));
        PartDefinition tumor2 = tumor.addOrReplaceChild("tumor2", CubeListBuilder.create().texOffs(64, 15).addBox(-2.0F, -2.0F, 0.0F, 4, 4, 3), PartPose.offsetAndRotation(-2.5F, 1.0F, 2.5F, 0.2F, -0.3F, 0.0F));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(120, 0).addBox(-1.0F, -3.0F, -1.0F, 2, 3, 2), PartPose.offsetAndRotation(0.0F, -6.0F, -0.5F, -0.26F, 0.0F, 0.0F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(54, 0).addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6), PartPose.offsetAndRotation(0.0F, -2.5F, -0.5F, -0.17F, 0.0F, 0.0F));
        PartDefinition hairBack = head.addOrReplaceChild("hairBack", CubeListBuilder.create().texOffs(78, 0).addBox(-3.5F, 0.0F, 0.0F, 7, 11, 1), PartPose.offsetAndRotation(0.0F, -6.0F, 3.0F, 0.2F, 0.0F, 0.0F));
        PartDefinition hairFront = head.addOrReplaceChild("hairFront", CubeListBuilder.create().texOffs(94, 0).addBox(-3.0F, 0.0F, -0.5F, 6, 8, 1), PartPose.offset(0.0F, -5.5F, -3.3F));
        PartDefinition hairL = head.addOrReplaceChild("hairL", CubeListBuilder.create().texOffs(108, 0).addBox(-0.5F, 0.0F, -2.5F, 1, 9, 5), PartPose.offset(3.3F, -6.0F, 0.3F));
        PartDefinition hairR = head.addOrReplaceChild("hairR", CubeListBuilder.create().texOffs(108, 0).mirror().addBox(-0.5F, 0.0F, -2.5F, 1, 9, 5), PartPose.offset(-3.3F, -6.0F, 0.3F));

        // right arm: rope-thin, hangs to the floor
        PartDefinition rArm = chest.addOrReplaceChild("rArm", CubeListBuilder.create().texOffs(94, 15).addBox(-1.0F, -0.5F, -1.0F, 2, 11, 2), PartPose.offsetAndRotation(-4.5F, -5.0F, 0.0F, -0.7F, 0.0F, 0.12F));
        PartDefinition rForearm = rArm.addOrReplaceChild("rForearm", CubeListBuilder.create().texOffs(102, 15).addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2), PartPose.offset(0.0F, 10.5F, 0.0F));
        PartDefinition rShackle = rForearm.addOrReplaceChild("rShackle", CubeListBuilder.create().texOffs(110, 22).addBox(-1.5F, 0.0F, -1.5F, 3, 2, 3), PartPose.offset(0.0F, 8.5F, 0.0F));
        PartDefinition rChain1 = rShackle.addOrReplaceChild("rChain1", CubeListBuilder.create().texOffs(124, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offset(0.0F, 2.0F, 1.0F));
        PartDefinition rChain2 = rChain1.addOrReplaceChild("rChain2", CubeListBuilder.create().texOffs(124, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition rHand = rForearm.addOrReplaceChild("rHand", CubeListBuilder.create().texOffs(110, 15).addBox(-1.5F, 0.0F, -1.0F, 3, 3, 2), PartPose.offset(0.0F, 10.5F, 0.0F));
        float[] fx = {-1.2F, -0.4F, 0.4F, 1.2F};
        for (int i = 0; i < 4; i++) {
            PartDefinition f = rHand.addOrReplaceChild(RF[i], CubeListBuilder.create().texOffs(120, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(fx[i], 2.5F, -0.3F, 0.0F, 0.0F, (i - 1.5F) * 0.08F));
        }
        // left arm: swollen upper arm
        PartDefinition lArm = chest.addOrReplaceChild("lArm", CubeListBuilder.create().texOffs(78, 15).addBox(-1.0F, -1.0F, -2.0F, 4, 11, 4), PartPose.offsetAndRotation(3.5F, -5.0F, 0.0F, -0.7F, 0.0F, -0.15F));
        PartDefinition lForearm = lArm.addOrReplaceChild("lForearm", CubeListBuilder.create().texOffs(102, 15).mirror().addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2), PartPose.offset(1.0F, 10.0F, 0.0F));
        PartDefinition lShackle = lForearm.addOrReplaceChild("lShackle", CubeListBuilder.create().texOffs(110, 22).addBox(-1.5F, 0.0F, -1.5F, 3, 2, 3), PartPose.offset(0.0F, 8.5F, 0.0F));
        PartDefinition lChain1 = lShackle.addOrReplaceChild("lChain1", CubeListBuilder.create().texOffs(124, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offset(0.0F, 2.0F, 1.0F));
        PartDefinition lChain2 = lChain1.addOrReplaceChild("lChain2", CubeListBuilder.create().texOffs(124, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition lHand = lForearm.addOrReplaceChild("lHand", CubeListBuilder.create().texOffs(110, 15).mirror().addBox(-1.5F, 0.0F, -1.0F, 3, 3, 2), PartPose.offset(0.0F, 10.5F, 0.0F));
        for (int i = 0; i < 4; i++) {
            PartDefinition f = lHand.addOrReplaceChild(LF[i], CubeListBuilder.create().texOffs(120, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(fx[i], 2.5F, -0.3F, 0.0F, 0.0F, (i - 1.5F) * 0.08F));
        }
        // form 1: extra arms bursting from the ribs
        PartDefinition rArm2 = chest.addOrReplaceChild("rArm2", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, 0.0F, -0.5F, 1, 11, 1), PartPose.offsetAndRotation(-3.2F, -2.0F, -1.0F, -0.4F, 0.0F, 0.5F));
        PartDefinition rForearm2 = rArm2.addOrReplaceChild("rForearm2", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, 0.0F, -0.5F, 1, 11, 1), PartPose.offsetAndRotation(0.0F, 10.5F, 0.0F, -0.4F, 0.0F, 0.0F));
        PartDefinition rClaw2a = rForearm2.addOrReplaceChild("rClaw2a", CubeListBuilder.create().texOffs(120, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(-0.3F, 10.5F, 0.0F, 0.0F, 0.0F, 0.25F));
        PartDefinition rClaw2b = rForearm2.addOrReplaceChild("rClaw2b", CubeListBuilder.create().texOffs(120, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(0.3F, 10.5F, 0.0F, 0.0F, 0.0F, -0.25F));
        PartDefinition lArm2 = chest.addOrReplaceChild("lArm2", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-0.5F, 0.0F, -0.5F, 1, 11, 1), PartPose.offsetAndRotation(3.2F, -2.0F, -1.0F, -0.4F, 0.0F, -0.5F));
        PartDefinition lForearm2 = lArm2.addOrReplaceChild("lForearm2", CubeListBuilder.create().texOffs(0, 27).mirror().addBox(-0.5F, 0.0F, -0.5F, 1, 11, 1), PartPose.offsetAndRotation(0.0F, 10.5F, 0.0F, -0.4F, 0.0F, 0.0F));
        PartDefinition lClaw2a = lForearm2.addOrReplaceChild("lClaw2a", CubeListBuilder.create().texOffs(120, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(-0.3F, 10.5F, 0.0F, 0.0F, 0.0F, 0.25F));
        PartDefinition lClaw2b = lForearm2.addOrReplaceChild("lClaw2b", CubeListBuilder.create().texOffs(120, 15).addBox(-0.5F, 0.0F, -0.5F, 1, 5, 1), PartPose.offsetAndRotation(0.3F, 10.5F, 0.0F, 0.0F, 0.0F, -0.25F));

        PartDefinition rThigh = hips.addOrReplaceChild("rThigh", CubeListBuilder.create().texOffs(4, 27).addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2), PartPose.offset(-1.8F, 1.0F, 0.0F));
        PartDefinition rShin = rThigh.addOrReplaceChild("rShin", CubeListBuilder.create().texOffs(12, 27).addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition rFoot = rShin.addOrReplaceChild("rFoot", CubeListBuilder.create().texOffs(20, 27).addBox(-1.0F, 0.0F, -2.5F, 2, 1, 3), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition lThigh = hips.addOrReplaceChild("lThigh", CubeListBuilder.create().texOffs(4, 27).mirror().addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2), PartPose.offset(1.8F, 1.0F, 0.0F));
        PartDefinition lShin = lThigh.addOrReplaceChild("lShin", CubeListBuilder.create().texOffs(12, 27).mirror().addBox(-1.0F, 0.0F, -1.0F, 2, 6, 2), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition lFoot = lShin.addOrReplaceChild("lFoot", CubeListBuilder.create().texOffs(20, 27).mirror().addBox(-1.0F, 0.0F, -2.5F, 2, 1, 3), PartPose.offset(0.0F, 6.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 64);
    }

    // ================================================================== shamble / idle
    @Override
    protected void baseAnim(LisaTrevorEntity e, float ls, float la, float age, float yaw, float pitch) {
        boolean mutated = e.getForm() == LisaTrevorEntity.FORM_MUTATED;
        float amt = Math.min(la, 1f);
        float ph = ls * 0.55f;
        float s = Mth.sin(ph), c = Mth.cos(ph);
        float lag = Mth.sin(ph - 0.9f), lag2 = Mth.sin(ph - 1.6f);
        float breath = Mth.sin(age * 0.09f);
        // involuntary twitches: sharp spikes of a high power sine
        float twitch = (float) Math.pow(Math.max(0f, Mth.sin(age * 0.23f)), 40);

        // limping shamble: right leg steps, left leg drags with a stiff knee and scraping toe
        rThigh.xRot += s * 0.55f * amt;
        rShin.xRot += Math.max(0f, -c) * 0.7f * amt;
        rFoot.xRot -= s * 0.2f * amt;
        lThigh.xRot -= s * 0.3f * amt;
        lShin.xRot += Math.max(0f, c) * 0.2f * amt;
        lFoot.xRot += 0.25f * amt;
        // lurching pelvis: big drop and roll onto the good leg
        hips.y += Math.max(0f, s) * 1.2f * amt;
        hips.zRot += s * 0.12f * amt;
        hips.yRot += s * 0.08f * amt;
        skirt.xRot += Math.abs(s) * 0.12f * amt;
        skirt.zRot -= s * 0.06f * amt;
        spine.zRot -= s * 0.08f * amt;
        spine.xRot += (mutated ? 0.25f : 0f) + 0.05f * amt;
        chest.y += breath * 0.2f;
        chest.xRot += breath * 0.02f;
        // pendulum arms, lagging behind the body sway; the chain lags further
        rArm.xRot += lag * 0.22f * amt;
        rArm.zRot += lag * 0.06f * amt;
        lArm.xRot -= lag * 0.18f * amt;
        rForearm.xRot -= 0.1f + Math.max(0f, lag) * 0.15f * amt;
        lForearm.xRot -= 0.1f + Math.max(0f, -lag) * 0.15f * amt;
        rChain1.xRot += 0.7f + lag2 * 0.35f * amt + Mth.sin(age * 0.1f) * 0.05f;
        lChain1.xRot += 0.7f - lag2 * 0.35f * amt + Mth.cos(age * 0.1f) * 0.05f;
        rChain2.xRot -= 0.3f + lag2 * 0.2f * amt;
        lChain2.xRot -= 0.3f - lag2 * 0.2f * amt;
        // fingertips drag on the floor and bend back
        for (int i = 0; i < 4; i++) {
            float wig = Mth.sin(age * 0.17f + i * 1.3f) * 0.08f;
            rFingers[i].xRot += 0.35f * amt + wig - 0.25f;
            lFingers[i].xRot += 0.35f * amt - wig - 0.25f;
        }
        rHand.xRot += 0.25f * amt;
        lHand.xRot += 0.25f * amt;
        // head lolls, tracks the player only loosely; hair swings
        neck.yRot += yaw * DEG * 0.2f;
        head.yRot += yaw * DEG * 0.5f;
        head.xRot += pitch * DEG * 0.4f + Math.abs(s) * 0.08f * amt;
        head.zRot += Mth.sin(age * 0.05f) * 0.12f + s * 0.1f * amt + twitch * 0.5f;
        hairFront.xRot += -0.05f + Math.abs(s) * 0.12f * amt - head.xRot * 0.3f;
        hairFront.zRot -= head.zRot * 0.5f;
        hairBack.xRot += Math.abs(lag) * 0.1f * amt;
        hairL.zRot -= Math.max(0f, s) * 0.1f * amt;
        hairR.zRot += Math.max(0f, -s) * 0.1f * amt;
        rArm.yRot += twitch * 0.3f;
        // the flesh mass pulses like a second heart
        float beat = (float) Math.pow(Math.max(0f, Mth.sin(age * 0.33f)), 4);
        float tp = 1f + 0.07f * beat;
        tumor.xScale = tp; tumor.yScale = 1f + 0.05f * beat; tumor.zScale = tp + 0.03f * beat;
        float tp2 = 1f + 0.1f * (float) Math.pow(Math.max(0f, Mth.sin(age * 0.33f - 0.8f)), 4);
        tumor2.xScale = tp2; tumor2.yScale = tp2; tumor2.zScale = tp2;

        rArm2.visible = lArm2.visible = mutated;
        if (mutated) {
            // the extra arms never rest: they flex and grope independently
            rArm2.xRot += Mth.sin(age * 0.21f) * 0.25f + lag * 0.15f * amt;
            lArm2.xRot += Mth.sin(age * 0.19f + 2f) * 0.25f - lag * 0.15f * amt;
            rForearm2.xRot += Mth.sin(age * 0.27f + 1f) * 0.3f;
            lForearm2.xRot += Mth.sin(age * 0.25f + 3f) * 0.3f;
            float open = Math.max(0f, Mth.sin(age * 0.4f)) * 0.4f;
            rClaw2a.zRot += open; rClaw2b.zRot -= open;
            lClaw2a.zRot += open; lClaw2b.zRot -= open;
            head.xRot -= 0.15f;
        }
    }

    @Override
    protected AnimClip clip(LisaTrevorEntity e, int moveId) { return CLIPS.get(moveId); }

    /** Per-digit keys (t, curl, spread)...; digit i lags by 0.008*i; spread scales with offset from the hand center; key at impact snaps. */
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
    /** (1) long-arm swipe: torso coils, the arm whips out sideways with a lagging forearm crack; chains and hair fly. */
    private static AnimClip armSwipe() {
        float p = 0.12f, a = 0.45f, h = 17f / 30f, f = 0.72f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").rot(0, 0, 0, 0).rot(a, 0, 30, -5).rot(h, 0, -25, 5, SNAP).rot(f, 0, -32, 5, OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(a, 0.8f, 1, 0).pos(h, -0.8f, 1, -1, SNAP).pos(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(a, -5, 15, 0).rot(h, 5, -18, 0, SNAP).rot(f, 5, -22, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(p, 0, 8, 0).rot(a, -10, 35, 8).rot(h, 8, -40, -8, SNAP).rot(f, 10, -48, -8, OUT).rot(0.88f, 3, -10, 0).rot(1, 0, 0, 0);
        b.bone("tumor").scale(0, 1, 1, 1).scale(a, 1.05f, 1.05f, 1.05f).scale(h, 1.12f, 1.1f, 1.12f, SNAP).scale(f, 1, 1, 1).scale(1, 1, 1, 1);
        b.bone("neck").rot(0, 0, 0, 0).rot(a, 0, -15, 0).rot(h, 0, 15, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(a, -10, -25, 8).rot(h, 5, 20, -8, SNAP).rot(f, 5, 25, -10).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(a, -10, 0, 10).rot(h, -25, 0, -15, SNAP).rot(f, 15, 0, 10).rot(0.88f, -5, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairL").rot(0, 0, 0, 0).rot(h, 0, 0, 25, SNAP).rot(f, 0, 0, -10).rot(1, 0, 0, 0);
        b.bone("hairR").rot(0, 0, 0, 0).rot(h, 0, 0, -25, SNAP).rot(f, 0, 0, 10).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(p, 10, 10, 10).rot(a, -85, 80, 0).rot(h, -90, -50, 0, SNAP).rot(f, -80, -75, 0, OUT).rot(0.88f, -30, -20, 0).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(a, -60, 0, 0).rot(0.52f, -50, 0, 0).rot(h, 5, 0, 0, SNAP).rot(0.64f, 12, 0, 0).rot(f, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(a, -30, 0, 0).rot(h, 25, 0, 0, SNAP).rot(f, 10, 0, 0).rot(1, 0, 0, 0);
        digits(b, RF, h, 0, 0, 0, a, -40, 6, h, 30, 20, f, 20, 16, 0.88f, -10, 4, 1, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(a, 20, 0, 25).rot(h, -50, 0, -35, SNAP).rot(f, 40, 0, 30).rot(0.88f, -15, 0, -10).rot(1, 0, 0, 0);
        b.bone("rChain2").rot(0, 0, 0, 0).rot(a, 15, 0, 0).rot(h, -30, 0, 0).rot(f, 35, 0, 0).rot(0.9f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(a, 20, 0, -25).rot(h, -30, 0, -15, SNAP).rot(f, -35, 0, -12).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(a, -20, 0, 0).rot(h, -40, 0, 0).rot(1, 0, 0, 0);
        digits(b, LF, h, 0, 0, 0, a, -30, 4, h, -60, 0, 1, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(a, -10, 0, 4).rot(h, -25, -10, 6, SNAP).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(a, 20, 0, 0).rot(h, 35, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(a, -20, 0, -4).rot(h, 10, 10, -6).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(a, 35, 0, 0).rot(h, 15, 0, 0).rot(1, 0, 0, 0);
        b.bone("skirt").rot(0, 0, 0, 0).rot(a, 0, 0, -8).rot(h, 0, 0, 10).rot(f, 0, 0, 12).rot(0.88f, 0, 0, -4).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (2) grab-and-hold: both arms shoot out, fingers clamp, drags the victim to her face and squeezes in pulses. */
    private static AnimClip grabHold() {
        float o = 0.12f, g = 14f / 64f, d = 0.32f, s1 = 26f / 64f, s2 = 38f / 64f, s3 = 50f / 64f, rel = 58f / 64f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(g, 0, 1, -1.5f, SNAP).pos(d, 0, 1.5f, 1).pos(rel, 0, 1, 0).pos(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(o, 10, 0, 0).rot(g, 20, 0, 0, SNAP).rot(d, -5, 0, 0).rot(s1, 0, 0, 4).rot(s2, 0, 0, -4).rot(s3, 0, 0, 4).rot(rel, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(g, 10, 0, 0, SNAP).rot(d, -8, 0, 0).rot(0.37f, -8, 0, 0).rot(s1, 4, 0, 0, SNAP).rot(0.5f, -8, 0, 0).rot(s2, 4, 0, 0, SNAP)
            .rot(0.7f, -8, 0, 0).rot(s3, 4, 0, 0, SNAP).rot(rel, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("tumor").scale(0, 1, 1, 1).scale(0.37f, 1, 1, 1).scale(s1, 1.15f, 1.12f, 1.15f, SNAP).scale(0.5f, 1, 1, 1).scale(s2, 1.15f, 1.12f, 1.15f, SNAP)
            .scale(0.7f, 1, 1, 1).scale(s3, 1.18f, 1.15f, 1.18f, SNAP).scale(rel, 1, 1, 1).scale(1, 1, 1, 1);
        b.bone("head").rot(0, 0, 0, 0).rot(o, -15, 0, 0).rot(g, -20, 0, 0).rot(d, -5, 0, 25).rot(s1, 0, 0, -20).rot(s2, 0, 0, 25).rot(s3, -10, 0, -15).rot(rel, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(g, -20, 0, 0).rot(d, 5, 0, -15).rot(s2, 5, 0, 15).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(o, -60, 10, 0).rot(g, -95, 10, 0, SNAP).rot(d, -70, 25, 0).rot(s1, -72, 25, 0).rot(s3, -70, 25, 0).rot(rel, -50, 0, 10).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(o, -20, 0, 0).rot(g, 0, 0, 0, SNAP).rot(d, -75, 0, 0).rot(s3, -80, 0, 0).rot(rel, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(g, -20, 0, 0).rot(d, -30, 0, 0).rot(rel, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(o, -60, -10, 0).rot(g, -95, -10, 0, SNAP).rot(d, -70, -25, 0).rot(s1, -72, -25, 0).rot(s3, -70, -25, 0).rot(rel, -50, 0, -10).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(o, -20, 0, 0).rot(g, 0, 0, 0, SNAP).rot(d, -75, 0, 0).rot(s3, -80, 0, 0).rot(rel, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lHand").rot(0, 0, 0, 0).rot(g, -20, 0, 0).rot(d, -30, 0, 0).rot(rel, 20, 0, 0).rot(1, 0, 0, 0);
        digits(b, RF, g, 0, 0, 0, o, 30, 18, g, -90, 0, 0.37f, -80, 0, s1, -105, 0, 0.5f, -85, 0, s2, -108, 0, 0.7f, -85, 0, s3, -110, 0, rel, 25, 16, 1, 0, 0);
        digits(b, LF, g, 0, 0, 0, o, 30, 18, g, -90, 0, 0.37f, -80, 0, s1, -105, 0, 0.5f, -85, 0, s2, -108, 0, 0.7f, -85, 0, s3, -110, 0, rel, 25, 16, 1, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(o, -40, 0, 0).rot(g, -60, 0, 0).rot(d, 20, 0, 0).rot(rel, 30, 0, 0).rot(0.95f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("lChain1").rot(0, 0, 0, 0).rot(o, -40, 0, 0).rot(g, -60, 0, 0).rot(d, 20, 0, 0).rot(rel, 30, 0, 0).rot(0.95f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(o, -10, 0, 0).rot(g, -30, 0, 5).rot(rel, -25, 0, 5).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(g, 50, 0, 0).rot(rel, 45, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(g, 10, 0, -5).rot(rel, 10, 0, -5).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(g, 15, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (3) fling: lunging one-hand grab, hoists the victim in an arc over her head and hurls it forward. */
    private static AnimClip fling() {
        float w = 0.15f, g = 16f / 48f, hoist = 0.45f, top = 0.60f, th = 34f / 48f, f = 0.85f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").rot(0, 0, 0, 0).rot(w, 0, 15, 0).rot(g, 0, -15, 0, SNAP).rot(top, 0, 30, 0).rot(th, 0, -35, 0, SNAP).rot(f, 0, -40, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(g, 0, 1.5f, -2, SNAP).pos(top, 0, 0, 1).pos(th, 0, 1.5f, -2, SNAP).pos(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(w, -5, 10, 0).rot(g, 25, -10, 0, SNAP).rot(hoist, -5, 0, 0).rot(top, -20, 20, 0).rot(th, 30, -25, 0, SNAP).rot(f, 32, -28, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(w, -5, 15, 0).rot(g, 15, -20, 0, SNAP).rot(hoist, -10, 5, 0).rot(top, -25, 30, -5).rot(th, 20, -40, 5, SNAP).rot(f, 22, -45, 5).rot(1, 0, 0, 0);
        b.bone("tumor").scale(0, 1, 1, 1).scale(top, 1.1f, 1.1f, 1.1f).scale(th, 1.15f, 1.12f, 1.15f, SNAP).scale(1, 1, 1, 1);
        b.bone("head").rot(0, 0, 0, 0).rot(g, -25, -10, 0).rot(hoist, -30, 0, 10).rot(top, -35, 15, -10).rot(th, 0, 25, 10, SNAP).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(top, -35, 0, 0).rot(th, 30, 0, 0, SNAP).rot(f, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairBack").rot(0, 0, 0, 0).rot(top, 20, 0, 0).rot(th, -15, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(w, 15, 0, 15).rot(g, -100, -5, 0, SNAP).rot(hoist, -150, 0, 10).rot(top, -185, 20, 20).rot(th, -30, -40, 0, SNAP).rot(f, -20, -45, -5, OUT).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(w, -30, 0, 0).rot(g, 0, 0, 0, SNAP).rot(hoist, -40, 0, 0).rot(top, -60, 0, 0).rot(th, 5, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("rHand").rot(0, 0, 0, 0).rot(g, -20, 0, 0).rot(th, 30, 0, 0).rot(1, 0, 0, 0);
        digits(b, RF, g, 0, 0, 0, w, 20, 16, g, -95, 0, top, -100, 0, th, 35, 20, f, 25, 14, 1, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(w, 30, 0, 0).rot(g, -60, 0, 0).rot(top, 50, 0, 20).rot(th, -70, 0, -20).rot(f, 30, 0, 0).rot(0.95f, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rChain2").rot(0, 0, 0, 0).rot(g, -30, 0, 0).rot(top, 30, 0, 0).rot(th, -40, 0, 0).rot(f, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(w, -20, 0, -20).rot(g, 30, 0, -25).rot(top, -40, 0, -40).rot(th, 40, 0, -30).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(g, -30, 0, 0).rot(th, -50, 0, 0).rot(1, 0, 0, 0);
        digits(b, LF, th, 0, 0, 0, g, -40, 6, th, -70, 0, 1, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(g, -35, 0, -4).rot(th, -40, 0, -4).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(g, 45, 0, 0).rot(th, 50, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(g, 25, 0, 0).rot(top, -10, 0, 0).rot(th, 30, 0, 0).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(g, 10, 0, 0).rot(top, 25, 0, 0).rot(th, 10, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (4) pounce: drops onto all fours, skitters forward, leaps stretched out, rakes three times on landing. */
    private static AnimClip pounce() {
        float dn = 0.15f, cr = 0.42f, lp = 0.45f, h1 = 22f / 44f, h2 = 26f / 44f, h3 = 30f / 44f, up = 0.85f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(dn, 0, 6, 0).pos(cr, 0, 6, 0).pos(lp, 0, 2, -3).pos(h1, 0, 6, -3, SNAP).pos(h3, 0, 6, -3).pos(up, 0, 3, 0).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(dn, 25, 0, 0).rot(cr, 25, 0, 0).rot(lp, 10, 0, 0).rot(h1, 25, 0, 0).rot(h3, 25, 0, 0).rot(up, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(dn, 45, 0, 0).rot(0.25f, 45, 0, 6).rot(0.33f, 45, 0, -6).rot(cr, 45, 0, 6).rot(lp, 35, 0, 0).rot(h1, 50, 0, 0).rot(h3, 50, 0, 0).rot(up, 20, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(dn, 10, 0, 0).rot(lp, 0, 0, 0).rot(h1, 15, 0, 0).rot(h2, 15, 10, 0).rot(h3, 15, -10, 0).rot(up, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("tumor").scale(0, 1, 1, 1).scale(lp, 1.1f, 1.1f, 1.1f).scale(h1, 1.18f, 1.15f, 1.18f, SNAP).scale(up, 1, 1, 1).scale(1, 1, 1, 1);
        b.bone("neck").rot(0, 0, 0, 0).rot(dn, -30, 0, 0).rot(h3, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(dn, -40, 0, 0).rot(0.25f, -40, 0, 10).rot(0.33f, -40, 0, -10).rot(lp, -45, 0, 0).rot(h1, -35, 0, 0).rot(h3, -35, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(dn, -45, 0, 0).rot(lp, -60, 0, 0).rot(h1, -30, 0, 0).rot(up, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairBack").rot(0, 0, 0, 0).rot(dn, -40, 0, 0).rot(lp, -60, 0, 0).rot(up, -10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(dn, -60, 0, 10).rot(0.20f, -90, 0, 10).rot(0.25f, -40, 0, 10).rot(0.30f, -90, 0, 10).rot(0.35f, -40, 0, 10).rot(0.40f, -90, 0, 10)
            .rot(lp, -170, 0, 10).rot(h1, -60, 0, 10, SNAP).rot(0.55f, -130, 0, 10).rot(h2, -55, 0, 10, SNAP).rot(h3, -110, 0, 10).rot(up, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(dn, -60, 0, -10).rot(0.20f, -40, 0, -10).rot(0.25f, -90, 0, -10).rot(0.30f, -40, 0, -10).rot(0.35f, -90, 0, -10).rot(0.40f, -40, 0, -10)
            .rot(lp, -170, 0, -10).rot(h1, -60, 0, -10, SNAP).rot(h2, -120, 0, -10).rot(h3, -55, 0, -10, SNAP).rot(up, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(dn, -20, 0, 0).rot(cr, -20, 0, 0).rot(lp, 0, 0, 0).rot(h1, -40, 0, 0, SNAP).rot(h3, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(dn, -20, 0, 0).rot(cr, -20, 0, 0).rot(lp, 0, 0, 0).rot(h1, -40, 0, 0, SNAP).rot(h3, -30, 0, 0).rot(1, 0, 0, 0);
        digits(b, RF, h1, 0, 0, 0, dn, 40, 10, cr, 40, 10, lp, 20, 20, h1, -80, 0, 0.55f, 10, 14, h2, -85, 0, h3, -85, 0, up, -20, 0, 1, 0, 0);
        digits(b, LF, h1, 0, 0, 0, dn, 40, 10, cr, 40, 10, lp, 20, 20, h1, -80, 0, h2, 10, 14, h3, -85, 0, up, -20, 0, 1, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(lp, -70, 0, 0).rot(h1, 40, 0, 0).rot(0.6f, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lChain1").rot(0, 0, 0, 0).rot(lp, -70, 0, 0).rot(h1, 40, 0, 0).rot(0.6f, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(dn, -70, 0, 8).rot(0.25f, -60, 0, 8).rot(0.33f, -75, 0, 8).rot(cr, -65, 0, 8).rot(lp, 30, 0, 4).rot(h1, -70, 0, 8).rot(h3, -70, 0, 8).rot(up, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(dn, -70, 0, -8).rot(0.25f, -75, 0, -8).rot(0.33f, -60, 0, -8).rot(cr, -70, 0, -8).rot(lp, 30, 0, -4).rot(h1, -70, 0, -8).rot(h3, -70, 0, -8).rot(up, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(dn, 100, 0, 0).rot(cr, 100, 0, 0).rot(lp, 10, 0, 0).rot(h1, 100, 0, 0).rot(h3, 100, 0, 0).rot(up, 50, 0, 0).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(dn, 100, 0, 0).rot(cr, 100, 0, 0).rot(lp, 10, 0, 0).rot(h1, 100, 0, 0).rot(h3, 100, 0, 0).rot(up, 50, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(dn, -30, 0, 0).rot(lp, 30, 0, 0).rot(h1, -30, 0, 0).rot(up, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(dn, -30, 0, 0).rot(lp, 30, 0, 0).rot(h1, -30, 0, 0).rot(up, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("skirt").rot(0, 0, 0, 0).rot(dn, -30, 0, 0).rot(lp, -15, 0, 0).rot(h1, -35, 0, 0).rot(up, -10, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (5) wail: clutches her head, inhales, throws head back screaming with arms flung wide, trembling. */
    private static AnimClip wail() {
        float cl = 0.25f, in = 0.35f, h = 22f / 54f, e = 0.82f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(cl, 0, 2, 0).pos(h, 0, 0.5f, 0.5f, SNAP).pos(e, 0, 0.5f, 0.5f).pos(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(cl, 25, 0, 0).rot(in, 15, 0, 0).rot(h, -25, 0, 0, SNAP).rot(e, -22, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(cl, 15, 0, 0).rot(in, 5, 0, 0).rot(h, -30, 0, 0, SNAP).rot(0.5f, -32, 0, 3).rot(0.56f, -30, 0, -3).rot(0.62f, -32, 0, 3)
            .rot(0.68f, -30, 0, -3).rot(0.74f, -32, 0, 3).rot(e, -28, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(in, 1.1f, 1.06f, 1.12f).scale(h, 1, 1, 1, SNAP).scale(1, 1, 1, 1);
        b.bone("tumor").scale(0, 1, 1, 1).scale(in, 1.05f, 1.05f, 1.05f).scale(h, 1.25f, 1.2f, 1.25f, SNAP).scale(0.6f, 1.15f, 1.12f, 1.15f).scale(e, 1.2f, 1.18f, 1.2f).scale(1, 1, 1, 1);
        b.bone("neck").rot(0, 0, 0, 0).rot(cl, 20, 0, 0).rot(h, -30, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(cl, 30, 0, 0).rot(in, 25, 0, 0).rot(h, -50, 0, 0, SNAP).rot(0.5f, -52, 0, 8).rot(0.56f, -50, 0, -8).rot(0.62f, -52, 0, 8)
            .rot(0.68f, -50, 0, -8).rot(0.74f, -52, 0, 8).rot(e, -45, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(cl, 5, 0, 0).rot(h, -60, 0, 0, SNAP).rot(0.62f, -55, 0, 10).rot(e, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairBack").rot(0, 0, 0, 0).rot(h, 30, 0, 0, SNAP).rot(e, 25, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairL").rot(0, 0, 0, 0).rot(h, 0, 0, 30, SNAP).rot(e, 0, 0, 25).rot(1, 0, 0, 0);
        b.bone("hairR").rot(0, 0, 0, 0).rot(h, 0, 0, -30, SNAP).rot(e, 0, 0, -25).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(cl, -150, 0, -20).rot(in, -155, 0, -22).rot(h, 25, 0, 60, SNAP).rot(e, 25, 0, 58).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(cl, -100, 0, 0).rot(in, -100, 0, 0).rot(h, -10, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(cl, -150, 0, 20).rot(in, -155, 0, 22).rot(h, 25, 0, -60, SNAP).rot(e, 25, 0, -58).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(cl, -100, 0, 0).rot(in, -100, 0, 0).rot(h, -10, 0, 0, SNAP).rot(1, 0, 0, 0);
        digits(b, RF, h, 0, 0, 0, cl, -60, 0, in, -75, 0, h, 35, 22, e, 30, 20, 1, 0, 0);
        digits(b, LF, h, 0, 0, 0, cl, -60, 0, in, -75, 0, h, 35, 22, e, 30, 20, 1, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(h, 60, 0, 30, SNAP).rot(0.6f, 40, 0, 20).rot(e, 20, 0, 10).rot(1, 0, 0, 0);
        b.bone("lChain1").rot(0, 0, 0, 0).rot(h, 60, 0, -30, SNAP).rot(0.6f, 40, 0, -20).rot(e, 20, 0, -10).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(cl, -30, 0, 0).rot(h, -10, 0, 8, SNAP).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(cl, -30, 0, 0).rot(h, -10, 0, -8, SNAP).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(cl, 55, 0, 0).rot(h, 15, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(cl, 55, 0, 0).rot(h, 15, 0, 0, SNAP).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (6) chain lash: arms out like flails, two full 360 spins with hair, skirt and chains flaring; dizzy stagger. */
    private static AnimClip chainSpin() {
        float w = 0.25f, go = 0.35f, end = 0.75f, st = 0.85f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").rot(0, 0, 0, 0).rot(w, 0, 30, 0).rot(end, 0, -690, 0).rot(st, 0, -720, 0).rot(1, 0, -720, 0)
            .pos(0, 0, 0, 0).pos(w, 0, 1.5f, 0).pos(end, 0, 1, 0).pos(st, 1, 2, 0).pos(0.92f, -1, 1.5f, 0).pos(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(w, -5, 0, 0).rot(go, 10, 0, 8).rot(end, 10, 0, 8).rot(st, 15, 0, -10).rot(0.92f, 5, 0, 6).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(w, 0, 40, 0).rot(go, 0, -10, 0).rot(end, 0, -10, 0).rot(st, 10, 0, 5).rot(1, 0, 0, 0);
        b.bone("head").rot(0, 0, 0, 0).rot(w, -10, -30, 0).rot(go, -20, 20, 0).rot(end, -20, 20, 0).rot(st, 15, 0, 20).rot(0.92f, 10, 0, -15).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(w, 0, 0, 85).rot(end, 0, 0, 90).rot(st, -20, 0, 30).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(w, 0, 0, -85).rot(end, 0, 0, -90).rot(st, -20, 0, -30).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(w, -20, 0, 0).rot(go, 0, 0, 0).rot(end, 0, 0, 0).rot(st, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(w, -20, 0, 0).rot(go, 0, 0, 0).rot(end, 0, 0, 0).rot(st, -20, 0, 0).rot(1, 0, 0, 0);
        digits(b, RF, -1, 0, 0, 0, w, 20, 20, end, 20, 20, st, -30, 0, 1, 0, 0);
        digits(b, LF, -1, 0, 0, 0, w, 20, 20, end, 20, 20, st, -30, 0, 1, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(go, 0, 0, 80).rot(end, 0, 0, 85).rot(st, 0, 0, 0).rot(0.92f, 0, 0, 20).rot(1, 0, 0, 0);
        b.bone("lChain1").rot(0, 0, 0, 0).rot(go, 0, 0, -80).rot(end, 0, 0, -85).rot(st, 0, 0, 0).rot(0.92f, 0, 0, -20).rot(1, 0, 0, 0);
        b.bone("rChain2").rot(0, 0, 0, 0).rot(go, 0, 0, 10).rot(end, 0, 0, 10).rot(st, 0, 0, -20).rot(1, 0, 0, 0);
        b.bone("lChain2").rot(0, 0, 0, 0).rot(go, 0, 0, -10).rot(end, 0, 0, -10).rot(st, 0, 0, 20).rot(1, 0, 0, 0);
        b.bone("hairL").rot(0, 0, 0, 0).rot(go, 0, 0, 40).rot(end, 0, 0, 45).rot(st, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairR").rot(0, 0, 0, 0).rot(go, 0, 0, -40).rot(end, 0, 0, -45).rot(st, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairBack").rot(0, 0, 0, 0).rot(go, 50, 0, 0).rot(end, 55, 0, 0).rot(st, 0, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(go, -50, 0, 0).rot(end, -50, 0, 0).rot(st, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("skirt").scale(0, 1, 1, 1).scale(go, 1.25f, 0.9f, 1.25f).scale(end, 1.3f, 0.85f, 1.3f).scale(st, 1, 1, 1).scale(1, 1, 1, 1);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(go, -20, 0, 10).rot(0.45f, 10, 0, 10).rot(0.55f, -20, 0, 10).rot(0.65f, 10, 0, 10).rot(end, -10, 0, 8).rot(st, -25, 0, 15).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(go, 10, 0, -10).rot(0.45f, -20, 0, -10).rot(0.55f, 10, 0, -10).rot(0.65f, -20, 0, -10).rot(end, 5, 0, -8).rot(st, 10, 0, -6).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(go, 30, 0, 0).rot(0.45f, 10, 0, 0).rot(0.55f, 30, 0, 0).rot(0.65f, 10, 0, 0).rot(st, 35, 0, 0).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(go, 10, 0, 0).rot(0.45f, 30, 0, 0).rot(0.55f, 10, 0, 0).rot(0.65f, 30, 0, 0).rot(st, 10, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (7) flesh recoil: violent jolt, clutches her head, rocks back and forth while the flesh mass swells and heals. */
    private static AnimClip recoil() {
        float j = 0.10f, cl = 0.18f, h = 28f / 56f, let = 0.88f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(j, 0, 1, 1, SNAP).pos(cl, 0, 3, 0).pos(0.74f, 0, 3, 0).pos(let, 0, 1, 0).pos(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(j, -20, 0, 0, SNAP).rot(cl, 25, 0, 0).rot(0.3f, 35, 0, 5).rot(0.42f, 15, 0, -5).rot(h, 40, 0, 0).rot(0.62f, 15, 0, 5).rot(0.74f, 35, 0, -5).rot(let, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(j, -15, 0, 8, SNAP).rot(cl, 10, 0, 0).rot(0.3f, 15, 10, 0).rot(0.42f, 5, -10, 0).rot(h, 20, 0, 0).rot(0.62f, 5, 10, 0).rot(0.74f, 15, -10, 0).rot(let, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("tumor").scale(0, 1, 1, 1).scale(j, 1.2f, 1.2f, 1.2f, SNAP).scale(0.3f, 1.05f, 1.05f, 1.05f).scale(h, 1.3f, 1.25f, 1.3f).scale(0.62f, 1.05f, 1.05f, 1.05f).scale(0.74f, 1.15f, 1.15f, 1.15f).scale(1, 1, 1, 1);
        b.bone("tumor2").scale(0, 1, 1, 1).scale(j, 1.3f, 1.3f, 1.3f, SNAP).scale(0.3f, 1.1f, 1.1f, 1.1f).scale(h, 1.4f, 1.4f, 1.4f).scale(0.74f, 1.1f, 1.1f, 1.1f).scale(1, 1, 1, 1);
        b.bone("head").rot(0, 0, 0, 0).rot(j, -35, 0, 15, SNAP).rot(cl, 25, 0, 0).rot(0.3f, 30, 0, -15).rot(0.42f, 20, 0, 15).rot(h, 35, 0, 0).rot(0.62f, 20, 0, -15).rot(0.74f, 30, 0, 15).rot(let, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(j, -30, 0, 0, SNAP).rot(cl, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(j, -60, 0, 40, SNAP).rot(cl, -140, 0, -30).rot(0.74f, -140, 0, -30).rot(let, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(j, -60, 0, -40, SNAP).rot(cl, -140, 0, 30).rot(0.74f, -140, 0, 30).rot(let, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(cl, -110, 0, 0).rot(0.74f, -110, 0, 0).rot(let, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(cl, -110, 0, 0).rot(0.74f, -110, 0, 0).rot(let, -30, 0, 0).rot(1, 0, 0, 0);
        digits(b, RF, j, 0, 0, 0, j, 30, 20, cl, -70, 0, 0.3f, -90, 0, 0.42f, -60, 0, h, -100, 0, 0.62f, -60, 0, 0.74f, -90, 0, let, -20, 0, 1, 0, 0);
        digits(b, LF, j, 0, 0, 0, j, 30, 20, cl, -70, 0, 0.3f, -90, 0, 0.42f, -60, 0, h, -100, 0, 0.62f, -60, 0, 0.74f, -90, 0, let, -20, 0, 1, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(j, -50, 0, 0).rot(cl, 30, 0, 0).rot(0.3f, -10, 0, 0).rot(let, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("lChain1").rot(0, 0, 0, 0).rot(j, -50, 0, 0).rot(cl, 30, 0, 0).rot(0.3f, -10, 0, 0).rot(let, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(cl, -35, 0, 5).rot(0.74f, -35, 0, 5).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(cl, -35, 0, -5).rot(0.74f, -35, 0, -5).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(cl, 65, 0, 0).rot(0.74f, 65, 0, 0).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(cl, 65, 0, 0).rot(0.74f, 65, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }

    /** (8, form 1) four-arm flurry: main and rib arms alternate overhead downward slashes, torso twisting into each. */
    private static AnimClip quadFlurry() {
        float h1 = 12f / 40f, h2 = 18f / 40f, h3 = 24f / 40f, h4 = 30f / 40f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("rArm").rot(0, 0, 0, 0).rot(0.2f, -165, 0, 15).rot(h1, -35, -15, 0, SNAP).rot(0.4f, -30, -10, 0).rot(0.5f, -60, 0, 0).rot(0.85f, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(0.2f, -50, 0, 0).rot(h1, 0, 0, 0, SNAP).rot(0.5f, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("lArm2").rot(0, 0, 0, 0).rot(h1, -160, 0, -15).rot(h2, -30, 15, 0, SNAP).rot(0.55f, -30, 0, 0).rot(0.85f, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lForearm2").rot(0, 0, 0, 0).rot(h1, -50, 0, 0).rot(h2, 0, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(h1, -20, 0, -15).rot(0.48f, -165, 0, -15).rot(h3, -35, 15, 0, SNAP).rot(0.7f, -30, 0, 0).rot(0.85f, -40, 0, 0).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(0.48f, -50, 0, 0).rot(h3, 0, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("rArm2").rot(0, 0, 0, 0).rot(h2, -30, 0, 0).rot(0.62f, -160, 0, 15).rot(h4, -30, -15, 0, SNAP).rot(0.85f, -25, 0, 0).rot(1, 0, 0, 0);
        b.bone("rForearm2").rot(0, 0, 0, 0).rot(0.62f, -50, 0, 0).rot(h4, 0, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lClaw2a").rot(0, 0, 0, 0).rot(h1, 0, 0, 30).rot(h2, 0, 0, -5, SNAP).rot(1, 0, 0, 0);
        b.bone("lClaw2b").rot(0, 0, 0, 0).rot(h1, 0, 0, -30).rot(h2, 0, 0, 5, SNAP).rot(1, 0, 0, 0);
        b.bone("rClaw2a").rot(0, 0, 0, 0).rot(0.62f, 0, 0, 30).rot(h4, 0, 0, -5, SNAP).rot(1, 0, 0, 0);
        b.bone("rClaw2b").rot(0, 0, 0, 0).rot(0.62f, 0, 0, -30).rot(h4, 0, 0, 5, SNAP).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(0.2f, -10, 25, 0).rot(h1, 15, -20, 0, SNAP).rot(0.38f, -5, -20, 0).rot(h2, 15, 15, 0, SNAP).rot(0.52f, -8, -25, 0)
            .rot(h3, 15, 20, 0, SNAP).rot(0.68f, -5, 25, 0).rot(h4, 15, -15, 0, SNAP).rot(0.85f, 5, 0, 0).rot(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(0.2f, -5, 0, 0).rot(h1, 10, 0, 0).rot(h4, 10, 0, 0).rot(1, 0, 0, 0);
        b.bone("tumor").scale(0, 1, 1, 1).scale(h1, 1.12f, 1.1f, 1.12f, SNAP).scale(0.38f, 1, 1, 1).scale(h2, 1.12f, 1.1f, 1.12f, SNAP).scale(0.52f, 1, 1, 1)
            .scale(h3, 1.12f, 1.1f, 1.12f, SNAP).scale(0.68f, 1, 1, 1).scale(h4, 1.12f, 1.1f, 1.12f, SNAP).scale(1, 1, 1, 1);
        b.bone("head").rot(0, 0, 0, 0).rot(h1, 10, 0, 15).rot(h2, 10, 0, -15).rot(h3, 10, 0, 15).rot(h4, 10, 0, -15).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(h1, -15, 0, 15).rot(h2, -15, 0, -15).rot(h3, -15, 0, 15).rot(h4, -15, 0, -15).rot(1, 0, 0, 0);
        b.bone("hips").pos(0, 0, 0, 0).pos(h1, 0, 1.5f, -1).pos(h4, 0, 1.5f, -1).pos(1, 0, 0, 0)
            .rot(0, 0, 0, 0).rot(h1, 0, -8, 0).rot(h2, 0, 8, 0).rot(h3, 0, -8, 0).rot(h4, 0, 8, 0).rot(1, 0, 0, 0);
        digits(b, RF, h1, 0, 0, 0, 0.2f, 30, 20, h1, -70, 0, 0.5f, -20, 6, 1, 0, 0);
        digits(b, LF, h3, 0, 0, 0, 0.48f, 30, 20, h3, -70, 0, 0.8f, -20, 6, 1, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(h1, -25, 0, 5).rot(h4, -25, 0, 5).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(h1, 40, 0, 0).rot(h4, 40, 0, 0).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(h1, 10, 0, -5).rot(h4, 10, 0, -5).rot(1, 0, 0, 0);
        return b.build();
    }

    // ================================================================== transformation
    /** form 0 -> 1: collapses to her knees convulsing and hugging herself, then arches back as two rib arms tear out. */
    private static AnimClip mutate() {
        float k = 0.10f, cv = 0.45f, h = 35f / 70f, un = 0.65f, sc = 0.85f;
        AnimClip.Builder b = AnimClip.create();
        b.bone("hips").pos(0, 0, 0, 0).pos(k, 0, 7, 0).pos(cv, 0, 7, 0).pos(h, 0, 3, 0, SNAP).pos(0.8f, 0, 1.5f, 0).pos(1, 0, 0, 0);
        b.bone("rThigh").rot(0, 0, 0, 0).rot(k, -62, 0, 6).rot(cv, -62, 0, 6).rot(h, -40, 0, 8, SNAP).rot(0.8f, -28, 0, 6).rot(1, 0, 0, 0);
        b.bone("lThigh").rot(0, 0, 0, 0).rot(k, -62, 0, -6).rot(cv, -62, 0, -6).rot(h, -40, 0, -8, SNAP).rot(0.8f, -28, 0, -6).rot(1, 0, 0, 0);
        b.bone("rShin").rot(0, 0, 0, 0).rot(k, 125, 0, 0).rot(cv, 125, 0, 0).rot(h, 80, 0, 0, SNAP).rot(0.8f, 56, 0, 0).rot(1, 0, 0, 0);
        b.bone("lShin").rot(0, 0, 0, 0).rot(k, 125, 0, 0).rot(cv, 125, 0, 0).rot(h, 80, 0, 0, SNAP).rot(0.8f, 56, 0, 0).rot(1, 0, 0, 0);
        b.bone("rFoot").rot(0, 0, 0, 0).rot(k, -62, 0, 0).rot(h, -40, 0, 0, SNAP).rot(0.8f, -28, 0, 0).rot(1, 0, 0, 0);
        b.bone("lFoot").rot(0, 0, 0, 0).rot(k, -62, 0, 0).rot(h, -40, 0, 0, SNAP).rot(0.8f, -28, 0, 0).rot(1, 0, 0, 0);
        b.bone("spine").rot(0, 0, 0, 0).rot(k, 50, 0, 0).rot(0.15f, 55, 0, 8).rot(0.22f, 50, 0, -8).rot(0.3f, 58, 0, 8).rot(0.38f, 52, 0, -8).rot(cv, 60, 0, 0)
            .rot(h, -30, 0, 0, SNAP).rot(un, -35, 0, 0).rot(sc, -30, 0, 0).rot(1, 0, 0, 0);
        b.bone("chest").rot(0, 0, 0, 0).rot(k, 20, 0, 0).rot(cv, 25, 0, 0).rot(h, -30, 0, 0, SNAP).rot(sc, -25, 0, 0).rot(1, 0, 0, 0)
            .scale(0, 1, 1, 1).scale(cv, 0.92f, 1, 0.92f).scale(h, 1.15f, 1.05f, 1.15f, SNAP).scale(sc, 1.1f, 1.03f, 1.1f).scale(1, 1, 1, 1);
        b.bone("tumor").scale(0, 1, 1, 1).scale(0.15f, 1.1f, 1.1f, 1.1f).scale(0.22f, 1, 1, 1).scale(0.3f, 1.15f, 1.15f, 1.15f).scale(0.38f, 1.05f, 1.05f, 1.05f)
            .scale(cv, 1.2f, 1.2f, 1.2f).scale(h, 1.5f, 1.4f, 1.5f, SNAP).scale(un, 1.3f, 1.25f, 1.3f).scale(sc, 1.35f, 1.3f, 1.35f).scale(1, 1, 1, 1);
        b.bone("head").rot(0, 0, 0, 0).rot(k, 30, 0, 0).rot(0.15f, 30, 0, 20).rot(0.22f, 30, 0, -20).rot(0.3f, 30, 0, 20).rot(0.38f, 30, 0, -20).rot(cv, 35, 0, 0)
            .rot(h, -50, 0, 0, SNAP).rot(un, -55, 10, 0).rot(0.75f, -55, -10, 0).rot(sc, -50, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairFront").rot(0, 0, 0, 0).rot(cv, 10, 0, 0).rot(h, -60, 0, 0, SNAP).rot(sc, -45, 0, 0).rot(1, 0, 0, 0);
        b.bone("hairBack").rot(0, 0, 0, 0).rot(h, 35, 0, 0, SNAP).rot(sc, 25, 0, 0).rot(1, 0, 0, 0);
        b.bone("rArm").rot(0, 0, 0, 0).rot(k, -70, 0, -40).rot(cv, -75, 0, -45).rot(h, -20, 0, 80, SNAP).rot(sc, -20, 0, 75).rot(1, 0, 0, 0);
        b.bone("lArm").rot(0, 0, 0, 0).rot(k, -70, 0, 40).rot(cv, -75, 0, 45).rot(h, -20, 0, -80, SNAP).rot(sc, -20, 0, -75).rot(1, 0, 0, 0);
        b.bone("rForearm").rot(0, 0, 0, 0).rot(k, -100, 0, 0).rot(cv, -105, 0, 0).rot(h, -10, 0, 0, SNAP).rot(1, 0, 0, 0);
        b.bone("lForearm").rot(0, 0, 0, 0).rot(k, -100, 0, 0).rot(cv, -105, 0, 0).rot(h, -10, 0, 0, SNAP).rot(1, 0, 0, 0);
        digits(b, RF, h, 0, 0, 0, k, -80, 0, cv, -90, 0, h, 40, 22, sc, 30, 18, 1, 0, 0);
        digits(b, LF, h, 0, 0, 0, k, -80, 0, cv, -90, 0, h, 40, 22, sc, 30, 18, 1, 0, 0);
        b.bone("rArm2").scale(0, 0.1f, 0.1f, 0.1f).scale(0.48f, 0.1f, 0.1f, 0.1f).scale(h, 0.6f, 0.6f, 0.6f, SNAP).scale(un, 1.1f, 1.1f, 1.1f).scale(0.75f, 1, 1, 1).scale(1, 1, 1, 1)
            .rot(0, 0, 0, 0).rot(h, -60, 0, 40).rot(un, -120, 0, 60).rot(0.75f, -90, 0, 50).rot(0.9f, -30, 0, 20).rot(1, 0, 0, 0);
        b.bone("lArm2").scale(0, 0.1f, 0.1f, 0.1f).scale(0.48f, 0.1f, 0.1f, 0.1f).scale(h, 0.6f, 0.6f, 0.6f, SNAP).scale(un, 1.1f, 1.1f, 1.1f).scale(0.75f, 1, 1, 1).scale(1, 1, 1, 1)
            .rot(0, 0, 0, 0).rot(h, -60, 0, -40).rot(un, -120, 0, -60).rot(0.75f, -90, 0, -50).rot(0.9f, -30, 0, -20).rot(1, 0, 0, 0);
        b.bone("rForearm2").rot(0, 0, 0, 0).rot(h, -90, 0, 0).rot(un, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("lForearm2").rot(0, 0, 0, 0).rot(h, -90, 0, 0).rot(un, -20, 0, 0).rot(1, 0, 0, 0);
        b.bone("rClaw2a").rot(0, 0, 0, 0).rot(un, 0, 0, 35).rot(1, 0, 0, 0);
        b.bone("rClaw2b").rot(0, 0, 0, 0).rot(un, 0, 0, -35).rot(1, 0, 0, 0);
        b.bone("lClaw2a").rot(0, 0, 0, 0).rot(un, 0, 0, 35).rot(1, 0, 0, 0);
        b.bone("lClaw2b").rot(0, 0, 0, 0).rot(un, 0, 0, -35).rot(1, 0, 0, 0);
        b.bone("rChain1").rot(0, 0, 0, 0).rot(h, 70, 0, 30, SNAP).rot(un, -30, 0, 0).rot(0.8f, 15, 0, 0).rot(1, 0, 0, 0);
        b.bone("lChain1").rot(0, 0, 0, 0).rot(h, 70, 0, -30, SNAP).rot(un, -30, 0, 0).rot(0.8f, 15, 0, 0).rot(1, 0, 0, 0);
        return b.build();
    }
}
