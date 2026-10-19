package com.reevil.client.buglarge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BugLargeRenderer extends BioRenderer<GiantMothEntity, BugLargeModel> {
    public BugLargeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BugLargeModel(ctx.bakeLayer(BugLargeModel.LAYER)), 0.6f, "bug_large");
    }

    @Override protected void scale(GiantMothEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GiantMothEntity e) { return 0f; }
}
