package com.reevil.client.brainsucker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.brainsucker.BrainSuckerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BrainSuckerRenderer extends BioRenderer<BrainSuckerEntity, BrainSuckerModel> {
    public BrainSuckerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BrainSuckerModel(ctx.bakeLayer(BrainSuckerModel.LAYER)), 0.3f, "brain_sucker");
    }

    @Override protected void scale(BrainSuckerEntity e, PoseStack ps, float partial) { ps.scale(1.2f, 1.2f, 1.2f); }
    @Override protected float getFlipDegrees(BrainSuckerEntity e) { return 0f; }
}
