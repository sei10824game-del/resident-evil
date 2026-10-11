package com.reevil.client.zealot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ZealotRenderer extends BioRenderer<GanadoEntity, ZealotModel> {
    public ZealotRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ZealotModel(ctx.bakeLayer(ZealotModel.LAYER)), 0.5f, "zealot");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
