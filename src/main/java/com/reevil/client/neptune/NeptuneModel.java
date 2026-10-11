package com.reevil.client.neptune;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.neptune.NeptuneEntity;
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
 * Mutated great white. Faces -Z. +xRot = nose DOWN. Texture 128x128 (tools/tex_neptune.py).
 * Chain: body > head > jaw ; body > tail1 > tail2 > caudalU/caudalL ; body > pecL/pecR, dorsal, pelvL/pelvR.
 * Impacts: bite 18/30 (.6), breach 24/40 (.6), roll 10,20,30,40/50, slap 15/30 (.5).
 */
public class NeptuneModel extends BioModel<NeptuneEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "neptune"), "main");

    private static final AnimClip BITE = bite(), BREACH = breach(), ROLL = roll(), SLAP = slap(), CIRCLE = circle();

    public NeptuneModel(ModelPart root) {
        super(root);
        reg("body", "body");
        reg("head", "body/head");
        reg("jaw", "body/head/jaw");
        reg("tail1", "body/tail1");
        reg("tail2", "body/tail1/tail2");
        reg("caudalU", "body/tail1/tail2/caudalU");
        reg("caudalL", "body/tail1/tail2/caudalL");
        reg("dorsal", "body/dorsal");
        reg("pecL", "body/pecL");
        reg("pecR", "body/pecR");
        reg("pelvL", "body/pelvL");
        reg("pelvR", "body/pelvR");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7, -6, -11, 14, 12, 22), PartPose.offset(0, 14, 0));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 36).addBox(-6, -5, -12, 12, 10, 12)
            .texOffs(48, 36).addBox(-5, 5, -11, 10, 2, 1).texOffs(48, 40).addBox(-5, 5, -7, 10, 2, 1), PartPose.offset(0, -0.5f, -11));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(72, 0).addBox(-5, 0, -11, 10, 3, 11)
            .texOffs(72, 14).addBox(-5, -2, -10, 10, 2, 1).texOffs(96, 14).addBox(-5, -2, -6, 10, 2, 1), PartPose.offset(0, 4.5f, -0.5f));
        PartDefinition t1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(72, 18).addBox(-5, -5, 0, 10, 10, 12), PartPose.offset(0, 0.5f, 10.5f));
        PartDefinition t2 = t1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 60).addBox(-3, -3.5f, 0, 6, 7, 10), PartPose.offset(0, 0, 11.5f));
        t2.addOrReplaceChild("caudalU", CubeListBuilder.create().texOffs(34, 60).addBox(-1, -12, 0, 2, 12, 6), PartPose.offsetAndRotation(0, -1, 8, 0.45f, 0, 0));
        t2.addOrReplaceChild("caudalL", CubeListBuilder.create().texOffs(52, 60).addBox(-1, 0, 0, 2, 8, 5), PartPose.offsetAndRotation(0, 1, 8, -0.5f, 0, 0));
        body.addOrReplaceChild("dorsal", CubeListBuilder.create().texOffs(34, 80).addBox(-1, -9, 0, 2, 9, 8), PartPose.offsetAndRotation(0, -6, -3, 0.35f, 0, 0));
        body.addOrReplaceChild("pecL", CubeListBuilder.create().texOffs(0, 80).addBox(0, -0.5f, -2, 10, 1, 6), PartPose.offsetAndRotation(6, 4, -6, 0, -0.3f, 0.5f));
        body.addOrReplaceChild("pecR", CubeListBuilder.create().texOffs(0, 80).mirror().addBox(-10, -0.5f, -2, 10, 1, 6), PartPose.offsetAndRotation(-6, 4, -6, 0, 0.3f, -0.5f));
        body.addOrReplaceChild("pelvL", CubeListBuilder.create().texOffs(56, 80).addBox(0, -0.5f, 0, 6, 1, 4), PartPose.offsetAndRotation(4, 5.5f, 6, 0, -0.3f, 0.4f));
        body.addOrReplaceChild("pelvR", CubeListBuilder.create().texOffs(56, 80).mirror().addBox(-6, -0.5f, 0, 6, 1, 4), PartPose.offsetAndRotation(-4, 5.5f, 6, 0, 0.3f, -0.4f));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    protected void baseAnim(NeptuneEntity e, float ls, float amt, float age, float yaw, float pitch) {
        boolean wet = e.isInWaterOrBubble();
        float speed = Mth.clamp(amt * 2f, 0f, 1f);
        float ph = age * (0.14f + speed * 0.22f);
        float sw = wet ? 0.28f + speed * 0.22f : 0f;
        bone("tail1").yRot = Mth.sin(ph) * sw;
        bone("tail2").yRot = Mth.sin(ph - 0.9f) * sw * 1.25f;
        bone("caudalU").yRot = Mth.sin(ph - 1.8f) * sw * 1.4f;
        bone("caudalL").yRot = Mth.sin(ph - 1.8f) * sw * 1.4f;
        bone("body").yRot = -Mth.sin(ph) * sw * 0.35f;
        bone("head").yRot = -Mth.sin(ph) * sw * 0.7f + Mth.clamp(yaw, -25f, 25f) * Mth.DEG_TO_RAD * 0.5f;
        bone("head").xRot = Mth.clamp(pitch, -25f, 25f) * Mth.DEG_TO_RAD * 0.5f;
        float p = Mth.sin(age * 0.07f);
        bone("pecL").zRot += 0.5f * 0 + p * 0.12f;
        bone("pecR").zRot += -p * 0.12f;
        bone("pelvL").zRot += p * 0.1f;
        bone("pelvR").zRot += -p * 0.1f;
        bone("dorsal").zRot += Mth.sin(ph) * 0.08f;
        // lazy menacing half-open jaw while hunting, slow gill-breath otherwise
        bone("jaw").xRot += wet ? 0.10f + Mth.sin(age * 0.09f) * 0.07f + (e.getTarget() != null ? 0.12f : 0f) : 0.5f + Mth.sin(age * 0.7f) * 0.2f;
        if (!wet && e.deathTime == 0) { // beached: violent flopping
            float f = Mth.sin(age * 0.9f);
            bone("body").zRot += f * 0.5f;
            bone("tail1").yRot = f * 0.7f;
            bone("tail2").yRot = -f * 0.9f;
            bone("caudalU").yRot = f * 0.8f;
            bone("caudalL").yRot = f * 0.8f;
            bone("body").y -= Math.abs(f) * 1.5f;
        }
        if (e.hurtTime > 0) { // flinch: whole body kinks away
            float h = e.hurtTime / (float) Math.max(1, e.hurtDuration);
            bone("body").yRot += h * 0.35f;
            bone("tail1").yRot -= h * 0.5f;
            bone("jaw").xRot += h * 0.6f;
        }
    }

    @Override
    protected AnimClip clip(NeptuneEntity e, int id) {
        switch (id) {
            case NeptuneEntity.BITE: return BITE;
            case NeptuneEntity.BREACH: return BREACH;
            case NeptuneEntity.ROLL: return ROLL;
            case NeptuneEntity.SLAP: return SLAP;
            case NeptuneEntity.CIRCLE: return CIRCLE;
            default: return null;
        }
    }

    /** death: rolls belly-up, jaw slack, fins limp, drifting nose-down. */
    @Override
    protected void extraAnim(NeptuneEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f);
        float s = d * d * (3 - 2 * d);
        bone("body").zRot = s * 3.14159f;
        bone("body").xRot += s * 0.25f;
        bone("tail1").yRot = Mth.sin(age * 0.2f) * 0.08f * (1 - s);
        bone("tail2").yRot = Mth.sin(age * 0.2f - 0.9f) * 0.1f * (1 - s);
        bone("jaw").xRot = 0.1f + s * 0.75f;
        bone("pecL").zRot = 0.5f + s * 0.9f;
        bone("pecR").zRot = -0.5f - s * 0.9f;
        bone("dorsal").zRot = s * 0.3f;
    }

    /** 30t: S-curve cock + jaws gape, head rears up, explosive thrust, jaws clamp @0.6, violent head-shake. */
    private static AnimClip bite() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, -4, 18, 8), k(0.5f, 6, -4, -3, SNAP), k(0.6f, 8, 0, 0), k(0.75f, 4, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.3f, 0, 0, 5), k(0.5f, 0, 0, -6, SNAP), k(0.6f, 0, 0, -9), k(0.8f, 0, 0, -5), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -22, -15, 0), k(0.5f, 12, 0, 0, SNAP), k(0.6f, 14, 0, 0, SNAP), k(0.67f, 10, 28, 6), k(0.74f, 12, -30, -6), k(0.81f, 10, 26, 5), k(0.88f, 8, -18, -3), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 55, 0, 0, OUT), k(0.52f, 62, 0, 0), k(0.6f, -5, 0, 0, SNAP), k(0.85f, 3, 0, 0), k(1, 0, 0, 0));
        rot(b, "tail1", k(0, 0, 0, 0), k(0.3f, 0, -22, 0), k(0.5f, 0, 18, 0, SNAP), k(0.7f, 0, -10, 0), k(0.85f, 0, 6, 0), k(1, 0, 0, 0));
        rot(b, "tail2", k(0, 0, 0, 0), k(0.3f, 0, -26, 0), k(0.5f, 0, 24, 0, SNAP), k(0.7f, 0, -14, 0), k(1, 0, 0, 0));
        rot(b, "caudalU", k(0, 0, 0, 0), k(0.3f, 0, -24, 0), k(0.52f, 0, 30, 0, SNAP), k(0.75f, 0, -12, 0), k(1, 0, 0, 0));
        rot(b, "caudalL", k(0, 0, 0, 0), k(0.3f, 0, -24, 0), k(0.52f, 0, 30, 0, SNAP), k(0.75f, 0, -12, 0), k(1, 0, 0, 0));
        pair(b, "pec", k(0, 0, 0, 0), k(0.3f, 0, 0, -30), k(0.5f, 0, 20, 45, SNAP), k(0.8f, 0, 0, 10), k(1, 0, 0, 0));
        rot(b, "dorsal", k(0, 0, 0, 0), k(0.5f, -22, 0, 0, SNAP), k(0.8f, -8, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 40t: dive and arch down, then rocket nose-up out of the water (apex @0.6 where jaws snap shut), crash back over. */
    private static AnimClip breach() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.18f, 38, 0, 0), k(0.3f, 30, 0, 0), k(0.42f, -55, 0, 0, SNAP), k(0.6f, -48, 0, 0), k(0.75f, 30, 0, 0, IN), k(0.9f, 55, 0, 0), k(1, 0, 0, 0));
        pos(b, "body", k(0, 0, 0, 0), k(0.2f, 0, 8, 0), k(0.3f, 0, 9, 0), k(0.42f, 0, -6, 0, SNAP), k(0.6f, 0, -14, 0), k(0.8f, 0, 2, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.18f, -15, 0, 0), k(0.42f, 20, 0, 0), k(0.55f, -5, 8, 0), k(0.6f, 10, -8, 0, SNAP), k(0.7f, 25, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 10, 0, 0), k(0.42f, 70, 0, 0, OUT), k(0.55f, 75, 0, 0), k(0.6f, -6, 0, 0, SNAP), k(0.75f, 5, 0, 0), k(1, 0, 0, 0));
        rot(b, "tail1", k(0, 0, 0, 0), k(0.18f, 0, 0, 0), k(0.3f, 18, 12, 0), k(0.42f, -8, -10, 0), k(0.6f, -6, 8, 0), k(0.8f, 10, 0, 0), k(1, 0, 0, 0));
        rot(b, "tail2", k(0, 0, 0, 0), k(0.3f, 22, 0, 0), k(0.42f, -14, 18, 0), k(0.6f, -10, -14, 0), k(0.8f, 12, 0, 0), k(1, 0, 0, 0));
        rot(b, "caudalU", k(0, 0, 0, 0), k(0.42f, 0, 25, 0), k(0.6f, 0, -22, 0), k(0.8f, 0, 12, 0), k(1, 0, 0, 0));
        rot(b, "caudalL", k(0, 0, 0, 0), k(0.42f, 0, 25, 0), k(0.6f, 0, -22, 0), k(0.8f, 0, 12, 0), k(1, 0, 0, 0));
        pair(b, "pec", k(0, 0, 0, 0), k(0.3f, 0, 0, -20), k(0.42f, 0, 0, 55), k(0.6f, 0, 0, 40), k(1, 0, 0, 0));
        rot(b, "dorsal", k(0, 0, 0, 0), k(0.42f, 18, 0, 0), k(0.75f, -15, 0, 0), k(1, 0, 0, 0));
        return b.build();
    }

    /** 50t: clamped on, rolls twice (hits every 10t) with the whole body corkscrewing and the head wrenching. */
    private static AnimClip roll() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.08f, 6, 0, 0), k(0.2f, 4, 0, 180, LIN), k(0.4f, 4, 0, 360, LIN), k(0.6f, 4, 0, 540, LIN), k(0.8f, 4, 0, 720, LIN), k(0.92f, 2, 0, 720), k(1, 0, 0, 720));
        rot(b, "head", k(0, 0, 0, 0), k(0.08f, 14, 0, 0), k(0.2f, 12, 22, 0), k(0.3f, 14, -24, 0), k(0.4f, 12, 24, 0), k(0.5f, 14, -26, 0), k(0.6f, 12, 24, 0), k(0.7f, 14, -24, 0), k(0.8f, 12, 20, 0), k(0.95f, 4, 0, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.06f, 40, 0, 0), k(0.2f, -4, 0, 0, SNAP), k(0.82f, -2, 0, 0), k(0.92f, 25, 0, 0), k(1, 0, 0, 0));
        rot(b, "tail1", k(0, 0, 0, 0), k(0.1f, 0, 20, 0), k(0.2f, 0, -22, 0), k(0.3f, 0, 22, 0), k(0.4f, 0, -22, 0), k(0.5f, 0, 22, 0), k(0.6f, 0, -22, 0), k(0.7f, 0, 22, 0), k(0.8f, 0, -22, 0), k(1, 0, 0, 0));
        rot(b, "tail2", k(0, 0, 0, 0), k(0.1f, 0, 30, 0), k(0.2f, 0, -30, 0), k(0.3f, 0, 30, 0), k(0.4f, 0, -30, 0), k(0.5f, 0, 30, 0), k(0.6f, 0, -30, 0), k(0.7f, 0, 30, 0), k(0.8f, 0, -30, 0), k(1, 0, 0, 0));
        rot(b, "caudalU", k(0, 0, 0, 0), k(0.2f, 0, 30, 0), k(0.3f, 0, -30, 0), k(0.4f, 0, 30, 0), k(0.5f, 0, -30, 0), k(0.6f, 0, 30, 0), k(0.7f, 0, -30, 0), k(1, 0, 0, 0));
        rot(b, "caudalL", k(0, 0, 0, 0), k(0.2f, 0, 30, 0), k(0.3f, 0, -30, 0), k(0.4f, 0, 30, 0), k(0.5f, 0, -30, 0), k(0.6f, 0, 30, 0), k(0.7f, 0, -30, 0), k(1, 0, 0, 0));
        pair(b, "pec", k(0, 0, 0, 0), k(0.1f, 0, 0, 50), k(0.85f, 0, 0, 50), k(1, 0, 0, 0));
        return b.build();
    }

    /** 30t: coils into a C away from the target, then whips the tail across (impact @0.5), caudal cracks, recoils. */
    private static AnimClip slap() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.3f, 0, 14, -6), k(0.5f, 0, -20, 4, SNAP), k(0.7f, 0, -8, 0), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, 0, -26, 0), k(0.5f, 0, 18, 0, SNAP), k(0.7f, 0, 8, 0), k(1, 0, 0, 0));
        rot(b, "tail1", k(0, 0, 0, 0), k(0.3f, 0, 38, 0), k(0.5f, 0, -45, 0, SNAP), k(0.7f, 0, -12, 0), k(1, 0, 0, 0));
        rot(b, "tail2", k(0, 0, 0, 0), k(0.3f, 0, 44, 0), k(0.5f, 0, -55, 0, SNAP), k(0.7f, 0, -10, 0), k(1, 0, 0, 0));
        rot(b, "caudalU", k(0, 0, 0, 0), k(0.3f, 0, 40, 0), k(0.5f, 0, -60, 0, SNAP), k(0.62f, 0, 35, 0), k(0.8f, 0, -8, 0), k(1, 0, 0, 0));
        rot(b, "caudalL", k(0, 0, 0, 0), k(0.3f, 0, 40, 0), k(0.5f, 0, -60, 0, SNAP), k(0.62f, 0, 35, 0), k(0.8f, 0, -8, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.3f, 30, 0, 0), k(0.5f, 8, 0, 0), k(1, 0, 0, 0));
        pair(b, "pec", k(0, 0, 0, 0), k(0.3f, 0, 0, 40), k(0.5f, 0, 0, -20), k(1, 0, 0, 0));
        rot(b, "dorsal", k(0, 0, 0, 0), k(0.3f, 0, 0, 14), k(0.5f, 0, 0, -18, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 60t: banked lazy arcs, dorsal slicing the surface, one sudden head-snap and jaw flash toward the prey. */
    private static AnimClip circle() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "body", k(0, 0, 0, 0), k(0.25f, -2, 6, 14), k(0.5f, 2, -6, -14), k(0.7f, 0, 10, 12), k(0.85f, 4, 0, 0, OUT), k(1, 0, 0, 0));
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, 0, -14, 0), k(0.6f, 0, 14, 0), k(0.74f, 0, -30, 0, SNAP), k(0.85f, 0, 4, 0), k(1, 0, 0, 0));
        rot(b, "jaw", k(0, 0, 0, 0), k(0.7f, 6, 0, 0), k(0.76f, 48, 0, 0, SNAP), k(0.84f, 4, 0, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "tail1", k(0, 0, 0, 0), k(0.25f, 0, -10, 0), k(0.5f, 0, 10, 0), k(0.74f, 0, -22, 0), k(1, 0, 0, 0));
        rot(b, "dorsal", k(0, 0, 0, 0), k(0.25f, 0, 0, -10), k(0.5f, 0, 0, 10), k(1, 0, 0, 0));
        pair(b, "pec", k(0, 0, 0, 0), k(0.25f, 0, 0, 15), k(0.5f, 0, 0, -15), k(0.74f, 0, 0, 40), k(1, 0, 0, 0));
        return b.build();
    }
}
