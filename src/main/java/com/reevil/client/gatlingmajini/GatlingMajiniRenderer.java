package com.reevil.client.gatlingmajini;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GatlingMajiniRenderer extends BioRenderer<GanadoEntity, GatlingMajiniModel> {
    public GatlingMajiniRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GatlingMajiniModel(ctx.bakeLayer(GatlingMajiniModel.LAYER)), 0.6f, "gatling_majini");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
