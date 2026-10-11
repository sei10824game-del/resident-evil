package com.reevil.client.salazar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SalazarRenderer extends BioRenderer<Plant42Entity, SalazarModel> {
    public SalazarRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SalazarModel(ctx.bakeLayer(SalazarModel.LAYER)), 1.8f, "salazar");
    }

    @Override protected void scale(Plant42Entity e, PoseStack ps, float partial) { ps.scale(1.15f, 1.15f, 1.15f); }
    @Override protected float getFlipDegrees(Plant42Entity e) { return 0f; }
}
