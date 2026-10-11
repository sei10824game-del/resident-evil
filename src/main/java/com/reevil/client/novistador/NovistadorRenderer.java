package com.reevil.client.novistador;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NovistadorRenderer extends BioRenderer<GiantMothEntity, NovistadorModel> {
    public NovistadorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NovistadorModel(ctx.bakeLayer(NovistadorModel.LAYER)), 0.3f, "novistador");
    }

    @Override protected void scale(GiantMothEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GiantMothEntity e) { return 0f; }
}
