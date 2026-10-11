package com.reevil.client.gnezdo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.larva.LarvaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GnezdoRenderer extends BioRenderer<LarvaEntity, GnezdoModel> {
    public GnezdoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GnezdoModel(ctx.bakeLayer(GnezdoModel.LAYER)), 0.8f, "gnezdo");
    }

    @Override protected void scale(LarvaEntity e, PoseStack ps, float partial) { ps.scale(1.3f, 1.3f, 1.3f); }
    @Override protected float getFlipDegrees(LarvaEntity e) { return 0f; }
}
