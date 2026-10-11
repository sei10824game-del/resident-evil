package com.reevil.client.webspinner;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.giantspider.SpiderRigModel;
import com.reevil.entity.webspinner.WebSpinnerEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Spiderling on the shared spider rig (with silk thread). Impacts: leap 11/22, strand 15/26. */
public class WebSpinnerModel extends SpiderRigModel<WebSpinnerEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "web_spinner"), "main");

    private static final AnimClip LEAP = leap(), STRAND = strand(), DROP = drop(), ALERT = alert();

    public WebSpinnerModel(ModelPart root) { super(root, true); }

    public static LayerDefinition createBodyLayer() { return createRig(true); }

    @Override
    protected void baseAnim(WebSpinnerEntity e, float ls, float amt, float age, float yaw, float pitch) {
        spiderBase(ls, amt, age, yaw, pitch, 1.15f); // small, skittering: fast leg cadence
        bone("thread").xRot = -0.4f;
        bone("thread").yScale = 0.01f; bone("thread").zScale = 0.01f; bone("thread").xScale = 0.01f; // hidden unless dangling
        if (e.isDangling()) {
            tuckLegs(0.55f, 0.45f);
            bone("body").xRot += 0.5f + Mth.sin(age * 0.1f) * 0.05f; // hangs nose-down on its thread
            bone("thread").xScale = 1f; bone("thread").yScale = 1f; bone("thread").zScale = 1f;
            bone("thread").xRot = -0.5f - 0.5f; // thread points up from the spinneret once the body tilts
            bone("spinneret").zRot += Mth.sin(age * 0.2f) * 0.15f;
        }
        if (e.isClimbing()) bone("body").xRot -= 0.35f;
    }

    @Override
    protected AnimClip clip(WebSpinnerEntity e, int id) {
        switch (id) {
            case WebSpinnerEntity.LEAP: return LEAP;
            case WebSpinnerEntity.STRAND: return STRAND;
            case WebSpinnerEntity.DROP: return DROP;
            case WebSpinnerEntity.ALERT: return ALERT;
            default: return null;
        }
    }

    /** 22t, impact @11 (0.5): coil all legs, spring off the back legs, front legs spread, fangs clamp, tumble to land. */
    private static AnimClip leap() {
        AnimClip.Builder b = AnimClip.create();
        pos(b, "body", k(0, 0, 0, 0), k(0.25f, 0, 3, 2), k(0.36f, 0, -2, -4, SNAP), k(0.5f, 0, -4, -9, OUT), k(0.7f, 0, -1, -7), k(0.85f, 0, 2, -2), k(1, 0, 0, 0));
        rot(b, "body", k(0, 0, 0, 0), k(0.25f, 14, 0, 0), k(0.36f, -18, 0, 0, SNAP), k(0.5f, -6, 0, 0), k(0.7f, 14, 0, 0), k(0.85f, 6, 0, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -12, 0, 0), k(0.5f, 14, 0, 0, SNAP), k(0.7f, 6, 0, 0), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.25f, 12, 0, 0), k(0.36f, -14, 0, 0), k(0.6f, 10, 0, 0), k(1, 0, 0, 0));
        pair(b, "chel", k(0, 0, 0, 0), k(0.25f, -10, 0, 16), k(0.42f, -22, 0, 20), k(0.5f, 20, 0, -4, SNAP), k(0.7f, 12, 0, -2), k(1, 0, 0, 0));
        pair(b, "fang", k(0, 0, 0, 0), k(0.25f, 0, 0, 25), k(0.42f, 0, 0, 60), k(0.5f, 0, 0, -12, SNAP), k(0.8f, 0, 0, -6), k(1, 0, 0, 0));
        legs(b, 0, k(0, 0, 0, 0, 0), k(0.25f, 8, 30, -20, -10), k(0.36f, 30, 50, -20, -10, OUT), k(0.5f, 35, 55, -10, -5), k(0.7f, 20, 30, -10, 0), k(1, 0, 0, 0, 0));
        legs(b, 1, k(0, 0, 0, 0, 0), k(0.25f, 5, 15, -5, 0), k(0.36f, 20, 35, -15, -5, OUT), k(0.5f, 25, 40, -10, 0), k(1, 0, 0, 0, 0));
        legs(b, 2, k(0, 0, 0, 0, 0), k(0.25f, 0, -5, 10, 5), k(0.36f, -25, -20, 25, 12, SNAP), k(0.5f, -30, 25, -20, -10), k(0.7f, -10, 15, -10, 0), k(1, 0, 0, 0, 0));
        legs(b, 3, k(0, 0, 0, 0, 0), k(0.25f, -5, -10, 15, 8), k(0.36f, -35, -30, 35, 15, SNAP), k(0.5f, -35, 30, -25, -10), k(0.7f, -12, 15, -10, 0), k(1, 0, 0, 0, 0));
        return b.build();
    }

    /** 26t, release @15 (0.577): rear up, abdomen pumps and arcs forward, spinneret jets, legs brace, recover. */
    private static AnimClip strand() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -22, 0, 0), k(0.5f, -26, 0, 0), k(0.577f, -12, 0, 0, SNAP), k(0.75f, -14, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.3f, 0, -2, 3), k(0.577f, 0, -1, 1), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.3f, 10, 0, 0), k(0.5f, -45, 0, 0, OUT), k(0.577f, -70, 0, 0, SNAP), k(0.75f, -60, 0, 0), k(1, 0, 0, 0));
        scale(b, "abdomen", k(0, 1, 1, 1), k(0.4f, 1.15f, 1.15f, 1.05f), k(0.577f, 0.88f, 0.88f, 0.92f, SNAP), k(0.7f, 1.06f, 1.06f, 1f), k(1, 1, 1, 1));
        rot(b, "spinneret", k(0, 0, 0, 0), k(0.5f, 25, 0, 0), k(0.577f, 40, 0, 0), k(0.8f, 15, 0, 0), k(1, 0, 0, 0));
        scale(b, "spinneret", k(0, 1, 1, 1), k(0.5f, 1, 1, 1), k(0.577f, 1.5f, 1.5f, 2f, SNAP), k(0.8f, 1, 1, 1), k(1, 1, 1, 1));
        rot(b, "head", k(0, 0, 0, 0), k(0.4f, 10, 0, 0), k(0.577f, 6, 0, 0), k(1, 0, 0, 0));
        pair(b, "palp", k(0, 0, 0, 0), k(0.4f, -30, 25, 0), k(0.577f, -40, 10, 0), k(1, 0, 0, 0));
        legs(b, 0, k(0, 0, 0, 0, 0), k(0.3f, 20, 60, -30, -10), k(0.577f, 15, 45, -20, -5), k(1, 0, 0, 0, 0));
        legs(b, 1, k(0, 0, 0, 0, 0), k(0.3f, 10, 25, -10, 0), k(1, 0, 0, 0, 0));
        legs(b, 2, k(0, 0, 0, 0, 0), k(0.3f, -8, -5, 10, 5), k(0.577f, -10, -8, 14, 6), k(1, 0, 0, 0, 0));
        legs(b, 3, k(0, 0, 0, 0, 0), k(0.3f, -12, -10, 15, 8), k(0.577f, -14, -12, 20, 10), k(1, 0, 0, 0, 0));
        return b.build();
    }

    /** 18t: lets go of the ceiling, falls on its thread, jerks at the end, legs splay for the landing. */
    private static AnimClip drop() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, 10, 0, 0), k(0.7f, -10, 0, 0, IN), k(0.85f, 18, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.5f, -12, 0, 0), k(0.85f, 14, 0, 0), k(1, 0, 0, 0));
        pair(b, "chel", k(0, 0, 0, 0), k(0.7f, -20, 0, 22), k(0.9f, 18, 0, -3, SNAP), k(1, 0, 0, 0));
        pair(b, "fang", k(0, 0, 0, 0), k(0.7f, 0, 0, 55), k(0.9f, 0, 0, -10, SNAP), k(1, 0, 0, 0));
        for (int i = 0; i < 4; i++) legs(b, i, k(0, 0, 0, 0, 0), k(0.4f, 5, 40 + i * 5, -15, -5), k(0.75f, 15 - i * 8, 60, -25, -15), k(0.9f, 5, -5, 20, 10, SNAP), k(1, 0, 0, 0, 0));
        return b.build();
    }

    /** 24t: stands tall on rear legs, front legs and palps drum the air, whole body quivers to call the swarm. */
    private static AnimClip alert() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.2f, -28, 0, 0, OUT), k(0.35f, -30, 4, 0), k(0.5f, -30, -4, 0), k(0.65f, -30, 4, 0), k(0.8f, -28, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.2f, 0, -3, 4), k(0.8f, 0, -3, 4), k(1, 0, 0, 0));
        pair(b, "chel", k(0, 0, 0, 0), k(0.2f, -10, 0, 30), k(0.35f, -14, 0, 38), k(0.5f, -10, 0, 30), k(0.65f, -14, 0, 38), k(0.8f, -10, 0, 30), k(1, 0, 0, 0));
        pair(b, "palp", k(0, 0, 0, 0), k(0.2f, -35, 20, 0), k(0.3f, -10, 30, 0), k(0.4f, -45, 15, 0), k(0.5f, -10, 30, 0), k(0.6f, -45, 15, 0), k(0.7f, -10, 30, 0), k(0.8f, -35, 20, 0), k(1, 0, 0, 0));
        rot(b, "abdomen", k(0, 0, 0, 0), k(0.2f, 16, 0, 0), k(0.4f, 10, 0, 0), k(0.6f, 16, 0, 0), k(0.8f, 10, 0, 0), k(1, 0, 0, 0));
        scale(b, "abdomen", k(0, 1, 1, 1), k(0.3f, 1.1f, 1.1f, 1.05f), k(0.45f, 0.95f, 0.95f, 1f), k(0.6f, 1.1f, 1.1f, 1.05f), k(0.75f, 0.95f, 0.95f, 1f), k(1, 1, 1, 1));
        legs(b, 0, k(0, 0, 0, 0, 0), k(0.2f, 25, 75, -35, -15), k(0.3f, 30, 85, -40, -20), k(0.4f, 20, 65, -30, -10), k(0.5f, 30, 85, -40, -20), k(0.6f, 20, 65, -30, -10), k(0.8f, 25, 75, -35, -15), k(1, 0, 0, 0, 0));
        legs(b, 1, k(0, 0, 0, 0, 0), k(0.2f, 10, 35, -15, -5), k(0.4f, 15, 50, -20, -8), k(0.6f, 10, 35, -15, -5), k(0.8f, 12, 45, -18, -6), k(1, 0, 0, 0, 0));
        legs(b, 2, k(0, 0, 0, 0, 0), k(0.2f, -5, -4, 8, 3), k(1, 0, 0, 0, 0));
        legs(b, 3, k(0, 0, 0, 0, 0), k(0.2f, -8, -6, 10, 5), k(1, 0, 0, 0, 0));
        return b.build();
    }
}
