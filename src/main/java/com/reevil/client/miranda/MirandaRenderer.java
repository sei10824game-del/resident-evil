package com.reevil.client.miranda;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MirandaRenderer extends BioRenderer<NemesisEntity, MirandaModel> {
    public MirandaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MirandaModel(ctx.bakeLayer(MirandaModel.LAYER)), 0.6f, "miranda");
    }

    @Override protected void scale(NemesisEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(NemesisEntity e) { return 0f; }
}
