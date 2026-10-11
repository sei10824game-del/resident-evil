package com.reevil.client.krauser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KrauserRenderer extends BioRenderer<GanadoEntity, KrauserModel> {
    public KrauserRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new KrauserModel(ctx.bakeLayer(KrauserModel.LAYER)), 0.5f, "krauser");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
