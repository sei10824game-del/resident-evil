package com.reevil.client.re1rhunter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re1rHunterRenderer extends BioRenderer<HunterEntity, Re1rHunterModel> {
    public Re1rHunterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re1rHunterModel(ctx.bakeLayer(Re1rHunterModel.LAYER)), 0.6f, "re1r_hunter");
    }

    @Override protected void scale(HunterEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(HunterEntity e) { return 0f; }
}
