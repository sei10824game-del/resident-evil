package com.reevil.client.ganado;

import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GanadoRenderer extends BioRenderer<GanadoEntity, GanadoModel> {
    public GanadoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GanadoModel(ctx.bakeLayer(GanadoModel.LAYER)), 0.5f, "ganado");
    }

    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
