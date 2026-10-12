package com.reevil.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.reevil.anim.AnimClip;
import com.reevil.entity.BioMonster;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

/**
 * Base model. Subclass: build parts in a static createBodyLayer(), call reg(name, "path/to/part") in the ctor,
 * override baseAnim() for idle/walk (set ABSOLUTE rotations, always reset every part you touch),
 * and override clip(moveId) to return the keyframed AnimClip for each attack move id (see BioMonster move ids).
 * Clip offsets are added on top of baseAnim by applyClip().
 */
public abstract class BioModel<T extends BioMonster> extends EntityModel<T> {
    protected final ModelPart root;
    private final Map<String, ModelPart> bones = new HashMap<>();
    /** Initial (rest) pose cache so clips are always additive over the rest pose and never accumulate. */
    private final Map<String, float[]> rest = new HashMap<>();

    protected BioModel(ModelPart root) { this.root = root; }

    /** Register a bone by logical name. path: slash separated child names from root, e.g. "body/head". */
    protected ModelPart reg(String name, String path) {
        ModelPart p = root;
        for (String s : path.split("/")) p = p.getChild(s);
        bones.put(name, p);
        rest.put(name, new float[]{p.x, p.y, p.z, p.xRot, p.yRot, p.zRot, 1f, 1f, 1f});
        return p;
    }

    protected ModelPart bone(String name) { return bones.get(name); }

    /** Reset all registered bones to rest pose. Call at the start of setupAnim. */
    protected void resetPose() {
        for (Map.Entry<String, ModelPart> e : bones.entrySet()) {
            float[] r = rest.get(e.getKey()); ModelPart p = e.getValue();
            p.x = r[0]; p.y = r[1]; p.z = r[2]; p.xRot = r[3]; p.yRot = r[4]; p.zRot = r[5];
            p.xScale = 1; p.yScale = 1; p.zScale = 1;
        }
    }

    /** Add the clip sample at normalized time t on top of the current pose. */
    protected void applyClip(AnimClip clip, float t) {
        if (clip == null) return;
        for (String name : clip.bones()) {
            ModelPart p = bones.get(name);
            if (p == null) continue;
            float[] s = clip.sample(name, t);
            p.xRot += s[0]; p.yRot += s[1]; p.zRot += s[2];
            p.x += s[3]; p.y += s[4]; p.z += s[5];
            p.xScale *= s[6]; p.yScale *= s[7]; p.zScale *= s[8];
        }
    }

    /** Normalized progress 0..1 of the entity's current attack move (smoothed with partial tick). */
    protected float moveProgress(T e, float partial) {
        int dur = e.getMoveDuration();
        if (dur <= 0) return 0f;
        return Mth.clamp((e.getMoveTick() + partial) / dur, 0f, 1f);
    }

    /** Attack clip for a move id (1..n), or null. */
    protected AnimClip clip(T e, int moveId) { return null; }

    /** Idle/walk/look pose, absolute values. Called after resetPose(). */
    protected abstract void baseAnim(T e, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch);

    @Override
    public void setupAnim(T e, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {
        resetPose();
        baseAnim(e, limbSwing, limbSwingAmount, age, headYaw, headPitch);
        int id = e.getMoveId();
        if (id > 0) {
            float partial = age - e.tickCount;
            AnimClip cc = id >= 40 ? com.reevil.client.canon.CanonClips.get(e, id) : null;
            applyClip(cc != null ? cc : clip(e, id), moveProgress(e, partial));
        }
        extraAnim(e, limbSwing, limbSwingAmount, age, headYaw, headPitch);
        com.reevil.client.canon.CanonVisuals.apply(root, e);
    }

    /** Hook after clip application (e.g. death pose, form-specific tweaks). */
    protected void extraAnim(T e, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack ps, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
        root.render(ps, vc, light, overlay, r, g, b, a);
    }
}
