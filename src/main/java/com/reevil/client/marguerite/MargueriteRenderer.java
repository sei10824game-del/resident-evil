package com.reevil.client.marguerite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MargueriteRenderer extends BioRenderer<MrXEntity, MargueriteModel> {
    public MargueriteRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MargueriteModel(ctx.bakeLayer(MargueriteModel.LAYER)), 0.5f, "marguerite");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
