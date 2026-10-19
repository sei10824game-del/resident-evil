package com.reevil.client.quickmolded;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class QuickMoldedRenderer extends BioRenderer<GanadoEntity, QuickMoldedModel> {
    public QuickMoldedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new QuickMoldedModel(ctx.bakeLayer(QuickMoldedModel.LAYER)), 0.6f, "quick_molded");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(0.95f, 0.95f, 0.95f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
