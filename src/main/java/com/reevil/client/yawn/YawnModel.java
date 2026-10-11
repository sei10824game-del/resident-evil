package com.reevil.client.yawn;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.yawn.YawnEntity;
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
 * Yawn: 14-segment spine (3 neck + body + 10 tail) chained parent->child so every relative rotation accumulates
 * into S-curves; head with hinged jaw, erectile fangs and a forked two-part tongue.
 * Texture 256x64, UVs mirrored in tools/tex_yawn.py (BOXES).
 */
public class YawnModel extends BioModel<YawnEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "yawn"), "main");

    static final String[] NECK = {"neck1", "neck2", "neck3"};
    static final String[] TAIL = {"t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10"};
    /** Whole spine ordered head -> tail tip (index = travelling-wave phase). */
    static final String[] CHAIN = {"neck3", "neck2", "neck1", "body", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8", "t9", "t10"};
    private static final int[][] TAIL_SIZE = {{12, 10}, {11, 9}, {11, 9}, {10, 8}, {9, 7}, {8, 6}, {7, 5}, {6, 4}, {4, 3}, {3, 2}};
    private static final int[][] TAIL_UV = {{40, 0}, {118, 0}, {156, 0}, {0, 18}, {112, 18}, {146, 18}, {216, 18}, {0, 34}, {28, 34}, {52, 34}};
    private static final float DEG = (float) (Math.PI / 180.0);

    private static final AnimClip.Ease SM = AnimClip.Ease.SMOOTH, OUT = AnimClip.Ease.OUT, IN = AnimClip.Ease.IN;
    private static final AnimClip[] CLIPS = new AnimClip[8];

    static {
        CLIPS[YawnEntity.BITE] = bite();
        CLIPS[YawnEntity.CONSTRICT] = constrict();
        CLIPS[YawnEntity.TAIL_WHIP] = tailWhip();
        CLIPS[YawnEntity.REAR_CRASH] = rearCrash();
        CLIPS[YawnEntity.SPIT] = spit();
        CLIPS[YawnEntity.BURROW] = burrow();
        CLIPS[YawnEntity.TRANSFORM] = transform();
    }

    public YawnModel(ModelPart root) {
        super(root);
        reg("body", "body");
        String p = "body";
        for (String n : NECK) { p = p + "/" + n; reg(n, p); }
        reg("head", p + "/head");
        reg("jaw", p + "/head/jaw");
        reg("tongue", p + "/head/jaw/tongue");
        reg("tongueTip", p + "/head/jaw/tongue/tongueTip");
        reg("fangL", p + "/head/fangL");
        reg("fangR", p + "/head/fangR");
        p = "body";
        for (String n : TAIL) { p = p + "/" + n; reg(n, p); }
    }

    /** Neck segment extending toward -Z, belly aligned at y=+5 of the spine axis. */
    private static CubeListBuilder neckSeg(int u, int v, int w, int h, boolean ridge) {
        CubeListBuilder c = CubeListBuilder.create().texOffs(u, v).addBox(-w / 2f, 5 - h, -8, w, h, 8);
        if (ridge) c.texOffs(74, 34).addBox(-0.5f, 4 - h, -7, 1, 1, 6);
        return c;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6, -5, -4, 12, 10, 8)
                .texOffs(74, 34).addBox(-0.5f, -6, -3, 1, 1, 6),
            PartPose.offset(0, 19, 0));
        PartDefinition n1 = body.addOrReplaceChild("neck1", neckSeg(80, 0, 11, 9, true), PartPose.offset(0, 0, -4));
        PartDefinition n2 = n1.addOrReplaceChild("neck2", neckSeg(194, 0, 10, 8, true), PartPose.offset(0, 0, -8));
        PartDefinition n3 = n2.addOrReplaceChild("neck3", neckSeg(78, 18, 9, 7, false), PartPose.offset(0, 0, -8));
        PartDefinition head = n3.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(36, 18).addBox(-5, -3, -11, 10, 4, 11)
                .texOffs(104, 34).addBox(-5, -4, -9, 3, 1, 4)
                .texOffs(104, 34).mirror().addBox(2, -4, -9, 3, 1, 4).mirror(false)
                .texOffs(146, 34).addBox(-4, 1, -10, 8, 1, 1),
            PartPose.offset(0, 1, -8));
        head.addOrReplaceChild("fangL", CubeListBuilder.create().texOffs(118, 34).addBox(-0.5f, 0, -0.5f, 1, 3, 1),
            PartPose.offset(-3, 1, -9.5f));
        head.addOrReplaceChild("fangR", CubeListBuilder.create().texOffs(118, 34).mirror().addBox(-0.5f, 0, -0.5f, 1, 3, 1),
            PartPose.offset(3, 1, -9.5f));
        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create()
                .texOffs(178, 18).addBox(-4.5f, 0, -10, 9, 3, 10)
                .texOffs(130, 34).addBox(-3.5f, -1, -9.5f, 7, 1, 1),
            PartPose.offset(0, 1, -1));
        PartDefinition tongue = jaw.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(88, 34).addBox(-1, -1, -6, 2, 1, 6),
            PartPose.offset(0, 0.5f, -1));
        tongue.addOrReplaceChild("tongueTip", CubeListBuilder.create()
                .texOffs(122, 34).addBox(-1.5f, -1, -3, 1, 1, 3)
                .texOffs(122, 34).addBox(0.5f, -1, -3, 1, 1, 3),
            PartPose.offset(0, 0, -6));

        PartDefinition prev = body;
        for (int i = 0; i < TAIL.length; i++) {
            int w = TAIL_SIZE[i][0], h = TAIL_SIZE[i][1];
            CubeListBuilder c = CubeListBuilder.create().texOffs(TAIL_UV[i][0], TAIL_UV[i][1]).addBox(-w / 2f, 5 - h, 0, w, h, 8);
            if (i < 6) c.texOffs(74, 34).addBox(-0.5f, 4 - h, 1, 1, 1, 6);
            prev = prev.addOrReplaceChild(TAIL[i], c, PartPose.offset(0, 0, i == 0 ? 4 : 8));
        }
        return LayerDefinition.create(mesh, 256, 64);
    }

    // ------------------------------------------------------------------ procedural base motion
    @Override
    protected void baseAnim(YawnEntity e, float ls, float lsa, float age, float headYaw, float headPitch) {
        boolean rage = e.getForm() == 1;
        float move = Mth.clamp(lsa * 2.2f, 0f, 1f);
        float ph = ls * 0.9f + age * (rage ? 0.09f : 0.06f);
        float amp = (rage ? 0.08f : 0.05f) + 0.2f * move;
        // lateral undulation: travelling sine wave head -> tail, neck steadier, tail tip loosest
        for (int i = 0; i < CHAIN.length; i++) {
            float k = i < 3 ? 0.45f : (i > 10 ? 1.25f : 1f);
            bone(CHAIN[i]).yRot += amp * k * Mth.sin(ph - i * 0.75f);
        }
        // raised alert neck: S held off the ground, head level
        bone("neck1").xRot += -0.35f + 0.03f * Mth.sin(age * 0.07f);
        bone("neck2").xRot += -0.12f;
        bone("neck3").xRot += 0.22f;
        float yaw = Mth.clamp(headYaw, -60f, 60f) * DEG;
        bone("neck3").yRot += yaw * 0.25f;
        ModelPart head = bone("head");
        head.xRot += 0.25f + Mth.clamp(headPitch, -40f, 40f) * DEG * 0.5f;
        head.yRot += yaw * 0.5f;
        ModelPart jaw = bone("jaw");
        jaw.xRot += rage ? 0.1f + 0.05f * Mth.sin(age * 0.2f) : 0.02f;
        // tongue flick: two quick darts every 2.5 s
        float cyc = (age % 50f) / 50f;
        if (cyc < 0.2f) {
            float s = Math.abs(Mth.sin(cyc / 0.2f * (float) Math.PI * 2f));
            jaw.xRot += 0.1f * s;
            bone("tongue").z -= 6f * s;
            bone("tongue").xRot += 0.2f * s * Mth.sin(age * 2.7f);
            bone("tongueTip").yRot += 0.35f * s * Mth.sin(age * 3.3f);
        }
        // breathing swell of the thick trunk
        bone("t1").xScale = 1f + 0.035f * Mth.sin(age * 0.11f);
    }

    @Override
    protected void extraAnim(YawnEntity e, float ls, float lsa, float age, float headYaw, float headPitch) {
        if (e.deathTime <= 0) return;
        float partial = age - e.tickCount;
        float d = Mth.clamp((e.deathTime + partial) / 20f, 0f, 1f);
        float fade = 1f - d;
        for (int i = 0; i < CHAIN.length; i++) bone(CHAIN[i]).yRot += 0.45f * fade * Mth.sin(age * 1.1f - i * 0.9f);
        bone("jaw").xRot += 0.9f * d;
        bone("neck1").xRot += 0.3f * d;
        bone("head").zRot += 0.4f * d;
    }

    @Override
    protected AnimClip clip(YawnEntity e, int moveId) {
        return moveId > 0 && moveId < CLIPS.length ? CLIPS[moveId] : null;
    }

    // ------------------------------------------------------------------ keyframe helpers
    /** kind 0 = rot (deg), 1 = pos (px), 2 = scale. */
    private static void key(AnimClip.BoneBuilder b, int kind, double t, double x, double y, double z, AnimClip.Ease e) {
        float ft = (float) t, fx = (float) x, fy = (float) y, fz = (float) z;
        if (kind == 0) b.rot(ft, fx, fy, fz, e);
        else if (kind == 1) b.pos(ft, fx, fy, fz, e);
        else b.scale(ft, fx, fy, fz, e);
    }

    /** d = t,x,y,z repeated; key number `fast` uses ease fe (the impact key), all others SMOOTH. */
    private static void keys(AnimClip.Builder c, String bone, int kind, int fast, AnimClip.Ease fe, double[] d) {
        AnimClip.BoneBuilder b = c.bone(bone);
        for (int k = 0; k * 4 + 3 < d.length; k++) key(b, kind, d[k * 4], d[k * 4 + 1], d[k * 4 + 2], d[k * 4 + 3], k == fast ? fe : SM);
    }

    private static void R(AnimClip.Builder c, String bone, int fast, AnimClip.Ease fe, double... d) { keys(c, bone, 0, fast, fe, d); }
    private static void P(AnimClip.Builder c, String bone, int fast, AnimClip.Ease fe, double... d) { keys(c, bone, 1, fast, fe, d); }
    private static void S(AnimClip.Builder c, String bone, int fast, AnimClip.Ease fe, double... d) { keys(c, bone, 2, fast, fe, d); }

    // ------------------------------------------------------------------ attack clips
    /** Venom strike: S-coil anticipation (0-.47), explosive head strike + jaw snap at .533 (hit), head-shake follow-through, recovery. */
    private static AnimClip bite() {
        AnimClip.Builder c = AnimClip.create();
        R(c, "body", 2, OUT, 0, 0, 0, 0, .42, -6, 0, 0, .533, 4, 0, 0, .7, 0, 0, 0, 1, 0, 0, 0);
        P(c, "body", 3, OUT, 0, 0, 0, 0, .42, 0, 0, 3, .47, 0, 0, 3.5, .533, 0, 0, -7, .7, 0, 0, -5, 1, 0, 0, 0);
        R(c, "neck1", 3, OUT, 0, 0, 0, 0, .42, -50, 0, 0, .47, -53, 0, 0, .533, 6, 0, 0, .62, 16, 0, 0, .75, 5, 0, 0, 1, 0, 0, 0);
        R(c, "neck2", 3, OUT, 0, 0, 0, 0, .42, 55, 0, 0, .47, 58, 0, 0, .533, -4, 0, 0, .62, -2, 0, 0, 1, 0, 0, 0);
        R(c, "neck3", 3, OUT, 0, 0, 0, 0, .42, -30, 0, 0, .47, -32, 0, 0, .533, 2, 0, 0, .62, 6, 0, 0, 1, 0, 0, 0);
        R(c, "head", 3, OUT, 0, 0, 0, 0, .42, 28, 0, 0, .47, 30, 0, 0, .533, -6, 0, 0, .6, 22, 12, 0, .66, 22, -12, 0, .72, 16, 5, 0, 1, 0, 0, 0);
        P(c, "head", 2, OUT, 0, 0, 0, 0, .42, 0, 0, 2, .533, 0, 0, -7, .62, 0, 1, -4, 1, 0, 0, 0);
        R(c, "jaw", 4, OUT, 0, 0, 0, 0, .3, 0, 0, 0, .42, 18, 0, 0, .5, 62, 0, 0, .533, -3, 0, 0, .6, 4, 0, 0, .66, 0, 0, 0, 1, 0, 0, 0);
        R(c, "fangL", -1, OUT, 0, 0, 0, 0, .42, 0, 0, 0, .5, -55, 0, 0, .533, -50, 0, 0, .64, 0, 0, 0, 1, 0, 0, 0);
        R(c, "fangR", -1, OUT, 0, 0, 0, 0, .42, 0, 0, 0, .5, -55, 0, 0, .533, -50, 0, 0, .64, 0, 0, 0, 1, 0, 0, 0);
        P(c, "tongue", -1, OUT, 0, 0, 0, 0, .18, 0, 0, -6, .24, 0, 0, 0, .3, 0, 0, -6, .36, 0, 0, 0, 1, 0, 0, 0);
        R(c, "tongueTip", -1, OUT, 0, 0, 0, 0, .18, 0, 25, 0, .24, 0, -25, 0, .3, 0, 25, 0, .36, 0, 0, 0, 1, 0, 0, 0);
        for (int i = 0; i < TAIL.length; i++) {
            double s = i < 3 ? 14 : i < 6 ? -16 : 12;
            R(c, TAIL[i], 3, OUT, 0, 0, 0, 0, .42, 0, s, 0, .47, 0, s * 1.05, 0, .533 + i * .012, 0, -s * .3, 0,
                .7 + i * .01, 0, s * .2, 0, 1, 0, 0, 0);
        }
        return c.build();
    }

    /** Constrict: rear + sway, lunge-grab at .25 (hit 0), neck and tail wrap the prey, four tightening squeezes (.4/.55/.7/.85), unwind. */
    private static AnimClip constrict() {
        AnimClip.Builder c = AnimClip.create();
        double[] sq = {.4, .55, .7, .85};
        for (int n = 0; n < 3; n++) {
            AnimClip.BoneBuilder b = c.bone(NECK[n]);
            double lift = n == 0 ? -35 : n == 1 ? 10 : 15;
            key(b, 0, 0, 0, 0, 0, SM);
            key(b, 0, .15, lift, 0, 0, SM);
            key(b, 0, .25, n == 0 ? 5 : 0, -15, 0, OUT);
            key(b, 0, .33, 0, -38, 0, SM);
            for (double s : sq) { key(b, 0, s - .05, 0, -38, 0, SM); key(b, 0, s, 0, -46, 0, OUT); }
            key(b, 0, .92, 0, -38, 0, SM);
            key(b, 0, 1, 0, 0, 0, SM);
        }
        AnimClip.BoneBuilder h = c.bone("head");
        key(h, 0, 0, 0, 0, 0, SM); key(h, 0, .15, 25, 0, 0, SM); key(h, 0, .25, -8, 0, 0, OUT); key(h, 0, .33, 25, -30, 0, SM);
        for (double s : sq) { key(h, 0, s - .05, 25, -30, 0, SM); key(h, 0, s, 35, -38, 8, OUT); }
        key(h, 0, .92, 25, -30, 0, SM); key(h, 0, 1, 0, 0, 0, SM);
        AnimClip.BoneBuilder j = c.bone("jaw");
        key(j, 0, 0, 0, 0, 0, SM); key(j, 0, .15, 30, 0, 0, SM); key(j, 0, .22, 50, 0, 0, SM); key(j, 0, .25, 0, 0, 0, OUT);
        for (double s : sq) { key(j, 0, s - .05, 5, 0, 0, SM); key(j, 0, s, 40, 0, 0, OUT); }
        key(j, 0, .92, 0, 0, 0, SM); key(j, 0, 1, 0, 0, 0, SM);
        AnimClip.BoneBuilder tg = c.bone("tongue");
        key(tg, 1, 0, 0, 0, 0, SM);
        for (double s : sq) { key(tg, 1, s - .05, 0, 0, 0, SM); key(tg, 1, s, 0, 0, -5, OUT); }
        key(tg, 1, .92, 0, 0, 0, SM);
        for (int i = 0; i < TAIL.length; i++) {
            AnimClip.BoneBuilder b = c.bone(TAIL[i]);
            key(b, 0, 0, 0, 0, 0, SM); key(b, 0, .15, 0, 6, 0, SM);
            key(b, 0, .25 + i * .007, 0, 22, 0, OUT); key(b, 0, .34, 0, 26, 0, SM);
            for (double s : sq) { key(b, 0, s - .04, 0, 26, 0, SM); key(b, 0, s + i * .004, 0, 32, 0, OUT); }
            key(b, 0, .92, 0, 26, 0, SM); key(b, 0, 1, 0, 0, 0, SM);
        }
        AnimClip.BoneBuilder bd = c.bone("body");
        key(bd, 0, 0, 0, 0, 0, SM); key(bd, 0, .25, 0, 20, 0, OUT);
        for (double s : sq) { key(bd, 0, s - .05, 0, 20, 0, SM); key(bd, 0, s, 0, 24, 0, OUT); }
        key(bd, 0, .92, 0, 20, 0, SM); key(bd, 0, 1, 0, 0, 0, SM);
        key(bd, 1, 0, 0, 0, 0, SM);
        for (double s : sq) { key(bd, 1, s - .05, 0, 0, 0, SM); key(bd, 1, s, 0, -1.5, 0, OUT); }
        key(bd, 1, 1, 0, 0, 0, SM);
        key(bd, 2, 0, 1, 1, 1, SM);
        for (double s : sq) { key(bd, 2, s - .05, 1, 1, 1, SM); key(bd, 2, s, 1.05, .95, 1.05, OUT); }
        key(bd, 2, 1, 1, 1, 1, SM);
        return c.build();
    }

    /** Tail whip: whole body winds one way, tail sweeps across with per-segment lag (crack at the tip ~.55, hit .53), overshoot, settle. */
    private static AnimClip tailWhip() {
        AnimClip.Builder c = AnimClip.create();
        R(c, "body", 2, OUT, 0, 0, 0, 0, .4, 0, 40, -6, .53, 0, -45, 8, .66, 0, -60, 4, .82, 0, -15, 0, 1, 0, 0, 0);
        R(c, "neck1", 2, OUT, 0, 0, 0, 0, .4, 12, -15, 0, .53, 6, 20, 0, .66, 0, 25, 0, 1, 0, 0, 0);
        R(c, "neck2", 2, OUT, 0, 0, 0, 0, .4, 4, -10, 0, .53, 0, 12, 0, 1, 0, 0, 0);
        R(c, "head", 2, OUT, 0, 0, 0, 0, .4, 10, -20, 0, .53, -5, 25, 0, .7, 0, 10, 0, 1, 0, 0, 0);
        R(c, "jaw", -1, OUT, 0, 0, 0, 0, .35, 10, 0, 0, .45, 35, 0, 0, .6, 0, 0, 0, 1, 0, 0, 0);
        for (int i = 0; i < TAIL.length; i++) {
            R(c, TAIL[i], 2, OUT, 0, 0, 0, 0, .4, 0, 9, 0, .45 + i * .009, 0, -13, 0, .62 + i * .01, 0, -19, 0, .85, 0, 4, 0, 1, 0, 0, 0);
        }
        S(c, "t8", 2, OUT, 0, 1, 1, 1, .5, 1, 1, 1, .56, 1, 1, 1.25, .7, 1, 1, 1, 1, 1, 1, 1);
        return c.build();
    }

    /** Rear-up hissing display (tongue flicks, tail-tip rattle), cocked hold, accelerating crash-down strike at .64, bounce, recovery. */
    private static AnimClip rearCrash() {
        AnimClip.Builder c = AnimClip.create();
        R(c, "body", 3, IN, 0, 0, 0, 0, .4, -14, 0, 0, .55, -16, 0, 0, .64, 7, 0, 0, .72, 3, 0, 0, 1, 0, 0, 0);
        P(c, "body", 2, IN, 0, 0, 0, 0, .4, 0, -2, 2, .64, 0, 0, -6, .75, 0, 0, -4, 1, 0, 0, 0);
        R(c, "neck1", 5, IN, 0, 0, 0, 0, .2, -45, 8, 0, .3, -62, -8, 0, .45, -72, 5, 0, .55, -76, 0, 0, .64, 32, 0, 0, .72, 22, 0, 0, .8, 25, 0, 0, 1, 0, 0, 0);
        R(c, "neck2", 3, IN, 0, 0, 0, 0, .45, -25, 0, 0, .55, -26, 0, 0, .64, 10, 0, 0, 1, 0, 0, 0);
        R(c, "neck3", 3, IN, 0, 0, 0, 0, .45, 25, 0, 0, .55, 28, 0, 0, .64, -6, 0, 0, 1, 0, 0, 0);
        R(c, "head", 5, IN, 0, 0, 0, 0, .2, 40, -10, 0, .3, 48, 10, 0, .45, 55, -6, 0, .55, 58, 0, 0, .64, -12, 0, 0, .72, 5, 0, 0, 1, 0, 0, 0);
        P(c, "head", 1, IN, 0, 0, 0, 0, .64, 0, 3, -3, .75, 0, 1, -1, 1, 0, 0, 0);
        R(c, "jaw", 5, OUT, 0, 0, 0, 0, .2, 35, 0, 0, .45, 45, 0, 0, .55, 60, 0, 0, .62, 65, 0, 0, .64, -2, 0, 0, .7, 6, 0, 0, .76, 0, 0, 0, 1, 0, 0, 0);
        R(c, "fangL", -1, OUT, 0, 0, 0, 0, .45, 0, 0, 0, .55, -50, 0, 0, .66, 0, 0, 0, 1, 0, 0, 0);
        R(c, "fangR", -1, OUT, 0, 0, 0, 0, .45, 0, 0, 0, .55, -50, 0, 0, .66, 0, 0, 0, 1, 0, 0, 0);
        P(c, "tongue", -1, OUT, 0, 0, 0, 0, .22, 0, 0, -6, .26, 0, 0, -1, .32, 0, 0, -6, .36, 0, 0, -1, .42, 0, 0, -6, .5, 0, 0, 0, 1, 0, 0, 0);
        R(c, "tongueTip", -1, OUT, 0, 0, 0, 0, .22, 0, 30, 0, .26, 0, -30, 0, .32, 0, 30, 0, .36, 0, -30, 0, .42, 0, 30, 0, .5, 0, 0, 0, 1, 0, 0, 0);
        for (int i = 0; i < 6; i++) {
            double s = i < 3 ? -12 : 14;
            R(c, TAIL[i], 3, IN, 0, 0, 0, 0, .45, 0, s, 0, .55, 0, s, 0, .64, 0, -s * .3, 0, 1, 0, 0, 0);
        }
        for (int i = 6; i < TAIL.length; i++) {   // warning rattle of the tail tip during the display
            AnimClip.BoneBuilder b = c.bone(TAIL[i]);
            key(b, 0, 0, 0, 0, 0, SM);
            for (int k = 0; k <= 10; k++) key(b, 0, .1 + .04 * k, 0, (k % 2 == 0 ? 1 : -1) * 6 * (i - 5), 0, SM);
            key(b, 0, .6, 0, 0, 0, SM);
            key(b, 0, 1, 0, 0, 0, SM);
        }
        return c.build();
    }

    /** Venom spit: head draws back and up while the throat pouch swells with gulps, thrust + jaw gape at release (.5625), recoil, recovery. */
    private static AnimClip spit() {
        AnimClip.Builder c = AnimClip.create();
        R(c, "neck1", 3, OUT, 0, 0, 0, 0, .35, -40, 0, 0, .45, -42, 0, 0, .5625, -8, 0, 0, .7, -18, 0, 0, 1, 0, 0, 0);
        R(c, "neck2", 2, OUT, 0, 0, 0, 0, .35, 30, 0, 0, .5625, 0, 0, 0, 1, 0, 0, 0);
        R(c, "neck3", 2, OUT, 0, 0, 0, 0, .35, 10, 0, 0, .5625, -4, 0, 0, 1, 0, 0, 0);
        R(c, "head", 3, OUT, 0, 0, 0, 0, .35, -25, 0, 0, .45, -28, 0, 0, .5625, -12, 0, 0, .7, 6, 0, 0, 1, 0, 0, 0);
        P(c, "head", 2, OUT, 0, 0, 0, 0, .35, 0, 0, 3, .5625, 0, 0, -5, .7, 0, 0, 1, 1, 0, 0, 0);
        R(c, "jaw", 6, OUT, 0, 0, 0, 0, .15, 8, 0, 0, .25, 0, 0, 0, .35, 12, 0, 0, .45, 6, 0, 0, .5, 20, 0, 0, .5625, 70, 0, 0, .68, 60, 0, 0, .8, 0, 0, 0, 1, 0, 0, 0);
        S(c, "jaw", 5, OUT, 0, 1, 1, 1, .2, 1.2, 1.5, 1.1, .28, 1.1, 1.25, 1.05, .38, 1.25, 1.7, 1.15, .45, 1.3, 1.8, 1.2, .5625, 1, 1, 1, 1, 1, 1, 1);
        P(c, "tongue", -1, OUT, 0, 0, 0, 0, .5, 0, 0, 0, .5625, 0, 0, -5, .7, 0, 0, -5, .8, 0, 0, 0, 1, 0, 0, 0);
        R(c, "body", 2, OUT, 0, 0, 0, 0, .35, -6, 0, 0, .5625, 2, 0, 0, 1, 0, 0, 0);
        P(c, "body", 2, OUT, 0, 0, 0, 0, .35, 0, 0, 2, .5625, 0, 0, -2, 1, 0, 0, 0);
        for (int i = 0; i < TAIL.length; i++) {
            double s = i < 5 ? 6 : -6;
            R(c, TAIL[i], 2, OUT, 0, 0, 0, 0, .35, 0, s, 0, .5625, 0, -s * .5, 0, 1, 0, 0, 0);
        }
        return c.build();
    }

    /** Burrow retreat: alarmed hiss, nose-first dive with a frantic slither (tail lingers up), hidden (.3-.75), head-first eruption, settle. */
    private static AnimClip burrow() {
        AnimClip.Builder c = AnimClip.create();
        AnimClip.BoneBuilder bd = c.bone("body");
        key(bd, 1, 0, 0, 0, 0, SM); key(bd, 1, .12, 0, 0, 0, SM); key(bd, 1, .3, 0, 26, 0, IN);
        key(bd, 1, .75, 0, 28, 0, SM); key(bd, 1, .9, 0, 0, 0, OUT); key(bd, 1, 1, 0, 0, 0, SM);
        key(bd, 0, 0, 0, 0, 0, SM); key(bd, 0, .12, 0, 0, 0, SM); key(bd, 0, .3, 20, 0, 0, SM); key(bd, 0, .75, 20, 0, 0, SM);
        key(bd, 0, .8, -25, 0, 0, OUT); key(bd, 0, .9, -10, 0, 0, SM); key(bd, 0, 1, 0, 0, 0, SM);
        R(c, "neck1", 5, OUT, 0, 0, 0, 0, .08, -30, 0, 0, .12, -32, 0, 0, .22, 35, 0, 0, .75, 35, 0, 0, .82, -45, 0, 0, .92, -15, 0, 0, 1, 0, 0, 0);
        R(c, "head", 4, OUT, 0, 0, 0, 0, .08, 10, 0, 0, .22, 50, 0, 0, .75, 50, 0, 0, .82, -20, 0, 0, .92, 10, 0, 0, 1, 0, 0, 0);
        R(c, "jaw", 4, OUT, 0, 0, 0, 0, .05, 40, 0, 0, .12, 0, 0, 0, .8, 0, 0, 0, .84, 55, 0, 0, .92, 0, 0, 0, 1, 0, 0, 0);
        for (int i = 0; i < TAIL.length; i++) {
            AnimClip.BoneBuilder b = c.bone(TAIL[i]);
            double sg = i % 2 == 0 ? 1 : -1;
            key(b, 0, 0, 0, 0, 0, SM);
            key(b, 0, .12, 0, 18 * sg, 0, SM); key(b, 0, .18, 0, -18 * sg, 0, SM); key(b, 0, .24, 0, 18 * sg, 0, SM);
            key(b, 0, .3 + i * .006, i < 3 ? -25 : 0, -10 * sg, 0, SM);
            key(b, 0, .4, 0, 0, 0, SM); key(b, 0, .8, 0, 0, 0, SM);
            key(b, 0, .86, 0, 14 * sg, 0, SM); key(b, 0, .93, 0, -10 * sg, 0, SM); key(b, 0, 1, 0, 0, 0, SM);
        }
        return c.build();
    }

    /** Transformation: convulsing spasms along the whole spine with swelling (skin splitting), rear-up skyward roar (.5 = form change), head shakes, settle. */
    private static AnimClip transform() {
        AnimClip.Builder c = AnimClip.create();
        for (int idx = 0; idx < CHAIN.length; idx++) {
            AnimClip.BoneBuilder b = c.bone(CHAIN[idx]);
            key(b, 0, 0, 0, 0, 0, SM);
            for (int k = 1; k <= 8; k++) {
                key(b, 0, .05 * k, (idx < 3 ? -10 : 0) + 8 * Math.cos(k * 2.3 + idx), 28 * Math.sin(k * 1.9 + idx * .9), 0, OUT);
            }
            // CHAIN[0]=neck3, [1]=neck2, [2]=neck1, [3]=body
            double rx = idx == 2 ? -80 : idx == 1 ? -10 : idx == 0 ? 10 : idx == 3 ? -15 : 0;
            key(b, 0, .5, rx, 0, 0, SM);
            key(b, 0, .8, rx, 0, 0, SM);
            key(b, 0, 1, 0, 0, 0, SM);
        }
        R(c, "head", -1, OUT, 0, 0, 0, 0, .1, 0, 25, 0, .2, 10, -25, 0, .3, 0, 25, 0, .4, 10, -20, 0, .5, -35, 0, 0,
            .6, -38, 10, 0, .65, -38, -10, 0, .7, -38, 10, 0, .75, -36, -8, 0, .8, -35, 0, 0, 1, 0, 0, 0);
        R(c, "jaw", -1, OUT, 0, 0, 0, 0, .1, 30, 0, 0, .2, 10, 0, 0, .3, 40, 0, 0, .4, 15, 0, 0, .5, 75, 0, 0, .8, 75, 0, 0, .9, 0, 0, 0, 1, 0, 0, 0);
        R(c, "fangL", -1, OUT, 0, 0, 0, 0, .45, 0, 0, 0, .5, -50, 0, 0, .8, -50, 0, 0, .9, 0, 0, 0, 1, 0, 0, 0);
        R(c, "fangR", -1, OUT, 0, 0, 0, 0, .45, 0, 0, 0, .5, -50, 0, 0, .8, -50, 0, 0, .9, 0, 0, 0, 1, 0, 0, 0);
        P(c, "tongue", -1, OUT, 0, 0, 0, 0, .45, 0, 0, 0, .5, 0, 0, -6, .8, 0, 0, -6, .9, 0, 0, 0, 1, 0, 0, 0);
        R(c, "tongueTip", -1, OUT, 0, 0, 0, 0, .5, 0, 0, 0, .55, 0, 30, 0, .6, 0, -30, 0, .65, 0, 30, 0, .7, 0, -30, 0, .8, 0, 0, 0, 1, 0, 0, 0);
        S(c, "body", -1, OUT, 0, 1, 1, 1, .2, 1.06, 1.06, 1.06, .3, .97, .97, .97, .4, 1.12, 1.12, 1.12, .45, .95, .95, .95,
            .5, 1.08, 1.08, 1.08, .8, 1.04, 1.04, 1.04, 1, 1, 1, 1);
        P(c, "body", -1, OUT, 0, 0, 0, 0, .5, 0, -3, 0, .8, 0, -3, 0, 1, 0, 0, 0);
        return c.build();
    }
}
