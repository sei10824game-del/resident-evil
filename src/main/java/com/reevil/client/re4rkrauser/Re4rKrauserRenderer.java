package com.reevil.client.re4rkrauser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re4rKrauserRenderer extends BioRenderer<GanadoEntity, Re4rKrauserModel> {
    public Re4rKrauserRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re4rKrauserModel(ctx.bakeLayer(Re4rKrauserModel.LAYER)), 0.6f, "re4r_krauser");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
