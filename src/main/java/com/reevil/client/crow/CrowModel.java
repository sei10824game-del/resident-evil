package com.reevil.client.crow;

import com.reevil.ReEvilMod;
import com.reevil.anim.Anim;
import com.reevil.anim.AnimClip;
import com.reevil.anim.AnimClip.Ease;
import com.reevil.client.BioModel;
import com.reevil.entity.crow.CrowEntity;
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
 * Infected crow. Bones: body > neck > head > beak/jaw; wing1 > wing2 > 3 primary feathers (each side); tail > tailTip;
 * leg > shin > foot. Rest pose = perched with wings folded along the flanks; baseAnim blends to flight via CrowEntity.fly.
 * Texture 64x32, UVs mirrored in tools/tex_crow.py.
 */
public class CrowModel extends BioModel<CrowEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "crow"), "main");
    private static final float D = (float) (Math.PI / 180.0);

    private final ModelPart body, neck, head, jaw, tail, tailTip;
    private final ModelPart wingL1, wingL2, wingR1, wingR2;
    private final ModelPart[] featherL = new ModelPart[3], featherR = new ModelPart[3];
    private final ModelPart legL, footL, legR, footR;

    public CrowModel(ModelPart root) {
        super(root);
        body = reg("body", "body");
        neck = reg("neck", "body/neck");
        head = reg("head", "body/neck/head");
        reg("beak", "body/neck/head/beak");
        jaw = reg("jaw", "body/neck/head/jaw");
        wingL1 = reg("wingL1", "body/wingL1");
        wingL2 = reg("wingL2", "body/wingL1/wingL2");
        wingR1 = reg("wingR1", "body/wingR1");
        wingR2 = reg("wingR2", "body/wingR1/wingR2");
        for (int i = 0; i < 3; i++) {
            featherL[i] = reg("featherL" + i, "body/wingL1/wingL2/featherL" + i);
            featherR[i] = reg("featherR" + i, "body/wingR1/wingR2/featherR" + i);
        }
        tail = reg("tail", "body/tail");
        tailTip = reg("tailTip", "body/tail/tailTip");
        legL = reg("legL", "body/legL");
        reg("shinL", "body/legL/shinL");
        footL = reg("footL", "body/legL/shinL/footL");
        legR = reg("legR", "body/legR");
        reg("shinR", "body/legR/shinR");
        footR = reg("footR", "body/legR/shinR/footR");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-2f, -2f, -4f, 4, 4, 8), PartPose.offsetAndRotation(0f, 17.5f, 0f, -0.35f, 0f, 0f));
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create()
            .texOffs(54, 0).addBox(-1.5f, -1.5f, -2f, 3, 3, 2), PartPose.offsetAndRotation(0f, -1f, -4f, -0.2f, 0f, 0f));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
            .texOffs(24, 0).addBox(-1.5f, -2f, -3f, 3, 3, 3), PartPose.offsetAndRotation(0f, -0.5f, -1.5f, 0.55f, 0f, 0f));
        head.addOrReplaceChild("beak", CubeListBuilder.create()
            .texOffs(30, 12).addBox(-0.5f, -0.5f, -3f, 1, 1, 3), PartPose.offset(0f, -0.5f, -3f));
        head.addOrReplaceChild("jaw", CubeListBuilder.create()
            .texOffs(10, 17).addBox(-0.5f, 0f, -2f, 1, 1, 2), PartPose.offset(0f, 0.5f, -3f));

        for (int s = 0; s < 2; s++) {
            String n = s == 0 ? "L" : "R";
            float sg = s == 0 ? 1f : -1f;                         // left wing extends +X, right -X
            float ox = s == 0 ? 0f : -4f;
            PartDefinition w1 = body.addOrReplaceChild("wing" + n + "1", CubeListBuilder.create()
                .texOffs(36, 0).addBox(ox, -0.5f, -1f, 4, 1, 5),
                PartPose.offsetAndRotation(2f * sg, -1.5f, -2f, 0f, -1.45f * sg, -1.4f * sg)); // folded along the flank
            PartDefinition w2 = w1.addOrReplaceChild("wing" + n + "2", CubeListBuilder.create()
                .texOffs(0, 12).addBox(ox, -0.5f, -1f, 4, 1, 4), PartPose.offset(4f * sg, 0f, 0f));
            for (int i = 0; i < 3; i++) {
                w2.addOrReplaceChild("feather" + n + i, CubeListBuilder.create()
                    .texOffs(16, 17).addBox(ox, -0.5f, -1f, 4, 1, 2), PartPose.offset(4f * sg, 0f, -0.5f + i * 1.5f));
            }
            PartDefinition leg = body.addOrReplaceChild("leg" + n, CubeListBuilder.create()
                .texOffs(28, 17).addBox(-0.5f, 0f, -0.5f, 1, 2, 1), PartPose.offsetAndRotation(1f * sg, 1.5f, 0.5f, 0.35f, 0f, 0f));
            PartDefinition shin = leg.addOrReplaceChild("shin" + n, CubeListBuilder.create()
                .texOffs(52, 12).addBox(-0.5f, 0f, -0.5f, 1, 3, 1), PartPose.offset(0f, 1.5f, 0f));
            shin.addOrReplaceChild("foot" + n, CubeListBuilder.create()
                .texOffs(0, 17).addBox(-1f, 0f, -2f, 2, 1, 3), PartPose.offset(0f, 2.5f, 0f));
        }

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create()
            .texOffs(16, 12).addBox(-1.5f, -0.5f, 0f, 3, 1, 4), PartPose.offsetAndRotation(0f, -1f, 4f, -0.1f, 0f, 0f));
        tail.addOrReplaceChild("tailTip", CubeListBuilder.create()
            .texOffs(38, 12).addBox(-2f, -0.5f, 0f, 4, 1, 3), PartPose.offset(0f, 0f, 4f));
        return LayerDefinition.create(mesh, 64, 32);
    }

    // ------------------------------------------------------------------ flight / perch

    @Override
    protected void baseAnim(CrowEntity e, float ls, float lsa, float age, float yaw, float pitch) {
        float partial = age - e.tickCount;
        float f = Mth.lerp(partial, e.flyO, e.fly);
        float ground = 1f - f;
        int move = e.getMoveId();
        float mp = move > 0 ? moveProgress(e, partial) : 0f;

        // flapping: suppressed while the dive folds the wings / the talon clip drives them, frantic when scattering
        float flapMul = 1f;
        if (move == CrowEntity.DIVE) flapMul = Mth.clamp(1f - Anim.smooth(Anim.window(mp, 0.36f, 0.46f)) + Anim.smooth(Anim.window(mp, 0.72f, 0.85f)), 0f, 1f);
        else if (move == CrowEntity.TALON) flapMul = 0.3f;
        if (e.deathTime > 0) flapMul = 0f;
        float speed = move == CrowEntity.SCATTER ? 1.6f : move == CrowEntity.PECK ? 1.2f : 0.9f;
        float glide = (move == 0 && e.getY() - e.yo < -0.03) ? 0.6f : 0f;
        float amp = f * flapMul * (1f - glide);
        float ph = age * speed;
        float s = Mth.sin(ph), c = Mth.cos(ph);

        // spread wings for flight
        wingL1.yRot += 1.45f * f; wingL1.zRot += 1.4f * f;
        wingR1.yRot -= 1.45f * f; wingR1.zRot -= 1.4f * f;
        // flap: inner arm leads, hand and primaries lag; hand sweeps back on the upstroke
        wingL1.zRot -= s * 0.9f * amp + 0.15f * glide * f;
        wingR1.zRot += s * 0.9f * amp + 0.15f * glide * f;
        float s2 = Mth.sin(ph - 0.6f), s3 = Mth.sin(ph - 1.1f);
        wingL2.zRot -= s2 * 0.5f * amp;
        wingR2.zRot += s2 * 0.5f * amp;
        float sweep = Math.max(0f, c) * 0.5f * amp;
        wingL2.yRot -= sweep;
        wingR2.yRot += sweep;
        for (int i = 0; i < 3; i++) {
            float fan = (0.05f + i * 0.2f) * f;
            featherL[i].yRot -= fan;
            featherR[i].yRot += fan;
            featherL[i].zRot -= s3 * (0.2f + 0.08f * i) * amp;
            featherR[i].zRot += s3 * (0.2f + 0.08f * i) * amp;
        }
        // body levels out in flight and bobs against the wingbeat; neck stretches, head stays level
        body.xRot += 0.35f * f + s * 0.05f * amp;
        body.y -= c * 0.5f * amp;
        neck.xRot += 0.1f * f;
        head.xRot -= 0.45f * f;
        legL.xRot += 1.2f * f; legR.xRot += 1.2f * f;            // legs trail behind
        footL.xRot += 0.6f * f; footR.xRot += 0.6f * f;
        tail.xRot += 0.1f * f + Mth.sin(ph + 1f) * 0.08f * amp;
        tailTip.xScale = 1f + 0.35f * f;

        // ground: walking / hopping with alternating legs and a head bob
        float a = Math.min(1f, lsa) * ground;
        float g = ls * 1.6f;
        legL.xRot += Mth.sin(g) * 0.7f * a;
        legR.xRot += Mth.sin(g + (float) Math.PI) * 0.7f * a;
        body.y -= Math.abs(Mth.sin(g)) * 0.6f * a;
        neck.xRot += Mth.sin(g * 2f) * 0.15f * a;
        neck.z += Mth.sin(g * 2f) * 0.4f * a;

        // perched idle behaviours: head cocking, preening, cawing (deterministic per crow)
        float cyc = age + e.getId() * 37f;
        float tc = cyc % 140f;
        int kind = Math.floorMod((int) (cyc / 140f) + e.getId(), 3);
        float wt = Anim.smooth(Anim.window(tc, 0f, 8f)) * (1f - Anim.smooth(Anim.window(tc, 50f, 60f))) * ground * (1f - a);
        if (kind == 0) {
            float side = Mth.clamp((25f - tc) / 2f, -1f, 1f);       // quick snaps between two cocked poses
            head.zRot += wt * 0.5f * side;
            head.yRot += wt * 0.3f * side;
        } else if (kind == 1) {
            neck.yRot += wt * 1.1f;
            neck.xRot += wt * 0.4f;
            head.xRot += wt * 0.5f;
            head.yRot += wt * 0.4f;
            jaw.xRot += wt * Math.max(0f, Mth.sin(age * 1.5f)) * 0.4f;
            head.zRot += wt * Mth.sin(age * 1.5f) * 0.15f;
            wingR1.zRot -= wt * 0.3f;
            featherR[2].yRot += wt * 0.3f;
        } else {
            float caw = Math.max(0f, Mth.sin(tc * 0.6f));
            neck.xRot -= wt * 0.4f;
            head.xRot += wt * 0.2f;
            jaw.xRot += wt * caw * 0.6f;
            body.xRot += wt * Mth.sin(tc * 0.6f) * 0.08f;
            wingL1.zRot -= wt * caw * 0.25f;
            wingR1.zRot += wt * caw * 0.25f;
        }
        body.y += Mth.sin(age * 0.15f) * 0.1f;
        head.yRot += yaw * D * 0.7f;
        neck.yRot += yaw * D * 0.3f;
        head.xRot += pitch * D * 0.6f;
    }

    // ------------------------------------------------------------------ hurt + death

    @Override
    protected void extraAnim(CrowEntity e, float ls, float lsa, float age, float yaw, float pitch) {
        float partial = age - e.tickCount;
        if (e.deathTime > 0) {
            death(Math.min(1f, (e.deathTime + partial) / 18f), age);
            return;
        }
        if (e.hurtTime > 0) {
            float k = Math.max(0f, (e.hurtTime - partial) / 10f);
            body.xScale *= 1f + 0.12f * k;                        // feathers puff up
            body.yScale *= 1f + 0.12f * k;
            head.xRot -= 0.4f * k;
            jaw.xRot += 0.5f * k;
            wingL1.zRot -= 0.4f * k;
            wingR1.zRot += 0.4f * k;
            tail.xRot -= 0.3f * k;
        }
    }

    private void death(float d, float age) {
        float fall = Anim.smooth(Anim.window(d, 0f, 0.4f));
        float flop = Anim.smooth(Anim.window(d, 0.3f, 0.8f));
        float settle = Anim.window(d, 0.8f, 1f);
        body.zRot += flop * 2.6f;                                  // flips onto its back
        body.y += flop * 3.5f;
        wingL1.yRot += 1.2f * fall;                                // one wing splays open, the other half
        wingL1.zRot += 1.0f * fall;
        wingR1.yRot -= 0.6f * fall;
        wingR1.zRot -= 0.5f * fall;
        featherL[2].yRot -= 0.5f * fall;
        float spasm = (1f - settle) * flop * 0.3f * Mth.sin(age * 2f);
        legL.xRot -= 0.8f * flop + spasm;
        legR.xRot -= 0.7f * flop - spasm;
        footL.xRot += 1.0f * flop;                                 // talons curl
        footR.xRot += 1.0f * flop;
        neck.xRot += 0.9f * flop;
        neck.yRot += 0.6f * fall;
        head.zRot += 0.5f * fall;
        jaw.xRot += 0.5f * d;
        tail.xRot += 0.3f * fall;
    }

    // ------------------------------------------------------------------ attack clips

    @Override
    protected AnimClip clip(CrowEntity e, int moveId) {
        switch (moveId) {
            case CrowEntity.DIVE: return DIVE;
            case CrowEntity.PECK: return PECK;
            case CrowEntity.TALON: return TALON;
            case CrowEntity.SCATTER: return SCATTER;
            default: return null;
        }
    }

    /** Wings, keys {t, wing1Y, wing1Z, wing2Y} for the LEFT wing; right is mirrored. */
    private static void wings(AnimClip.Builder b, float[]... k) {
        AnimClip.BoneBuilder l1 = b.bone("wingL1"), l2 = b.bone("wingL2"), r1 = b.bone("wingR1"), r2 = b.bone("wingR2");
        for (float[] q : k) {
            l1.rot(q[0], 0, q[1], q[2]);
            r1.rot(q[0], 0, -q[1], -q[2]);
            l2.rot(q[0], 0, q[3], 0);
            r2.rot(q[0], 0, -q[3], 0);
        }
    }

    /** Legs, keys {t, legX, footX}; right leg lags by lag. */
    private static void legs(AnimClip.Builder b, float lag, float[]... k) {
        AnimClip.BoneBuilder ll = b.bone("legL"), fl = b.bone("footL"), lr = b.bone("legR"), fr = b.bone("footR");
        for (float[] q : k) {
            float t2 = (q[0] <= 0f || q[0] >= 1f) ? q[0] : Math.min(0.97f, q[0] + lag);
            ll.rot(q[0], q[1], 0, 0);
            fl.rot(q[0], q[2], 0, 0);
            lr.rot(t2, q[1], 0, 0);
            fr.rot(t2, q[2], 0, 0);
        }
    }

    /** Circle overhead, stall, fold wings, plummet beak-first (hit 30/44), flare and pull up. */
    private static final AnimClip DIVE;
    /** Hover at head height and peck 4 times (hits 10,16,22,28 / 36) with head-bob wind-ups. */
    private static final AnimClip PECK;
    /** Rear up, swing talons forward (hit 13/24), beat back. */
    private static final AnimClip TALON;
    /** Hurt panic: feathers puff, tumble, frantic climb away. */
    private static final AnimClip SCATTER;

    static {
        AnimClip.Builder b = AnimClip.create();
        b.bone("body").rot(0f, 0, 0, 0).rot(0.15f, -25, 0, 0).rot(0.3f, -10, 0, 30).rot(0.41f, -35, 0, 10)
            .rot(0.5f, 75, 0, 0, Ease.IN).rot(0.6f, 80, 0, 8).rot(0.682f, 60, 0, 0, Ease.SNAP).rot(0.75f, -40, 0, 0, Ease.OUT)
            .rot(0.85f, -15, 0, 0).rot(1f, 0, 0, 0)
            .pos(0f, 0, 0, 0).pos(0.5f, 0, 0, 0).pos(0.682f, 0, 1, -1).pos(0.75f, 0, -1, 0).pos(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.15f, 20, 0, 0).rot(0.3f, 25, 0, -15).rot(0.41f, 30, 0, 0).rot(0.5f, -20, 0, 0)
            .rot(0.682f, 10, 0, 0, Ease.SNAP).rot(0.75f, -20, 0, 0).rot(1f, 0, 0, 0)
            .pos(0f, 0, 0, 0).pos(0.5f, 0, 0, -1).pos(0.682f, 0, 0.5f, -2).pos(0.8f, 0, 0, 0).pos(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.15f, 25, 0, 0).rot(0.3f, 30, 0, -20).rot(0.41f, 40, 0, 0).rot(0.5f, -10, 0, 0)
            .rot(0.6f, -10, 0, 0).rot(0.682f, 25, 0, 0, Ease.SNAP).rot(0.75f, -15, 0, 0).rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.41f, 10, 0, 0).rot(0.5f, 0, 0, 0).rot(0.64f, 35, 0, 0)
            .rot(0.682f, 0, 0, 0, Ease.SNAP).rot(0.75f, 25, 0, 0).rot(0.85f, 0, 0, 0).rot(1f, 0, 0, 0);
        wings(b, new float[]{0f, 0, 0, 0}, new float[]{0.36f, 0, 0, 0}, new float[]{0.41f, -20, -50, -10},
            new float[]{0.5f, -80, -75, -15}, new float[]{0.6f, -80, -75, -15}, new float[]{0.682f, -40, -20, 0},
            new float[]{0.75f, 0, -55, 10}, new float[]{0.85f, 0, -20, 0}, new float[]{1f, 0, 0, 0});
        legs(b, 0.02f, new float[]{0f, 0, 0}, new float[]{0.41f, 0, 0}, new float[]{0.5f, 30, 40},
            new float[]{0.682f, -40, -30}, new float[]{0.75f, -20, 20}, new float[]{0.9f, 0, 0}, new float[]{1f, 0, 0});
        b.bone("tail").rot(0f, 0, 0, 0).rot(0.15f, 10, 0, 0).rot(0.41f, -20, 0, 0).rot(0.5f, 10, 0, 0).rot(0.682f, -10, 0, 0)
            .rot(0.75f, -30, 0, 0).rot(0.9f, 0, 0, 0).rot(1f, 0, 0, 0)
            .scale(0f, 1, 1, 1).scale(0.45f, 1, 1, 1).scale(0.5f, 0.7f, 1, 1).scale(0.7f, 0.7f, 1, 1).scale(0.75f, 1.4f, 1, 1)
            .scale(0.9f, 1, 1, 1).scale(1f, 1, 1, 1);
        DIVE = b.build();

        b = AnimClip.create();
        b.bone("body").rot(0f, 0, 0, 0).rot(0.18f, -15, 0, 0).rot(0.278f, 15, 0, 0, Ease.OUT).rot(0.36f, -10, 0, 0)
            .rot(0.444f, 18, 0, 0, Ease.OUT).rot(0.53f, -12, 0, 0).rot(0.611f, 18, 0, 0, Ease.OUT).rot(0.69f, -15, 0, 0)
            .rot(0.778f, 25, 0, 0, Ease.OUT).rot(0.88f, -5, 0, 0).rot(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.18f, -35, 0, 0).rot(0.278f, 45, 0, 0, Ease.OUT).rot(0.36f, -30, 10, 0)
            .rot(0.444f, 45, -10, 0, Ease.OUT).rot(0.53f, -35, -10, 0).rot(0.611f, 50, 10, 0, Ease.OUT).rot(0.69f, -40, 0, 0)
            .rot(0.778f, 55, 0, 0, Ease.OUT).rot(0.88f, -10, 0, 0).rot(1f, 0, 0, 0)
            .pos(0f, 0, 0, 0).pos(0.18f, 0, -0.5f, 1.5f).pos(0.278f, 0, 0.5f, -2, Ease.OUT).pos(0.36f, 0, -0.5f, 1.5f)
            .pos(0.444f, 0, 0.5f, -2, Ease.OUT).pos(0.53f, 0, -0.5f, 1.5f).pos(0.611f, 0, 0.5f, -2, Ease.OUT)
            .pos(0.69f, 0, -0.5f, 1.8f).pos(0.778f, 0, 1, -2.5f, Ease.OUT).pos(0.88f, 0, 0, 0).pos(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.18f, -15, 0, 0).rot(0.278f, 25, 0, 0).rot(0.36f, -15, 0, 10)
            .rot(0.444f, 25, 0, -10).rot(0.53f, -15, 0, -10).rot(0.611f, 25, 0, 10).rot(0.69f, -20, 0, 0)
            .rot(0.778f, 30, 0, 0).rot(0.88f, 0, 0, 0).rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.18f, 20, 0, 0).rot(0.278f, 0, 0, 0, Ease.SNAP).rot(0.36f, 18, 0, 0)
            .rot(0.444f, 0, 0, 0, Ease.SNAP).rot(0.53f, 18, 0, 0).rot(0.611f, 0, 0, 0, Ease.SNAP).rot(0.69f, 22, 0, 0)
            .rot(0.778f, 0, 0, 0, Ease.SNAP).rot(0.88f, 10, 0, 0).rot(1f, 0, 0, 0);
        b.bone("tail").rot(0f, 0, 0, 0).rot(0.18f, 10, 0, 0).rot(0.278f, -20, 0, 0).rot(0.36f, 10, 0, 0).rot(0.444f, -20, 0, 0)
            .rot(0.53f, 10, 0, 0).rot(0.611f, -20, 0, 0).rot(0.69f, 12, 0, 0).rot(0.778f, -25, 0, 0).rot(0.9f, 0, 0, 0)
            .rot(1f, 0, 0, 0);
        wings(b, new float[]{0f, 0, 0, 0}, new float[]{0.278f, 0, -15, 0}, new float[]{0.36f, 0, 10, 0},
            new float[]{0.444f, 0, -15, 0}, new float[]{0.53f, 0, 10, 0}, new float[]{0.611f, 0, -15, 0},
            new float[]{0.69f, 0, 10, 0}, new float[]{0.778f, 0, -20, 0}, new float[]{1f, 0, 0, 0});
        legs(b, 0.03f, new float[]{0f, 0, 0}, new float[]{0.278f, -20, -20}, new float[]{0.444f, -15, 10},
            new float[]{0.611f, -20, -20}, new float[]{0.778f, -25, -25}, new float[]{0.9f, 0, 0}, new float[]{1f, 0, 0});
        PECK = b.build();

        b = AnimClip.create();
        b.bone("body").rot(0f, 0, 0, 0).rot(0.2f, -50, 0, 0).rot(0.45f, -70, 0, 0).rot(0.542f, -55, 0, 0, Ease.SNAP)
            .rot(0.65f, -20, 0, 0).rot(0.8f, -5, 0, 0).rot(1f, 0, 0, 0)
            .pos(0f, 0, 0, 0).pos(0.2f, 0, -1, 1).pos(0.45f, 0, 0, 0).pos(0.542f, 0, 0, -1).pos(0.65f, 0, 0, 1).pos(1f, 0, 0, 0);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.2f, 30, 0, 0).rot(0.45f, 40, 0, 0).rot(0.542f, 35, 0, 0).rot(0.65f, 10, 0, 0)
            .rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.2f, 30, 0, 0).rot(0.45f, 35, 0, 0).rot(0.542f, 25, 0, 0).rot(0.65f, 5, 0, 0)
            .rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.2f, 25, 0, 0).rot(0.45f, 10, 0, 0).rot(0.542f, 30, 0, 0).rot(0.65f, 0, 0, 0)
            .rot(1f, 0, 0, 0);
        wings(b, new float[]{0f, 0, 0, 0}, new float[]{0.2f, 0, -40, 0}, new float[]{0.45f, 0, 40, 10},
            new float[]{0.542f, 0, 30, 0}, new float[]{0.65f, 0, -50, -10}, new float[]{0.8f, 0, 20, 0}, new float[]{1f, 0, 0, 0});
        legs(b, 0.02f, new float[]{0f, 0, 0}, new float[]{0.2f, -40, -30}, new float[]{0.45f, -90, -50},
            new float[]{0.542f, -100, 40}, new float[]{0.65f, -30, 40}, new float[]{0.8f, 20, 10}, new float[]{1f, 0, 0});
        b.bone("tail").rot(0f, 0, 0, 0).rot(0.2f, -30, 0, 0).rot(0.45f, -20, 0, 0).rot(0.542f, 10, 0, 0).rot(0.65f, 20, 0, 0)
            .rot(1f, 0, 0, 0)
            .scale(0f, 1, 1, 1).scale(0.2f, 1.3f, 1, 1).scale(0.65f, 1.3f, 1, 1).scale(1f, 1, 1, 1);
        TALON = b.build();

        b = AnimClip.create();
        b.bone("body").rot(0f, 0, 0, 0).rot(0.1f, -30, 0, 20, Ease.OUT).rot(0.3f, 20, 0, -35).rot(0.5f, -15, 0, 10)
            .rot(0.8f, 5, 0, -3).rot(1f, 0, 0, 0)
            .scale(0f, 1, 1, 1).scale(0.1f, 1.15f, 1.15f, 1).scale(0.5f, 1.08f, 1.08f, 1).scale(1f, 1, 1, 1);
        b.bone("neck").rot(0f, 0, 0, 0).rot(0.1f, -30, 30, 0).rot(0.3f, 10, -25, 0).rot(0.5f, -10, 0, 0).rot(1f, 0, 0, 0);
        b.bone("head").rot(0f, 0, 0, 0).rot(0.1f, -20, 30, 0).rot(0.3f, 10, -20, 15).rot(0.6f, 0, 0, 0).rot(1f, 0, 0, 0);
        b.bone("jaw").rot(0f, 0, 0, 0).rot(0.1f, 35, 0, 0, Ease.OUT).rot(0.25f, 10, 0, 0).rot(0.35f, 30, 0, 0)
            .rot(0.5f, 0, 0, 0).rot(1f, 0, 0, 0);
        wings(b, new float[]{0f, 0, 0, 0}, new float[]{0.1f, 10, -40, 0}, new float[]{0.3f, 0, 30, 5},
            new float[]{0.5f, 0, -20, 0}, new float[]{1f, 0, 0, 0});
        legs(b, 0.04f, new float[]{0f, 0, 0}, new float[]{0.1f, -30, -40}, new float[]{0.3f, 40, 20},
            new float[]{0.6f, 20, 0}, new float[]{1f, 0, 0});
        b.bone("tail").rot(0f, 0, 0, 0).rot(0.1f, -35, 0, 0).rot(0.3f, 20, 0, 0).rot(0.6f, 0, 0, 0).rot(1f, 0, 0, 0)
            .scale(0f, 1, 1, 1).scale(0.1f, 1.5f, 1, 1).scale(0.6f, 1.2f, 1, 1).scale(1f, 1, 1, 1);
        SCATTER = b.build();
    }
}
