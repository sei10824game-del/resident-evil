package com.reevil.client.molded;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MoldedRenderer extends BioRenderer<GanadoEntity, MoldedModel> {
    public MoldedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MoldedModel(ctx.bakeLayer(MoldedModel.LAYER)), 0.5f, "molded");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
