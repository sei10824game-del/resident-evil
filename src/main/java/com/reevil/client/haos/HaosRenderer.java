package com.reevil.client.haos;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HaosRenderer extends BioRenderer<Plant42Entity, HaosModel> {
    public HaosRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HaosModel(ctx.bakeLayer(HaosModel.LAYER)), 2.0f, "haos");
    }

    @Override protected void scale(Plant42Entity e, PoseStack ps, float partial) { ps.scale(1.3f, 1.3f, 1.3f); }
    @Override protected float getFlipDegrees(Plant42Entity e) { return 0f; }
}
