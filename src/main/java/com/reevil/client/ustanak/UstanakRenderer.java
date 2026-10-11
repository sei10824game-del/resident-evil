package com.reevil.client.ustanak;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class UstanakRenderer extends BioRenderer<MrXEntity, UstanakModel> {
    public UstanakRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new UstanakModel(ctx.bakeLayer(UstanakModel.LAYER)), 1.0f, "ustanak");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.2f, 1.2f, 1.2f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
