package com.reevil.client.dimitrescu;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DimitrescuRenderer extends BioRenderer<MrXEntity, DimitrescuModel> {
    public DimitrescuRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DimitrescuModel(ctx.bakeLayer(DimitrescuModel.LAYER)), 1.2f, "dimitrescu");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.35f, 1.35f, 1.35f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
