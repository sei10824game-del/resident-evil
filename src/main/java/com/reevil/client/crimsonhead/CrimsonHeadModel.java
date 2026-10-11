package com.reevil.client.crimsonhead;

import com.reevil.ReEvilMod;
import com.reevil.anim.Anim;
import com.reevil.anim.AnimClip;
import com.reevil.anim.AnimClip.Ease;
import com.reevil.client.BioModel;
import com.reevil.entity.crimsonhead.CrimsonHeadEntity;
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
 * Crimson Head: lanky skinless body with exposed spine ridge, long forearms and 3+1 claws per hand.
 * Skeleton like the zombie (hips > waist > chest > neck > head > jaw, arm > forearm > hand > claws, thigh > shin)
 * but every pose and clip is its own: predator crouch, whip-armed sprint, slashing combos. Texture 128x64 (tools/tex_crimson_head.py).
 */
public class CrimsonHeadModel extends BioModel<CrimsonHeadEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "crimson_head"), "main");

    public CrimsonHeadModel(ModelPart root) {
        super(root);
        reg("body", "body");
        reg("waist", "body/waist");
        reg("chest", "body/waist/chest");
        reg("neck", "body/waist/chest/neck");
        reg("head", "body/waist/chest/neck/head");
        reg("jaw", "body/waist/chest/neck/head/jaw");
        for (String s : new String[]{"right", "left"}) {
            String a = "body/waist/chest/" + s + "Arm";
            reg(s + "Arm", a);
            reg(s + "Forearm", a + "/" + s + "Forearm");
            reg(s + "Hand", a + "/" + s + "Forearm/" + s + "Hand");
            reg(s + "Claws", a + "/" + s + "Forearm/" + s + "Hand/" + s + "Claws");
            reg(s + "Thigh", "body/" + s + "Thigh");
            reg(s + "Shin", "body/" + s + "Thigh/" + s + "Shin");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0f, 12f, 0f));
        PartDefinition waist = body.addOrReplaceChild("waist", CubeListBuilder.create()
            .texOffs(24, 14).addBox(-3.5f, -6f, -1.5f, 7f, 6f, 3f)
            .texOffs(44, 14).addBox(-0.5f, -6f, 1.5f, 1f, 6f, 1f), PartPose.ZERO);
        PartDefinition chest = waist.addOrReplaceChild("chest", CubeListBuilder.create()
            .texOffs(0, 14).addBox(-4f, -6f, -2f, 8f, 6f, 4f)
            .texOffs(44, 14).addBox(-0.5f, -6f, 2f, 1f, 6f, 1f), PartPose.offset(0f, -6f, 0f));
        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(58, 0).addBox(-1.5f, -2f, -1.5f, 3f, 2f, 3f), PartPose.offset(0f, -6f, 0f));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4f, -6f, -4f, 8f, 6f, 8f), PartPose.offset(0f, -2f, 0f));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(32, 0).addBox(-3.5f, 0f, -6f, 7f, 2f, 6f), PartPose.offset(0f, 0f, 2f));
        arm(chest, "right", -5.5f, 26);
        arm(chest, "left", 5.5f, 35);
        PartDefinition rt = body.addOrReplaceChild("rightThigh", CubeListBuilder.create().texOffs(0, 44).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(-2f, 0f, 0f));
        rt.addOrReplaceChild("rightShin", CubeListBuilder.create().texOffs(16, 44).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(0f, 6f, 0f));
        PartDefinition lt = body.addOrReplaceChild("leftThigh", CubeListBuilder.create().texOffs(32, 44).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(2f, 0f, 0f));
        lt.addOrReplaceChild("leftShin", CubeListBuilder.create().texOffs(48, 44).addBox(-2f, 0f, -2f, 4f, 6f, 4f), PartPose.offset(0f, 6f, 0f));
        return LayerDefinition.create(mesh, 128, 64);
    }

    private static void arm(PartDefinition chest, String side, float x, int v) {
        PartDefinition a = chest.addOrReplaceChild(side + "Arm", CubeListBuilder.create().texOffs(0, v).addBox(-1.5f, -1f, -1.5f, 3f, 6f, 3f), PartPose.offset(x, -5f, 0f));
        PartDefinition f = a.addOrReplaceChild(side + "Forearm", CubeListBuilder.create().texOffs(12, v).addBox(-1.5f, 0f, -1.5f, 3f, 6f, 3f), PartPose.offset(0f, 5f, 0f));
        PartDefinition h = f.addOrReplaceChild(side + "Hand", CubeListBuilder.create().texOffs(24, v).addBox(-1.5f, 0f, -1.5f, 3f, 2f, 3f), PartPose.offset(0f, 6f, 0f));
        h.addOrReplaceChild(side + "Claws", CubeListBuilder.create()
            .texOffs(36, v).addBox(-1.5f, 0f, -1.5f, 0.8f, 4f, 0.8f)
            .texOffs(36, v).addBox(-0.4f, 0f, -1.5f, 0.8f, 4.5f, 0.8f)
            .texOffs(36, v).addBox(0.7f, 0f, -1.5f, 0.8f, 4f, 0.8f)
            .texOffs(36, v).addBox(-1.5f, 0f, 0.6f, 0.8f, 3f, 0.8f), PartPose.offset(0f, 2f, 0f));
    }

    // ------------------------------------------------------------------ pose tables

    /** predator crouch: knees bent, deep hunch, head craned up, claws hanging forward */
    static final Map<String, float[]> STAND = map(
        "body", v(0, 0, 0, 0, 1.5f, 0), "waist", v(18, 0, 0), "chest", v(16, 0, 0), "neck", v(-14, 0, 0), "head", v(-16, 0, 0), "jaw", v(14, 0, 0),
        "rightArm", v(-20, 0, 12), "rightForearm", v(-25, 0, 0), "rightHand", v(10, 0, 0), "rightClaws", v(-10, 0, 0),
        "leftArm", v(-20, 0, -12), "leftForearm", v(-25, 0, 0), "leftHand", v(10, 0, 0), "leftClaws", v(-10, 0, 0),
        "rightThigh", v(-22, 0, 6), "rightShin", v(35, 0, 0), "leftThigh", v(-22, 0, -6), "leftShin", v(35, 0, 0));

    /** the corpse it rises from: face-down */
    static final Map<String, float[]> LIE = map(
        "body", v(90, 0, 0, 0, 9.5f, 5), "neck", v(-5, 0, 0), "head", v(0, -75, 0), "jaw", v(15, 0, 0),
        "rightArm", v(-10, 0, 20), "leftArm", v(-160, 0, -20), "rightClaws", v(30, 0, 0), "leftClaws", v(40, 0, 0),
        "rightThigh", v(0, 0, 8), "leftThigh", v(0, 0, -4), "leftShin", v(20, 0, 0));

    /** death: thrown on its back, arms flung over the head */
    static final Map<String, float[]> BACK = map(
        "body", v(-90, 0, 0, 0, 10, -5), "neck", v(10, 0, 0), "head", v(0, 50, 0), "jaw", v(35, 0, 0),
        "rightArm", v(-150, 0, 30), "rightForearm", v(-30, 0, 0), "rightClaws", v(40, 0, 0),
        "leftArm", v(-120, 0, -45), "leftForearm", v(-10, 0, 0), "leftClaws", v(60, 0, 0),
        "rightThigh", v(-20, 0, 6), "rightShin", v(40, 0, 0), "leftThigh", v(0, 0, -10));

    // ------------------------------------------------------------------ clips

    /** 1: two-hit combo, 26t, impacts 9/17 (t=.346/.654): right rake down-across, twist, left rake back. */
    static final AnimClip SLASH2 = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.2f, 0, 22, 0).rot(0.346f, 0, -20, 0, Ease.IN).rot(0.45f, 0, -26, 0).rot(0.654f, 0, 24, 0, Ease.IN).rot(0.76f, 0, 28, 0, Ease.OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.2f, 0, 0.8f, 1).pos(0.346f, 0, 1, -2.5f, Ease.IN).pos(0.5f, 0, 1, -2).pos(0.654f, 0, 1.2f, -4.5f, Ease.IN).pos(0.76f, 0, 1, -4.5f).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.2f, -6, 8, 0).rot(0.346f, 14, -10, 0, Ease.IN).rot(0.5f, 6, -12, 0).rot(0.654f, 16, 12, 0, Ease.IN).rot(0.76f, 18, 14, 0).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.2f, -10, 30, 8).rot(0.346f, 12, -34, -8, Ease.IN).rot(0.46f, 0, -38, -4).rot(0.654f, 14, 36, 8, Ease.IN).rot(0.76f, 16, 42, 10, Ease.OUT).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.2f, -6, -12, 0).rot(0.346f, 6, 12, 0).rot(0.46f, -6, 14, 0).rot(0.654f, 6, -14, 0).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.2f, -10, -18, 6).rot(0.346f, 8, 16, -6, Ease.IN).rot(0.46f, -8, 18, 0).rot(0.654f, 10, -18, 6, Ease.IN).rot(0.8f, 4, -10, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.2f, 30, 0, 0).rot(0.346f, 40, 0, 0).rot(0.46f, 25, 0, 0).rot(0.654f, 45, 0, 0).rot(0.8f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.2f, -120, -20, 70).rot(0.346f, -30, 40, -40, Ease.IN).rot(0.46f, 30, 30, -20, Ease.OUT).rot(0.654f, 40, 0, 10).rot(0.8f, 20, 0, 10).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.2f, -60, 0, 0).rot(0.346f, 0, 0, 0, Ease.IN).rot(0.46f, -20, 0, 0).rot(0.654f, -40, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightHand").rot(0, 0, 0, 0).rot(0.2f, -30, 0, 0).rot(0.346f, 20, 0, 0, Ease.IN).rot(0.5f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightClaws").rot(0, 0, 0, 0).rot(0.2f, -40, 0, 0).rot(0.346f, 10, 0, 0, Ease.IN).rot(0.5f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.2f, 30, 0, -10).rot(0.346f, 20, 0, -20).rot(0.5f, -120, 20, -70).rot(0.654f, -30, -40, 40, Ease.IN).rot(0.76f, 30, -30, 20, Ease.OUT).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.346f, -10, 0, 0).rot(0.5f, -60, 0, 0).rot(0.654f, 0, 0, 0, Ease.IN).rot(0.76f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftHand").rot(0, 0, 0, 0).rot(0.5f, -30, 0, 0).rot(0.654f, 20, 0, 0, Ease.IN).rot(1, 0, 0, 0).done()
        .bone("leftClaws").rot(0, 0, 0, 0).rot(0.5f, -40, 0, 0).rot(0.654f, 10, 0, 0, Ease.IN).rot(0.8f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.2f, 10, 0, 0).rot(0.346f, -30, 0, 0, Ease.IN).rot(0.5f, -20, 0, 0).rot(0.654f, 5, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.346f, 20, 0, 0).rot(0.654f, 30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.2f, -5, 0, 0).rot(0.346f, 15, 0, 0).rot(0.5f, 0, 0, 0).rot(0.654f, -35, 0, 0, Ease.IN).rot(0.76f, -35, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.346f, 25, 0, 0).rot(0.654f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /** 2: three-hit combo, 38t, impacts 9/18/28 (t=.237/.474/.737): right rake, left rake, rearing two-story overhead right slam. */
    static final AnimClip SLASH3 = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.12f, 0, 20, 0).rot(0.237f, 0, -20, 0, Ease.IN).rot(0.37f, 0, -24, 0).rot(0.474f, 0, 22, 0, Ease.IN)
            .rot(0.6f, -12, 10, 0).rot(0.737f, 22, -8, 0, Ease.IN).rot(0.84f, 26, -10, 0, Ease.OUT).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.12f, 0, 0.5f, 1).pos(0.237f, 0, 1, -2, Ease.IN).pos(0.37f, 0, 1, -2).pos(0.474f, 0, 1, -4, Ease.IN)
            .pos(0.6f, 0, -1.5f, -3.5f).pos(0.737f, 0, 2.5f, -6, Ease.IN).pos(0.84f, 0, 2.5f, -6).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.12f, -6, 8, 0).rot(0.237f, 12, -10, 0, Ease.IN).rot(0.37f, 4, -10, 0).rot(0.474f, 14, 10, 0, Ease.IN)
            .rot(0.6f, -18, 0, 0).rot(0.737f, 24, 0, 0, Ease.IN).rot(0.84f, 26, 0, 0).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.12f, -8, 28, 6).rot(0.237f, 10, -32, -8, Ease.IN).rot(0.37f, 0, -36, -4).rot(0.474f, 12, 34, 8, Ease.IN)
            .rot(0.6f, -22, 8, 0).rot(0.737f, 26, -10, -4, Ease.IN).rot(0.84f, 28, -12, -4).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.12f, -6, -10, 0).rot(0.237f, 6, 10, 0).rot(0.37f, -6, 12, 0).rot(0.474f, 6, -12, 0).rot(0.6f, -20, 0, 0).rot(0.737f, 18, 0, 0, Ease.IN).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.12f, -8, -16, 6).rot(0.237f, 8, 14, -6, Ease.IN).rot(0.37f, -8, 16, 0).rot(0.474f, 8, -16, 6, Ease.IN)
            .rot(0.6f, -30, 0, 0).rot(0.737f, 20, 5, 0, Ease.IN).rot(0.86f, 10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.12f, 30, 0, 0).rot(0.237f, 40, 0, 0).rot(0.37f, 25, 0, 0).rot(0.474f, 40, 0, 0).rot(0.6f, 55, 0, 0).rot(0.737f, 30, 0, 0).rot(0.86f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.12f, -115, -20, 65).rot(0.237f, -30, 40, -40, Ease.IN).rot(0.33f, 30, 30, -20, Ease.OUT).rot(0.474f, 40, 0, 10)
            .rot(0.6f, -170, 0, 20).rot(0.737f, -10, 10, -15, Ease.IN).rot(0.84f, 25, 10, -10, Ease.OUT).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.12f, -60, 0, 0).rot(0.237f, 0, 0, 0, Ease.IN).rot(0.33f, -20, 0, 0).rot(0.474f, -40, 0, 0).rot(0.6f, -50, 0, 0).rot(0.737f, 0, 0, 0, Ease.IN).rot(0.84f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightHand").rot(0, 0, 0, 0).rot(0.12f, -30, 0, 0).rot(0.237f, 20, 0, 0, Ease.IN).rot(0.37f, 0, 0, 0).rot(0.6f, -40, 0, 0).rot(0.737f, 30, 0, 0, Ease.IN).rot(1, 0, 0, 0).done()
        .bone("rightClaws").rot(0, 0, 0, 0).rot(0.12f, -40, 0, 0).rot(0.237f, 10, 0, 0, Ease.IN).rot(0.37f, -20, 0, 0).rot(0.6f, -45, 0, 0).rot(0.737f, 15, 0, 0, Ease.IN).rot(0.86f, -15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.12f, 30, 0, -10).rot(0.237f, 20, 0, -20).rot(0.37f, -115, 20, -65).rot(0.474f, -30, -40, 40, Ease.IN)
            .rot(0.56f, 30, -30, 20, Ease.OUT).rot(0.68f, 20, 0, -30).rot(0.737f, 30, 0, -35).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.37f, -60, 0, 0).rot(0.474f, 0, 0, 0, Ease.IN).rot(0.56f, -20, 0, 0).rot(0.737f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftHand").rot(0, 0, 0, 0).rot(0.37f, -30, 0, 0).rot(0.474f, 20, 0, 0, Ease.IN).rot(0.6f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftClaws").rot(0, 0, 0, 0).rot(0.37f, -40, 0, 0).rot(0.474f, 10, 0, 0, Ease.IN).rot(0.6f, -20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.12f, 10, 0, 0).rot(0.237f, -30, 0, 0, Ease.IN).rot(0.37f, -20, 0, 0).rot(0.474f, 5, 0, 0).rot(0.6f, -10, 0, 0).rot(0.737f, -40, 0, 0, Ease.IN).rot(0.84f, -40, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.237f, 20, 0, 0).rot(0.474f, 30, 0, 0).rot(0.6f, 10, 0, 0).rot(0.737f, 50, 0, 0).rot(0.84f, 50, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.12f, -5, 0, 0).rot(0.237f, 15, 0, 0).rot(0.37f, 0, 0, 0).rot(0.474f, -35, 0, 0, Ease.IN).rot(0.6f, -5, 0, 0).rot(0.737f, 25, 0, 0).rot(0.84f, 25, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.237f, 25, 0, 0).rot(0.474f, 20, 0, 0).rot(0.737f, 40, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /**
     * 3: leaping tackle-bite, 48t. Coil 0..10, launch t=.208, superman flight, tackle impact tick 18 (t=.375),
     * crouched on the victim, two rear-back/snap bites at 28/37 (t=.583/.771), climbs off.
     */
    static final AnimClip LEAP = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.19f, 12, 0, 0).rot(0.23f, -15, 0, 0, Ease.IN).rot(0.33f, 45, 0, 0, Ease.OUT).rot(0.375f, 60, 0, 0)
            .rot(0.45f, 50, 0, 0).rot(0.52f, 40, 0, 0).rot(0.583f, 58, 0, 0, Ease.SNAP).rot(0.71f, 40, 0, 0).rot(0.771f, 58, 0, 0, Ease.SNAP).rot(0.86f, 45, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.19f, 0, 4, 2).pos(0.23f, 0, -2, 0, Ease.IN).pos(0.33f, 0, -1, -2).pos(0.375f, 0, 3, -3).pos(0.45f, 0, 5, -3).pos(0.86f, 0, 5, -3).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.19f, 30, 0, 0).rot(0.23f, -10, 0, 0, Ease.IN).rot(0.33f, 0, 0, 0).rot(0.375f, 15, 0, 0).rot(0.45f, 20, 0, 0).rot(0.86f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.19f, 20, 0, 0).rot(0.23f, -15, 0, 0, Ease.IN).rot(0.33f, -10, 0, 0).rot(0.375f, 10, 0, 0)
            .rot(0.52f, -15, 0, 0).rot(0.583f, 20, 0, 0, Ease.SNAP).rot(0.71f, -15, 0, 0).rot(0.771f, 22, 0, 0, Ease.SNAP).rot(0.86f, 5, 0, 0).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.19f, -20, 0, 0).rot(0.33f, -25, 0, 0).rot(0.375f, 10, 0, 0)
            .rot(0.52f, -25, 0, 0).rot(0.583f, 25, 0, 0, Ease.SNAP).rot(0.64f, 20, 0, 12).rot(0.71f, -25, 0, 0).rot(0.771f, 28, 0, 0, Ease.SNAP).rot(0.82f, 20, 0, -12).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.19f, -30, 0, 0).rot(0.33f, -40, 0, 0).rot(0.375f, 15, 0, 0)
            .rot(0.52f, -30, 0, 0).rot(0.583f, 30, 0, 0, Ease.SNAP).rot(0.64f, 25, 20, 0).rot(0.71f, -30, 0, 0).rot(0.771f, 32, 0, 0, Ease.SNAP).rot(0.82f, 25, -20, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.19f, 25, 0, 0).rot(0.33f, 55, 0, 0).rot(0.375f, 5, 0, 0)
            .rot(0.52f, 50, 0, 0).rot(0.583f, 0, 0, 0, Ease.SNAP).rot(0.64f, 10, 0, 0).rot(0.71f, 50, 0, 0).rot(0.771f, 0, 0, 0, Ease.SNAP).rot(0.86f, 25, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.19f, 50, 0, 20).rot(0.23f, -110, 0, 25, Ease.IN).rot(0.33f, -150, 0, 25).rot(0.375f, -85, 0, -25, Ease.IN).rot(0.45f, -60, 0, -15).rot(0.86f, -60, 0, -15).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.19f, 50, 0, -20).rot(0.23f, -110, 0, -25, Ease.IN).rot(0.33f, -150, 0, -25).rot(0.375f, -85, 0, 25, Ease.IN).rot(0.45f, -60, 0, 15).rot(0.86f, -60, 0, 15).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.19f, -20, 0, 0).rot(0.33f, -5, 0, 0).rot(0.375f, -40, 0, 0).rot(0.45f, -50, 0, 0).rot(0.86f, -50, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.19f, -20, 0, 0).rot(0.33f, -5, 0, 0).rot(0.375f, -40, 0, 0).rot(0.45f, -50, 0, 0).rot(0.86f, -50, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightClaws").rot(0, 0, 0, 0).rot(0.19f, -30, 0, 0).rot(0.33f, -50, 0, 0).rot(0.36f, -50, 0, 0).rot(0.39f, 60, 0, 0, Ease.SNAP).rot(0.86f, 60, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftClaws").rot(0, 0, 0, 0).rot(0.19f, -30, 0, 0).rot(0.33f, -50, 0, 0).rot(0.36f, -50, 0, 0).rot(0.39f, 60, 0, 0, Ease.SNAP).rot(0.86f, 60, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.19f, -55, 0, 0).rot(0.23f, 25, 0, 0, Ease.IN).rot(0.33f, 40, 0, 0).rot(0.375f, -20, 0, 0).rot(0.45f, -60, 0, 0).rot(0.86f, -60, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.19f, -50, 0, 0).rot(0.23f, 30, 0, 0, Ease.IN).rot(0.33f, 30, 0, 0).rot(0.375f, -15, 0, 0).rot(0.45f, -55, 0, 0).rot(0.86f, -55, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.19f, 95, 0, 0).rot(0.23f, 0, 0, 0, Ease.IN).rot(0.33f, 35, 0, 0).rot(0.375f, 60, 0, 0).rot(0.45f, 100, 0, 0).rot(0.86f, 100, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.19f, 90, 0, 0).rot(0.23f, 5, 0, 0, Ease.IN).rot(0.33f, 50, 0, 0).rot(0.375f, 55, 0, 0).rot(0.45f, 95, 0, 0).rot(0.86f, 95, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /** 4: lunge grab-bite, 40t. Grab tick 8 (t=.2), bites 19/29 (t=.475/.725), shove-off release. */
    static final AnimClip LUNGE = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.1f, -8, 0, 0).rot(0.2f, 18, 0, 0, Ease.IN).rot(0.4f, 10, 0, 0).rot(0.475f, 22, 0, 0, Ease.SNAP).rot(0.55f, 12, 0, 4)
            .rot(0.65f, 10, 0, 0).rot(0.725f, 24, 0, 0, Ease.SNAP).rot(0.8f, 12, 0, -4).rot(0.88f, -10, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.1f, 0, 1.5f, 2).pos(0.2f, 0, 1, -5, Ease.IN).pos(0.8f, 0, 1, -4.5f).pos(0.88f, 0, 0.5f, -1).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.1f, -10, 0, 0).rot(0.2f, 20, 0, 0, Ease.IN).rot(0.8f, 15, 0, 0).rot(0.88f, -5, 0, 0).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.1f, -15, 0, 0).rot(0.2f, 15, 0, 0, Ease.IN).rot(0.4f, -8, 0, 0).rot(0.475f, 18, 0, 0, Ease.SNAP).rot(0.55f, 10, 12, 0)
            .rot(0.65f, -8, 0, 0).rot(0.725f, 20, 0, 0, Ease.SNAP).rot(0.8f, 10, -12, 0).rot(0.88f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.1f, -20, 0, 0).rot(0.2f, 5, 0, 0).rot(0.4f, -25, 0, 0).rot(0.475f, 25, 0, 0, Ease.SNAP).rot(0.55f, 18, 0, 12)
            .rot(0.65f, -25, 0, 0).rot(0.725f, 28, 0, 0, Ease.SNAP).rot(0.8f, 18, 0, -12).rot(0.9f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.1f, -25, 0, 0).rot(0.2f, 5, 0, 0).rot(0.4f, -30, 0, 0).rot(0.475f, 30, 0, 0, Ease.SNAP).rot(0.55f, 22, 22, 10)
            .rot(0.65f, -30, 0, 0).rot(0.725f, 32, 0, 0, Ease.SNAP).rot(0.8f, 22, -22, -10).rot(0.9f, -15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.1f, 40, 0, 0).rot(0.2f, 30, 0, 0).rot(0.4f, 55, 0, 0).rot(0.475f, 0, 0, 0, Ease.SNAP).rot(0.55f, 10, 0, 0)
            .rot(0.65f, 55, 0, 0).rot(0.725f, 0, 0, 0, Ease.SNAP).rot(0.8f, 12, 0, 0).rot(0.9f, 40, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.1f, -60, 0, 60).rot(0.2f, -80, 0, -30, Ease.IN).rot(0.8f, -75, 0, -35).rot(0.88f, -40, 0, 10).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.1f, -60, 0, -60).rot(0.2f, -80, 0, 30, Ease.IN).rot(0.8f, -75, 0, 35).rot(0.88f, -40, 0, -10).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.1f, -20, 0, 0).rot(0.2f, 0, 0, 0, Ease.IN).rot(0.3f, -60, 0, 0).rot(0.8f, -60, 0, 0).rot(0.88f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.1f, -20, 0, 0).rot(0.2f, 0, 0, 0, Ease.IN).rot(0.3f, -60, 0, 0).rot(0.8f, -60, 0, 0).rot(0.88f, 0, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightHand").rot(0, 0, 0, 0).rot(0.1f, -30, 0, 0).rot(0.2f, 15, 0, 0).rot(0.8f, 15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftHand").rot(0, 0, 0, 0).rot(0.1f, -30, 0, 0).rot(0.2f, 15, 0, 0).rot(0.8f, 15, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightClaws").rot(0, 0, 0, 0).rot(0.1f, -45, 0, 0).rot(0.18f, -45, 0, 0).rot(0.22f, 60, 0, 0, Ease.SNAP).rot(0.8f, 60, 0, 0).rot(0.88f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftClaws").rot(0, 0, 0, 0).rot(0.1f, -45, 0, 0).rot(0.18f, -45, 0, 0).rot(0.22f, 60, 0, 0, Ease.SNAP).rot(0.8f, 60, 0, 0).rot(0.88f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.1f, 10, 0, 0).rot(0.2f, -45, 0, 0, Ease.IN).rot(0.8f, -40, 0, 0).rot(0.9f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.1f, 30, 0, 0).rot(0.2f, 30, 0, 0).rot(0.8f, 30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.1f, -10, 0, 0).rot(0.2f, 25, 0, 0, Ease.IN).rot(0.8f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.1f, 30, 0, 0).rot(0.2f, 15, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /** 5: scream on spotting prey, 30t, scream burst tick 10 (t=.333): gathers in, then rears up arms flung wide, trembling shriek. */
    static final AnimClip SCREAM = AnimClip.create()
        .bone("body").rot(0, 0, 0, 0).rot(0.2f, 15, 0, 0).rot(0.333f, -15, 0, 0, Ease.OUT).rot(0.45f, -17, 0, 2).rot(0.55f, -14, 0, -2).rot(0.65f, -17, 0, 2).rot(0.75f, -15, 0, 0).rot(1, 0, 0, 0)
            .pos(0, 0, 0, 0).pos(0.2f, 0, 2, 0).pos(0.333f, 0, -1.5f, 0, Ease.OUT).pos(0.75f, 0, -1.5f, 0).pos(1, 0, 0, 0).done()
        .bone("waist").rot(0, 0, 0, 0).rot(0.2f, 15, 0, 0).rot(0.333f, -25, 0, 0, Ease.OUT).rot(0.75f, -25, 0, 0).rot(1, 0, 0, 0).done()
        .bone("chest").rot(0, 0, 0, 0).rot(0.2f, 15, 0, 0).rot(0.333f, -30, 0, 0, Ease.OUT).rot(0.45f, -33, 0, 3).rot(0.55f, -28, 0, -3).rot(0.65f, -33, 0, 3).rot(0.75f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("neck").rot(0, 0, 0, 0).rot(0.2f, 20, 0, 0).rot(0.333f, -30, 0, 0, Ease.OUT).rot(0.75f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("head").rot(0, 0, 0, 0).rot(0.2f, 25, 0, 0).rot(0.333f, -40, 0, 0, Ease.OUT).rot(0.4f, -42, 6, 0).rot(0.47f, -40, -6, 0).rot(0.54f, -42, 6, 0).rot(0.61f, -40, -6, 0).rot(0.75f, -40, 0, 0).rot(1, 0, 0, 0).done()
        .bone("jaw").rot(0, 0, 0, 0).rot(0.2f, 5, 0, 0).rot(0.333f, 65, 0, 0, Ease.OUT).rot(0.75f, 60, 0, 0).rot(0.85f, 20, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightArm").rot(0, 0, 0, 0).rot(0.2f, 20, 0, -10).rot(0.333f, 30, 0, 80, Ease.OUT).rot(0.45f, 28, 0, 84).rot(0.55f, 32, 0, 78).rot(0.65f, 28, 0, 84).rot(0.75f, 30, 0, 80).rot(1, 0, 0, 0).done()
        .bone("leftArm").rot(0, 0, 0, 0).rot(0.2f, 20, 0, 10).rot(0.333f, 30, 0, -80, Ease.OUT).rot(0.45f, 28, 0, -84).rot(0.55f, 32, 0, -78).rot(0.65f, 28, 0, -84).rot(0.75f, 30, 0, -80).rot(1, 0, 0, 0).done()
        .bone("rightForearm").rot(0, 0, 0, 0).rot(0.2f, -60, 0, 0).rot(0.333f, -10, 0, 0).rot(0.75f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftForearm").rot(0, 0, 0, 0).rot(0.2f, -60, 0, 0).rot(0.333f, -10, 0, 0).rot(0.75f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightHand").rot(0, 0, 0, 0).rot(0.333f, -30, 0, 0).rot(0.75f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftHand").rot(0, 0, 0, 0).rot(0.333f, -30, 0, 0).rot(0.75f, -30, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightClaws").rot(0, 0, 0, 0).rot(0.2f, 40, 0, 0).rot(0.333f, -60, 0, 0, Ease.OUT).rot(0.45f, -50, 0, 0).rot(0.55f, -60, 0, 0).rot(0.65f, -50, 0, 0).rot(0.75f, -60, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftClaws").rot(0, 0, 0, 0).rot(0.2f, 40, 0, 0).rot(0.333f, -60, 0, 0, Ease.OUT).rot(0.45f, -50, 0, 0).rot(0.55f, -60, 0, 0).rot(0.65f, -50, 0, 0).rot(0.75f, -60, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightThigh").rot(0, 0, 0, 0).rot(0.2f, -20, 0, 0).rot(0.333f, 10, 0, 0).rot(0.75f, 10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftThigh").rot(0, 0, 0, 0).rot(0.2f, -20, 0, 0).rot(0.333f, 10, 0, 0).rot(0.75f, 10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("rightShin").rot(0, 0, 0, 0).rot(0.2f, 30, 0, 0).rot(0.333f, -10, 0, 0).rot(0.75f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .bone("leftShin").rot(0, 0, 0, 0).rot(0.2f, 30, 0, 0).rot(0.333f, -10, 0, 0).rot(0.75f, -10, 0, 0).rot(1, 0, 0, 0).done()
        .build();

    /** 6: rise from the zombie corpse, 40t, scream at tick 22 (t=.55): claws scratch the floor, jerks up, kneeling shriek, springs to its feet. */
    static final AnimClip RISE = rise();

    /** death, driven by deathTime: knees buckle, thrown backwards onto its back. */
    static final AnimClip DEATH = death();

    private static AnimClip rise() {
        AnimClip.Builder c = AnimClip.create();
        track(c, "body", LIE, null, k(0.15f, 88, 0, 4, 0, 9.5f, 5), k(0.22f, 92, 0, -4, 0, 9.5f, 5), k(0.35f, 60, 0, 0, 0, 7, 3), k(0.5f, 10, 0, 0, 0, 4, 1),
            k(0.55f, -20, 0, 0, 0, 3.5f, 1), k(0.7f, -15, 0, 0, 0, 3, 1), k(0.85f, 20, 0, 0, 0, 1.5f, -1));
        track(c, "waist", LIE, null, k(0.35f, 10, 0, 0), k(0.5f, 0, 0, 0), k(0.55f, -20, 0, 0), k(0.7f, -20, 0, 0), k(0.85f, 15, 0, 0));
        track(c, "chest", LIE, null, k(0.15f, -10, 0, 0), k(0.35f, -25, 0, 0), k(0.55f, -30, 0, 0), k(0.7f, -28, 0, 0), k(0.85f, 20, 0, 0));
        track(c, "neck", LIE, null, k(0.15f, -20, 0, 0), k(0.35f, -30, 0, 0), k(0.55f, -30, 0, 0), k(0.85f, 0, 0, 0));
        track(c, "head", LIE, null, k(0.12f, 0, -75, 0), k(0.18f, -20, -30, 0), k(0.35f, -35, 0, 0), k(0.55f, -45, 0, 0), k(0.7f, -40, 8, 0), k(0.85f, -10, 0, 0));
        track(c, "jaw", LIE, null, k(0.18f, 40, 0, 0), k(0.35f, 20, 0, 0), k(0.55f, 65, 0, 0), k(0.72f, 55, 0, 0), k(0.85f, 20, 0, 0));
        track(c, "rightArm", LIE, null, k(0.15f, -10, 0, 30), k(0.3f, -80, 0, 15), k(0.45f, -20, 0, 20), k(0.55f, 30, 0, 75), k(0.72f, 30, 0, 70), k(0.85f, -40, 0, 20));
        track(c, "leftArm", LIE, null, k(0.15f, -150, 0, -30), k(0.3f, -80, 0, -15), k(0.45f, -20, 0, -20), k(0.55f, 30, 0, -75), k(0.72f, 30, 0, -70), k(0.85f, -40, 0, -20));
        track(c, "rightForearm", LIE, null, k(0.3f, -10, 0, 0), k(0.45f, -40, 0, 0), k(0.55f, -10, 0, 0), k(0.85f, -30, 0, 0));
        track(c, "leftForearm", LIE, null, k(0.3f, -10, 0, 0), k(0.45f, -40, 0, 0), k(0.55f, -10, 0, 0), k(0.85f, -30, 0, 0));
        track(c, "rightHand", LIE, null, k(0.3f, 60, 0, 0), k(0.45f, 0, 0, 0));
        track(c, "leftHand", LIE, null, k(0.3f, 60, 0, 0), k(0.45f, 0, 0, 0));
        track(c, "rightClaws", LIE, null, k(0.08f, -20, 0, 0), k(0.12f, 40, 0, 0), k(0.16f, -20, 0, 0), k(0.3f, -30, 0, 0), k(0.55f, -60, 0, 0), k(0.72f, -50, 0, 0));
        track(c, "leftClaws", LIE, null, k(0.06f, -10, 0, 0), k(0.1f, 50, 0, 0), k(0.14f, -15, 0, 0), k(0.3f, -30, 0, 0), k(0.55f, -60, 0, 0), k(0.72f, -50, 0, 0));
        track(c, "rightThigh", LIE, null, k(0.35f, -40, 0, 5), k(0.5f, -30, 0, 0), k(0.7f, -35, 0, 0), k(0.85f, -60, 0, 0));
        track(c, "rightShin", LIE, null, k(0.35f, 90, 0, 0), k(0.5f, 110, 0, 0), k(0.7f, 100, 0, 0), k(0.85f, 60, 0, 0));
        track(c, "leftThigh", LIE, null, k(0.35f, -30, 0, 0), k(0.5f, -35, 0, 0), k(0.7f, -35, 0, 0), k(0.85f, -15, 0, 0));
        track(c, "leftShin", LIE, null, k(0.35f, 95, 0, 0), k(0.5f, 110, 0, 0), k(0.7f, 100, 0, 0), k(0.85f, 40, 0, 0));
        return c.build();
    }

    private static AnimClip death() {
        AnimClip.Builder c = AnimClip.create();
        track(c, "body", STAND, BACK, k(0.2f, -10, 0, 0, 0, 3, 0), k(0.45f, -40, 0, 10, 0, 6, -2), k(0.75f, -95, 0, 0, 0, 10.5f, -5));
        track(c, "waist", STAND, BACK, k(0.2f, 25, 0, 0), k(0.45f, -10, 0, 0));
        track(c, "chest", STAND, BACK, k(0.2f, -25, 0, 0), k(0.45f, -15, 0, 0));
        track(c, "neck", STAND, BACK, k(0.2f, -30, 0, 0), k(0.75f, 20, 0, 0));
        track(c, "head", STAND, BACK, k(0.2f, -40, 0, 0), k(0.45f, -20, 20, 0), k(0.75f, 15, 40, 0));
        track(c, "jaw", STAND, BACK, k(0.2f, 60, 0, 0));
        track(c, "rightArm", STAND, BACK, k(0.2f, -60, 0, 70), k(0.45f, -130, 0, 50));
        track(c, "leftArm", STAND, BACK, k(0.2f, -40, 0, -80), k(0.45f, -110, 0, -60));
        track(c, "rightForearm", STAND, BACK, k(0.2f, -10, 0, 0));
        track(c, "leftForearm", STAND, BACK, k(0.2f, -10, 0, 0));
        track(c, "rightHand", STAND, BACK);
        track(c, "leftHand", STAND, BACK);
        track(c, "rightClaws", STAND, BACK, k(0.2f, -60, 0, 0));
        track(c, "leftClaws", STAND, BACK, k(0.2f, -60, 0, 0));
        track(c, "rightThigh", STAND, BACK, k(0.2f, -50, 0, 6), k(0.45f, -40, 0, 0));
        track(c, "rightShin", STAND, BACK, k(0.2f, 90, 0, 0), k(0.45f, 60, 0, 0));
        track(c, "leftThigh", STAND, BACK, k(0.2f, -45, 0, -6), k(0.45f, -20, 0, 0));
        track(c, "leftShin", STAND, BACK, k(0.2f, 85, 0, 0), k(0.45f, 30, 0, 0));
        return c.build();
    }

    @Override
    protected AnimClip clip(CrimsonHeadEntity e, int moveId) {
        switch (moveId) {
            case CrimsonHeadEntity.SLASH2: return SLASH2;
            case CrimsonHeadEntity.SLASH3: return SLASH3;
            case CrimsonHeadEntity.LEAP: return LEAP;
            case CrimsonHeadEntity.LUNGE_GRAB: return LUNGE;
            case CrimsonHeadEntity.SCREAM: return SCREAM;
            case CrimsonHeadEntity.RISE: return RISE;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ procedural base

    @Override
    protected void baseAnim(CrimsonHeadEntity e, float ls, float la, float age, float yaw, float pitch) {
        float partial = age - e.tickCount;
        if (e.deathTime > 0) {
            applyClip(DEATH, Anim.clamp01((e.deathTime + partial) / 16f));
            return;
        }
        int move = e.getMoveId();
        if (move == CrimsonHeadEntity.RISE) {
            standing(0f, 0f, age, yaw, pitch, Anim.window(moveProgress(e, partial), 0.75f, 1f));
            return;
        }
        if (move != 0) la *= 0.3f;
        standing(ls, la, age, yaw, pitch, 1f);
    }

    private static float twitch(float age, float speed, float phase) {
        return (float) Math.pow(Math.max(0f, Mth.sin(age * speed + phase)), 14);
    }

    /** Idle: coiled crouch, heaving breath, jerky head twitches, flexing claws. Run: low sprint, long strides, arms whipping back. */
    private void standing(float ls, float la, float age, float yaw, float pitch, float k) {
        if (k <= 0f) return;
        applyPose(this::bone, STAND, k);
        float run = Mth.clamp(la * 1.3f, 0f, 1f);
        float idle = 1f - run;
        float p = ls * 0.62f;
        float s = Mth.sin(p), c = Mth.cos(p);
        float breathe = Mth.sin(age * 0.25f);
        bone("body").y += ((1.0f - Math.abs(c) * 1.6f) * run + breathe * 0.3f * idle) * k;
        bone("body").yRot -= s * 0.12f * run * k;
        bone("body").zRot += c * 0.05f * run * k;
        bone("waist").xRot += (0.25f * run + breathe * 0.04f * idle) * k;
        bone("chest").xRot += (0.2f * run + Mth.sin(age * 0.25f + 0.5f) * 0.06f * idle) * k;
        bone("chest").yRot += s * 0.25f * run * k;
        bone("neck").xRot -= 0.25f * run * k;
        bone("neck").yRot += twitch(age, 0.31f, 1f) * 0.35f * idle * k;
        bone("head").xRot += (pitch * R * 0.6f - 0.25f * run) * k;
        bone("head").yRot += yaw * R * 0.8f * k;
        bone("head").zRot += (twitch(age, 0.23f, 0f) * 0.5f - twitch(age, 0.19f, 2f) * 0.4f) * k;
        bone("jaw").xRot += (Mth.sin(age * 0.4f) * 0.08f + 0.2f * run) * k;
        // arms whip back while sprinting, opposite to the legs
        bone("rightArm").xRot += (0.6f * run + c * 0.9f * run + Mth.sin(age * 0.11f) * 0.05f * idle) * k;
        bone("rightArm").zRot += 0.2f * run * k;
        bone("leftArm").xRot += (0.6f * run - c * 0.9f * run + Mth.sin(age * 0.11f + 1.7f) * 0.05f * idle) * k;
        bone("leftArm").zRot -= 0.2f * run * k;
        bone("rightForearm").xRot += (-0.3f * run - Math.max(0f, -c) * 0.6f * run) * k;
        bone("leftForearm").xRot += (-0.3f * run - Math.max(0f, c) * 0.6f * run) * k;
        bone("rightClaws").xRot += (-0.3f * run + Mth.sin(age * 0.6f) * 0.15f * idle) * k;
        bone("leftClaws").xRot += (-0.3f * run + Mth.sin(age * 0.6f + 2f) * 0.15f * idle) * k;
        // sprinting legs: long strides, high knee recovery
        bone("rightThigh").xRot += -c * 1.1f * run * k;
        bone("leftThigh").xRot += c * 1.1f * run * k;
        bone("rightShin").xRot += Math.max(0f, s) * 1.3f * run * k;
        bone("leftShin").xRot += Math.max(0f, -s) * 1.3f * run * k;
    }

    @Override
    protected void extraAnim(CrimsonHeadEntity e, float ls, float la, float age, float yaw, float pitch) {
        float partial = age - e.tickCount;
        if (e.deathTime > 0) {
            float w = 1f - Anim.clamp01((e.deathTime + partial) / 20f);
            bone("rightClaws").xRot += Mth.sin(age * 1.7f) * 0.6f * w;
            bone("leftClaws").xRot += Mth.sin(age * 1.5f + 1f) * 0.6f * w;
            bone("rightShin").xRot += Math.max(0f, Mth.sin(age * 0.9f)) * 0.5f * w;
            bone("head").yRot += Mth.sin(age * 1.2f) * 0.25f * w;
            return;
        }
        if (e.hurtTime > 0) {
            float h = Mth.clamp((e.hurtTime - partial) / 10f, 0f, 1f);
            h *= h;
            bone("chest").xRot -= 0.4f * h;
            bone("chest").yRot += 0.25f * h;
            bone("head").xRot -= 0.45f * h;
            bone("jaw").xRot += 0.6f * h;          // snarls at the pain
            bone("rightArm").zRot += 0.4f * h;
            bone("leftArm").zRot -= 0.4f * h;
            bone("rightClaws").xRot -= 0.5f * h;
            bone("leftClaws").xRot -= 0.5f * h;
            bone("body").z += 1.0f * h;
        }
    }
}
