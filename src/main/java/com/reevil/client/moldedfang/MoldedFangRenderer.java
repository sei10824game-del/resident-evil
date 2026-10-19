package com.reevil.client.moldedfang;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MoldedFangRenderer extends BioRenderer<GanadoEntity, MoldedFangModel> {
    public MoldedFangRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MoldedFangModel(ctx.bakeLayer(MoldedFangModel.LAYER)), 0.6f, "molded_fang");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
