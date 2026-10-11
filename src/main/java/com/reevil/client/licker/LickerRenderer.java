package com.reevil.client.licker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.licker.LickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LickerRenderer extends BioRenderer<LickerEntity, LickerModel> {
    public LickerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LickerModel(ctx.bakeLayer(LickerModel.LAYER)), 0.6f, "licker");
    }

    @Override
    protected void scale(LickerEntity e, PoseStack ps, float partial) {
        float s = e.isBeta() ? 1.12f : 1.0f;
        ps.scale(s, s, s);
    }

    @Override
    protected float getFlipDegrees(LickerEntity e) { return 0f; }
}
