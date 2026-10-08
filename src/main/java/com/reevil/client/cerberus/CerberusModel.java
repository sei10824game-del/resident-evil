package com.reevil.client.cerberus;

import com.reevil.ReEvilMod;
import com.reevil.anim.Anim;
import com.reevil.anim.AnimClip;
import com.reevil.anim.AnimClip.Ease;
import com.reevil.client.BioModel;
import com.reevil.entity.cerberus.CerberusEntity;
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
 * Zombie dobermann. Bones: body > chest(neck>head>jaw/ears, front legs upper>lower>paw) + hips(hind legs thigh>shin>hock, tail1>2>3).
 * Model faces -Z. Texture 64x64, UVs mirrored in tools/tex_cerberus.py.
 */
public class CerberusModel extends BioModel<CerberusEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "cerberus"), "main");
    private static final float D = (float) (Math.PI / 180.0);
    private static final float PI = (float) Math.PI;

    private final ModelPart body, chest, hips, neck, head, jaw, earL, earR;
    private final ModelPart flUpper, flLower, flPaw, frUpper, frLower, frPaw;
    private final ModelPart hlThigh, hlShin, hlHock, hrThigh, hrShin, hrHock;
    private final ModelPart tail1, tail2, tail3;

    public CerberusModel(ModelPart root) {
        super(root);
        body = reg("body", "body");
        chest = reg("chest", "body/chest");
        hips = reg("hips", "body/hips");
        neck = reg("neck", "body/chest/neck");
        head = reg("head", "body/chest/neck/head");
        jaw = reg("jaw", "body/chest/neck/head/jaw");
        earL = reg("earL", "body/chest/neck/head/earL");
        earR = reg("earR", "body/chest/neck/head/earR");
        flUpper = reg("flUpper", "body/chest/flUpper");
        flLower = reg("flLower", "body/chest/flUpper/flLower");
        flPaw = reg("flPaw", "body/chest/flUpper/flLower/flPaw");
        frUpper = reg("frUpper", "body/chest/frUpper");
        frLower = reg("frLower", "body/chest/frUpper/frLower");
        frPaw = reg("frPaw", "body/chest/frUpper/frLower/frPaw");
        hlThigh = reg("hlThigh", "body/hips/hlThigh");
        hlShin = reg("hlShin", "body/hips/hlThigh/hlShin");
        hlHock = reg("hlHock", "body/hips/hlThigh/hlShin/hlHock");
        hrThigh = reg("hrThigh", "body/hips/hrThigh");
        hrShin = reg("hrShin", "body/hips/hrThigh/hrShin");
        hrHock = reg("hrHock", "body/hips/hrThigh/hrShin/hrHock");
        tail1 = reg("tail1", "body/hips/tail1");
        tail2 = reg("tail2", "body/hips/tail1/tail2");
        tail3 = reg("tail3", "body/hips/tail1/tail2/tail3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0f, 13f, 0f));

        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-3.5f, -3.5f, -10f, 7, 7, 10)
            .texOffs(0, 28).addBox(-0.5f, -4.5f, -9f, 1, 1, 8), PartPose.ZERO);
        PartDefinition hips = body.addOrReplaceChild("hips", CubeListBuilder.create()
            .texOffs(34, 0).addBox(-3f, -3f, 0f, 6, 6, 9), PartPose.ZERO);

        PartDefinition neck = chest.addOrReplaceChild("neck", CubeListBuilder.create()
            .texOffs(22, 17).addBox(-2f, -2.5f, -5f, 4, 5, 5), PartPose.offsetAndRotation(0f, -2f, -9f, -0.6f, 0f, 0f));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(0, 17).addBox(-3f, -3f, -4f, 6, 6, 5)
            .texOffs(18, 28).addBox(-1.5f, -1f, -9f, 3, 3, 5)
            .texOffs(50, 37).addBox(-1.5f, 2f, -8.5f, 1, 1, 1)
            .texOffs(50, 37).addBox(0.5f, 2f, -8.5f, 1, 1, 1), PartPose.offsetAndRotation(0f, -0.5f, -4.5f, 0.6f, 0f, 0f));
        head.addOrReplaceChild("jaw", CubeListBuilder.create()
            .texOffs(34, 28).addBox(-1.5f, 0f, -5f, 3, 2, 5), PartPose.offset(0f, 2f, -3.5f));
        head.addOrReplaceChild("earL", CubeListBuilder.create()
            .texOffs(58, 28).addBox(-1f, -4f, -0.5f, 2, 4, 1), PartPose.offsetAndRotation(2f, -3f, -1f, 0f, 0f, 0.2f));
        head.addOrReplaceChild("earR", CubeListBuilder.create()
            .texOffs(58, 28).addBox(-1f, -4f, -0.5f, 2, 4, 1), PartPose.offsetAndRotation(-2f, -3f, -1f, 0f, 0f, -0.2f));

        for (int s = 0; s < 2; s++) {
            String p = s == 0 ? "fl" : "fr";
            float x = s == 0 ? 2f : -2f;
            PartDefinition up = chest.addOrReplaceChild(p + "Upper", CubeListBuilder.create()
                .texOffs(40, 17).addBox(-1f, -1f, -1.5f, 2, 6, 3), PartPose.offset(x, 2f, -7.5f));
            PartDefinition lo = up.addOrReplaceChild(p + "Lower", CubeListBuilder.create()
                .texOffs(0, 37).addBox(-1f, 0f, -1f, 2, 3, 2), PartPose.offset(0f, 5f, 0f));
            lo.addOrReplaceChild(p + "Paw", CubeListBuilder.create()
                .texOffs(18, 37).addBox(-1f, 0f, -2f, 2, 1, 3), PartPose.offset(0f, 3f, 0f));

            String h = s == 0 ? "hl" : "hr";
            PartDefinition th = hips.addOrReplaceChild(h + "Thigh", CubeListBuilder.create()
                .texOffs(50, 17).addBox(-1f, -1f, -1.5f, 2, 6, 3), PartPose.offsetAndRotation(x, 0.5f, 7.5f, -0.35f, 0f, 0f));
            PartDefinition sh = th.addOrReplaceChild(h + "Shin", CubeListBuilder.create()
                .texOffs(50, 28).addBox(-1f, 0f, -1f, 2, 4, 2), PartPose.offsetAndRotation(0f, 5f, 0f, 0.7f, 0f, 0f));
            sh.addOrReplaceChild(h + "Hock", CubeListBuilder.create()
                .texOffs(28, 37).addBox(-1f, 0f, -1f, 2, 2, 2)
                .texOffs(18, 37).addBox(-1f, 1f, -2f, 2, 1, 3), PartPose.offsetAndRotation(0f, 4f, 0f, -0.35f, 0f, 0f));
        }

        PartDefinition t1 = hips.addOrReplaceChild("tail1", CubeListBuilder.create()
            .texOffs(8, 37).addBox(-1f, -1f, 0f, 2, 2, 3), PartPose.offsetAndRotation(0f, -2f, 8.5f, 0.5f, 0f, 0f));
        PartDefinition t2 = t1.addOrReplaceChild("tail2", CubeListBuilder.create()
            .texOffs(36, 37).addBox(-1f, -1f, 0f, 2, 2, 2), PartPose.offsetAndRotation(0f, 0f, 2.6f, 0.15f, 0f, 0f));
        t2.addOrReplaceChild("tail3", CubeListBuilder.create()
            .texOffs(44, 37).addBox(-0.5f, -0.5f, 0f, 1, 1, 2), PartPose.offsetAndRotation(0f, 0f, 1.8f, 0.1f, 0f, 0f));
        return LayerDefinition.create(mesh, 64, 64);
    }

    // ------------------------------------------------------------------ gait / idle

    @Override
    protected void baseAnim(CerberusEntity e, float ls, float lsa, float age, float yaw, float pitch) {
        float a = Math.min(1f, lsa);
        if (e.getMoveId() > 0 || e.deathTime > 0) a *= 0.25f;
        float g = Anim.smooth((a - 0.45f) / 0.4f);           // 0 = trot, 1 = rotary gallop
        float ph = ls * 0.66f;
        float id = e.getId() * 1.7f;

        // each leg has its own phase: trot = diagonal pairs (FL+HR / FR+HL), gallop = hinds lead, fronts follow
        frontLeg(flUpper, flLower, flPaw, ph + Anim.lerp(0f, PI, g), a, g);
        frontLeg(frUpper, frLower, frPaw, ph + Anim.lerp(PI, PI + 0.6f, g), a, g);
        hindLeg(hlThigh, hlShin, hlHock, ph + Anim.lerp(PI, 0f, g), a, g);
        hindLeg(hrThigh, hrShin, hrHock, ph + Anim.lerp(0f, 0.6f, g), a, g);

        // spine: gallop flexes chest/hips against each other (gather / extend), trot rolls side to side
        float flex = Mth.cos(ph + 0.5f);
        chest.xRot += flex * 0.12f * g * a;
        hips.xRot -= flex * 0.16f * g * a;
        body.xRot += Mth.sin(ph) * 0.07f * g * a;
        body.y += (-Math.abs(Mth.sin(ph)) * 1.4f * g + Mth.sin(ph * 2f) * 0.35f * (1f - g)) * a;
        body.zRot += Mth.sin(ph) * 0.04f * (1f - g) * a;
        hips.yRot += Mth.sin(ph) * 0.07f * (1f - g) * a;

        // head carriage: lowered & extended at the gallop, bobbing with the stride
        neck.xRot += 0.35f * g * a + Mth.sin(ph * 2f) * 0.06f * a;
        head.xRot -= 0.2f * g * a;
        earL.xRot -= 0.6f * g * a;
        earR.xRot -= 0.6f * g * a;
        tail1.xRot -= 0.35f * a;
        tail1.yRot += Mth.sin(ph) * 0.25f * a;
        tail2.yRot += Mth.sin(ph - 0.6f) * 0.2f * a;
        tail3.yRot += Mth.sin(ph - 1.2f) * 0.2f * a;
        jaw.xRot += 0.12f + 0.18f * a + Mth.sin(age * 0.5f) * 0.05f * a;

        // idle: breathing, scanning, zombie tics, ear twitches, drooling jaw
        float still = 1f - a;
        float breath = Mth.sin(age * 0.1f);
        body.y += breath * 0.2f * still;
        chest.xRot += breath * 0.015f * still;
        neck.yRot += Mth.sin(age * 0.03f + id) * 0.25f * still + yaw * D * 0.4f;
        head.yRot += yaw * D * 0.5f;
        head.xRot += pitch * D * 0.6f;
        float tic = (float) Math.pow(Math.max(0f, Mth.sin(age * 0.07f + id)), 30);
        head.zRot += tic * 0.4f * Mth.sin(age * 1.7f);
        neck.xRot += tic * 0.15f;
        earL.zRot += (float) Math.pow(Math.max(0f, Mth.sin(age * 0.23f + id)), 16) * 0.35f;
        earR.zRot -= (float) Math.pow(Math.max(0f, Mth.sin(age * 0.19f + id * 2f)), 16) * 0.35f;
        tail1.yRot += Mth.sin(age * 0.15f) * 0.12f * still;
        jaw.xRot += Math.max(0f, Mth.sin(age * 0.05f + id)) * 0.12f * still;
    }

    private static void frontLeg(ModelPart up, ModelPart lo, ModelPart paw, float p, float a, float g) {
        float s = Mth.sin(p), c = Mth.cos(p);
        float swing = Math.max(0f, -c);                       // leg travelling forward = lifted
        up.xRot += s * (0.55f + 0.35f * g) * a;
        lo.xRot += swing * (0.8f + 0.5f * g) * a;             // carpus folds during swing
        paw.xRot += swing * 0.6f * a - Math.max(0f, c) * 0.15f * a;
    }

    private static void hindLeg(ModelPart th, ModelPart sh, ModelPart hk, float p, float a, float g) {
        float s = Mth.sin(p), c = Mth.cos(p);
        float swing = Math.max(0f, -c);
        th.xRot += s * (0.5f + 0.35f * g) * a;
        sh.xRot += swing * (0.7f + 0.4f * g) * a;             // stifle/hock flex during swing
        hk.xRot += -swing * 0.5f * a + Math.max(0f, s) * 0.25f * a; // push-off extension
    }

    // ------------------------------------------------------------------ hurt + death

    @Override
    protected void extraAnim(CerberusEntity e, float ls, float lsa, float age, float yaw, float pitch) {
        float partial = age - e.tickCount;
        if (e.deathTime > 0) {
            death(e, Math.min(1f, (e.deathTime + partial) / 18f), age);
            return;
        }
        if (e.hurtTime > 0 && e.getMoveId() != CerberusEntity.YELP) {
            float k = Math.max(0f, (e.hurtTime - partial) / 10f);
            k = k * k;
            body.z += k * 1.5f;
            chest.zRot += k * 0.15f;
            hips.zRot -= k * 0.1f;
            neck.xRot -= k * 0.4f;
            head.zRot += k * 0.3f;
            jaw.xRot += k * 0.5f;
            earL.xRot -= k * 0.7f;
            earR.xRot -= k * 0.7f;
            tail1.xRot -= k * 0.6f;
            flUpper.zRot += k * 0.2f;
            frUpper.zRot -= k * 0.2f;
        }
    }

    private void death(CerberusEntity e, float d, float age) {
        float buckle = Anim.smooth(Anim.window(d, 0f, 0.35f));
        float roll = Anim.smooth(Anim.window(d, 0.25f, 0.75f));
        float settle = Anim.window(d, 0.75f, 1f);
        float up = 1f - roll;
        body.y += buckle * 3f * up + roll * 7.5f;
        body.zRot += roll * 1.5f;
        body.xRot += buckle * 0.15f * up;
        // legs buckle under, then go stiff and paddle in a last spasm
        flUpper.xRot += buckle * 0.9f * up - roll * 0.3f;
        frUpper.xRot += buckle * 0.9f * up - roll * 0.5f;
        flLower.xRot -= buckle * 1.2f * up;
        frLower.xRot -= buckle * 1.2f * up;
        hlThigh.xRot -= buckle * 0.8f * up - roll * 0.4f;
        hrThigh.xRot -= buckle * 0.8f * up - roll * 0.6f;
        hlShin.xRot += buckle * 1.0f * up;
        hrShin.xRot += buckle * 1.0f * up;
        float tw = (1f - settle) * roll * 0.3f * Mth.sin(age * 1.8f);
        flUpper.xRot += tw;
        frUpper.xRot -= tw * 0.7f;
        hlThigh.xRot -= tw;
        hrThigh.xRot += tw * 0.8f;
        flUpper.zRot -= roll * 0.25f;
        hlThigh.zRot -= roll * 0.2f;
        neck.xRot += buckle * 0.5f * up + roll * 0.3f;
        neck.yRot += roll * 0.3f;
        head.zRot += roll * 0.25f;
        jaw.xRot += 0.2f + roll * 0.45f;
        earL.xRot -= 0.5f * d;
        earR.xRot -= 0.5f * d;
        tail1.xRot -= roll * 0.6f;
        tail2.xRot -= roll * 0.2f;
    }

    // ------------------------------------------------------------------ attack clips

    @Override
    protected AnimClip clip(CerberusEntity e, int moveId) {
        switch (moveId) {
            case CerberusEntity.LEAP: return LEAP;
            case CerberusEntity.SNAP: return SNAP;
            case CerberusEntity.CLAW: return CLAW;
            case CerberusEntity.SHAKE: return SHAKE;
            case CerberusEntity.DIVE: return DIVE;
            case CerberusEntity.HOWL: return HOWL;
            case CerberusEntity.YELP: return YELP;
            default: return null;
        }
    }

    /** Front legs, keys {t, upperX, upperZ, lowerX, pawX}; right side mirrors Z and lags by lag. */
    private static void fronts(AnimClip.Builder b, float lag, float[]... k) {
        limb(b, "flUpper", "flLower", "flPaw", 0f, 1f, k);
        limb(b, "frUpper", "frLower", "frPaw", lag, -1f, k);
    }

    /** Hind legs, keys {t, thighX, shinX, hockX}; right side lags by lag. */
    private static void hinds(AnimClip.Builder b, float lag, float[]... k) {
        for (int s = 0; s < 2; s++) {
            String p = s == 0 ? "hl" : "hr";
            float l = s == 0 ? 0f : lag;
            AnimClip.BoneBuilder th = b.bone(p + "Thigh"), sh = b.bone(p + "Shin"), hk = b.bone(p + "Hock");
            for (float[] q : k) {
                float t = shift(q[0], l);
                th.rot(t, q[1], 0, 0);
                sh.rot(t, q[2], 0, 0);
                hk.rot(t, q[3], 0, 0);
            }
        }
    }

    private static void limb(AnimClip.Builder b, String u, String l, String p, float lag, float zs, float[][] k) {
        AnimClip.BoneBuilder bu = b.bone(u), bl = b.bone(l), bp = b.bone(p);
        for (float[] q : k) {
            float t = shift(q[0], lag);
            bu.rot(t, q[1], 0, q[2] * zs);
            bl.rot(t, q[3], 0, 0);
            bp.rot(t, q[4], 0, 0);
        }
    }

    private static float shift(float t, float lag) {
        return (t <= 0f || t >= 1f) ? t : Math.min(0.97f, t + lag);
    }

    private static void ears(AnimClip.Builder b, float[]... k) {
        AnimClip.BoneBuilder l = b.bone("earL"), r = b.bone("earR");
        for (float[] q : k) { l.rot(q[0], q[1], 0, 0); r.rot(q[0], q[1], 0, 0); }
    }

    /** Leaping bite: crouch, spring, airborne reach, jaws clamp (hit 14/30), thrash (hit 20/30), release. */
    private static final AnimClip LEAP;
    /** Snap bite: snarl wind-up, head strike (hit 7/14), tug back. */
    private static final AnimClip SNAP;
    /** Rear up on hind legs, rake both forepaws down (hit 12/22), land. */
    private static final AnimClip CLAW;
    /** Lunge & latch, then whip head side to side (hits 10,16,22,28 / 40), release. */
    private static final AnimClip SHAKE;
    /** Window dive: crouch, spring, crash through glass head-tucked, bite on landing (hit 16/26), shake off shards. */
    private static final AnimClip DIVE;
    /** Sit back, raise head, howl (peak 16/40) with chest heave and jaw vibrato. */
    private static final AnimClip HOWL;
    /** Hit stagger: yelp, recoil, stumble back on independent legs. */
    private static final AnimClip YELP;

    static {
        AnimClip.Builder b = AnimClip.create();
        b.bone("body").pos(0f, 0, 0, 0).pos(0.2f, 0, 3, 1).pos(0.27f, 0, -2, -3, Ease.OUT).pos(0.4f, 0, -2, -4)
            .pos(0.467f, 0, 0, -3, Ease.SNAP).pos(0.667f, 0, 0.5f, -2).pos(0.85f, 0, 0.5f, 0).pos(1f, 0, 0, 0)
            .rot(0f, 0, 0, 0).rot(0.2f, 4, 0, 0).rot(0.27f, -12, 0, 0, Ease.OUT).rot(0.4f, -4, 0, 0).rot(0.467f, 10, 0, 0, Ease.SNAP)
            .rot(0.55f, 8, 0, 6).rot(0.667f, 8, 0, -6).rot(0.78f, 6, 0, 3).rot(0.9f, 0, 0, 0).rot(1f, 0, 0, 0);
        b.bone("chest").rot(0f, 0, 0, 0).rot(0.2f, 8, 0, 0).rot(0.27f, -10, 0, 0, Ease.OUT).rot(0.4f, -4, 0, 0).rot(0.467f, 10, 0, 0, Ease.SNAP)
            .rot(0.55f, 8, 12, 0).rot(0.667f, 8, -14, 0).rot(0.78f, 6, 6, 0).rot(0.9f, 2, 0, 0).rot(1f, 0, 0, 0);
        b.bone("hips").rot(0f, 0, 0, 0).rot(0.2f, -12, 0, 0).rot(0.27f, 14, 0, 0, Ease.OUT).rot(0.4f, 6, 0, 0).rot(0.467f, -4, 0, 0)
            .rot(0.55f, 0, -10, 0).rot(0.667f, 0, 12, 0).rot(0.78f, 0, -5, 0).rot(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.2f, 18, 0, 0).rot(0.27f, -15, 0, 0).rot(0.4f, -8, 0, 0).rot(0.467f, 28, 0, 0, Ease.SNAP)
            .rot(0.55f, 25, 35, 0).rot(0.667f, 25, -40, 0).rot(0.78f, 18, 18, 0).rot(0.9f, -5, 0, 0).rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.2f, -12, 0, 0).rot(0.27f, 8, 0, 0).rot(0.4f, -6, 0, 0).rot(0.467f, 18, 0, 0, Ease.SNAP)
            .rot(0.55f, 14, 0, -28).rot(0.667f, 14, 0, 30).rot(0.78f, 8, 0, -12).rot(0.9f, -6, 0, 0).rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.2f, 8, 0, 0).rot(0.27f, 25, 0, 0).rot(0.4f, 48, 0, 0).rot(0.467f, 0, 0, 0, Ease.SNAP)
            .rot(0.78f, 0, 0, 0).rot(0.88f, 28, 0, 0).rot(1f, 0, 0, 0);
        ears(b, new float[]{0f, 0}, new float[]{0.2f, -35}, new float[]{0.27f, -55}, new float[]{0.467f, -25},
            new float[]{0.667f, -30}, new float[]{0.9f, -10}, new float[]{1f, 0});
        fronts(b, 0.02f, new float[]{0f, 0, 0, 0, 0}, new float[]{0.2f, 40, 0, -70, 30}, new float[]{0.27f, -70, 0, -15, -25},
            new float[]{0.4f, -90, -6, 0, -15}, new float[]{0.467f, -45, -12, 35, -30}, new float[]{0.55f, -35, -14, 30, -25},
            new float[]{0.667f, -40, -10, 30, -20}, new float[]{0.85f, -10, 0, 10, 0}, new float[]{1f, 0, 0, 0, 0});
        hinds(b, 0.03f, new float[]{0f, 0, 0, 0}, new float[]{0.2f, -38, 55, -25}, new float[]{0.27f, 65, -25, 35},
            new float[]{0.4f, 75, -10, 45}, new float[]{0.467f, 25, 25, -10}, new float[]{0.667f, -10, 30, -15},
            new float[]{0.85f, -5, 10, 0}, new float[]{1f, 0, 0, 0});
        b.bone("tail1").rot(0f, 0, 0, 0).rot(0.2f, -20, 0, 0).rot(0.27f, 25, 0, 0).rot(0.4f, 10, 0, 0).rot(0.467f, 20, 0, 0)
            .rot(0.55f, 15, -30, 0).rot(0.667f, 15, 30, 0).rot(0.78f, 10, -15, 0).rot(1f, 0, 0, 0);
        b.bone("tail2").rot(0f, 0, 0, 0).rot(0.27f, 10, 0, 0).rot(0.55f, 5, -25, 0).rot(0.667f, 5, 25, 0).rot(1f, 0, 0, 0);
        b.bone("tail3").rot(0f, 0, 0, 0).rot(0.55f, 0, -20, 0).rot(0.667f, 0, 20, 0).rot(1f, 0, 0, 0);
        LEAP = b.build();

        b = AnimClip.create();
        b.bone("body").pos(0f, 0, 0, 0).pos(0.3f, 0, 1, 2).pos(0.42f, 0, 0, -1).pos(0.5f, 0, 0, -3, Ease.OUT)
            .pos(0.65f, 0, 0, -1.5f).pos(1f, 0, 0, 0);
        b.bone("chest").rot(0f, 0, 0, 0).rot(0.3f, 6, 0, 0).rot(0.5f, -6, 0, 0, Ease.OUT).rot(0.65f, 2, 0, 0).rot(1f, 0, 0, 0);
        b.bone("hips").rot(0f, 0, 0, 0).rot(0.3f, -6, 0, 0).rot(0.5f, 6, 0, 0).rot(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.3f, -25, 0, 0).rot(0.42f, 15, 0, 0, Ease.IN).rot(0.5f, 35, 0, 0, Ease.OUT)
            .rot(0.65f, 12, 0, 0).rot(0.8f, 4, 0, 0).rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.3f, 12, 0, 0).rot(0.42f, -12, 0, 0).rot(0.5f, -8, 0, 0).rot(0.65f, 14, 0, 0)
            .rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.3f, 15, 0, 0).rot(0.42f, 42, 0, 0).rot(0.5f, 0, 0, 0, Ease.SNAP)
            .rot(0.65f, 4, 0, 0).rot(0.8f, 10, 0, 0).rot(1f, 0, 0, 0);
        ears(b, new float[]{0f, 0}, new float[]{0.3f, -35}, new float[]{0.5f, -20}, new float[]{1f, 0});
        fronts(b, 0.03f, new float[]{0f, 0, 0, 0, 0}, new float[]{0.3f, 18, 0, -25, 10}, new float[]{0.5f, -25, -5, 10, -10},
            new float[]{0.65f, -10, 0, 5, 0}, new float[]{1f, 0, 0, 0, 0});
        hinds(b, 0.03f, new float[]{0f, 0, 0, 0}, new float[]{0.3f, -18, 25, -12}, new float[]{0.5f, 25, -10, 20},
            new float[]{0.65f, 10, 0, 5}, new float[]{1f, 0, 0, 0});
        b.bone("tail1").rot(0f, 0, 0, 0).rot(0.3f, -15, 0, 0).rot(0.5f, 10, 0, 0).rot(0.65f, 5, 15, 0).rot(1f, 0, 0, 0);
        SNAP = b.build();

        b = AnimClip.create();
        b.bone("body").pos(0f, 0, 0, 0).pos(0.25f, 0, -3, 2).pos(0.45f, 0, -4.5f, 2).pos(0.545f, 0, -1, -2, Ease.IN)
            .pos(0.65f, 0, 1, -2).pos(0.8f, 0, 0.5f, -0.5f).pos(1f, 0, 0, 0)
            .rot(0f, 0, 0, 0).rot(0.25f, -35, 0, 0).rot(0.45f, -48, 0, -5).rot(0.545f, -12, 0, 4, Ease.IN)
            .rot(0.65f, 4, 0, -3).rot(0.8f, 1, 0, 0).rot(1f, 0, 0, 0);
        b.bone("chest").rot(0f, 0, 0, 0).rot(0.25f, -5, 0, 0).rot(0.45f, -8, 8, 0).rot(0.545f, 6, -10, 0).rot(0.65f, 8, 6, 0)
            .rot(1f, 0, 0, 0);
        b.bone("hips").rot(0f, 0, 0, 0).rot(0.25f, 10, 0, 0).rot(0.45f, 12, 0, 0).rot(0.545f, 4, 0, 0).rot(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.25f, 25, 0, 0).rot(0.45f, 30, -10, 0).rot(0.545f, 35, 8, 0).rot(0.65f, 15, 0, 0)
            .rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.25f, 12, 0, 0).rot(0.45f, 10, 0, 0).rot(0.545f, -12, 0, 0).rot(0.65f, 0, 0, 0)
            .rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.25f, 20, 0, 0).rot(0.45f, 30, 0, 0).rot(0.545f, 40, 0, 0).rot(0.65f, 15, 0, 0)
            .rot(0.8f, 5, 0, 0).rot(1f, 0, 0, 0);
        ears(b, new float[]{0f, 0}, new float[]{0.25f, 10}, new float[]{0.45f, -20}, new float[]{0.545f, -45}, new float[]{1f, 0});
        limb(b, "flUpper", "flLower", "flPaw", 0f, 1f, new float[][]{{0f, 0, 0, 0, 0}, {0.25f, -60, -10, -40, -20},
            {0.45f, -115, -15, -30, -35}, {0.545f, -10, -5, 15, 10}, {0.65f, 0, 0, 5, 0}, {0.8f, 8, 0, -10, 0}, {1f, 0, 0, 0, 0}});
        limb(b, "frUpper", "frLower", "frPaw", 0f, 1f, new float[][]{{0f, 0, 0, 0, 0}, {0.25f, -55, 10, -45, -20},
            {0.45f, -100, 15, -50, -30}, {0.545f, -70, 10, -30, -20}, {0.65f, -5, 5, 15, 10}, {0.8f, 6, 0, -8, 0}, {1f, 0, 0, 0, 0}});
        hinds(b, 0.02f, new float[]{0f, 0, 0, 0}, new float[]{0.25f, 30, 15, -10}, new float[]{0.45f, 38, 22, -14},
            new float[]{0.545f, 10, 10, -4}, new float[]{0.65f, -8, 15, 0}, new float[]{1f, 0, 0, 0});
        b.bone("tail1").rot(0f, 0, 0, 0).rot(0.25f, -25, 0, 0).rot(0.45f, -30, 0, 0).rot(0.545f, 10, 0, 0).rot(0.65f, 0, 15, 0)
            .rot(1f, 0, 0, 0);
        CLAW = b.build();

        b = AnimClip.create();
        b.bone("body").pos(0f, 0, 0, 0).pos(0.15f, 0, 0, -3, Ease.OUT).pos(0.2f, 0, 0, -3).pos(0.25f, 1.5f, 0, -2)
            .pos(0.4f, -1.5f, 0, -1.5f).pos(0.55f, 1.8f, 0, -1.5f).pos(0.7f, -1.8f, 0, -1.2f).pos(0.8f, 0, 0, 1).pos(1f, 0, 0, 0)
            .rot(0f, 0, 0, 0).rot(0.2f, 0, 0, 0).rot(0.25f, 0, 0, 5).rot(0.4f, 0, 0, -5).rot(0.55f, 0, 0, 6).rot(0.7f, 0, 0, -6)
            .rot(0.8f, 0, 0, 0).rot(1f, 0, 0, 0);
        b.bone("chest").rot(0f, 0, 0, 0).rot(0.15f, -6, 0, 0).rot(0.2f, 4, 0, 0).rot(0.25f, 5, 15, 0).rot(0.4f, 5, -15, 0)
            .rot(0.55f, 5, 18, 0).rot(0.7f, 5, -18, 0).rot(0.8f, 0, 0, 0).rot(1f, 0, 0, 0);
        b.bone("hips").rot(0f, 0, 0, 0).rot(0.15f, 6, 0, 0).rot(0.25f, -4, -15, 0).rot(0.4f, -4, 15, 0).rot(0.55f, -4, -18, 0)
            .rot(0.7f, -4, 18, 0).rot(0.8f, -5, 0, 0).rot(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.15f, 28, 0, 0).rot(0.2f, 30, 0, 0, Ease.SNAP).rot(0.25f, 25, 40, 0, Ease.OUT)
            .rot(0.325f, 28, 0, 0, Ease.IN).rot(0.4f, 25, -40, 0, Ease.OUT).rot(0.475f, 28, 0, 0, Ease.IN)
            .rot(0.55f, 22, 45, 0, Ease.OUT).rot(0.625f, 25, 0, 0, Ease.IN).rot(0.7f, 22, -45, 0, Ease.OUT)
            .rot(0.8f, -5, 0, 0).rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.15f, -8, 0, 0).rot(0.2f, 15, 0, 0, Ease.SNAP).rot(0.25f, 10, 0, -30, Ease.OUT)
            .rot(0.325f, 14, 0, 0, Ease.IN).rot(0.4f, 10, 0, 30, Ease.OUT).rot(0.475f, 14, 0, 0, Ease.IN)
            .rot(0.55f, 10, 0, -35, Ease.OUT).rot(0.625f, 14, 0, 0, Ease.IN).rot(0.7f, 10, 0, 35, Ease.OUT)
            .rot(0.8f, -10, 0, 0).rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.15f, 40, 0, 0).rot(0.2f, 0, 0, 0, Ease.SNAP).rot(0.75f, 0, 0, 0)
            .rot(0.8f, 30, 0, 0).rot(0.9f, 10, 0, 0).rot(1f, 0, 0, 0);
        ears(b, new float[]{0f, 0}, new float[]{0.15f, -30}, new float[]{0.25f, -45}, new float[]{0.7f, -45},
            new float[]{0.85f, -10}, new float[]{1f, 0});
        fronts(b, 0f, new float[]{0f, 0, 0, 0, 0}, new float[]{0.15f, -25, 0, 10, -10}, new float[]{0.25f, -20, -12, 5, -5},
            new float[]{0.4f, -15, 12, 5, -5}, new float[]{0.55f, -22, -14, 5, -5}, new float[]{0.7f, -18, 14, 5, -5},
            new float[]{0.8f, 10, 0, -10, 0}, new float[]{1f, 0, 0, 0, 0});
        hinds(b, 0.07f, new float[]{0f, 0, 0, 0}, new float[]{0.15f, -25, 30, -15}, new float[]{0.3f, -35, 35, -20},
            new float[]{0.45f, -15, 20, -10}, new float[]{0.6f, -35, 35, -20}, new float[]{0.75f, -20, 25, -10},
            new float[]{0.85f, 5, 0, 0}, new float[]{1f, 0, 0, 0});
        b.bone("tail1").rot(0f, 0, 0, 0).rot(0.25f, 0, -35, 0).rot(0.4f, 0, 35, 0).rot(0.55f, 0, -40, 0).rot(0.7f, 0, 40, 0)
            .rot(0.8f, 0, 0, 0).rot(1f, 0, 0, 0);
        b.bone("tail2").rot(0f, 0, 0, 0).rot(0.25f, 0, -20, 0).rot(0.4f, 0, 25, 0).rot(0.55f, 0, -25, 0).rot(0.7f, 0, 25, 0)
            .rot(0.8f, 0, 0, 0).rot(1f, 0, 0, 0);
        SHAKE = b.build();

        b = AnimClip.create();
        b.bone("body").pos(0f, 0, 0, 0).pos(0.15f, 0, 3, 1).pos(0.23f, 0, -3, -3, Ease.OUT).pos(0.385f, 0, -3, -4)
            .pos(0.5f, 0, -1.5f, -4).pos(0.615f, 0, 1.5f, -2, Ease.SNAP).pos(0.75f, 0, 0.5f, -0.5f).pos(0.9f, 0, 0, 0).pos(1f, 0, 0, 0)
            .rot(0f, 0, 0, 0).rot(0.15f, 5, 0, 0).rot(0.23f, -14, 0, 0, Ease.OUT).rot(0.385f, 6, 0, 15).rot(0.5f, -4, 0, 6)
            .rot(0.615f, 12, 0, -4, Ease.SNAP).rot(0.75f, 2, 0, -8).rot(0.9f, 0, 0, 4).rot(1f, 0, 0, 0);
        b.bone("chest").rot(0f, 0, 0, 0).rot(0.15f, 8, 0, 0).rot(0.23f, -10, 0, 0).rot(0.385f, 10, 0, 0).rot(0.5f, -6, 0, 0)
            .rot(0.615f, 14, 0, 0, Ease.SNAP).rot(0.75f, 4, 0, 0).rot(1f, 0, 0, 0);
        b.bone("hips").rot(0f, 0, 0, 0).rot(0.15f, -12, 0, 0).rot(0.23f, 15, 0, 0).rot(0.385f, 8, 0, 0).rot(0.5f, 4, 0, 0)
            .rot(0.615f, -6, 0, 0).rot(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.15f, 18, 0, 0).rot(0.23f, -10, 0, 0).rot(0.385f, 35, 0, 0).rot(0.5f, -12, 0, 0)
            .rot(0.615f, 28, 0, 0, Ease.SNAP).rot(0.75f, 5, 0, 0).rot(0.87f, 0, 25, 0).rot(0.93f, 0, -25, 0).rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.15f, -12, 0, 0).rot(0.23f, 8, 0, 0).rot(0.385f, 20, 0, 0).rot(0.5f, -8, 0, 0)
            .rot(0.615f, 16, 0, 0, Ease.SNAP).rot(0.75f, 0, 0, 0).rot(0.87f, 0, 0, 30).rot(0.93f, 0, 0, -30).rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.23f, 10, 0, 0).rot(0.385f, 0, 0, 0).rot(0.5f, 45, 0, 0).rot(0.615f, 0, 0, 0, Ease.SNAP)
            .rot(0.75f, 10, 0, 0).rot(0.87f, 25, 0, 0).rot(1f, 0, 0, 0);
        ears(b, new float[]{0f, 0}, new float[]{0.15f, -35}, new float[]{0.385f, -70}, new float[]{0.615f, -30},
            new float[]{0.87f, 10}, new float[]{1f, 0});
        fronts(b, 0.02f, new float[]{0f, 0, 0, 0, 0}, new float[]{0.15f, 40, 0, -70, 30}, new float[]{0.23f, -65, 0, -15, -25},
            new float[]{0.385f, 55, -5, -80, 30}, new float[]{0.5f, -80, -8, 0, -15}, new float[]{0.615f, -30, -12, 30, -20},
            new float[]{0.75f, -5, -6, 10, 0}, new float[]{1f, 0, 0, 0, 0});
        hinds(b, 0.02f, new float[]{0f, 0, 0, 0}, new float[]{0.15f, -38, 55, -25}, new float[]{0.23f, 65, -25, 35},
            new float[]{0.385f, 50, 20, 20}, new float[]{0.5f, 30, 10, 10}, new float[]{0.615f, -10, 30, -15},
            new float[]{0.75f, 0, 10, 0}, new float[]{1f, 0, 0, 0});
        b.bone("tail1").rot(0f, 0, 0, 0).rot(0.15f, -20, 0, 0).rot(0.23f, 25, 0, 0).rot(0.385f, 15, 0, 0).rot(0.615f, 20, 0, 0)
            .rot(0.75f, 5, 0, 0).rot(1f, 0, 0, 0);
        DIVE = b.build();

        b = AnimClip.create();
        b.bone("body").pos(0f, 0, 0, 0).pos(0.15f, 0, 2, 2).pos(0.85f, 0, 2, 2).pos(1f, 0, 0, 0)
            .rot(0f, 0, 0, 0).rot(0.15f, -18, 0, 0).rot(0.85f, -18, 0, 0).rot(1f, 0, 0, 0);
        b.bone("hips").rot(0f, 0, 0, 0).rot(0.15f, -22, 0, 0).rot(0.85f, -22, 0, 0).rot(1f, 0, 0, 0);
        b.bone("chest").rot(0f, 0, 0, 0).rot(0.15f, -6, 0, 0).rot(0.35f, -10, 0, 0).rot(0.4f, -12, 0, 0).rot(0.85f, -8, 0, 0)
            .rot(1f, 0, 0, 0)
            .scale(0f, 1, 1, 1).scale(0.3f, 1, 1, 1).scale(0.38f, 1.06f, 1.08f, 1).scale(0.7f, 1.04f, 1.05f, 1)
            .scale(0.9f, 1, 1, 1).scale(1f, 1, 1, 1);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.15f, -10, 0, 0).rot(0.3f, -50, 0, 0).rot(0.4f, -65, 0, 0, Ease.OUT)
            .rot(0.55f, -62, 0, 4).rot(0.7f, -66, 0, -4).rot(0.85f, -25, 0, 0).rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.15f, 0, 0, 0).rot(0.3f, -25, 0, 0).rot(0.4f, -35, 0, 0).rot(0.55f, -35, 0, 6)
            .rot(0.7f, -35, 0, -6).rot(0.85f, -5, 0, 0).rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.3f, 10, 0, 0).rot(0.4f, 38, 0, 0, Ease.OUT).rot(0.5f, 28, 0, 0).rot(0.6f, 34, 0, 0)
            .rot(0.7f, 26, 0, 0).rot(0.8f, 30, 0, 0).rot(0.9f, 4, 0, 0).rot(1f, 0, 0, 0);
        ears(b, new float[]{0f, 0}, new float[]{0.15f, -10}, new float[]{0.4f, -30}, new float[]{0.85f, -30}, new float[]{1f, 0});
        fronts(b, 0.02f, new float[]{0f, 0, 0, 0, 0}, new float[]{0.15f, 18, 0, -5, -10}, new float[]{0.4f, 22, 0, -6, -10},
            new float[]{0.85f, 20, 0, -5, -10}, new float[]{1f, 0, 0, 0, 0});
        hinds(b, 0.03f, new float[]{0f, 0, 0, 0}, new float[]{0.15f, -55, 85, -35}, new float[]{0.85f, -55, 85, -35},
            new float[]{1f, 0, 0, 0});
        b.bone("tail1").rot(0f, 0, 0, 0).rot(0.15f, -35, 0, 0).rot(0.85f, -35, 0, 0).rot(1f, 0, 0, 0);
        b.bone("tail2").rot(0f, 0, 0, 0).rot(0.15f, -10, 0, 0).rot(0.85f, -10, 0, 0).rot(1f, 0, 0, 0);
        HOWL = b.build();

        b = AnimClip.create();
        b.bone("body").pos(0f, 0, 0, 0).pos(0.15f, 0, -1, 3, Ease.OUT).pos(0.35f, 0, 1, 4).pos(0.55f, 0, 0.5f, 3)
            .pos(0.8f, 0, 0, 1).pos(1f, 0, 0, 0)
            .rot(0f, 0, 0, 0).rot(0.15f, -8, 0, 10, Ease.OUT).rot(0.35f, 4, 0, -8).rot(0.55f, 0, 0, 6).rot(0.8f, 0, 0, -2)
            .rot(1f, 0, 0, 0);
        b.bone("chest").rot(0f, 0, 0, 0).rot(0.15f, -15, 0, 10).rot(0.35f, -5, 0, -8).rot(0.55f, 3, 0, 0).rot(1f, 0, 0, 0);
        b.bone("hips").rot(0f, 0, 0, 0).rot(0.15f, 10, 0, 0).rot(0.35f, -6, 0, 0).rot(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.15f, -30, 25, 0, Ease.OUT).rot(0.35f, 10, -10, 0).rot(0.55f, 0, 10, 0)
            .rot(0.8f, 0, -15, 0).rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.15f, -10, 0, 20, Ease.OUT).rot(0.35f, 5, 0, -10).rot(0.8f, 0, 0, 12)
            .rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.15f, 32, 0, 0, Ease.OUT).rot(0.35f, 10, 0, 0).rot(0.55f, 18, 0, 0)
            .rot(0.8f, 5, 0, 0).rot(1f, 0, 0, 0);
        ears(b, new float[]{0f, 0}, new float[]{0.15f, -55}, new float[]{0.55f, -40}, new float[]{1f, 0});
        fronts(b, 0.1f, new float[]{0f, 0, 0, 0, 0}, new float[]{0.15f, -30, -20, -10, 0}, new float[]{0.35f, 20, -5, -20, 10},
            new float[]{0.55f, -15, 0, 0, 0}, new float[]{1f, 0, 0, 0, 0});
        hinds(b, 0.1f, new float[]{0f, 0, 0, 0}, new float[]{0.15f, 30, -10, 10}, new float[]{0.35f, -20, 30, -10},
            new float[]{0.55f, 25, -5, 5}, new float[]{1f, 0, 0, 0});
        b.bone("tail1").rot(0f, 0, 0, 0).rot(0.15f, -40, 0, 0).rot(0.55f, -30, 0, 0).rot(0.9f, -10, 0, 0).rot(1f, 0, 0, 0);
        YELP = b.build();
    }
}
