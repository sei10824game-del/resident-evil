package com.reevil.client.lycan;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LycanRenderer extends BioRenderer<HunterEntity, LycanModel> {
    public LycanRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LycanModel(ctx.bakeLayer(LycanModel.LAYER)), 0.6f, "lycan");
    }

    @Override protected void scale(HunterEntity e, PoseStack ps, float partial) { ps.scale(1.05f, 1.05f, 1.05f); }
    @Override protected float getFlipDegrees(HunterEntity e) { return 0f; }
}
