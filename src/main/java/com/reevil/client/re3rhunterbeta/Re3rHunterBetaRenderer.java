package com.reevil.client.re3rhunterbeta;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re3rHunterBetaRenderer extends BioRenderer<HunterEntity, Re3rHunterBetaModel> {
    public Re3rHunterBetaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re3rHunterBetaModel(ctx.bakeLayer(Re3rHunterBetaModel.LAYER)), 0.6f, "re3r_hunter_beta");
    }

    @Override protected void scale(HunterEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(HunterEntity e) { return 0f; }
}
