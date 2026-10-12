package com.reevil.client.daughter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DaughterRenderer extends BioRenderer<MrXEntity, DaughterModel> {
    public DaughterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DaughterModel(ctx.bakeLayer(DaughterModel.LAYER)), 0.5f, "daughter");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
