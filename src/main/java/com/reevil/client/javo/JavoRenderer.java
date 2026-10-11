package com.reevil.client.javo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class JavoRenderer extends BioRenderer<GanadoEntity, JavoModel> {
    public JavoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new JavoModel(ctx.bakeLayer(JavoModel.LAYER)), 0.5f, "javo");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
