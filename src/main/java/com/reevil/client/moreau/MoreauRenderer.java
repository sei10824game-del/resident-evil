package com.reevil.client.moreau;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.neptune.NeptuneEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MoreauRenderer extends BioRenderer<NeptuneEntity, MoreauModel> {
    public MoreauRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MoreauModel(ctx.bakeLayer(MoreauModel.LAYER)), 0f, "moreau");
    }

    @Override protected void scale(NeptuneEntity e, PoseStack ps, float partial) { ps.scale(1.6f, 1.6f, 1.6f); }
    @Override protected float getFlipDegrees(NeptuneEntity e) { return 0f; }
}
