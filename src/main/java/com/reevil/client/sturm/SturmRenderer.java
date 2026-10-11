package com.reevil.client.sturm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SturmRenderer extends BioRenderer<MrXEntity, SturmModel> {
    public SturmRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SturmModel(ctx.bakeLayer(SturmModel.LAYER)), 1.5f, "sturm");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.7f, 1.7f, 1.7f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
