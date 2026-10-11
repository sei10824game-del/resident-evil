package com.reevil.client.ivy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class IvyRenderer extends BioRenderer<Plant42Entity, IvyModel> {
    public IvyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new IvyModel(ctx.bakeLayer(IvyModel.LAYER)), 0.9f, "ivy");
    }

    @Override
    protected void scale(Plant42Entity e, PoseStack ps, float partial) {
        float s = e.getForm() == 1 ? 0.66f : 0.6f;
        ps.scale(s, s, s);
    }

    @Override
    protected float getFlipDegrees(Plant42Entity e) { return 0f; }
}
