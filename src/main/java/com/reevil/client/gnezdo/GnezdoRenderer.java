package com.reevil.client.gnezdo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GnezdoRenderer extends BioRenderer<MrXEntity, GnezdoModel> {
    public GnezdoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GnezdoModel(ctx.bakeLayer(GnezdoModel.LAYER)), 0.5f, "gnezdo");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
