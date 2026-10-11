package com.reevil.client.daughter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.licker.LickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DaughterRenderer extends BioRenderer<LickerEntity, DaughterModel> {
    public DaughterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DaughterModel(ctx.bakeLayer(DaughterModel.LAYER)), 0.6f, "daughter");
    }

    @Override protected void scale(LickerEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(LickerEntity e) { return 0f; }
}
