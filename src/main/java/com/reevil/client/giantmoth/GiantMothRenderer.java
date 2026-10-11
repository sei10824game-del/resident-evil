package com.reevil.client.giantmoth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GiantMothRenderer extends BioRenderer<GiantMothEntity, GiantMothModel> {
    public GiantMothRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GiantMothModel(ctx.bakeLayer(GiantMothModel.LAYER)), 0.5f, "giant_moth");
    }

    @Override protected void scale(GiantMothEntity e, PoseStack ps, float partial) { ps.scale(1.35f, 1.35f, 1.35f); }
    @Override protected float getFlipDegrees(GiantMothEntity e) { return 0f; }
}
