package com.reevil.client.garrador;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.garrador.GarradorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GarradorRenderer extends BioRenderer<GarradorEntity, GarradorModel> {
    public GarradorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GarradorModel(ctx.bakeLayer(GarradorModel.LAYER)), 0.9f, "garrador");
    }

    @Override protected void scale(GarradorEntity e, PoseStack ps, float partial) { ps.scale(1.25f, 1.25f, 1.25f); }
    @Override protected float getFlipDegrees(GarradorEntity e) { return 0f; }
}
