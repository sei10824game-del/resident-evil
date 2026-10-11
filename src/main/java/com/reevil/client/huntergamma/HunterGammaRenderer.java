package com.reevil.client.huntergamma;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.licker.LickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HunterGammaRenderer extends BioRenderer<LickerEntity, HunterGammaModel> {
    public HunterGammaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HunterGammaModel(ctx.bakeLayer(HunterGammaModel.LAYER)), 0.7f, "hunter_gamma");
    }

    @Override protected void scale(LickerEntity e, PoseStack ps, float partial) { ps.scale(1.25f, 1.25f, 1.25f); }
    @Override protected float getFlipDegrees(LickerEntity e) { return 0f; }
}
