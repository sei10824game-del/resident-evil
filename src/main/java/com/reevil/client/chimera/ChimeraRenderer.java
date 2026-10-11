package com.reevil.client.chimera;

import com.reevil.client.BioRenderer;
import com.reevil.entity.chimera.ChimeraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ChimeraRenderer extends BioRenderer<ChimeraEntity, ChimeraModel> {
    public ChimeraRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ChimeraModel(ctx.bakeLayer(ChimeraModel.LAYER)), 0.5f, "chimera");
    }

    /** the model plays its own flip-onto-the-back insect death */
    @Override
    protected float getFlipDegrees(ChimeraEntity e) { return 0f; }
}
