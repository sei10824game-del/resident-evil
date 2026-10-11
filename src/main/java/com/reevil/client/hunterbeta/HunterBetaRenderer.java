package com.reevil.client.hunterbeta;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HunterBetaRenderer extends BioRenderer<HunterEntity, HunterBetaModel> {
    public HunterBetaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HunterBetaModel(ctx.bakeLayer(HunterBetaModel.LAYER)), 0.7f, "hunter_beta");
    }

    @Override protected void scale(HunterEntity e, PoseStack ps, float partial) { float s = e.getForm() == 1 ? 1.2f : 1.12f; ps.scale(s, s, s); }
    @Override protected float getFlipDegrees(HunterEntity e) { return 0f; }
}
