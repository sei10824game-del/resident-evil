package com.reevil.client.mrx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MrXRenderer extends BioRenderer<MrXEntity, MrXModel> {
    public MrXRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MrXModel(ctx.bakeLayer(MrXModel.LAYER)), 0.8f, "mr_x");
    }

    @Override
    protected void scale(MrXEntity e, PoseStack ps, float partial) {
        float s = e.isSuper() ? 1.1f : 1.0f;
        ps.scale(s, s, s);
    }

    @Override
    protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
