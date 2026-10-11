package com.reevil.client.giantspider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantspider.GiantSpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GiantSpiderRenderer extends BioRenderer<GiantSpiderEntity, GiantSpiderModel> {
    public GiantSpiderRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GiantSpiderModel(ctx.bakeLayer(GiantSpiderModel.LAYER)), 1.2f, "giant_spider");
    }

    @Override
    protected void scale(GiantSpiderEntity e, PoseStack ps, float partial) {
        float s = e.getForm() == 1 ? 1.45f : 1.3f;
        ps.scale(s, s, s);
    }

    /** ceiling ambush: flip the whole spider upside down around its body centre. */
    @Override
    protected void setupRotations(GiantSpiderEntity e, PoseStack ps, float age, float yaw, float partial) {
        super.setupRotations(e, ps, age, yaw, partial);
        if (e.isHanging()) {
            ps.translate(0, e.getBbHeight() * 0.9, 0);
            ps.mulPose(Axis.ZP.rotationDegrees(180f));
        }
    }

    @Override
    protected float getFlipDegrees(GiantSpiderEntity e) { return 0f; }
}
