package com.reevil.client.wesker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class WeskerRenderer extends BioRenderer<MrXEntity, WeskerModel> {
    public WeskerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WeskerModel(ctx.bakeLayer(WeskerModel.LAYER)), 0.5f, "wesker");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
