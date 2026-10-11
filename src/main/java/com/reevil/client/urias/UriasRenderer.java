package com.reevil.client.urias;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class UriasRenderer extends BioRenderer<MrXEntity, UriasModel> {
    public UriasRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new UriasModel(ctx.bakeLayer(UriasModel.LAYER)), 1.1f, "urias");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.3f, 1.3f, 1.3f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
