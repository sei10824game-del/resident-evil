package com.reevil.client.blademolded;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BladeMoldedRenderer extends BioRenderer<GanadoEntity, BladeMoldedModel> {
    public BladeMoldedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BladeMoldedModel(ctx.bakeLayer(BladeMoldedModel.LAYER)), 0.6f, "blade_molded");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.05f, 1.05f, 1.05f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
