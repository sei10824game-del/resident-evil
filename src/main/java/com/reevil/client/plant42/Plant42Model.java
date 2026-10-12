package com.reevil.client.plant42;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.plant42.Plant42Entity;
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
 * Plant 42. Faces -Z, +xRot = down/forward-down. Texture 256x256 (tools/tex_plant42.py, UV table at the top of that file).
 * base > rootA..D, stem1 > (leafA..D, t0..t5 each s0>s1>s2>s3>s4 [tentacle k points outward at yRot = k*60deg, t0 = forward]) ,
 * stem1 > stem2 > head > petalT/B/L/R, sacL/R.
 * Impacts (hitTick/duration): whip .5, grab snare .233 / squeeze .5 / devour .767, gas .5, devour .556, slam .55,
 * flurry .227/.455/.68, roar .5.
 */
public class Plant42Model extends BioModel<Plant42Entity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "plant42"), "main");
    private static final float DEG = Mth.DEG_TO_RAD;
    private static final float[] SEG_LEN = {18, 18, 16, 16, 14};
    /** per-segment weights: how much of a keyed tentacle angle each segment receives, and the lag of its keys. */
    private static final float[] WX = {1f, 0.9f, 0.8f, 0.75f, 0.7f}, WY = {1f, 0.8f, 0.6f, 0.5f, 0.4f};
    private static final float[] REST_X = {0.10f, 0.20f, 0.25f, 0.25f, 0.30f};

    private static final AnimClip WHIP = whip(), GRAB = grab(), GAS = gas(), DEVOUR = devour(), SLAM = slam(), FLURRY = flurry(), ROAR = roar();

    public Plant42Model(ModelPart root) {
        super(root);
        reg("base", "base");
        reg("stem1", "base/stem1");
        reg("stem2", "base/stem1/stem2");
        reg("head", "base/stem1/stem2/head");
        for (String p : new String[]{"petalT", "petalB", "petalL", "petalR", "sacL", "sacR"}) reg(p, "base/stem1/stem2/head/" + p);
        for (int k = 0; k < 4; k++) { reg("leaf" + k, "base/stem1/leaf" + k); reg("root" + k, "base/root" + k); }
        for (int n = 0; n < 6; n++) {
            String path = "base/stem1/t" + n;
            reg("t" + n, path);
            for (int i = 0; i < 5; i++) { path += "/t" + n + "s" + i; reg("t" + n + "s" + i, path); }
        }
    }

    public static LayerDefinition createBodyLayer() { return LayerDefinition.create(mesh(), 256, 256); }
    public static MeshDefinition mesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-12, -6, -12, 24, 6, 24), PartPose.offset(0, 24, 0));
        for (int k = 0; k < 4; k++) {
            base.addOrReplaceChild("root" + k, CubeListBuilder.create().texOffs(100, 0).addBox(-2, -1.5f, -14, 4, 3, 14), PartPose.offsetAndRotation(0, -3, 0, 0.18f, (45 + k * 90) * DEG, 0));
        }
        PartDefinition stem1 = base.addOrReplaceChild("stem1", CubeListBuilder.create().texOffs(0, 32).addBox(-5, -28, -5, 10, 28, 10)
            .texOffs(206, 114).addBox(5, -22, -1, 4, 2, 2).texOffs(206, 114).addBox(-9, -15, -1, 4, 2, 2)
            .texOffs(206, 114).addBox(-1, -19, 5, 2, 2, 4).texOffs(206, 114).addBox(-1, -9, -9, 2, 2, 4)
            .texOffs(206, 114).addBox(5, -8, 2, 4, 2, 2).texOffs(206, 114).addBox(-9, -24, 2, 4, 2, 2), PartPose.offset(0, -5, 0));
        for (int k = 0; k < 4; k++) {
            stem1.addOrReplaceChild("leaf" + k, CubeListBuilder.create().texOffs(84, 92).addBox(-7, -0.5f, -10, 14, 1, 10), PartPose.offsetAndRotation(0, -9 - k * 3, 0, 0.45f, (k * 90 + 20) * DEG, 0));
        }
        for (int n = 0; n < 6; n++) {
            float th = n * 60 * DEG;
            PartDefinition seg = stem1.addOrReplaceChild("t" + n, CubeListBuilder.create(), PartPose.offsetAndRotation(-5 * Mth.sin(th), -14, -5 * Mth.cos(th), 0, th, 0));
            int[][] uv = {{0, 114}, {46, 114}, {92, 114}, {132, 114}, {172, 114}};
            float[] w = {4, 4, 3, 3, 2};
            for (int i = 0; i < 5; i++) {
                CubeListBuilder cb = CubeListBuilder.create().texOffs(uv[i][0], uv[i][1]).addBox(-w[i] / 2, -w[i] / 2, -SEG_LEN[i], w[i], w[i], SEG_LEN[i]);
                if (i == 4) cb.texOffs(206, 114).addBox(-0.5f, -0.5f, -SEG_LEN[i] - 4, 1, 1, 4);
                float oz = i == 0 ? 0 : -SEG_LEN[i - 1];
                seg = seg.addOrReplaceChild("t" + n + "s" + i, cb, PartPose.offsetAndRotation(0, 0, oz, REST_X[i], 0, 0));
            }
        }
        PartDefinition stem2 = stem1.addOrReplaceChild("stem2", CubeListBuilder.create().texOffs(42, 32).addBox(-4, -24, -4, 8, 24, 8), PartPose.offset(0, -28, 0));
        PartDefinition head = stem2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 72).addBox(-10, -18, -10, 20, 18, 20), PartPose.offset(0, -22, 0));
        CubeListBuilder top = CubeListBuilder.create().texOffs(84, 72).addBox(-8, -2, -14, 16, 2, 14);
        CubeListBuilder bot = CubeListBuilder.create().texOffs(84, 72).addBox(-8, 0, -14, 16, 2, 14);
        for (int i = 0; i < 4; i++) {
            top.texOffs(146, 72).addBox(-7 + i * 4, 0, -14, 2, 4, 2);
            bot.texOffs(146, 72).addBox(-5 + i * 4, -4, -14, 2, 4, 2);
        }
        head.addOrReplaceChild("petalT", top, PartPose.offsetAndRotation(0, -14, -10, 0.35f, 0, 0));
        head.addOrReplaceChild("petalB", bot, PartPose.offsetAndRotation(0, -4, -10, -0.35f, 0, 0));
        head.addOrReplaceChild("petalL", CubeListBuilder.create().texOffs(156, 72).addBox(-2, -6, -14, 2, 12, 14), PartPose.offsetAndRotation(8, -9, -10, 0, 0.35f, 0));
        head.addOrReplaceChild("petalR", CubeListBuilder.create().texOffs(156, 72).addBox(0, -6, -14, 2, 12, 14), PartPose.offsetAndRotation(-8, -9, -10, 0, -0.35f, 0));
        head.addOrReplaceChild("sacL", CubeListBuilder.create().texOffs(190, 72).addBox(-3, -3, -3, 6, 6, 6), PartPose.offset(12, -7, 2));
        head.addOrReplaceChild("sacR", CubeListBuilder.create().texOffs(190, 72).addBox(-3, -3, -3, 6, 6, 6), PartPose.offset(-12, -7, 2));
        return mesh;
    }

    // ------------------------------------------------------------------ idle / hurt / death
    @Override
    protected void baseAnim(Plant42Entity e, float ls, float amt, float age, float yaw, float pitch) {
        boolean p2 = e.getForm() == 1;
        float spd = p2 ? 1.7f : 1f;
        bone("stem1").xRot += Mth.sin(age * 0.04f * spd) * 0.03f;
        bone("stem1").zRot += Mth.sin(age * 0.031f * spd) * 0.035f;
        bone("stem2").xRot += Mth.sin(age * 0.05f * spd + 1f) * 0.05f;
        bone("stem2").zRot += Mth.sin(age * 0.037f * spd + 2f) * 0.04f;
        ModelPart head = bone("head");
        head.yRot += Mth.clamp(yaw, -50f, 50f) * DEG * 0.8f;
        head.xRot += Mth.clamp(pitch, -30f, 30f) * DEG * 0.5f + Mth.sin(age * 0.06f) * 0.03f;
        float beat = (float) Math.pow(Math.max(0f, Mth.sin(age * (p2 ? 0.42f : 0.28f))), 6);
        head.xScale *= 1f + beat * 0.04f; head.yScale *= 1f + beat * 0.05f; head.zScale *= 1f + beat * 0.04f;
        if (p2) head.zRot += Mth.sin(age * 0.9f) * 0.02f;
        float open = e.getTarget() != null ? 0.22f : 0.06f;
        float br = Mth.sin(age * 0.1f) * 0.05f;
        bone("petalT").xRot -= open + br;
        bone("petalB").xRot += open + br;
        bone("petalL").yRot -= (open + br) * 0.8f;
        bone("petalR").yRot += (open + br) * 0.8f;
        float sac = 1f + Mth.sin(age * 0.2f) * 0.12f + beat * 0.15f;
        bone("sacL").xScale *= sac; bone("sacL").yScale *= sac; bone("sacL").zScale *= sac;
        bone("sacR").xScale *= 2f - sac; bone("sacR").yScale *= 2f - sac; bone("sacR").zScale *= 2f - sac;
        for (int k = 0; k < 4; k++) bone("leaf" + k).xRot += Mth.sin(age * 0.06f + k * 1.7f) * 0.1f;
        for (int n = 0; n < 6; n++) {
            for (int i = 0; i < 5; i++) {
                ModelPart s = bone("t" + n + "s" + i);
                s.xRot += Mth.sin(age * 0.07f * spd + n * 1.1f + i * 0.6f) * 0.07f * (0.5f + i * 0.2f);
                s.yRot += Mth.sin(age * 0.05f * spd + n * 0.9f + i * 0.5f) * 0.08f * (0.4f + 0.2f * i);
            }
        }
        if (e.isGrabbing()) { // the snaring vine stays taut and trembles
            for (int i = 0; i < 5; i++) bone("t0s" + i).yRot += Mth.sin(age * 1.2f + i) * 0.03f;
        }
        if (e.hurtTime > 0) {
            float h = e.hurtTime / (float) Math.max(1, e.hurtDuration);
            head.xRot -= h * 0.2f;
            bone("stem2").xRot -= h * 0.12f;
            bone("petalT").xRot -= h * 0.5f;
            bone("petalB").xRot += h * 0.5f;
        }
    }

    /** wilting: head droops, stalk slumps, vines go limp, petals hang open, bulb shrivels, leaves fold down. */
    @Override
    protected void extraAnim(Plant42Entity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / (float) Plant42Entity.WILT_TICKS, 0f, 1f);
        float s = d * d * (3 - 2 * d);
        bone("stem1").xRot += s * 0.45f;
        bone("stem2").xRot += s * 0.8f;
        bone("head").xRot += s * 1.25f;
        bone("head").yScale *= 1f - s * 0.15f;
        bone("head").xScale *= 1f - s * 0.08f;
        bone("petalT").xRot -= s * 0.9f;
        bone("petalB").xRot += s * 0.8f;
        bone("petalL").yRot -= s * 0.6f;
        bone("petalR").yRot += s * 0.6f;
        for (int k = 0; k < 4; k++) bone("leaf" + k).xRot += s * 0.9f;
        for (int n = 0; n < 6; n++) for (int i = 0; i < 5; i++) {
            bone("t" + n + "s" + i).xRot += s * 0.5f;
            bone("t" + n + "s" + i).yRot *= 1f - s;
        }
    }

    @Override
    protected AnimClip clip(Plant42Entity e, int id) {
        switch (id) {
            case Plant42Entity.WHIP: return WHIP;
            case Plant42Entity.GRAB: return GRAB;
            case Plant42Entity.GAS: return GAS;
            case Plant42Entity.DEVOUR: return DEVOUR;
            case Plant42Entity.SLAM: return SLAM;
            case Plant42Entity.FLURRY: return FLURRY;
            case Plant42Entity.ROAR: return ROAR;
            default: return null;
        }
    }

    // ------------------------------------------------------------------ clip helpers
    /**
     * Keys a whole tentacle as a travelling wave. Rows {t, x, y[, ease]} in degrees (x negative = up/back, positive = down/forward);
     * each segment gets the angle scaled by WX/WY and its keys delayed by i*delay so the tip lags and cracks like a whip.
     */
    private static void tent(AnimClip.Builder b, int n, float delay, float[]... rows) {
        for (int i = 0; i < 5; i++) {
            float[][] r = new float[rows.length][];
            for (int j = 0; j < rows.length; j++) {
                float t = rows[j][0] * (1f - 4f * delay) + i * delay;
                r[j] = rows[j].length > 3 ? new float[]{t, rows[j][1] * WX[i], rows[j][2] * WY[i], 0, rows[j][3]} : new float[]{t, rows[j][1] * WX[i], rows[j][2] * WY[i], 0};
            }
            rot(b, "t" + n + "s" + i, r);
        }
    }

    /** Tentacle "root" bone (the whole vine pivoting at the stem). */
    private static void tentRoot(AnimClip.Builder b, int n, float[]... rows) { rot(b, "t" + n, rows); }

    private static void headMaw(AnimClip.Builder b, float[] tt, float[] open) {
        float[][] t = new float[tt.length][], bt = new float[tt.length][], l = new float[tt.length][], r = new float[tt.length][];
        for (int i = 0; i < tt.length; i++) {
            float o = open[i];
            t[i] = new float[]{tt[i], -o * 45, 0, 0};
            bt[i] = new float[]{tt[i], o * 40, 0, 0};
            l[i] = new float[]{tt[i], 0, -o * 38, 0};
            r[i] = new float[]{tt[i], 0, o * 38, 0};
        }
        rot(b, "petalT", t); rot(b, "petalB", bt); rot(b, "petalL", l); rot(b, "petalR", r);
    }

    // ------------------------------------------------------------------ clips
    /** 24t, impact @12 (.5): stalk arches back, front vines cock up over the bulb, lash across, tips crack, recoil. */
    private static AnimClip whip() {
        AnimClip.Builder b = AnimClip.create();
        tent(b, 0, 0.012f, k(0, 0, 0), k(0.4f, -70, 55), k(0.5f, 12, -62, SNAP), k(0.6f, 20, -28), k(0.8f, 6, 4), k(1, 0, 0));
        tent(b, 5, 0.012f, k(0, 0, 0), k(0.36f, -60, -40), k(0.5f, 14, 55, SNAP), k(0.62f, 18, 22), k(0.82f, 5, -3), k(1, 0, 0));
        tent(b, 1, 0.012f, k(0, 0, 0), k(0.4f, -35, 45), k(0.5f, 8, -35, SNAP), k(0.8f, 4, 0), k(1, 0, 0));
        for (int n = 2; n <= 4; n++) tent(b, n, 0.01f, k(0, 0, 0), k(0.4f, 10, 0), k(0.5f, 24, 0, SNAP), k(0.75f, 14, 0), k(1, 0, 0));
        rot(b, "stem1", k(0, 0, 0, 0), k(0.4f, -14, 20, 0), k(0.5f, 12, -22, 0, SNAP), k(0.7f, 6, -6, 0), k(1, 0, 0, 0));
        rot(b, "stem2", k(0, 0, 0, 0), k(0.4f, -16, 18, 0), k(0.5f, 14, -20, 0, SNAP), k(0.7f, 4, -6, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, -20, 14, 0), k(0.5f, 12, -16, 0, SNAP), k(0.7f, 4, 0, 0), k(1, 0, 0, 0));
        headMaw(b, new float[]{0, 0.4f, 0.5f, 0.8f, 1}, new float[]{0, 0.9f, 0.5f, 0.2f, 0});
        return b.build();
    }

    /**
     * 60t: snare lash @14 (.233), vine retracts toward the maw while the bulb lowers and gapes (squeeze @30 = .5),
     * jaws slam shut @46 (.767), chewing, swallow.
     */
    private static AnimClip grab() {
        AnimClip.Builder b = AnimClip.create();
        tent(b, 0, 0.01f, k(0, 0, 0), k(0.14f, -65, 10), k(0.233f, 2, 0, SNAP), k(0.3f, 4, 18), // snare: straight at the prey, tip hooks around
            k(0.45f, -40, 0), k(0.6f, -85, 0), k(0.74f, -70, 0), k(0.8f, -60, 0), k(0.9f, -20, 0), k(1, 0, 0));
        tent(b, 1, 0.012f, k(0, 0, 0), k(0.2f, -50, 30), k(0.5f, -25, 20), k(0.77f, -40, 25), k(1, 0, 0));
        tent(b, 5, 0.012f, k(0, 0, 0), k(0.2f, -50, -30), k(0.5f, -25, -20), k(0.77f, -40, -25), k(1, 0, 0));
        for (int n = 2; n <= 4; n++) tent(b, n, 0.01f, k(0, 0, 0), k(0.2f, 14, 0), k(0.7f, 22, 0), k(1, 0, 0));
        rot(b, "stem1", k(0, 0, 0, 0), k(0.14f, -12, 0, 0), k(0.233f, 12, 0, 0, SNAP), k(0.5f, 14, 0, 0), k(0.75f, 20, 0, 0), k(0.767f, 12, 0, 0, SNAP), k(0.88f, 4, 0, 0), k(1, 0, 0, 0));
        rot(b, "stem2", k(0, 0, 0, 0), k(0.14f, -15, 0, 0), k(0.233f, 8, 0, 0, SNAP), k(0.55f, 22, 0, 0), k(0.75f, 26, 0, 0), k(0.767f, 10, 0, 0, SNAP), k(0.85f, 4, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.14f, -12, 0, 0), k(0.5f, 14, 0, 0), k(0.7f, 22, 0, 0), k(0.767f, 6, 0, 0, SNAP), k(0.82f, 16, 10, 0), k(0.88f, 6, -10, 0), k(0.94f, 12, 6, 0), k(1, 0, 0, 0));
        pos(b, "head", k(0, 0, 0, 0), k(0.7f, 0, 0, -3), k(0.767f, 0, 0, -6, SNAP), k(0.9f, 0, 0, -2), k(1, 0, 0, 0));
        headMaw(b, new float[]{0, 0.3f, 0.5f, 0.7f, 0.755f, 0.767f, 0.82f, 0.88f, 0.94f, 1},
            new float[]{0, 0.1f, 0.4f, 1.0f, 1.2f, -0.1f, 0.25f, -0.05f, 0.15f, 0});
        scale(b, "head", k(0, 1, 1, 1), k(0.6f, 1, 1, 1), k(0.78f, 1.08f, 1.1f, 1.08f), k(0.95f, 1.0f, 1.04f, 1.0f), k(1, 1, 1, 1)); // the swallow bulges the bulb
        return b.build();
    }

    /** 50t, expulsion @25 (.5): bulb inflates, sacs swell, vines spread skyward, burst. */
    private static AnimClip gas() {
        AnimClip.Builder b = AnimClip.create();
        scale(b, "head", k(0, 1, 1, 1), k(0.25f, 1.18f, 1.22f, 1.18f), k(0.45f, 1.3f, 1.35f, 1.3f), k(0.5f, 0.88f, 0.88f, 0.88f, SNAP), k(0.7f, 1.05f, 1.05f, 1.05f), k(1, 1, 1, 1));
        scale(b, "sacL", k(0, 1, 1, 1), k(0.4f, 1.8f, 1.8f, 1.8f), k(0.5f, 0.6f, 0.6f, 0.6f, SNAP), k(1, 1, 1, 1));
        scale(b, "sacR", k(0, 1, 1, 1), k(0.4f, 1.8f, 1.8f, 1.8f), k(0.5f, 0.6f, 0.6f, 0.6f, SNAP), k(1, 1, 1, 1));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, -25, 0, 0), k(0.5f, 20, 0, 0, SNAP), k(0.62f, 6, 0, 0), k(0.8f, -4, 0, 0), k(1, 0, 0, 0));
        rot(b, "stem2", k(0, 0, 0, 0), k(0.4f, -18, 0, 0), k(0.5f, 12, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "stem1", k(0, 0, 0, 0), k(0.4f, -8, 0, 0), k(0.5f, 6, 0, 0, SNAP), k(0.55f, 3, 0, 0), k(0.6f, 0, 2, 0), k(0.65f, 0, -2, 0), k(0.7f, 0, 2, 0), k(1, 0, 0, 0));
        for (int n = 0; n < 6; n++) tent(b, n, 0.012f, k(0, 0, 0), k(0.3f, -75, n % 2 == 0 ? 25 : -25), k(0.5f, -85, n % 2 == 0 ? 35 : -35), k(0.6f, -60, 0), k(0.85f, -20, 0), k(1, 0, 0));
        headMaw(b, new float[]{0, 0.3f, 0.45f, 0.5f, 0.55f, 0.6f, 0.65f, 0.7f, 1}, new float[]{0, 0.4f, 1.0f, 1.3f, 0.9f, 0.5f, 0.8f, 0.3f, 0});
        for (int k = 0; k < 4; k++) rot(b, "leaf" + k, k(0, 0, 0, 0), k(0.45f, -30, 0, 0), k(0.55f, -40, 0, 0, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 36t, bite @20 (.556): stalk rears back, bulb tilts up, maw gapes wide, plunges forward and clamps, shakes the prey. */
    private static AnimClip devour() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "stem1", k(0, 0, 0, 0), k(0.35f, -22, 0, 0), k(0.5f, 14, 0, 0, SNAP), k(0.556f, 22, 0, 0, SNAP), k(0.8f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "stem2", k(0, 0, 0, 0), k(0.35f, -28, 0, 0), k(0.5f, 20, 0, 0, SNAP), k(0.556f, 26, 0, 0), k(0.8f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.35f, -32, 0, 0), k(0.5f, 18, 0, 0, SNAP), k(0.556f, 22, 0, 0, SNAP), k(0.64f, 18, 24, 0), k(0.72f, 20, -26, 0), k(0.8f, 16, 20, 0), k(0.9f, 6, 0, 0), k(1, 0, 0, 0));
        pos(b, "head", k(0, 0, 0, 0), k(0.35f, 0, 3, 5), k(0.556f, 0, 3, -9, SNAP), k(0.8f, 0, 2, -4), k(1, 0, 0, 0));
        headMaw(b, new float[]{0, 0.2f, 0.35f, 0.5f, 0.556f, 0.64f, 0.72f, 0.8f, 1}, new float[]{0, 0.7f, 1.3f, 1.2f, -0.15f, 0.1f, -0.1f, 0.15f, 0});
        for (int n = 0; n < 6; n++) tent(b, n, 0.01f, k(0, 0, 0), k(0.35f, -30, n % 2 == 0 ? 20 : -20), k(0.556f, 15, 0, SNAP), k(0.85f, -6, 0), k(1, 0, 0));
        scale(b, "head", k(0, 1, 1, 1), k(0.556f, 1.0f, 1.0f, 1.0f), k(0.64f, 1.06f, 1.08f, 1.06f), k(0.85f, 1, 1, 1), k(1, 1, 1, 1));
        return b.build();
    }

    /** 40t, smash @22 (.55): all six vines rear straight up, stalk bends back, then everything crashes down; rubble tremor. */
    private static AnimClip slam() {
        AnimClip.Builder b = AnimClip.create();
        for (int n = 0; n < 6; n++) {
            tentRoot(b, n, k(0, 0, 0, 0), k(0.45f, -40, 0, 0), k(0.55f, 20, 0, 0, SNAP), k(0.8f, 6, 0, 0), k(1, 0, 0, 0));
            tent(b, n, 0.01f, k(0, 0, 0), k(0.3f, -50, 0), k(0.45f, -70, 0), k(0.55f, 70, 0, SNAP), k(0.62f, 40, 0), k(0.85f, 10, 0), k(1, 0, 0));
        }
        rot(b, "stem1", k(0, 0, 0, 0), k(0.45f, -22, 0, 0), k(0.55f, 20, 0, 0, SNAP), k(0.62f, 14, 0, 0), k(0.72f, 16, 0, 0), k(0.82f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "stem2", k(0, 0, 0, 0), k(0.45f, -26, 0, 0), k(0.55f, 28, 0, 0, SNAP), k(0.8f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.45f, -35, 0, 0), k(0.55f, 30, 0, 0, SNAP), k(0.62f, 18, 0, 0), k(1, 0, 0, 0));
        pos(b, "base", k(0, 0, 0, 0), k(0.55f, 0, 0, 0), k(0.58f, 0, 1.2f, 0), k(0.62f, 0, -0.6f, 0), k(0.68f, 0, 0.6f, 0), k(0.75f, 0, 0, 0), k(1, 0, 0, 0));
        headMaw(b, new float[]{0, 0.45f, 0.55f, 0.8f, 1}, new float[]{0, 0.8f, 0.2f, 0.1f, 0});
        for (int k = 0; k < 4; k++) rot(b, "leaf" + k, k(0, 0, 0, 0), k(0.45f, -25, 0, 0), k(0.55f, 30, 0, 0, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 44t, lashes @10/20/30 (.227/.455/.68): left vine, right vine, then a double lash; head thrashes with each. */
    private static AnimClip flurry() {
        AnimClip.Builder b = AnimClip.create();
        tent(b, 1, 0.012f, k(0, 0, 0), k(0.1f, -60, 50), k(0.227f, 10, -60, SNAP), k(0.3f, 14, -20), k(0.4f, 0, 0), k(1, 0, 0));
        tent(b, 5, 0.012f, k(0, 0, 0), k(0.3f, -60, -50), k(0.455f, 10, 60, SNAP), k(0.55f, 14, 20), k(0.65f, 0, 0), k(1, 0, 0));
        tent(b, 0, 0.012f, k(0, 0, 0), k(0.55f, -75, 40), k(0.68f, 12, -50, SNAP), k(0.78f, 14, -14), k(0.9f, 4, 0), k(1, 0, 0));
        tent(b, 2, 0.012f, k(0, 0, 0), k(0.5f, -55, 45), k(0.68f, 10, -55, SNAP), k(0.8f, 8, -10), k(1, 0, 0));
        tent(b, 4, 0.012f, k(0, 0, 0), k(0.5f, -55, -45), k(0.68f, 10, 55, SNAP), k(0.8f, 8, 10), k(1, 0, 0));
        tent(b, 3, 0.01f, k(0, 0, 0), k(0.3f, 8, 0), k(0.7f, 12, 0), k(1, 0, 0));
        rot(b, "stem1", k(0, 0, 0, 0), k(0.1f, -8, 14, 0), k(0.227f, 8, -16, 0, SNAP), k(0.33f, 2, 0, 0), k(0.4f, -8, -14, 0), k(0.455f, 8, 16, 0, SNAP), k(0.55f, 2, 0, 0), k(0.6f, -10, 0, 0), k(0.68f, 12, 0, 0, SNAP), k(0.85f, 4, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.1f, -10, 20, 0), k(0.227f, 10, -22, 0, SNAP), k(0.4f, -8, -18, 0), k(0.455f, 12, 22, 0, SNAP), k(0.6f, -14, 0, 0), k(0.68f, 16, 0, 0, SNAP), k(1, 0, 0, 0));
        headMaw(b, new float[]{0, 0.1f, 0.227f, 0.4f, 0.455f, 0.6f, 0.68f, 1}, new float[]{0, 0.7f, 0.3f, 0.7f, 0.3f, 0.9f, 0.1f, 0});
        return b.build();
    }

    /** 40t, burst @20 (.5): withering roar - stalk bows back, bulb rears up, maw flung wide and shuddering, vines flare, spores. */
    private static AnimClip roar() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "stem1", k(0, 0, 0, 0), k(0.3f, -20, 0, 0), k(0.5f, -26, 0, 2), k(0.6f, -24, 0, -2), k(0.7f, -26, 0, 2), k(0.85f, -10, 0, 0), k(1, 0, 0, 0));
        rot(b, "stem2", k(0, 0, 0, 0), k(0.3f, -26, 0, 0), k(0.5f, -34, 0, 0), k(0.85f, -12, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -40, 0, 0), k(0.5f, -52, 0, 0, SNAP), k(0.56f, -46, 6, 0), k(0.62f, -52, -6, 0), k(0.68f, -46, 6, 0), k(0.85f, -14, 0, 0), k(1, 0, 0, 0));
        headMaw(b, new float[]{0, 0.3f, 0.5f, 0.56f, 0.62f, 0.68f, 0.85f, 1}, new float[]{0, 1.0f, 1.4f, 1.2f, 1.4f, 1.2f, 0.3f, 0});
        scale(b, "head", k(0, 1, 1, 1), k(0.5f, 1.25f, 1.25f, 1.25f), k(0.7f, 1.15f, 1.15f, 1.15f), k(1, 1, 1, 1));
        for (int n = 0; n < 6; n++) tent(b, n, 0.012f, k(0, 0, 0), k(0.3f, -50, n % 2 == 0 ? 30 : -30), k(0.5f, -70, n % 2 == 0 ? 45 : -45), k(0.62f, -60, n % 2 == 0 ? 35 : -35), k(0.85f, -25, 0), k(1, 0, 0));
        for (int k = 0; k < 4; k++) rot(b, "leaf" + k, k(0, 0, 0, 0), k(0.4f, -35, 0, 0), k(0.8f, -25, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }
}
