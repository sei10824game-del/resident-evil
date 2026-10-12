package com.reevil.client.re2rlicker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.licker.LickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re2rLickerRenderer extends BioRenderer<LickerEntity, Re2rLickerModel> {
    public Re2rLickerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re2rLickerModel(ctx.bakeLayer(Re2rLickerModel.LAYER)), 0.6f, "re2r_licker");
    }

    @Override protected void scale(LickerEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(LickerEntity e) { return 0f; }
}
