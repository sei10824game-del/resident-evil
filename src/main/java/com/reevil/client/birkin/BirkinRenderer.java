package com.reevil.client.birkin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.birkin.BirkinEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BirkinRenderer extends BioRenderer<BirkinEntity, BirkinModel> {
    public BirkinRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BirkinModel(ctx.bakeLayer(BirkinModel.LAYER)), 1.0f, "birkin");
    }

    @Override
    protected void scale(BirkinEntity e, PoseStack ps, float partial) {
        float s = new float[]{1.0f, 1.05f, 1.5f, 1.15f, 2.0f}[Math.min(4, e.getForm())];
        ps.scale(s, s, s);
    }

    @Override
    protected float getFlipDegrees(BirkinEntity e) { return 0f; }
}
