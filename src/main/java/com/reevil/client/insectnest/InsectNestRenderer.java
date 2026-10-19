package com.reevil.client.insectnest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class InsectNestRenderer extends BioRenderer<Plant42Entity, InsectNestModel> {
    public InsectNestRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new InsectNestModel(ctx.bakeLayer(InsectNestModel.LAYER)), 0.6f, "insect_nest");
    }

    @Override protected void scale(Plant42Entity e, PoseStack ps, float partial) { ps.scale(0.8f, 0.8f, 0.8f); }
    @Override protected float getFlipDegrees(Plant42Entity e) { return 0f; }
}
