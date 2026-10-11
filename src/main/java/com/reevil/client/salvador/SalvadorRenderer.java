package com.reevil.client.salvador;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SalvadorRenderer extends BioRenderer<GanadoEntity, SalvadorModel> {
    public SalvadorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SalvadorModel(ctx.bakeLayer(SalvadorModel.LAYER)), 0.6f, "salvador");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.2f, 1.2f, 1.2f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
