package com.reevil.client.reaper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.licker.LickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ReaperRenderer extends BioRenderer<LickerEntity, ReaperModel> {
    public ReaperRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ReaperModel(ctx.bakeLayer(ReaperModel.LAYER)), 0.8f, "reaper");
    }

    @Override protected void scale(LickerEntity e, PoseStack ps, float partial) { ps.scale(1.35f, 1.35f, 1.35f); }
    @Override protected float getFlipDegrees(LickerEntity e) { return 0f; }
}
