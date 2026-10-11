package com.reevil.client.webspinner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.webspinner.WebSpinnerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WebSpinnerRenderer extends BioRenderer<WebSpinnerEntity, WebSpinnerModel> {
    public WebSpinnerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WebSpinnerModel(ctx.bakeLayer(WebSpinnerModel.LAYER)), 0.5f, "web_spinner");
    }

    @Override
    protected void scale(WebSpinnerEntity e, PoseStack ps, float partial) { ps.scale(0.62f, 0.62f, 0.62f); }

    @Override
    protected float getFlipDegrees(WebSpinnerEntity e) { return 0f; }
}
