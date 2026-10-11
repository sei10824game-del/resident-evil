package com.reevil.client.marguerite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MargueriteRenderer extends BioRenderer<GiantMothEntity, MargueriteModel> {
    public MargueriteRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MargueriteModel(ctx.bakeLayer(MargueriteModel.LAYER)), 0.7f, "marguerite");
    }

    @Override protected void scale(GiantMothEntity e, PoseStack ps, float partial) { ps.scale(1.6f, 1.6f, 1.6f); }
    @Override protected float getFlipDegrees(GiantMothEntity e) { return 0f; }
}
