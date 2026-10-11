package com.reevil.client.larva;

import com.reevil.client.BioRenderer;
import com.reevil.entity.larva.LarvaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LarvaRenderer extends BioRenderer<LarvaEntity, LarvaModel> {
    public LarvaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LarvaModel(ctx.bakeLayer(LarvaModel.LAYER)), 0.7f, "larva");
    }

    @Override protected float getFlipDegrees(LarvaEntity e) { return 0f; }
}
