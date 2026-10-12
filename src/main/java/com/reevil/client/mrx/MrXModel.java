package com.reevil.client.mrx;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.mrx.MrXEntity;
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
 * Mr. X / Super Tyrant. Faces -Z, +xRot = swings the limb FORWARD-DOWN... for arms negative xRot raises them forward, positive swings them back.
 * Texture 128x64 (tools/tex_mr_x.py). base(hips) > torso > neck > head > hatBrim > hatCrown; torso > armL/R > foreL/R > handL/R (+ clawA..D on the right hand),
 * torso > tumor; base > thighL/R (> coatL/R, shinL/R > footL/R).
 * Impacts: haymaker .5, jab-hook .321/.679, choke .194/.417/.611/.833, stomp .5, claw stab .5, claw sweep .306/.667, pound .548, transform .489.
 */
public class MrXModel extends BioModel<MrXEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "mr_x"), "main");
    private static final float D = Mth.DEG_TO_RAD;
    private static final String[] CLAWS = {"clawA", "clawB", "clawC", "clawD"};
    private static final AnimClip HAY = haymaker(), JAB = jabHook(), CHOKE = choke(), STOMP = stomp(), STAB = clawStab(), SWEEP = clawSweep(), POUND = pound(),
        TRANSFORM = transform(), STAGGER = stagger();

    public MrXModel(ModelPart root) {
        super(root);
        reg("base", "base");
        reg("torso", "base/torso");
        reg("neck", "base/torso/neck");
        reg("head", "base/torso/neck/head");
        reg("hatBrim", "base/torso/neck/head/hatBrim");
        reg("tumor", "base/torso/tumor");
        for (String s : new String[]{"L", "R"}) {
            reg("arm" + s, "base/torso/arm" + s);
            reg("fore" + s, "base/torso/arm" + s + "/fore" + s);
            reg("hand" + s, "base/torso/arm" + s + "/fore" + s + "/hand" + s);
            reg("thigh" + s, "base/thigh" + s);
            reg("coat" + s, "base/thigh" + s + "/coat" + s);
            reg("shin" + s, "base/thigh" + s + "/shin" + s);
            reg("foot" + s, "base/thigh" + s + "/shin" + s + "/foot" + s);
        }
        for (String c : CLAWS) reg(c, "base/torso/armR/foreR/handR/" + c);
    }

    public static LayerDefinition createBodyLayer() { return LayerDefinition.create(mesh(), 128, 64); }
    public static MeshDefinition mesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-4, -1.5f, -2, 8, 3, 4), PartPose.offset(0, 6.5f, 0));
        PartDefinition torso = base.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 8).addBox(-4.5f, -10, -2.5f, 9, 10, 5), PartPose.offset(0, -1.5f, 0));
        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(30, 0).addBox(-1.5f, -1, -1.5f, 3, 1, 3), PartPose.offset(0, -10, 0));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 6).addBox(-3.5f, -7, -3.5f, 7, 7, 7), PartPose.offset(0, -1, 0));
        PartDefinition brim = head.addOrReplaceChild("hatBrim", CubeListBuilder.create().texOffs(60, 0).addBox(-5, -1, -5, 10, 1, 10), PartPose.offset(0, -5.5f, 0));
        brim.addOrReplaceChild("hatCrown", CubeListBuilder.create().texOffs(60, 12).addBox(-3.5f, -3, -3.5f, 7, 3, 7), PartPose.offset(0, -1, 0));
        torso.addOrReplaceChild("tumor", CubeListBuilder.create().texOffs(82, 24).addBox(-3, -3, -3, 6, 6, 6), PartPose.offset(6, -10, 1));
        for (int i = 0; i < 2; i++) {
            boolean l = i == 0; float sg = l ? 1 : -1; String n = l ? "L" : "R";
            PartDefinition arm = torso.addOrReplaceChild("arm" + n, CubeListBuilder.create().texOffs(0, 24).addBox(-2, 0, -2, 4, 9, 4), PartPose.offsetAndRotation(sg * 6.5f, -9, 0, 0, 0, sg * 0.04f));
            PartDefinition fore = arm.addOrReplaceChild("fore" + n, CubeListBuilder.create().texOffs(18, 24).addBox(-2, 0, -2, 4, 9, 4), PartPose.offsetAndRotation(0, 9, 0, -0.1f, 0, 0));
            PartDefinition hand = fore.addOrReplaceChild("hand" + n, CubeListBuilder.create().texOffs(36, 24).addBox(-2, 0, -2, 4, 3, 4), PartPose.offset(0, 9, 0));
            if (!l) for (int c = 0; c < 4; c++) hand.addOrReplaceChild(CLAWS[c], CubeListBuilder.create().texOffs(54, 24).addBox(-0.5f, 0, -0.5f, 1, 7, 1), PartPose.offsetAndRotation(-1.5f + c, 3, -1.5f + (c % 2) * 3, 0.15f, 0, (c - 1.5f) * 0.12f));
            PartDefinition th = base.addOrReplaceChild("thigh" + n, CubeListBuilder.create().texOffs(0, 40).addBox(-2, 0, -2, 4, 7, 4), PartPose.offset(sg * 2, 1.5f, 0));
            th.addOrReplaceChild("coat" + n, CubeListBuilder.create().texOffs(58, 40).addBox(-2.5f, -1, -3, 5, 10, 6), PartPose.ZERO);
            PartDefinition sh = th.addOrReplaceChild("shin" + n, CubeListBuilder.create().texOffs(18, 40).addBox(-2, 0, -2, 4, 7, 4), PartPose.offset(0, 7, 0));
            sh.addOrReplaceChild("foot" + n, CubeListBuilder.create().texOffs(36, 40).addBox(-2, 0, -5, 4, 2, 6), PartPose.offset(0, 7, 0));
        }
        return mesh;
    }

    @Override
    protected void baseAnim(MrXEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        boolean sup = e.isSuper();
        float p = ls * (sup ? 0.75f : 0.6f), s = Mth.sin(p), c = Mth.cos(p);
        float stride = sup ? 0.62f : 0.5f;
        ModelPart base = bone("base");
        base.y += -Math.abs(c) * 0.9f * amt + Mth.sin(age * 0.07f) * 0.1f;
        base.xRot += (sup ? 0.18f : 0.04f) + 0.04f * amt;
        base.zRot += s * 0.03f * amt;
        bone("torso").yRot += -s * 0.12f * amt;
        bone("torso").xRot += (sup ? 0.2f : 0.06f);
        bone("head").xRot += (sup ? -0.12f : 0.05f) + Mth.clamp(pitch, -25f, 25f) * D * 0.4f;
        bone("head").yRot += Mth.clamp(yaw, -50f, 50f) * D * 0.6f;
        for (int k = 0; k < 2; k++) {
            String n = k == 0 ? "L" : "R";
            float ph = k == 0 ? p : p + Mth.PI, sw = Mth.sin(ph), lf = Math.max(0, Mth.cos(ph));
            bone("thigh" + n).xRot += sw * stride * amt;
            bone("shin" + n).xRot += lf * stride * 1.1f * amt;
            bone("foot" + n).xRot += -lf * 0.3f * amt;
            bone("coat" + n).xRot += -sw * stride * 0.35f * amt - Math.abs(sw) * 0.05f; // coat lags the leg
            bone("arm" + n).xRot += -sw * 0.28f * amt + Mth.sin(age * 0.06f + k) * 0.03f;
            bone("fore" + n).xRot += -0.18f * amt;
        }
        // super form: the right arm becomes a hanging claw, the shoulder tumour pulses
        float hide = sup ? 1f : 0.001f;
        for (String cl : CLAWS) { ModelPart m = bone(cl); m.xScale *= hide; m.yScale *= hide; m.zScale *= hide; }
        ModelPart tu = bone("tumor");
        float pulse = hide * (1f + Mth.sin(age * 0.25f) * 0.08f);
        tu.xScale *= pulse; tu.yScale *= pulse; tu.zScale *= pulse;
        if (sup) {
            for (String n : new String[]{"armR", "foreR", "handR"}) { ModelPart m = bone(n); m.xScale *= 1.35f; m.zScale *= 1.35f; if (n.equals("armR")) m.yScale *= 1.1f; }
            bone("armR").xRot += 0.15f; bone("armR").zRot += 0.14f;
            bone("foreR").xRot -= 0.25f;
            bone("handR").xRot += Mth.sin(age * 0.2f) * 0.1f;
            for (int i = 0; i < 4; i++) bone(CLAWS[i]).xRot += Mth.sin(age * 0.2f + i) * 0.05f;
            bone("coatL").zScale *= 0.55f; bone("coatR").zScale *= 0.55f; // shredded coat
        }
        if (e.hurtTime > 0) {
            float h = e.hurtTime / (float) Math.max(1, e.hurtDuration);
            bone("head").xRot -= h * 0.25f; bone("torso").xRot -= h * 0.12f;
        }
    }

    @Override
    protected void extraAnim(MrXEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        bone("base").xRot += s * 1.0f;
        bone("base").y += s * 12f;
        bone("thighL").xRot += -s * 1.3f; bone("thighR").xRot += -s * 0.9f;
        bone("shinL").xRot += s * 1.5f; bone("shinR").xRot += s * 1.2f;
        bone("head").xRot += s * 0.8f;
        bone("armL").xRot += s * 0.5f; bone("armR").xRot += s * 0.4f;
    }

    @Override
    protected AnimClip clip(MrXEntity e, int id) {
        switch (id) {
            case MrXEntity.HAYMAKER: return HAY;
            case MrXEntity.JAB_HOOK: return JAB;
            case MrXEntity.CHOKE: return CHOKE;
            case MrXEntity.STOMP: return STOMP;
            case MrXEntity.CLAW_STAB: return STAB;
            case MrXEntity.CLAW_SWEEP: return SWEEP;
            case MrXEntity.POUND: return POUND;
            case MrXEntity.TRANSFORM: return TRANSFORM;
            case MrXEntity.STAGGER: return STAGGER;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ clips (right arm = punching arm)
    /** 30t, impact @15 (.5): shoulders wind away, fist drawn way back and low, weight onto the back foot, then a huge arcing haymaker with the whole torso. */
    private static AnimClip haymaker() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.4f, 6, 38, 4), k(0.5f, 14, -42, -3, SNAP), k(0.7f, 10, -22, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.4f, -4, 18, 0), k(0.5f, 8, -22, 0, SNAP), k(0.7f, 4, -8, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.4f, 0, 1.5f, 2.5f), k(0.5f, 0, 1f, -3f, SNAP), k(0.8f, 0, 0.5f, -1), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, -4, 20, 0), k(0.5f, 8, -26, 0, SNAP), k(0.7f, 6, -12, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.4f, 55, -10, 55), k(0.46f, -40, 20, 75, SNAP), k(0.5f, -95, 10, 35, SNAP), k(0.62f, -60, 0, 10), k(0.85f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.4f, -50, 0, 0), k(0.5f, -6, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.4f, 10, 0, 0), k(0.5f, 0, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.4f, -45, 0, -10), k(0.5f, -20, -10, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.4f, -80, 0, 0), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.4f, 12, 0, 0), k(0.5f, -18, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.4f, -16, 0, 0), k(0.5f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinL", k(0, 0, 0, 0), k(0.4f, 10, 0, 0), k(0.5f, 24, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** Different rows for L and R thighs: rowsL..., rowsR... split by the t=0 key. */
    private static void pair2(AnimClip.Builder b, String prefix, float[]... rows) {
        java.util.List<float[]> l = new java.util.ArrayList<>(), r = new java.util.ArrayList<>();
        java.util.List<float[]> cur = l; int zeros = 0;
        for (float[] row : rows) { if (row[0] == 0f && ++zeros == 2) cur = r; cur.add(row); }
        rot(b, prefix + "L", l.toArray(new float[0][])); rot(b, prefix + "R", r.toArray(new float[0][]));
    }

    /** 28t, jab @9 (.321) then hooking cross @19 (.679): quick straight jab, retract, hips rotate into a looping hook. */
    private static AnimClip jabHook() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.18f, 4, 12, 0), k(0.321f, 8, -20, 0, SNAP), k(0.45f, 4, -4, 0), k(0.55f, 4, -28, 0), k(0.679f, 12, 34, 0, SNAP), k(0.85f, 6, 10, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.321f, 4, -8, 0), k(0.55f, 2, -20, 0), k(0.679f, 6, 22, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.321f, 0, 0.5f, -2), k(0.679f, 0, 0.8f, -3), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.321f, 4, -10, 0), k(0.679f, 6, 18, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.18f, -50, 0, 12), k(0.321f, -88, -10, 4, SNAP), k(0.45f, -50, 0, 12), k(0.55f, -40, 0, 30), k(0.679f, -60, 55, 40, SNAP), k(0.85f, -20, 10, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.18f, -85, 0, 0), k(0.321f, -4, 0, 0, SNAP), k(0.45f, -85, 0, 0), k(0.679f, -60, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.18f, -55, 0, -10), k(0.321f, -50, 0, -10), k(0.55f, -85, -10, 0), k(0.679f, -100, 0, 0, SNAP), k(0.85f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.18f, -90, 0, 0), k(0.55f, -95, 0, 0), k(0.679f, -20, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.321f, -10, 0, 0), k(0.679f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighR", k(0, 0, 0, 0), k(0.321f, 12, 0, 0), k(0.679f, -12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 72t: left hand seizes the throat @14 (.194), lifts him, chokes (@30,@44), shakes, then slam-throws @60 (.833). */
    private static AnimClip choke() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.1f, -40, 0, 12), k(0.194f, -92, -14, 4, SNAP), k(0.42f, -105, -6, 0), k(0.68f, -118, 0, 0), k(0.833f, -35, 0, 0, SNAP), k(0.92f, -50, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.194f, -10, 0, 0), k(0.42f, -35, 0, 0), k(0.68f, -45, 0, 0), k(0.833f, -5, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.194f, -30, 0, 0), k(0.68f, -50, 0, 0), k(0.833f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.1f, -35, 0, -10), k(0.194f, -70, 10, -4), k(0.42f, -84, 8, 0), k(0.68f, -96, 0, 0), k(0.833f, -25, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.42f, -30, 0, 0), k(0.68f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.1f, 10, 0, 0), k(0.194f, 18, 0, 0, SNAP), k(0.42f, 12, 5, 0), k(0.5f, 12, -5, 0), k(0.611f, 12, 5, 0), k(0.7f, 6, 0, 0), k(0.833f, 26, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.194f, 22, 0, 0), k(0.7f, 18, 0, 0), k(0.833f, -6, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.194f, 0, 0.5f, -2), k(0.7f, 0, 0, -1), k(0.833f, 0, 1.5f, -4, SNAP), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.194f, -10, 0, 0), k(0.833f, 8, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.194f, 14, 0, 0), k(0.833f, -18, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 24t, front kick @12 (.5): knee drives up, then a piston stomp straight out; supporting leg locked, torso leaning back for balance. */
    private static AnimClip stomp() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "thighR", k(0, 0, 0, 0), k(0.3f, -85, 0, 0), k(0.5f, -90, 0, 0, SNAP), k(0.75f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinR", k(0, 0, 0, 0), k(0.3f, 100, 0, 0), k(0.5f, 6, 0, 0, SNAP), k(0.75f, 40, 0, 0), k(1, 0, 0, 0));
        rot(b, "footR", k(0, 0, 0, 0), k(0.3f, 20, 0, 0), k(0.5f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.3f, 6, 0, 0), k(0.5f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -8, 0, 0), k(0.5f, -18, 0, 0, SNAP), k(0.8f, -6, 0, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.5f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -30, 0, -20), k(0.5f, -20, 0, -35), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, 25, 0, 20), k(0.5f, 40, 0, 30), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.5f, 12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 30t, impact @15 (.5): claw drawn back along the flank, hips coil, then an explosive lunging thrust with all four blades. */
    private static AnimClip clawStab() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.4f, 10, 42, 0), k(0.5f, 24, -26, 0, SNAP), k(0.75f, 14, -10, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.4f, 0, 20, 0), k(0.5f, 12, -14, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.4f, 0, 2, 2), k(0.5f, 0, 2.5f, -7, SNAP), k(0.85f, 0, 1, -3), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, -6, 20, 0), k(0.5f, 14, -14, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.4f, 70, 0, 28), k(0.5f, -92, -18, -6, SNAP), k(0.7f, -80, -8, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.4f, -70, 0, 0), k(0.5f, -4, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "handR", k(0, 0, 0, 0), k(0.4f, 20, 0, 0), k(0.5f, -14, 0, 0), k(1, 0, 0, 0));
        for (int i = 0; i < 4; i++) rot(b, CLAWS[i], k(0, 0, 0, 0), k(0.4f, 0, 0, -(i - 1.5f) * 10), k(0.5f, 0, 0, (i - 1.5f) * 12, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.4f, -30, 0, -16), k(0.5f, 30, 0, -10), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.5f, 20, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.5f, -30, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 36t, slashes @11 (.306) and @24 (.667): forehand sweep left-to-right, whips back across the other way, claws trailing. */
    private static AnimClip clawSweep() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, 8, 40, 0), k(0.306f, 12, -38, 0, SNAP), k(0.45f, 8, -20, 0), k(0.55f, 8, -36, 0), k(0.667f, 12, 44, 0, SNAP), k(0.88f, 4, 8, 0), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.306f, 6, -16, 0), k(0.667f, 6, 20, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.15f, 0, 24, 0), k(0.306f, 8, -24, 0), k(0.667f, 8, 28, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.15f, -70, 70, 40), k(0.306f, -85, -70, 10, SNAP), k(0.45f, -80, -60, 20), k(0.55f, -75, -78, 36), k(0.667f, -82, 82, 10, SNAP), k(0.88f, -30, 10, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.15f, -35, 0, 0), k(0.306f, -6, 0, 0, SNAP), k(0.55f, -30, 0, 0), k(0.667f, -6, 0, 0, SNAP), k(1, 0, 0, 0));
        for (int i = 0; i < 4; i++) rot(b, CLAWS[i], k(0, 0, 0, 0), k(0.306f, 0, 0, (i - 1.5f) * 10, SNAP), k(0.667f, 0, 0, -(i - 1.5f) * 10, SNAP), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.15f, -30, 0, -20), k(0.306f, -20, 20, -28), k(0.667f, -30, -20, -28), k(1, 0, 0, 0));
        pair2(b, "thigh", k(0, 0, 0, 0), k(0.306f, 12, 0, 0), k(0.667f, -12, 0, 0), k(1, 0, 0, 0), k(0, 0, 0, 0), k(0.306f, -12, 0, 0), k(0.667f, 12, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 42t, shockwave @23 (.548): both fists (claw and fist) raised overhead, back arched, then slammed into the floor with the whole body. */
    private static AnimClip pound() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -165, 0, 8), k(0.45f, -178, 0, 6), k(0.548f, -22, 0, 0, SNAP), k(0.7f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -160, 0, -8), k(0.45f, -172, 0, -6), k(0.548f, -22, 0, 0, SNAP), k(0.7f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.45f, -20, 0, 0), k(0.548f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.3f, -22, 0, 0), k(0.45f, -32, 0, 0), k(0.548f, 40, 0, 0, SNAP), k(0.75f, 28, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.45f, -22, 0, 0), k(0.548f, 22, 0, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.45f, 0, -1, 1), k(0.548f, 0, 3.5f, -3, SNAP), k(0.75f, 0, 2.5f, -2), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.548f, -26, 0, 0, SNAP), k(0.75f, -20, 0, 0), k(1, 0, 0, 0));
        pair(b, "shin", k(0, 0, 0, 0), k(0.548f, 34, 0, 0, SNAP), k(0.75f, 26, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 90t, change @44 (.489): head thrown back, arms flung wide, torso heaving as the coat bursts; calms into the hunched Super Tyrant stance. */
    private static AnimClip transform() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "torso", k(0, 0, 0, 0), k(0.15f, -10, 0, 0), k(0.3f, -28, 4, 0), k(0.36f, -30, -4, 0), k(0.42f, -28, 4, 0), k(0.489f, -34, 0, 0, SNAP), k(0.6f, -26, 0, 0), k(0.85f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.15f, 20, 0, 0), k(0.3f, -50, 0, 0), k(0.489f, -62, 0, 0, SNAP), k(0.6f, -44, 6, 0), k(0.72f, -40, -6, 0), k(0.88f, 14, 0, 0), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.3f, -20, 0, -40), k(0.489f, -30, 0, -78, SNAP), k(0.7f, -25, 0, -60), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.3f, -20, 0, 40), k(0.489f, -30, 0, 82, SNAP), k(0.7f, -25, 0, 62), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.489f, -20, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreR", k(0, 0, 0, 0), k(0.489f, -20, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.3f, 0, -1, 1), k(0.489f, 0, -1.5f, 1.5f), k(1, 0, 0, 0));
        pair(b, "thigh", k(0, 0, 0, 0), k(0.3f, -4, 0, 6), k(0.489f, -10, 0, 12), k(1, 0, 0, 0));
        return b.build();
    }

    /** 90t kneel: the legs give way onto one knee, head and shoulders slumped, fist planted, holds, then pushes back up to full height. */
    private static AnimClip stagger() {
        AnimClip.Builder b = AnimClip.create();
        pos(b, "base", k(0, 0, 0, 0), k(0.12f, 0, 5, 3), k(0.22f, 0, 8.5f, 4), k(0.7f, 0, 8.5f, 4), k(0.85f, 0, 4, 2), k(1, 0, 0, 0));
        rot(b, "base", k(0, 0, 0, 0), k(0.22f, 18, 0, 0), k(0.7f, 20, 0, 0), k(0.85f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "torso", k(0, 0, 0, 0), k(0.22f, 26, 0, 0), k(0.7f, 30, 0, 0), k(0.85f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.22f, 30, 0, 0), k(0.7f, 34, 0, 0), k(0.85f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighL", k(0, 0, 0, 0), k(0.22f, -88, 0, 6), k(0.7f, -88, 0, 6), k(0.85f, -40, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinL", k(0, 0, 0, 0), k(0.22f, 90, 0, 0), k(0.7f, 90, 0, 0), k(0.85f, 40, 0, 0), k(1, 0, 0, 0));
        rot(b, "thighR", k(0, 0, 0, 0), k(0.22f, -20, 0, -6), k(0.7f, -20, 0, -6), k(0.85f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "shinR", k(0, 0, 0, 0), k(0.22f, 105, 0, 0), k(0.7f, 105, 0, 0), k(0.85f, 40, 0, 0), k(1, 0, 0, 0));
        rot(b, "armR", k(0, 0, 0, 0), k(0.22f, -10, 0, 8), k(0.7f, -10, 0, 8), k(1, 0, 0, 0));
        rot(b, "armL", k(0, 0, 0, 0), k(0.22f, -62, 0, -6), k(0.7f, -62, 0, -6), k(0.85f, -30, 0, 0), k(1, 0, 0, 0));
        rot(b, "foreL", k(0, 0, 0, 0), k(0.22f, -30, 0, 0), k(0.7f, -30, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }
}
