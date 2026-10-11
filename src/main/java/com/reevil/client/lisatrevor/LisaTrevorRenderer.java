package com.reevil.client.lisatrevor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.lisatrevor.LisaTrevorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LisaTrevorRenderer extends BioRenderer<LisaTrevorEntity, LisaTrevorModel> {
    public LisaTrevorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LisaTrevorModel(ctx.bakeLayer(LisaTrevorModel.LAYER)), 0.9f, "lisa_trevor");
    }

    @Override
    protected void scale(LisaTrevorEntity e, PoseStack ps, float partial) {
        float s = e.getForm() == LisaTrevorEntity.FORM_MUTATED ? 1.12f : 1.0f;
        ps.scale(s, s, s);
    }

    @Override
    protected float getFlipDegrees(LisaTrevorEntity e) { return 0f; }
}
