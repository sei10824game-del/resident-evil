package com.reevil.client.uroboros;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class UroborosRenderer extends BioRenderer<Plant42Entity, UroborosModel> {
    public UroborosRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new UroborosModel(ctx.bakeLayer(UroborosModel.LAYER)), 2.2f, "uroboros");
    }

    @Override protected void scale(Plant42Entity e, PoseStack ps, float partial) { ps.scale(1.4f, 1.4f, 1.4f); }
    @Override protected float getFlipDegrees(Plant42Entity e) { return 0f; }
}
