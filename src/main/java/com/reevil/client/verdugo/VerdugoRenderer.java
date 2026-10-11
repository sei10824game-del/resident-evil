package com.reevil.client.verdugo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class VerdugoRenderer extends BioRenderer<MrXEntity, VerdugoModel> {
    public VerdugoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new VerdugoModel(ctx.bakeLayer(VerdugoModel.LAYER)), 0.8f, "verdugo");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.15f, 1.15f, 1.15f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
