package com.reevil.client.re2rmrx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re2rMrXRenderer extends BioRenderer<MrXEntity, Re2rMrXModel> {
    public Re2rMrXRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re2rMrXModel(ctx.bakeLayer(Re2rMrXModel.LAYER)), 0.6f, "re2r_mr_x");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
