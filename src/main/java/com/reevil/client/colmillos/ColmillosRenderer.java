package com.reevil.client.colmillos;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.cerberus.CerberusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ColmillosRenderer extends BioRenderer<CerberusEntity, ColmillosModel> {
    public ColmillosRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ColmillosModel(ctx.bakeLayer(ColmillosModel.LAYER)), 0.5f, "colmillos");
    }

    @Override protected void scale(CerberusEntity e, PoseStack ps, float partial) { ps.scale(1.15f, 1.15f, 1.15f); }
    @Override protected float getFlipDegrees(CerberusEntity e) { return 0f; }
}
