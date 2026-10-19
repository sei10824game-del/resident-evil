package com.reevil.client.mia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MiaRenderer extends BioRenderer<MrXEntity, MiaModel> {
    public MiaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MiaModel(ctx.bakeLayer(MiaModel.LAYER)), 0.6f, "mia");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(0.95f, 0.95f, 0.95f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
