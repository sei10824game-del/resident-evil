package com.reevil.client.re4rverdugo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re4rVerdugoRenderer extends BioRenderer<MrXEntity, Re4rVerdugoModel> {
    public Re4rVerdugoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re4rVerdugoModel(ctx.bakeLayer(Re4rVerdugoModel.LAYER)), 0.6f, "re4r_verdugo");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
