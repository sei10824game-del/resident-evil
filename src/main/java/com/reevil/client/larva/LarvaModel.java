package com.reevil.client.larva;

import com.reevil.ReEvilMod;
import com.reevil.anim.AnimClip;
import com.reevil.client.BioModel;
import com.reevil.entity.larva.LarvaEntity;
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
 * Moth larva: a chain head > s1 > s2 > s3 > s4 > s5 running toward +Z (tail), mandibles mandL/R on the head. Texture 64x64 (tools/tex_larva.py).
 * Impacts: bite .4545, lunge .464, spit .528.
 */
public class LarvaModel extends BioModel<LarvaEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "larva"), "main");
    private static final AnimClip C_BITE = bite(), C_LUNGE = lunge(), C_SPIT = spit();

    public LarvaModel(ModelPart root) {
        super(root);
        String p = "head"; reg("head", p);
        reg("mandL", "head/mandL"); reg("mandR", "head/mandR");
        for (int i = 1; i <= 5; i++) { p += "/s" + i; reg("s" + i, p); }
    }

    public static LayerDefinition createBodyLayer() { return LayerDefinition.create(mesh(), 64, 64); }
    public static MeshDefinition mesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5f, -4, -5, 5, 4, 5)
            .texOffs(30, 0).addBox(-2.5f, -3.5f, -5.3f, 1, 1, 1).texOffs(30, 0).addBox(1.5f, -3.5f, -5.3f, 1, 1, 1), PartPose.offset(0, 22, -15));
        head.addOrReplaceChild("mandL", CubeListBuilder.create().texOffs(22, 0).addBox(-0.5f, 0, -3, 1, 1, 3), PartPose.offsetAndRotation(1.2f, -1.5f, -5, 0, 0.2f, 0));
        head.addOrReplaceChild("mandR", CubeListBuilder.create().texOffs(22, 0).addBox(-0.5f, 0, -3, 1, 1, 3), PartPose.offsetAndRotation(-1.2f, -1.5f, -5, 0, -0.2f, 0));
        int[][] dim = {{6, 5, 7}, {6, 5, 7}, {5, 4, 7}, {5, 4, 6}, {4, 3, 6}};
        int[][] uv = {{0, 10}, {28, 10}, {0, 24}, {26, 24}, {0, 36}};
        PartDefinition prev = head;
        for (int i = 0; i < 5; i++) {
            float w = dim[i][0], h = dim[i][1], d = dim[i][2];
            CubeListBuilder c = CubeListBuilder.create().texOffs(uv[i][0], uv[i][1]).addBox(-w / 2, -h, 0, w, h, d);
            if (i < 4) c.texOffs(22, 36).addBox(-w / 2 - 0.5f, 0, 2, 1, 2, 1).texOffs(22, 36).addBox(w / 2 - 0.5f, 0, 2, 1, 2, 1);
            prev = prev.addOrReplaceChild("s" + (i + 1), c, PartPose.offset(0, 0, i == 0 ? 0 : dim[i - 1][2]));
        }
        return mesh;
    }

    @Override
    protected void baseAnim(LarvaEntity e, float ls, float amt, float age, float yaw, float pitch) {
        amt = Math.min(amt, 1f);
        float p = ls * 1.3f;
        for (int i = 1; i <= 5; i++) {
            bone("s" + i).yRot += Mth.sin(p - i * 0.8f) * (0.1f + amt * 0.4f) + Mth.sin(age * 0.07f - i * 0.5f) * 0.05f;
            bone("s" + i).xRot += Math.max(0, Mth.sin(p * 1.0f - i * 0.8f + 1.5f)) * 0.25f * amt - 0.03f;   // humping crawl
        }
        bone("head").y += -Math.abs(Mth.sin(p)) * 0.8f * amt;
        bone("head").yRot += Mth.sin(p) * 0.2f * amt + Mth.clamp(yaw, -40f, 40f) * Mth.DEG_TO_RAD * 0.5f;
        bone("head").xRot += Mth.clamp(pitch, -25f, 25f) * Mth.DEG_TO_RAD * 0.4f + Mth.sin(age * 0.08f) * 0.05f;
        float ch = Mth.sin(age * 0.5f) * 0.08f + 0.1f;
        bone("mandL").yRot += ch; bone("mandR").yRot -= ch;
        if (e.hurtTime > 0) { float h = e.hurtTime / (float) Math.max(1, e.hurtDuration); bone("head").xRot -= h * 0.5f; bone("s1").xRot -= h * 0.3f; }
    }

    @Override
    protected void extraAnim(LarvaEntity e, float ls, float amt, float age, float yaw, float pitch) {
        if (e.deathTime <= 0) return;
        float d = Mth.clamp(e.deathTime / 20f, 0f, 1f), s = d * d * (3 - 2 * d);
        for (int i = 0; i < 6; i++) { String n = i == 0 ? "head" : "s" + i; bone(n).zRot += s * 0.4f * (i % 2 == 0 ? 1 : -1); }
        bone("head").zRot += s * 1.0f; bone("head").y += s * 2f;
    }

    @Override
    protected AnimClip clip(LarvaEntity e, int id) {
        switch (id) {
            case LarvaEntity.BITE: return C_BITE;
            case LarvaEntity.LUNGE: return C_LUNGE;
            case LarvaEntity.SPIT: return C_SPIT;
            default: return null;
        }
    }

    private static void chain(AnimClip.Builder b, float[]... rows) { // rows {t, xPerSeg, yPerSeg}: cumulative pitch/yaw spread over s1..s5 with lag
        for (int i = 1; i <= 5; i++) {
            float[][] r = new float[rows.length][];
            for (int j = 0; j < rows.length; j++) r[j] = k(Math.min(1f, rows[j][0] + (i - 1) * 0.015f), rows[j][1] * (1.2f - i * 0.15f), rows[j][2], 0);
            rot(b, "s" + i, r);
        }
    }

    /** 22t, snap @10 (.4545): front third rears up, mandibles flare, the head whips down and the mandibles clamp. */
    private static AnimClip bite() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -34, 0, 0), k(0.4545f, 22, 0, 0, SNAP), k(0.65f, 14, 12, 0), k(0.85f, 4, 0, 0), k(1, 0, 0, 0));
        chain(b, k(0, 0, 0), k(0.3f, -16, 0), k(0.4545f, 10, 0, SNAP), k(0.85f, 2, 0), k(1, 0, 0));
        rot(b, "mandL", k(0, 0, 0, 0), k(0.3f, 0, 50, 0), k(0.4545f, 0, -20, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "mandR", k(0, 0, 0, 0), k(0.3f, 0, -50, 0), k(0.4545f, 0, 20, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "head", k(0, 0, 0, 0), k(0.3f, 0, 1, 2), k(0.4545f, 0, 2, -4, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 28t, impact @13 (.464): body bunches up (segments squash), then snaps out straight, the head reaching and mandibles agape. */
    private static AnimClip lunge() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "head", k(0, 0, 0, 0), k(0.25f, -26, 0, 0), k(0.464f, 12, 0, 0, SNAP), k(0.7f, 6, 0, 0), k(1, 0, 0, 0));
        chain(b, k(0, 0, 0), k(0.25f, -22, 10), k(0.464f, 6, 0, SNAP), k(1, 0, 0));
        for (int i = 1; i <= 5; i++) scale(b, "s" + i, k(0, 1, 1, 1), k(0.25f, 1.1f, 1.2f, 0.72f), k(0.464f, 0.92f, 0.9f, 1.25f, SNAP), k(0.8f, 1, 1, 1), k(1, 1, 1, 1));
        rot(b, "mandL", k(0, 0, 0, 0), k(0.25f, 0, 40, 0), k(0.464f, 0, 55, 0), k(0.6f, 0, -15, 0, SNAP), k(1, 0, 0, 0));
        rot(b, "mandR", k(0, 0, 0, 0), k(0.25f, 0, -40, 0), k(0.464f, 0, -55, 0), k(0.6f, 0, 15, 0, SNAP), k(1, 0, 0, 0));
        pos(b, "head", k(0, 0, 0, 0), k(0.25f, 0, 0, 3), k(0.464f, 0, 0, -5, SNAP), k(1, 0, 0, 0));
        return b.build();
    }

    /** 36t, spit @19 (.528): rears up high, the throat swells (head and s1 balloon), then a convulsive forward jerk fires the acid. */
    private static AnimClip spit() {
        AnimClip.Builder b = AnimClip.create();
        rot(b, "head", k(0, 0, 0, 0), k(0.3f, -58, 0, 0), k(0.5f, -64, 0, 0), k(0.528f, -18, 0, 0, SNAP), k(0.75f, -24, 0, 0), k(1, 0, 0, 0));
        chain(b, k(0, 0, 0), k(0.3f, -26, 0), k(0.5f, -32, 0), k(0.528f, -4, 0, SNAP), k(0.8f, -8, 0), k(1, 0, 0));
        scale(b, "head", k(0, 1, 1, 1), k(0.4f, 1.2f, 1.3f, 1.2f), k(0.5f, 1.3f, 1.4f, 1.3f), k(0.528f, 0.9f, 0.9f, 1f, SNAP), k(0.7f, 1, 1, 1), k(1, 1, 1, 1));
        scale(b, "s1", k(0, 1, 1, 1), k(0.4f, 1.15f, 1.2f, 1), k(0.5f, 1.25f, 1.3f, 1), k(0.56f, 0.9f, 0.9f, 1), k(0.75f, 1, 1, 1), k(1, 1, 1, 1));
        rot(b, "mandL", k(0, 0, 0, 0), k(0.3f, 0, 55, 0), k(0.528f, 0, 70, 0, SNAP), k(0.85f, 0, 30, 0), k(1, 0, 0, 0));
        rot(b, "mandR", k(0, 0, 0, 0), k(0.3f, 0, -55, 0), k(0.528f, 0, -70, 0, SNAP), k(0.85f, 0, -30, 0), k(1, 0, 0, 0));
        pos(b, "head", k(0, 0, 0, 0), k(0.5f, 0, 0, 2), k(0.528f, 0, 1, -4, SNAP), k(1, 0, 0, 0));
        return b.build();
    }
}
