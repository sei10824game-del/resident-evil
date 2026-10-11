package com.reevil.client.zombie;

import com.reevil.ReEvilMod;
import com.reevil.anim.Anim;
import com.reevil.anim.AnimClip;
import com.reevil.anim.AnimClip.Ease;
import com.reevil.client.BioModel;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Map;

import static com.reevil.client.zombie.Poses.R;
import static com.reevil.client.zombie.Poses.applyPose;
import static com.reevil.client.zombie.Poses.k;
import static com.reevil.client.zombie.Poses.map;
import static com.reevil.client.zombie.Poses.track;
import static com.reevil.client.zombie.Poses.v;

/**
 * RE1 zombie. Skeleton: body(hips) > waist > chest > neck > head > jaw; chest > arm > forearm > hand > fingers;
 * body > thigh > shin; waist > flap (torn shirt tail). Texture 128x64, see tools/tex_zombie.py for the UV map.
 */
public class ZombieModel extends BioModel<ZombieEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "zombie"), "main");

    public ZombieModel(ModelPart root) {
        super(root);
        reg("body", "body");
        reg("waist", "body/waist");
        reg("flap", "body/waist/flap");
        reg("chest", "body/waist/chest");
        reg("neck", "body/waist/chest/neck");
        reg("head", "body/waist/chest/neck/head");
        reg("jaw", "body/waist/chest/neck/head/jaw");
        reg("rightArm", "body/waist/chest/rightArm");
        reg("rightForearm", "body/waist/chest/rightArm/rightForearm");
        reg("rightHand", "body/waist/chest/rightArm/rightForearm/rightHand");
        reg("rightFingers", "body/waist/chest/rightArm/rightForearm/rightHand/rightFingers");
        reg("leftArm", "body/waist/chest/leftArm");
        reg("leftForearm", "body/waist/chest/leftArm/leftForearm");
        reg("leftHand", "body/waist/chest/leftArm/leftForearm/leftHand");
        reg("leftFingers", "body/waist/chest/leftArm/leftForearm/leftHand/leftFingers");
        reg("rightThigh", "body/rightThigh");
        reg("rightShin", "body/rightThigh/rightShin");
        reg("leftThigh", "body/leftThigh");
        reg("leftShin", "body/leftThigh/leftShin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0f, 12f, 0f));
        PartDefinition waist = body.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(28, 14).addBox(-4f, -6f, -2f, 8f, 6f, 4f), PartPose.ZERO);
        waist.addOrReplaceChild("flap", CubeListBuilder.create().texOffs(52, 14).addBox(-2f, 0f, 0f, 4f, 5f, 0f), PartPose.offset(1.5f, -1f, -2.1f));
        PartDefinition chest = waist.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 14).addBox(-4.5f, -6f, -2.5f, 9f, 6f, 5f), PartPose.offset(0f, -6f, 0f));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(58, 0).addBox(-1.5f, -2f, -1.5f, 3f, 2f, 3f), PartPose.offset(0f, -6f, 0f));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -6f, -4f, 8f, 6f, 8f), PartPose.offset(0f, -2f, 0f));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(32, 0).addBox(-3.5f, 0f, -6f, 7f, 2f, 6f), PartPose.offset(0f, 0f, 2f));
        arm(chest, "right", -6f, 0, 26);
        arm(chest, "left", 6f, 0, 34);
        PartDefinition rt = body.addOrReplaceChild("rightThigh", CubeListBuilder.create().texOffs(0, 42).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(-2f, 0f, 0f));
        rt.addOrReplaceChild("rightShin", CubeListBuilder.create().texOffs(16, 42).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(0f, 6f, 0f));
        PartDefinition lt = body.addOrReplaceChild("leftThigh", CubeListBuilder.create().texOffs(32, 42).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(2f, 0f, 0f));
        lt.addOrReplaceChild("leftShin", CubeListBuilder.create().texOffs(48, 42).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(0f, 6f, 0f));
        return LayerDefinition.create(mesh, 128, 64);
    }

    private static void arm(PartDefinition chest, String side, float x, int u, int v) {
        PartDefinition a = chest.addOrReplaceChild(side + "Arm", CubeListBuilder.create().texOffs(u, v).addBox(-1.5f, -1f, -1.5f, 3f, 5f, 3f), PartPose.offset(x, -5f, 0f));
        PartDefinition f = a.addOrReplaceChild(side + "Forearm", CubeListBuilder.create().texOffs(u + 12, v).addBox(-1.5f, 0f, -1.5f, 3f, 5f, 3f), PartPose.offset(0f, 4f, 0f));
        PartDefinition h = f.addOrReplaceChild(side + "Hand", CubeListBuilder.create().texOffs(u + 24, v).addBox(-1.5f, 0f, -1.5f, 3f, 2f, 3f), PartPose.offset(0f, 5f, 0f));
        h.addOrReplaceChild(side + "Fingers", CubeListBuilder.create().texOffs(u + 36, v).addBox(-1.5f, 0f, -1.5f, 3f, 2f, 1f), PartPose.offset(0f, 2f, 0f));
    }

    // ------------------------------------------------------------------ pose tables (degrees / pixels, offsets from rest)

    /** static part of the standing shamble: hunched, arms reaching out, head tilted, right leg turned out */
    static final Map<String, float[]> STAND = map(
        "waist", v(8, 0, 0), "chest", v(12, 0, -4), "neck", v(0, 0, 10), "head", v(6, 0, 0), "jaw", v(9, 0, 0),
        "rightArm", v(-75, 0, 6), "rightForearm", v(-15, 0, 0), "rightHand", v(15, 0, 0), "rightFingers", v(25, 0, 0),
        "leftArm", v(-65, 0, -8), "leftForearm", v(-15, 0, 0), "leftHand", v(15, 0, 0), "leftFingers", v(25, 0, 0),
        "rightThigh", v(0, 12, 0));

    /** face-down on the floor, head turned on its cheek, one arm above the head */
    static final Map<String, float[]> LIE = map(
        "body", v(90, 0, 0, 0, 9.5f, 5), "chest", v(0, 0, -5), "neck", v(-10, 0, 0), "head", v(0, 80, 0), "jaw", v(20, 0, 0),
        "rightArm", v(-165, 0, 15), "rightForearm", v(-20, 0, 0), "rightHand", v(10, 0, 0), "rightFingers", v(30, 0, 0),
        "leftArm", v(10, 0, -10), "leftForearm", v(-10, 0, 0), "leftFingers", v(40, 0, 0),
        "rightThigh", v(0, 0, 5), "rightShin", v(15, 0, 0), "leftThigh", v(-5, 10, -5));

    /** legless crawl: torso low, chest raised on the arms, head craned up at the prey */
    static final Map<String, float[]> CRAWL = map(
        "body", v(75, 0, 0, 0, 8, 4), "waist", v(5, 0, 0), "chest", v(-10, 0, 0), "neck", v(-30, 0, 0), "head", v(-35, 0, 0), "jaw", v(15, 0, 0),
        "rightArm", v(-125, 0, 10), "rightForearm", v(-20, 0, 0), "rightHand", v(30, 0, 0), "rightFingers", v(20, 0, 0),
        "leftArm", v(-115, 0, -10), "leftForearm", v(-20, 0, 0), "leftHand", v(30, 0, 0), "leftFingers", v(20, 0, 0),
        "rightThigh", v(0, 0, 6), "rightShin", v(10, 0, 0), "leftThigh", v(0, 0, -6), "leftShin", v(5, 0, 0));

    // ------------------------------------------------------------------ clips

    /** 1: claw swipe, 24t, impact tick 13 (t=.54). Wind-up twist right with arm cocked high, diagonal rake across, follow-through. */
    static final AnimClip SWIPE = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.38f, 0, 8, 0).rot(0.54f, 0, -14, 0, Ease.IN).rot(0.7f, 0, -18, 0, Ease.OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.38f, 0, 0.5f, 1.2f).pos(0.54f, 0, 0.8f, -1.8f, Ease.IN).pos(0.7f, 0, 0.6f, -2f, Ease.OUT).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.38f, -8, 10, 0).rot(0.54f, 14, -12, 0, Ease.IN).rot(0.7f, 18, -15, 0, Ease.OUT).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.38f, -6, 28, 6).rot(0.54f, 12, -30, -4, Ease.IN).rot(0.7f, 16, -40, -6, Ease.OUT).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.38f, -8, -10, -6).rot(0.54f, 6, 8, 4, Ease.IN).rot(0.75f, 8, 10, 10).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.38f, -12, -20, 8).rot(0.54f, 10, 15, -6, Ease.IN).rot(0.62f, 14, 22, -10, Ease.OUT).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.38f, 22, 0, 0).rot(0.54f, 30, 0, 0).rot(0.7f, 8, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.38f, -55, -20, 45).rot(0.54f, 40, 30, -25, Ease.IN).rot(0.7f, 60, 35, -30, Ease.OUT).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.38f, -45, 0, 0).rot(0.54f, 10, 0, 0, Ease.IN).rot(0.7f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightHand").rot(0, 0, 0, 0).rot(0.38f, -25, 0, 0).rot(0.54f, 20, 0, 0, Ease.IN).rot(1, 0, 0, 0).done()
        .bone("rightFingers").rot(0, 0, 0, 0).rot(0.38f, -35, 0, 0).rot(0.5f, -35, 0, 0).rot(0.56f, 55, 0, 0, Ease.SNAP).rot(0.8f, 40, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.38f, 25, 0, -10).rot(0.54f, -10, 0, 15).rot(0.7f, -15, 0, 20).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.38f, -25, 0, 0).rot(0.6f, -5, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftFingers").rot(0, 0, 0, 0).rot(0.38f, 40, 0, 0).rot(0.6f, 10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.38f, 10, 0, 0).rot(0.54f, -8, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.38f, 6, 0, 0).rot(0.54f, 14, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.38f, -6, 0, 0).rot(0.54f, -28, 0, 0, Ease.IN).rot(0.7f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.38f, 4, 0, 0).rot(0.54f, 30, 0, 0).rot(0.7f, 28, 0, 0).rot(1, 0, 0, 0).done()
        .bone("flap").rot(0, 0, 0, 0).rot(0.38f, -10, 0, 0).rot(0.6f, -35, 0, 0, Ease.OUT).rot(1, 0, 0, 0).done()
        .build();

    /**
     * 2: lunging grab and neck bite, 60t. Grab tick 14 (t=.233), bites at 26/36/46 (t=.433/.6/.767).
     * Arms spread wide and rear back, lunge and clamp, pull the victim in, then three rear-back / snap / tear bites, shove away.
     */
    static final AnimClip GRAB = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.15f, -6, 0, 0).rot(0.233f, 10, 0, 0, Ease.IN).rot(0.33f, 12, 0, 0).rot(0.85f, 12, 0, 0).rot(0.93f, -4, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.15f, 0, 1f, 1.5f).pos(0.233f, 0, 0.5f, -3.5f, Ease.IN).pos(0.33f, 0, 0.5f, -3f).pos(0.85f, 0, 0.5f, -3f).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.15f, -10, 0, 0).rot(0.233f, 20, 0, 0, Ease.IN).rot(0.33f, 15, 0, 0).rot(0.85f, 15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.15f, -12, 0, 0).rot(0.233f, 18, 0, 0, Ease.IN).rot(0.33f, 14, 0, 0)
            .rot(0.37f, 6, 0, 0).rot(0.433f, 22, 0, 0, Ease.IN).rot(0.48f, 14, 8, 0).rot(0.53f, 6, 0, 0)
            .rot(0.6f, 24, 0, 0, Ease.IN).rot(0.65f, 14, -8, 0).rot(0.7f, 6, 0, 0)
            .rot(0.767f, 26, 0, 0, Ease.IN).rot(0.82f, 14, 10, 0).rot(0.9f, -5, 0, 0).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.15f, -15, 0, 0).rot(0.233f, 10, 0, 0)
            .rot(0.37f, -20, 0, 0).rot(0.433f, 20, 0, 0, Ease.SNAP).rot(0.48f, 15, 0, 10)
            .rot(0.53f, -20, 0, 0).rot(0.6f, 22, 0, 0, Ease.SNAP).rot(0.65f, 15, 0, -10)
            .rot(0.7f, -20, 0, 0).rot(0.767f, 24, 0, 0, Ease.SNAP).rot(0.82f, 18, 0, 12).rot(0.9f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.15f, -25, 0, 0).rot(0.233f, 5, 0, 0)
            .rot(0.37f, -25, 0, 0).rot(0.433f, 25, 0, 0, Ease.SNAP).rot(0.48f, 18, 18, 8)
            .rot(0.53f, -25, -5, 0).rot(0.6f, 28, 0, 0, Ease.SNAP).rot(0.65f, 20, -18, -8)
            .rot(0.7f, -25, 0, 0).rot(0.767f, 30, 0, 0, Ease.SNAP).rot(0.82f, 20, 20, 10).rot(0.9f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.15f, 30, 0, 0).rot(0.233f, 25, 0, 0)
            .rot(0.37f, 45, 0, 0).rot(0.433f, -2, 0, 0, Ease.SNAP).rot(0.48f, 5, 0, 0)
            .rot(0.53f, 45, 0, 0).rot(0.6f, -2, 0, 0, Ease.SNAP).rot(0.65f, 5, 0, 0)
            .rot(0.7f, 45, 0, 0).rot(0.767f, -2, 0, 0, Ease.SNAP).rot(0.82f, 8, 0, 0).rot(0.9f, 30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.15f, -40, 0, 40).rot(0.233f, -15, 0, -25, Ease.IN).rot(0.33f, -10, 0, -30)
            .rot(0.433f, -4, 0, -33).rot(0.6f, -4, 0, -33).rot(0.85f, -10, 0, -30).rot(0.93f, 20, 0, 10).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.15f, -10, 0, 0).rot(0.233f, 0, 0, 0, Ease.IN).rot(0.33f, -55, 0, 0).rot(0.85f, -55, 0, 0).rot(0.93f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightHand").rot(0, 0, 0, 0).rot(0.15f, -30, 0, 0).rot(0.233f, 10, 0, 0).rot(0.33f, 20, 0, 0).rot(0.85f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightFingers").rot(0, 0, 0, 0).rot(0.15f, -40, 0, 0).rot(0.22f, -40, 0, 0).rot(0.25f, 70, 0, 0, Ease.SNAP).rot(0.85f, 70, 0, 0).rot(0.93f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.15f, -30, 0, -40).rot(0.233f, -25, 0, 25, Ease.IN).rot(0.33f, -20, 0, 30)
            .rot(0.433f, -14, 0, 33).rot(0.6f, -14, 0, 33).rot(0.85f, -20, 0, 30).rot(0.93f, 15, 0, -10).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.15f, -10, 0, 0).rot(0.233f, 0, 0, 0, Ease.IN).rot(0.33f, -50, 0, 0).rot(0.85f, -50, 0, 0).rot(0.93f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftHand").rot(0, 0, 0, 0).rot(0.15f, -30, 0, 0).rot(0.233f, 10, 0, 0).rot(0.33f, 20, 0, 0).rot(0.85f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftFingers").rot(0, 0, 0, 0).rot(0.15f, -40, 0, 0).rot(0.22f, -40, 0, 0).rot(0.25f, 70, 0, 0, Ease.SNAP).rot(0.85f, 70, 0, 0).rot(0.93f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.15f, 8, 0, 0).rot(0.233f, -30, 0, 0, Ease.IN).rot(0.33f, -25, 0, 0).rot(0.85f, -25, 0, 0).rot(0.95f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.15f, 15, 0, 0).rot(0.233f, 25, 0, 0).rot(0.33f, 20, 0, 0).rot(0.85f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.15f, -5, 0, 0).rot(0.233f, 18, 0, 0, Ease.IN).rot(0.33f, 12, 0, 0).rot(0.85f, 12, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.15f, 10, 0, 0).rot(0.233f, 5, 0, 0).rot(1, 0, 0, 0).done()
        .bone("flap").rot(0, 0, 0, 0).rot(0.233f, -30, 0, 0, Ease.OUT).rot(0.4f, -15, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /** 3: vomit, 40t, spew impact tick 24 (t=.6). Stomach heaves twice, rears back, then retches forward with the jaw wide. */
    static final AnimClip VOMIT = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.1f, 0, 0, 3).rot(0.3f, 0, 0, -3).rot(0.6f, 0, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.5f, 0, 0, 1.2f).pos(0.6f, 0, 0.6f, -1.2f, Ease.IN).pos(0.8f, 0, 0.6f, -1f).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.1f, 18, 0, 0).rot(0.2f, -4, 0, 0).rot(0.3f, 22, 0, 0).rot(0.4f, -8, 0, 0)
            .rot(0.5f, -14, 0, 0).rot(0.6f, 28, 0, 0, Ease.IN).rot(0.7f, 24, 0, 0).rot(0.8f, 26, 0, 0).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.1f, 14, 0, 4).rot(0.2f, -6, 0, -3).rot(0.3f, 18, 0, 5).rot(0.4f, -6, 0, 0)
            .rot(0.5f, -18, 0, 0).rot(0.6f, 25, 0, 0, Ease.IN).rot(0.7f, 20, 0, 3).rot(0.8f, 22, 0, -3).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.1f, 10, 0, 0).rot(0.2f, -5, 0, 0).rot(0.3f, 15, 0, 0).rot(0.5f, -25, 0, 0).rot(0.6f, 20, 0, 0, Ease.IN).rot(0.8f, 18, 0, 0).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.1f, 20, 0, 0).rot(0.2f, -5, 0, 0).rot(0.3f, 25, 0, 0).rot(0.5f, -35, 0, 0)
            .rot(0.6f, 15, 0, 0, Ease.IN).rot(0.65f, 18, 0, 6).rot(0.72f, 14, 0, -6).rot(0.8f, 16, 0, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.1f, 25, 0, 0).rot(0.2f, 5, 0, 0).rot(0.3f, 30, 0, 0).rot(0.45f, 10, 0, 0)
            .rot(0.55f, 40, 0, 0).rot(0.6f, 50, 0, 0).rot(0.8f, 48, 0, 0).rot(0.9f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.1f, 50, 0, -15).rot(0.3f, 55, 0, -20).rot(0.5f, 35, 0, -10).rot(0.6f, 20, 0, 15).rot(0.8f, 25, 0, 15).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.1f, -60, 0, 0).rot(0.5f, -55, 0, 0).rot(0.6f, -10, 0, 0).rot(0.8f, -15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightFingers").rot(0, 0, 0, 0).rot(0.1f, 60, 0, 0).rot(0.5f, 60, 0, 0).rot(0.6f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.1f, 45, 0, 15).rot(0.3f, 50, 0, 20).rot(0.5f, 30, 0, 10).rot(0.6f, 15, 0, -15).rot(0.8f, 20, 0, -15).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.1f, -60, 0, 0).rot(0.5f, -55, 0, 0).rot(0.6f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftFingers").rot(0, 0, 0, 0).rot(0.1f, 60, 0, 0).rot(0.5f, 60, 0, 0).rot(0.6f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.5f, 5, 0, 0).rot(0.6f, -15, 0, 0, Ease.IN).rot(0.8f, -15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.6f, 20, 0, 0).rot(0.8f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.5f, -4, 0, 0).rot(0.6f, 8, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.6f, 10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("flap").rot(0, 0, 0, 0).rot(0.1f, -20, 0, 0).rot(0.3f, -25, 0, 0).rot(0.6f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /** 4 (crawl form): ankle grab and bite, 40t. Grab tick 12 (t=.3), bites 22/32 (t=.55/.8). Rears up on one arm, slams the other onto the ankle, gnaws. */
    static final AnimClip ANKLE = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.2f, -15, 0, 0).rot(0.3f, 5, 0, 0, Ease.IN).rot(0.45f, -5, 0, 0).rot(0.55f, 8, 0, 0, Ease.SNAP)
            .rot(0.7f, -5, 0, 0).rot(0.8f, 8, 0, 0, Ease.SNAP).rot(0.9f, 0, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.2f, 0, -1.5f, 1.5f).pos(0.3f, 0, 0.5f, -3.5f, Ease.IN).pos(0.85f, 0, 0, -3f).pos(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.2f, -20, 15, 0).rot(0.3f, 10, -10, 0, Ease.IN).rot(0.45f, -12, 0, 0).rot(0.55f, 12, 0, 0, Ease.SNAP)
            .rot(0.62f, 8, 10, 0).rot(0.7f, -12, 0, 0).rot(0.8f, 14, 0, 0, Ease.SNAP).rot(0.86f, 8, -10, 0).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.2f, -20, 0, 0).rot(0.3f, 10, 0, 0).rot(0.45f, -15, 0, 0).rot(0.55f, 25, 0, 0, Ease.SNAP)
            .rot(0.62f, 20, 0, 10).rot(0.7f, -15, 0, 0).rot(0.8f, 28, 0, 0, Ease.SNAP).rot(0.86f, 20, 0, -10).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.2f, -25, 0, 0).rot(0.3f, 10, 0, 0).rot(0.45f, -20, 0, 0).rot(0.55f, 30, 0, 0, Ease.SNAP)
            .rot(0.62f, 25, 15, 0).rot(0.7f, -20, 0, 0).rot(0.8f, 35, 0, 0, Ease.SNAP).rot(0.86f, 25, -15, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.2f, 35, 0, 0).rot(0.3f, 20, 0, 0).rot(0.45f, 45, 0, 0).rot(0.55f, 0, 0, 0, Ease.SNAP)
            .rot(0.62f, 8, 0, 0).rot(0.7f, 45, 0, 0).rot(0.8f, 0, 0, 0, Ease.SNAP).rot(0.9f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.2f, -50, 0, 20).rot(0.3f, 40, 0, -10, Ease.IN).rot(0.85f, 35, 0, -10).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.2f, -30, 0, 0).rot(0.3f, 10, 0, 0, Ease.IN).rot(0.4f, -40, 0, 0).rot(0.85f, -40, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightFingers").rot(0, 0, 0, 0).rot(0.2f, -40, 0, 0).rot(0.28f, -40, 0, 0).rot(0.32f, 70, 0, 0, Ease.SNAP).rot(0.85f, 70, 0, 0).rot(0.95f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.2f, 10, 0, -10).rot(0.3f, -20, 0, 10, Ease.IN).rot(0.4f, -10, 0, 0).rot(0.85f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.2f, 20, 0, 0).rot(0.3f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftFingers").rot(0, 0, 0, 0).rot(0.3f, 50, 0, 0).rot(0.85f, 50, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.2f, -10, 0, 0).rot(0.3f, 15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.2f, 40, 0, 0).rot(0.35f, 10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.25f, 10, 0, 0).rot(0.35f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /** 5: collapse (legs give out) standing -> crawl, 26t. Knee buckles, flails, drops to knees, catches itself on its hands. */
    static final AnimClip COLLAPSE = collapse();

    /** 6: rise from lying (feigned death) -> standing, 50t. Fingers scratch, push-up, knees under, stagger to its feet. */
    static final AnimClip RISE = rise();

    /** death (non-fire) -> lying corpse, driven by formTicks; also used for real death. Ends exactly on LIE. */
    static final AnimClip FALL = fall();

    private static AnimClip collapse() {
        AnimClip.Builder c = AnimClip.create();
        track(c, "body", null, CRAWL, k(0.2f, -8, 0, 4, 0, 1, 0), k(0.45f, 35, 0, 8, 0, 5, 0), k(0.65f, 70, 0, 3, 0, 8, 2), k(0.8f, 82, 0, 0, 0, 8.5f, 4));
        track(c, "waist", null, CRAWL, k(0.2f, -12, 0, 0), k(0.5f, 15, 0, 0));
        track(c, "chest", null, CRAWL, k(0.2f, -10, 15, 0), k(0.5f, 10, -10, 0), k(0.75f, -15, 0, 0));
        track(c, "neck", null, CRAWL, k(0.2f, -15, 0, 0), k(0.5f, 10, 0, 0), k(0.75f, -25, 0, 0));
        track(c, "head", null, CRAWL, k(0.2f, -35, 0, 15), k(0.5f, 20, 0, -10), k(0.75f, -30, 0, 0));
        track(c, "jaw", null, CRAWL, k(0.2f, 35, 0, 0), k(0.5f, 10, 0, 0));
        track(c, "rightArm", null, CRAWL, k(0.2f, -60, 0, 50), k(0.5f, -60, 0, 10), k(0.7f, -115, 0, 10));
        track(c, "leftArm", null, CRAWL, k(0.22f, -40, 0, -60), k(0.5f, -50, 0, -10), k(0.7f, -105, 0, -10));
        track(c, "rightForearm", null, CRAWL, k(0.2f, -30, 0, 0), k(0.6f, -5, 0, 0));
        track(c, "leftForearm", null, CRAWL, k(0.2f, -30, 0, 0), k(0.6f, -5, 0, 0));
        track(c, "rightHand", null, CRAWL, k(0.6f, 40, 0, 0));
        track(c, "leftHand", null, CRAWL, k(0.6f, 40, 0, 0));
        track(c, "rightFingers", null, CRAWL, k(0.2f, -30, 0, 0), k(0.6f, 10, 0, 0));
        track(c, "leftFingers", null, CRAWL, k(0.2f, -30, 0, 0), k(0.6f, 10, 0, 0));
        track(c, "rightThigh", null, CRAWL, k(0.2f, -25, 0, 0), k(0.45f, -60, 0, 10), k(0.7f, -10, 0, 0));
        track(c, "rightShin", null, CRAWL, k(0.2f, 50, 0, 0), k(0.45f, 90, 0, 0), k(0.7f, 30, 0, 0));
        track(c, "leftThigh", null, CRAWL, k(0.25f, 10, 0, 0), k(0.5f, -50, 0, 0), k(0.75f, -5, 0, 0));
        track(c, "leftShin", null, CRAWL, k(0.3f, 20, 0, 0), k(0.5f, 85, 0, 0), k(0.75f, 20, 0, 0));
        return c.build();
    }

    private static AnimClip rise() {
        AnimClip.Builder c = AnimClip.create();
        track(c, "body", LIE, null, k(0.12f, 88, 0, 0, 0, 9.5f, 5), k(0.3f, 70, 0, 0, 0, 8, 4), k(0.5f, 30, 0, 0, 0, 6, 2), k(0.7f, 10, 0, -5, 0, 2, 0), k(0.85f, -5, 0, 3, 0, 0.5f, 0));
        track(c, "waist", LIE, null, k(0.3f, -10, 0, 0), k(0.5f, 20, 0, 0), k(0.7f, 25, 0, 0));
        track(c, "chest", LIE, null, k(0.3f, -20, 0, 0), k(0.5f, 15, 10, 0), k(0.75f, 20, -10, 5));
        track(c, "neck", LIE, null, k(0.12f, -25, 0, 0), k(0.35f, -30, 0, 0), k(0.55f, 10, 0, 0), k(0.8f, 20, 0, 15));
        track(c, "head", LIE, null, k(0.12f, -10, 60, 0), k(0.35f, -40, 0, 0), k(0.6f, 15, 0, -20), k(0.85f, -10, 0, 20));
        track(c, "jaw", LIE, null, k(0.35f, 30, 0, 0), k(0.6f, 10, 0, 0), k(0.85f, 35, 0, 0));
        track(c, "rightArm", LIE, null, k(0.15f, -160, 0, 20), k(0.32f, -90, 0, 10), k(0.5f, -40, 0, 15), k(0.7f, -30, 0, 25), k(0.85f, -60, 0, 10));
        track(c, "rightForearm", LIE, null, k(0.32f, -10, 0, 0), k(0.5f, -30, 0, 0));
        track(c, "rightHand", LIE, null, k(0.32f, 60, 0, 0), k(0.55f, 0, 0, 0));
        track(c, "rightFingers", LIE, null, k(0.08f, -10, 0, 0), k(0.14f, 40, 0, 0), k(0.32f, -20, 0, 0));
        track(c, "leftArm", LIE, null, k(0.15f, -40, 0, -20), k(0.32f, -90, 0, -10), k(0.5f, -60, 0, -20), k(0.7f, -20, 0, -30), k(0.85f, -50, 0, -10));
        track(c, "leftForearm", LIE, null, k(0.32f, -10, 0, 0));
        track(c, "leftHand", LIE, null, k(0.32f, 60, 0, 0), k(0.55f, 0, 0, 0));
        track(c, "leftFingers", LIE, null, k(0.06f, 0, 0, 0), k(0.11f, 50, 0, 0), k(0.32f, -15, 0, 0));
        track(c, "rightThigh", LIE, null, k(0.3f, -30, 0, 5), k(0.5f, -35, 0, 0), k(0.65f, -80, 0, 0), k(0.8f, -20, 0, 0));
        track(c, "rightShin", LIE, null, k(0.3f, 60, 0, 0), k(0.5f, 100, 0, 0), k(0.65f, 90, 0, 0), k(0.8f, 20, 0, 0));
        track(c, "leftThigh", LIE, null, k(0.3f, -25, 0, 0), k(0.5f, -30, 0, 0), k(0.65f, -25, 0, 0), k(0.8f, -10, 0, 0));
        track(c, "leftShin", LIE, null, k(0.3f, 70, 0, 0), k(0.5f, 100, 0, 0), k(0.65f, 100, 0, 0), k(0.8f, 25, 0, 0));
        return c.build();
    }

    private static AnimClip fall() {
        AnimClip.Builder c = AnimClip.create();
        track(c, "body", STAND, LIE, k(0.25f, -10, 0, 5, 0, 1, 0), k(0.5f, 20, 0, -5, 0, 5, 0), k(0.75f, 70, 0, 0, 0, 8, 3), k(0.88f, 95, 0, 0, 0, 10, 5));
        track(c, "waist", STAND, LIE, k(0.25f, -15, 0, 0), k(0.5f, 10, 0, 0));
        track(c, "chest", STAND, LIE, k(0.25f, -10, 0, 8), k(0.5f, 15, 0, -5));
        track(c, "neck", STAND, LIE, k(0.25f, -20, 0, 0), k(0.6f, 10, 0, 0));
        track(c, "head", STAND, LIE, k(0.25f, -40, 0, 0), k(0.5f, 20, 0, 0), k(0.75f, 10, 40, 0));
        track(c, "jaw", STAND, LIE, k(0.25f, 40, 0, 0));
        track(c, "rightArm", STAND, LIE, k(0.25f, -110, 0, 40), k(0.5f, -60, 0, 20), k(0.75f, -140, 0, 15));
        track(c, "rightForearm", STAND, LIE, k(0.25f, -30, 0, 0));
        track(c, "rightHand", STAND, LIE);
        track(c, "rightFingers", STAND, LIE, k(0.25f, -30, 0, 0));
        track(c, "leftArm", STAND, LIE, k(0.25f, -30, 0, -50), k(0.5f, -20, 0, -20), k(0.75f, 0, 0, -10));
        track(c, "leftForearm", STAND, LIE, k(0.25f, -30, 0, 0));
        track(c, "leftHand", STAND, LIE);
        track(c, "leftFingers", STAND, LIE, k(0.25f, -30, 0, 0));
        track(c, "rightThigh", STAND, LIE, k(0.5f, -60, 6, 0), k(0.75f, -20, 0, 0));
        track(c, "rightShin", STAND, LIE, k(0.5f, 90, 0, 0), k(0.75f, 40, 0, 0));
        track(c, "leftThigh", STAND, LIE, k(0.5f, -50, 0, 0), k(0.75f, -10, 0, 0));
        track(c, "leftShin", STAND, LIE, k(0.5f, 80, 0, 0), k(0.75f, 30, 0, 0));
        return c.build();
    }

    @Override
    protected AnimClip clip(ZombieEntity e, int moveId) {
        switch (moveId) {
            case ZombieEntity.SWIPE: return SWIPE;
            case ZombieEntity.GRAB_BITE: return GRAB;
            case ZombieEntity.VOMIT: return VOMIT;
            case ZombieEntity.ANKLE_BITE: return ANKLE;
            case ZombieEntity.COLLAPSE: return COLLAPSE;
            case ZombieEntity.RISE: return RISE;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ procedural base

    @Override
    protected void baseAnim(ZombieEntity e, float ls, float la, float age, float yaw, float pitch) {
        float partial = age - e.tickCount;
        int form = e.getForm();
        int move = e.getMoveId();
        if (e.deathTime > 0) {
            float d = Anim.clamp01((e.deathTime + partial) / 18f);
            if (form == ZombieEntity.FORM_STAND) applyClip(FALL, d);
            else if (form == ZombieEntity.FORM_CRAWL) { applyPose(this::bone, CRAWL, 1f - d); applyPose(this::bone, LIE, d); }
            else applyPose(this::bone, LIE, 1f);
            return;
        }
        if (form == ZombieEntity.FORM_FEIGN || form == ZombieEntity.FORM_CORPSE) {
            if (move == ZombieEntity.RISE) {
                standing(e, 0f, 0f, age, yaw, pitch, Anim.window(moveProgress(e, partial), 0.7f, 1f));
            } else if (form == ZombieEntity.FORM_CORPSE && e.getFormTicks() < 24) {
                applyClip(FALL, Anim.clamp01((e.getFormTicks() + partial) / 24f));
            } else {
                applyPose(this::bone, LIE, 1f);
                if (form == ZombieEntity.FORM_FEIGN) { // "dead" body that still twitches now and then
                    float tw = twitch(age, 0.13f, 3f);
                    bone("rightFingers").xRot += tw * 0.8f;
                    bone("leftFingers").xRot += twitch(age, 0.09f, 1f) * 0.6f;
                    bone("jaw").xRot += tw * 0.2f;
                }
            }
            return;
        }
        if (form == ZombieEntity.FORM_CRAWL) { crawl(e, ls, la, age, yaw, pitch); return; }
        float k = 1f;
        if (move == ZombieEntity.COLLAPSE) { k = 1f - Anim.window(moveProgress(e, partial), 0f, 0.3f); la = 0f; }
        else if (move != 0) la *= 0.3f; // planted during attacks
        standing(e, ls, la, age, yaw, pitch, k);
    }

    /** sharp occasional spike in 0..1 */
    private static float twitch(float age, float speed, float phase) {
        return (float) Math.pow(Math.max(0f, Mth.sin(age * speed + phase)), 16);
    }

    /** RE1 shamble: dragging right leg, lurching hips, arms outstretched and bobbing, head lolling behind the steps. */
    private void standing(ZombieEntity e, float ls, float la, float age, float yaw, float pitch, float k) {
        if (k <= 0f) return;
        applyPose(this::bone, STAND, k);
        float p = ls * 0.5f;
        float s = Mth.sin(p), c = Mth.cos(p);
        float idle = 1f - Mth.clamp(la * 2f, 0f, 1f);
        float breathe = Mth.sin(age * 0.06f);
        // pelvis: drops onto the good leg, rolls and twists with each lurch; idle sway
        bone("body").y += Math.abs(s) * 1.1f * la * k;
        bone("body").zRot += (s * 0.09f * la + Mth.sin(age * 0.031f) * 0.035f * idle) * k;
        bone("body").yRot += s * 0.14f * la * k;
        bone("waist").xRot += (0.06f * la + breathe * 0.02f) * k;
        bone("chest").yRot -= s * 0.18f * la * k;
        bone("chest").zRot += (c * 0.06f * la + Mth.sin(age * 0.043f) * 0.03f * idle) * k;
        bone("chest").xRot += breathe * 0.03f * k;
        // head lolls, lagging behind the step
        bone("neck").zRot += (Mth.sin(age * 0.045f) * 0.12f + Mth.sin(p - 0.8f) * 0.10f * la) * k;
        bone("head").xRot += (pitch * R * 0.5f + Mth.sin(age * 0.07f + 1.3f) * 0.06f + Math.abs(c) * 0.08f * la) * k;
        bone("head").yRot += yaw * R * 0.6f * k;
        bone("head").zRot += Mth.sin(age * 0.037f + 2f) * 0.10f * k;
        bone("jaw").xRot += Math.abs(Mth.sin(age * 0.09f)) * 0.18f * k;
        // arms reach for prey, bob with the steps; fingers twitch
        bone("rightArm").xRot += (c * 0.12f * la + Mth.sin(age * 0.067f) * 0.05f) * k;
        bone("rightArm").zRot += Mth.cos(age * 0.05f) * 0.04f * k;
        bone("leftArm").xRot += (-c * 0.12f * la + Mth.sin(age * 0.067f + 2f) * 0.05f) * k;
        bone("leftArm").zRot -= Mth.cos(age * 0.05f + 1f) * 0.04f * k;
        bone("rightForearm").xRot += Mth.sin(age * 0.08f) * 0.06f * k;
        bone("leftForearm").xRot += Mth.sin(age * 0.08f + 1.5f) * 0.06f * k;
        bone("rightFingers").xRot += twitch(age, 0.21f, 0f) * 0.5f * k;
        bone("leftFingers").xRot += twitch(age, 0.17f, 2f) * 0.5f * k;
        // legs: left steps, right drags stiff-kneed with the toe scraping
        bone("leftThigh").xRot += c * 0.85f * la * k;
        bone("leftShin").xRot += Math.max(0f, -s) * 1.0f * la * k;
        bone("rightThigh").xRot += (-c * 0.35f + 0.12f) * la * k;
        bone("rightThigh").zRot += 0.06f * la * k;
        bone("rightShin").xRot += (0.3f + Math.max(0f, s) * 0.15f) * la * k;
        bone("flap").xRot += (-Math.abs(c) * 0.25f * la + Mth.sin(age * 0.05f) * 0.05f) * k;
    }

    /** legless crawl: alternate arm pulls, shoulders roll, dead legs dragged behind. */
    private void crawl(ZombieEntity e, float ls, float la, float age, float yaw, float pitch) {
        applyPose(this::bone, CRAWL, 1f);
        if (e.getMoveId() != 0) la *= 0.2f;
        float p = ls * 0.45f;
        float s = Mth.sin(p), c = Mth.cos(p);
        bone("rightArm").xRot += s * 0.55f * la;
        bone("rightForearm").xRot -= Math.max(0f, c) * 0.6f * la;
        bone("leftArm").xRot -= s * 0.55f * la;
        bone("leftForearm").xRot -= Math.max(0f, -c) * 0.6f * la;
        bone("chest").yRot += s * 0.16f * la;
        bone("chest").zRot += s * 0.08f * la;
        bone("body").zRot += c * 0.06f * la;
        bone("body").y -= Math.abs(s) * 0.6f * la;
        bone("rightThigh").yRot += s * 0.1f * la;
        bone("leftThigh").yRot += s * 0.1f * la;
        bone("rightShin").xRot += Math.max(0f, c) * 0.3f * la;
        bone("head").yRot += yaw * R * 0.5f;
        bone("head").xRot += pitch * R * 0.3f + Mth.sin(age * 0.06f) * 0.05f;
        bone("head").zRot += Mth.sin(age * 0.04f) * 0.12f;
        bone("jaw").xRot += Math.abs(Mth.sin(age * 0.1f)) * 0.25f;
        bone("rightFingers").xRot += twitch(age, 0.2f, 0f) * 0.5f;
        bone("leftFingers").xRot += twitch(age, 0.15f, 1f) * 0.5f;
    }

    @Override
    protected void extraAnim(ZombieEntity e, float ls, float la, float age, float yaw, float pitch) {
        float partial = age - e.tickCount;
        if (e.deathTime > 0) {
            // only fire kills a zombie for good: it thrashes while burning, fading as it goes still
            float w = 1f - Anim.clamp01((e.deathTime + partial) / 20f) * 0.7f;
            bone("rightArm").xRot += Mth.sin(age * 0.9f) * 0.5f * w;
            bone("rightArm").zRot += Mth.cos(age * 0.7f) * 0.3f * w;
            bone("leftArm").xRot += Mth.sin(age * 0.8f + 2f) * 0.5f * w;
            bone("leftArm").zRot += Mth.cos(age * 0.6f + 1f) * 0.3f * w;
            bone("head").yRot += Mth.sin(age * 0.6f) * 0.3f * w;
            bone("jaw").xRot += (0.5f + Mth.sin(age * 1.1f) * 0.2f) * w;
            bone("rightThigh").xRot += Mth.sin(age * 0.7f) * 0.2f * w;
            bone("leftThigh").xRot += Mth.sin(age * 0.7f + 3f) * 0.2f * w;
            bone("body").zRot += Mth.sin(age * 0.5f) * 0.06f * w;
            return;
        }
        if (e.hurtTime > 0 && e.getForm() <= ZombieEntity.FORM_CRAWL) {
            float h = Mth.clamp((e.hurtTime - partial) / 10f, 0f, 1f);
            h *= h;
            bone("chest").xRot -= 0.35f * h;
            bone("head").xRot -= 0.5f * h;
            bone("neck").zRot += 0.2f * h;
            bone("jaw").xRot += 0.4f * h;
            bone("rightArm").xRot += 0.4f * h;
            bone("leftArm").xRot += 0.3f * h;
            bone("body").z += 1.2f * h;
        }
    }
}
