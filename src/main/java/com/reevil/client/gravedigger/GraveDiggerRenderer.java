package com.reevil.client.gravedigger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.yawn.YawnEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GraveDiggerRenderer extends BioRenderer<YawnEntity, GraveDiggerModel> {
    public GraveDiggerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GraveDiggerModel(ctx.bakeLayer(GraveDiggerModel.LAYER)), 1.6f, "grave_digger");
    }

    @Override
    protected void scale(YawnEntity e, PoseStack ps, float partial) { float s = e.bodyScale() * 1.35f; ps.scale(s, s, s); }

    @Override protected float getFlipDegrees(YawnEntity e) { return 0f; }
}
