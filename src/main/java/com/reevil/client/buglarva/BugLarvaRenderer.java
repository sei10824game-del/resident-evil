package com.reevil.client.buglarva;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.larva.LarvaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BugLarvaRenderer extends BioRenderer<LarvaEntity, BugLarvaModel> {
    public BugLarvaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BugLarvaModel(ctx.bakeLayer(BugLarvaModel.LAYER)), 0.6f, "bug_larva");
    }

    @Override protected void scale(LarvaEntity e, PoseStack ps, float partial) { ps.scale(0.8f, 0.8f, 0.8f); }
    @Override protected float getFlipDegrees(LarvaEntity e) { return 0f; }
}
