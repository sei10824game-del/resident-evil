package com.reevil.client.giantspider;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.entity.giantspider.GiantSpiderEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Black Tiger. Clip times are normalized; impacts sit at hitTick/duration of the matching AttackMove. */
public class GiantSpiderModel extends SpiderRigModel<GiantSpiderEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "giant_spider"), "main");

    private static final AnimClip BITE = bite(), SPIT = spit(), IMPALE = impale(), WEB = web(), DROP = drop(), BROOD = brood();

    public GiantSpiderModel(ModelPart root) { super(root, false); }

    public static LayerDefinition createBodyLayer() { return createRig(false); }

    @Override
    protected void baseAnim(GiantSpiderEntity e, float ls, float amt, float age, float yaw, float pitch) {
        boolean enraged = e.getForm() == 1;
        spiderBase(ls, amt, age, yaw, pitch, enraged ? 0.95f : 0.7f);
        if (e.isHanging()) {
            tuckLegs(0.35f, 0.3f); // clutching the ceiling (renderer flips the whole spider)
            bone("abdomen").xRot += Mth.sin(age * 0.05f) * 0.08f;
        } else if (e.isAscending()) { // hauling itself up its dragline: pairs reach and pull alternately
            for (int s = 0; s < 2; s++) {
                float sg = s == 0 ? 1 : -1;
                for (int i = 0; i < 4; i++) {
                    float c = Mth.sin(age * 0.45f + i * 1.6f + s * Mth.PI);
                    bone("fem" + SIDES[s] + i).zRot += sg * (0.6f + c * 0.35f);
                    bone("tib" + SIDES[s] + i).zRot += sg * (0.4f + c * 0.3f);
                }
            }
            bone("body").xRot -= 0.25f;
        }
        if (e.isClimbing()) bone("body").xRot -= 0.35f;
        if (enraged) { // ragged breathing, twitching palps
            bone("abdomen").yScale *= 1f + Mth.sin(age * 0.6f) * 0.04f;
            bone("palpR").xRot += Mth.sin(age * 1.3f) * 0.2f;
            bone("palpL").xRot += Mth.cos(age * 1.3f) * 0.2f;
        }
    }

    @Override
    protected AnimClip clip(GiantSpiderEntity e, int id) {
        switch (id) {
            case GiantSpiderEntity.BITE: return BITE;
            case GiantSpiderEntity.SPIT: return SPIT;
            case GiantSpiderEntity.IMPALE: return IMPALE;
            case GiantSpiderEntity.WEB: return WEB;
            case GiantSpiderEntity.DROP: return DROP;
            case GiantSpiderEntity.BROOD: return BROOD;
            default: return null;
        }
    }

    /** 34t, pin @12 (0.353), fangs @20 (0.588): rear back, front legs crash down to pin, fangs plunge, venom pump, release. */
    private static AnimClip bite() {
        AnimClip.Builder b = AnimClip.create();
        pos(b, "body", k(0, 0, 0, 0), k(0.25f, 0, 1.5f, 2), k(0.353f, 0, 0.5f, -3, OUT), k(0.5f, 0, 2.5f, -4), k(0.588f, 0, 3, -5, SNAP), k(0.7f, 0, 2.5f, -4.5f), k(0.82f, 0, 2, -3.5f), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.25f, -8, 0, 0), k(0.353f, 6, 0, 0, SNAP), k(0.5f, 10, 0, 0), k(0.588f, 18, 0, 0, SNAP), k(0.66f, 14, 4, 0), k(0.74f, 14, -4, 0), k(0.82f, 12, 3, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -15, 0, 0), k(0.353f, -5, 0, 0), k(0.5f, -20, 0, 0), k(0.588f, 25, 0, 0, SNAP), k(0.66f, 20, 6, 0), k(0.74f, 22, -6, 0), k(0.82f, 18, 0, 0), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.25f, 15, 0, 0), k(0.5f, 10, 0, 0), k(0.588f, -5, 0, 0, SNAP), k(0.74f, 4, 0, 0), k(1, 0, 0, 0));
        scale(b, "abdomen", k(0, 1, 1, 1), k(0.588f, 1, 1, 1), k(0.66f, 1.12f, 1.08f, 0.95f), k(0.74f, 0.95f, 0.95f, 1.04f), k(0.82f, 1.1f, 1.06f, 0.96f), k(1, 1, 1, 1));
        pair(b, "chel", k(0, 0, 0, 0), k(0.25f, -10, 0, 18), k(0.5f, -30, 0, 22), k(0.588f, 25, 0, -4, SNAP), k(0.82f, 20, 0, -4), k(1, 0, 0, 0));
        pair(b, "fang", k(0, 0, 0, 0), k(0.25f, 0, 0, 20), k(0.5f, 0, 0, 65), k(0.588f, 0, 0, -15, SNAP), k(0.66f, 0, 0, -10), k(0.74f, 0, 0, -18), k(0.9f, 0, 0, 0), k(1, 0, 0, 0));
        pair(b, "palp", k(0, 0, 0, 0), k(0.25f, -35, 20, 0), k(0.353f, 10, 25, 0, OUT), k(0.5f, -10, 30, 0), k(0.588f, 20, 15, 0), k(1, 0, 0, 0));
        pair(b, "palp2", k(0, 0, 0, 0), k(0.25f, -30, 0, 0), k(0.588f, 30, 0, 0), k(1, 0, 0, 0));
        legs(b, 0, k(0, 0, 0, 0, 0), k(0.25f, 25, 55, -30, -10), k(0.3f, 28, 60, -35, -15), k(0.353f, 15, -15, 35, 20, SNAP),
            k(0.5f, 12, -12, 30, 18), k(0.588f, 10, -18, 38, 22), k(0.8f, 10, -14, 30, 15), k(1, 0, 0, 0, 0));
        legs(b, 1, k(0, 0, 0, 0, 0), k(0.25f, 10, 15, -5, 0), k(0.353f, 5, -8, 12, 5, SNAP), k(0.588f, 0, -12, 15, 5), k(1, 0, 0, 0, 0));
        legs(b, 2, k(0, 0, 0, 0, 0), k(0.25f, -5, -5, 5, 0), k(0.353f, -10, 5, -5, 0), k(0.588f, -15, 10, -12, 0), k(1, 0, 0, 0, 0));
        legs(b, 3, k(0, 0, 0, 0, 0), k(0.25f, -8, -10, 8, 3), k(0.588f, -18, 12, -15, -5), k(1, 0, 0, 0, 0));
        return b.build();
    }

    /** 36t, release @22 (0.611): rock back on the haunches, abdomen arcs up and over, pumps, flicks the venom glob. */
    private static AnimClip spit() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.15f, -8, 0, 0), k(0.42f, 70, 0, 0), k(0.5f, 75, 0, 0), k(0.58f, 62, 0, 0), k(0.611f, 95, 0, 0, SNAP),
            k(0.68f, 80, 0, 0), k(0.76f, 88, 0, 0), k(0.9f, 30, 0, 0), k(1, 0, 0, 0));
        scale(b, "abdomen", k(0, 1, 1, 1), k(0.42f, 1.08f, 1.08f, 1), k(0.5f, 1.18f, 1.15f, 1.02f), k(0.58f, 1.22f, 1.2f, 1), k(0.611f, 0.88f, 0.88f, 1.05f, SNAP), k(0.7f, 1.05f, 1.04f, 1), k(1, 1, 1, 1));
        rot(b, "spinneret", k(0, 0, 0, 0), k(0.42f, 30, 0, 0), k(0.611f, 50, 0, 0, SNAP), k(0.8f, 20, 0, 0), k(1, 0, 0, 0));
        scale(b, "spinneret", k(0, 1, 1, 1), k(0.5f, 1.2f, 1.2f, 1.2f), k(0.58f, 1.4f, 1.4f, 1.4f), k(0.611f, 0.7f, 0.7f, 0.7f, SNAP), k(0.7f, 1.1f, 1.1f, 1.1f), k(1, 1, 1, 1));
        rot(b, "body", k(0, 0, 0, 0), k(0.42f, -12, 0, 0), k(0.611f, -4, 0, 0, SNAP), k(0.75f, -8, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.42f, 0, 1, 1.5f), k(0.611f, 0, 0, -1, SNAP), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.42f, -20, 0, 0), k(0.611f, -8, 0, 0), k(1, 0, 0, 0));
        pair(b, "fang", k(0, 0, 0, 0), k(0.5f, 0, 0, 30), k(0.611f, 0, 0, 45), k(0.8f, 0, 0, 10), k(1, 0, 0, 0));
        pair(b, "palp", k(0, 0, 0, 0), k(0.3f, -25, 15, 0), k(0.45f, -10, 25, 0), k(0.611f, -30, 20, 0), k(1, 0, 0, 0));
        legs(b, 0, k(0, 0, 0, 0, 0), k(0.42f, 15, 20, -10, 0), k(0.611f, 18, 10, 5, 5, SNAP), k(1, 0, 0, 0, 0));
        legs(b, 1, k(0, 0, 0, 0, 0), k(0.42f, 5, -5, 5, 0), k(1, 0, 0, 0, 0));
        legs(b, 2, k(0, 0, 0, 0, 0), k(0.42f, -5, 15, -10, 0), k(0.611f, -5, 5, -2, 0), k(1, 0, 0, 0, 0));
        legs(b, 3, k(0, 0, 0, 0, 0), k(0.42f, -10, 25, -18, -5), k(0.611f, -12, 15, -10, 0), k(1, 0, 0, 0, 0));
        return b.build();
    }

    /** 30t, stab @17 (0.567): rear up, front legs hooked high and trembling, slam down spearing, twist free, recover. */
    private static AnimClip impale() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.15f, 4, 0, 0), k(0.42f, -35, 0, 0), k(0.5f, -38, 0, 0), k(0.567f, 14, 0, 0, SNAP), k(0.65f, 10, 0, 0), k(0.78f, 6, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.15f, 0, 1, 1), k(0.42f, 0, -4, 2), k(0.5f, 0, -4.5f, 2.5f), k(0.567f, 0, 2, -3, SNAP), k(0.7f, 0, 1.5f, -2.5f), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.42f, -10, 0, 0), k(0.567f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.42f, -25, 0, 0), k(0.567f, 15, 0, 0, SNAP), k(0.7f, 5, 0, 0), k(1, 0, 0, 0));
        legs(b, 0, k(0, 0, 0, 0, 0), k(0.15f, 5, 10, 5, 0), k(0.42f, 30, 85, 40, -20), k(0.47f, 32, 90, 45, -20), k(0.5f, 29, 86, 42, -18), k(0.53f, 32, 90, 46, -20),
            k(0.567f, 20, -10, -20, -25, SNAP), k(0.62f, 18, -14, -22, -25), k(0.68f, 26, -10, -18, -20), k(0.75f, 12, -8, -15, -20), k(1, 0, 0, 0, 0));
        legs(b, 1, k(0, 0, 0, 0, 0), k(0.42f, 15, 40, 10, 0), k(0.567f, 10, -5, 5, 0, SNAP), k(1, 0, 0, 0, 0));
        legs(b, 2, k(0, 0, 0, 0, 0), k(0.42f, -10, -15, 15, 5), k(0.567f, -5, 5, -5, 0), k(1, 0, 0, 0, 0));
        legs(b, 3, k(0, 0, 0, 0, 0), k(0.42f, -15, -20, 20, 5), k(0.567f, -8, 8, -5, 0), k(1, 0, 0, 0, 0));
        pair(b, "palp", k(0, 0, 0, 0), k(0.42f, -40, 30, 0), k(0.567f, 20, 10, 0, SNAP), k(1, 0, 0, 0));
        pair(b, "fang", k(0, 0, 0, 0), k(0.42f, 0, 0, 40), k(0.567f, 0, 0, 50), k(0.75f, 0, 0, 10), k(1, 0, 0, 0));
        pair(b, "chel", k(0, 0, 0, 0), k(0.42f, -20, 0, 15), k(0.567f, 10, 0, 5), k(1, 0, 0, 0));
        return b.build();
    }

    /** 40t, shot @26 (0.65): stand tall, abdomen curls under to aim the spinnerets, pumps, fires; hind legs comb the silk. */
    private static AnimClip web() {
        AnimClip.Builder b = AnimClip.create();
        pos(b, "body", k(0, 0, 0, 0), k(0.3f, 0, -3, 1), k(0.65f, 0, -3.5f, 1.5f), k(0.75f, 0, -2.5f, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -6, 0, 0), k(0.65f, -10, 0, 0, SNAP), k(0.8f, -4, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, 8, 0, 0), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.3f, -55, 0, 0), k(0.45f, -62, 0, 0), k(0.55f, -58, 0, 0), k(0.6f, -64, 0, 0), k(0.65f, -80, 0, 0, SNAP),
            k(0.72f, -70, 0, 0), k(0.85f, -40, 0, 0), k(1, 0, 0, 0));
        pos(b, "abdomen", k(0, 0, 0, 0), k(0.6f, 0, 0, 0), k(0.65f, 0, 0, -2, SNAP), k(0.85f, 0, 0, 0), k(1, 0, 0, 0));
        scale(b, "abdomen", k(0, 1, 1, 1), k(0.45f, 1.1f, 1.1f, 1), k(0.5f, 0.97f, 0.97f, 1), k(0.55f, 1.12f, 1.12f, 1), k(0.6f, 0.96f, 0.96f, 1),
            k(0.65f, 0.9f, 0.9f, 1.1f, SNAP), k(0.75f, 1.04f, 1.04f, 1), k(1, 1, 1, 1));
        rot(b, "spinneret", k(0, 0, 0, 0), k(0.3f, -20, 0, 0), k(0.65f, -35, 0, 0, SNAP), k(0.85f, 0, 0, 0), k(1, 0, 0, 0));
        scale(b, "spinneret", k(0, 1, 1, 1), k(0.6f, 1.3f, 1.3f, 1.3f), k(0.65f, 0.7f, 0.7f, 0.7f, SNAP), k(0.75f, 1.2f, 1.2f, 1.2f), k(1, 1, 1, 1));
        legs(b, 0, k(0, 0, 0, 0, 0), k(0.3f, 10, -18, -15, -5), k(0.65f, 12, -20, -18, -5), k(1, 0, 0, 0, 0));
        legs(b, 1, k(0, 0, 0, 0, 0), k(0.3f, 5, -15, -12, -3), k(0.65f, 5, -15, -12, -3), k(1, 0, 0, 0, 0));
        legs(b, 2, k(0, 0, 0, 0, 0), k(0.3f, -5, -15, -12, -3), k(0.65f, -5, -15, -12, -3), k(1, 0, 0, 0, 0));
        leg(b, "R", 3, k(0, 0, 0, 0, 0), k(0.3f, -5, -10, -10, 0), k(0.65f, -5, -10, -10, 0), k(0.7f, 25, 30, 10, 0), k(0.76f, -10, 10, 0, 0),
            k(0.82f, 25, 30, 10, 0), k(0.9f, -10, 5, 0, 0), k(1, 0, 0, 0, 0));
        leg(b, "L", 3, k(0, 0, 0, 0, 0), k(0.3f, -5, -10, -10, 0), k(0.65f, -5, -10, -10, 0), k(0.73f, 25, 30, 10, 0), k(0.79f, -10, 10, 0, 0),
            k(0.85f, 25, 30, 10, 0), k(0.93f, -5, 5, 0, 0), k(1, 0, 0, 0, 0));
        pair(b, "palp", k(0, 0, 0, 0), k(0.3f, -15, 10, 0), k(0.65f, 5, 20, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 28t, landing ~0.4 (hit applied on touchdown): twist upright mid-fall, legs flare then brace, squash, front legs strike. */
    private static AnimClip drop() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 180), k(0.12f, 0, 0, 180), k(0.3f, -20, 0, 0, OUT), k(0.4f, 10, 0, 0), k(0.5f, 6, 0, 0), k(0.7f, 0, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.38f, 0, -1, 0), k(0.42f, 0, 4, 0, SNAP), k(0.55f, 0, 1.5f, -2), k(0.7f, 0, 0.5f, -1), k(1, 0, 0, 0));
        scale(b, "body", k(0, 1, 1, 1), k(0.4f, 1, 1, 1), k(0.42f, 1.08f, 0.85f, 1.08f, SNAP), k(0.55f, 0.97f, 1.04f, 0.97f), k(0.65f, 1, 1, 1), k(1, 1, 1, 1));
        rot(b, "abdomen", k(0, 30, 0, 0), k(0.3f, -10, 0, 0), k(0.42f, 15, 0, 0, SNAP), k(0.6f, 0, 0, 0), k(1, 0, 0, 0));
        pair(b, "fang", k(0, 0, 0, 0), k(0.3f, 0, 0, 50), k(0.5f, 0, 0, 55), k(0.58f, 0, 0, -10, SNAP), k(1, 0, 0, 0));
        legs(b, 0, k(0, 0, 40, -30, -10), k(0.15f, 20, 50, -50, -20), k(0.38f, 10, -10, -25, -10), k(0.42f, 10, 25, 25, 10, SNAP),
            k(0.5f, 25, 50, 20, -10), k(0.58f, 20, -10, 30, 10, SNAP), k(0.75f, 10, -5, 15, 5), k(1, 0, 0, 0, 0));
        for (int i = 1; i < 4; i++) {
            legs(b, i, k(0, 0, 40, -30, -10), k(0.15f, 0, 50, -50, -20), k(0.38f, 0, -10, -25, -10), k(0.42f, 0, 25, 25, 10, SNAP), k(0.6f, 0, 10, 8, 3), k(1, 0, 0, 0, 0));
        }
        return b.build();
    }

    /** 50t, burst @30 (0.6): convulsing swelling abdomen, whole-body tremor, burst + splay flat, rear-up screech with legs raised. */
    private static AnimClip brood() {
        AnimClip.Builder b = AnimClip.create();
        scale(b, "abdomen", k(0, 1, 1, 1), k(0.15f, 1.1f, 1.1f, 1.05f), k(0.2f, 1.05f, 1.04f, 1.02f), k(0.3f, 1.2f, 1.18f, 1.08f), k(0.35f, 1.14f, 1.12f, 1.05f),
            k(0.45f, 1.3f, 1.28f, 1.12f), k(0.5f, 1.25f, 1.22f, 1.1f), k(0.58f, 1.4f, 1.38f, 1.15f, IN), k(0.6f, 0.85f, 0.85f, 0.95f, SNAP),
            k(0.7f, 1.05f, 1.05f, 1), k(0.8f, 0.95f, 0.95f, 1), k(1, 1, 1, 1));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.3f, 10, 0, 0), k(0.45f, 15, 6, 0), k(0.5f, 15, -6, 0), k(0.58f, 20, 0, 0), k(0.6f, 45, 0, 0, SNAP), k(0.7f, 30, 0, 0), k(0.85f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.1f, 0, 4, 3, LIN), k(0.15f, 0, -4, -3, LIN), k(0.2f, 0, 5, 2, LIN), k(0.25f, 0, -5, -2, LIN), k(0.3f, 5, 4, 4, LIN),
            k(0.35f, 5, -4, -4, LIN), k(0.4f, 6, 6, 3, LIN), k(0.45f, 6, -6, -3, LIN), k(0.5f, 8, 5, 4, LIN), k(0.55f, 8, -5, -4, LIN),
            k(0.6f, 10, 0, 0, SNAP), k(0.72f, -28, 0, 0), k(0.8f, -30, 0, 0), k(0.9f, -10, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.55f, 0, 1.5f, 0), k(0.6f, 0, 3, 0, SNAP), k(0.72f, 0, -3, 1), k(0.85f, 0, -2, 0.5f), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.5f, 15, 0, 0), k(0.6f, 20, 0, 0), k(0.72f, -25, 0, 0), k(0.85f, -20, 0, 0), k(1, 0, 0, 0));
        pair(b, "fang", k(0, 0, 0, 0), k(0.6f, 0, 0, 10), k(0.72f, 0, 0, 70), k(0.85f, 0, 0, 60), k(1, 0, 0, 0));
        pair(b, "chel", k(0, 0, 0, 0), k(0.6f, 0, 0, 0), k(0.72f, -25, 0, 25), k(0.85f, -20, 0, 20), k(1, 0, 0, 0));
        pair(b, "palp", k(0, 0, 0, 0), k(0.6f, 10, 0, 0), k(0.72f, -40, 35, 0), k(0.8f, -30, 20, 0), k(0.88f, -45, 35, 0), k(1, 0, 0, 0));
        for (int i = 0; i < 4; i++) {
            float[][] rows = new float[i == 0 ? 17 : 14][];
            int n = 0;
            rows[n++] = k(0, 0, 0, 0, 0);
            for (int j = 0; j < 10; j++) { // tremor: legs jitter harder as the abdomen swells
                float t = 0.1f + j * 0.05f, a = 3 + j * 0.6f;
                rows[n++] = j % 2 == 0 ? k(t, 0, a, a * 0.8f, 0, LIN) : k(t, 0, -a * 0.4f, -a * 0.3f, 0, LIN);
            }
            rows[n++] = k(0.6f, 0, -25, -20, -5, SNAP);
            if (i == 0) { // front pair rears up and flails
                rows[n++] = k(0.72f, 25, 75, 30, -15);
                rows[n++] = k(0.78f, 30, 85, 20, -10);
                rows[n++] = k(0.84f, 22, 70, 35, -15);
                rows[n++] = k(0.9f, 28, 80, 25, -10);
            } else {
                rows[n++] = k(0.72f, 0, -10, -5, 0);
            }
            rows[n] = k(1, 0, 0, 0, 0);
            legs(b, i, rows);
        }
        return b.build();
    }
}
