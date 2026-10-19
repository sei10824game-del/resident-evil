package com.reevil.client.bugsmall;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BugSmallRenderer extends BioRenderer<GiantMothEntity, BugSmallModel> {
    public BugSmallRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BugSmallModel(ctx.bakeLayer(BugSmallModel.LAYER)), 0.2f, "bug_small");
    }

    @Override protected void scale(GiantMothEntity e, PoseStack ps, float partial) { ps.scale(0.35f, 0.35f, 0.35f); }
    @Override protected float getFlipDegrees(GiantMothEntity e) { return 0f; }
}
