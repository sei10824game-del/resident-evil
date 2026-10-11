package com.reevil.client.neptune;

import com.reevil.client.BioRenderer;
import com.reevil.entity.neptune.NeptuneEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NeptuneRenderer extends BioRenderer<NeptuneEntity, NeptuneModel> {
    public NeptuneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NeptuneModel(ctx.bakeLayer(NeptuneModel.LAYER)), 0f, "neptune");
    }

    @Override
    protected float getFlipDegrees(NeptuneEntity e) { return 0f; }
}
