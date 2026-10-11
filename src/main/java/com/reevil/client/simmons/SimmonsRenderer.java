package com.reevil.client.simmons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SimmonsRenderer extends BioRenderer<NemesisEntity, SimmonsModel> {
    public SimmonsRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SimmonsModel(ctx.bakeLayer(SimmonsModel.LAYER)), 0.8f, "simmons");
    }

    @Override protected void scale(NemesisEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(NemesisEntity e) { return 0f; }
}
