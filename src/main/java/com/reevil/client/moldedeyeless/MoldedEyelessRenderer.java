package com.reevil.client.moldedeyeless;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MoldedEyelessRenderer extends BioRenderer<GanadoEntity, MoldedEyelessModel> {
    public MoldedEyelessRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MoldedEyelessModel(ctx.bakeLayer(MoldedEyelessModel.LAYER)), 0.6f, "molded_eyeless");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
