package com.reevil.client.soldat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SoldatRenderer extends BioRenderer<GanadoEntity, SoldatModel> {
    public SoldatRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SoldatModel(ctx.bakeLayer(SoldatModel.LAYER)), 0.5f, "soldat");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.05f, 1.05f, 1.05f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
