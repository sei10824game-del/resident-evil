package com.reevil.client.draindeimos;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DrainDeimosRenderer extends BioRenderer<GiantMothEntity, DrainDeimosModel> {
    public DrainDeimosRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DrainDeimosModel(ctx.bakeLayer(DrainDeimosModel.LAYER)), 0.5f, "drain_deimos");
    }

    @Override protected void scale(GiantMothEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GiantMothEntity e) { return 0f; }
}
