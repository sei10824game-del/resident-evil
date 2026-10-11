package com.reevil.client.saddler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SaddlerRenderer extends BioRenderer<NemesisEntity, SaddlerModel> {
    public SaddlerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SaddlerModel(ctx.bakeLayer(SaddlerModel.LAYER)), 0.7f, "saddler");
    }

    @Override protected void scale(NemesisEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(NemesisEntity e) { return 0f; }
}
