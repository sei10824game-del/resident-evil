package com.reevil.client.plant42;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Plant42Renderer extends BioRenderer<Plant42Entity, Plant42Model> {
    public Plant42Renderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Plant42Model(ctx.bakeLayer(Plant42Model.LAYER)), 1.6f, "plant42");
    }

    /** phase 2 swells slightly. */
    @Override
    protected void scale(Plant42Entity e, PoseStack ps, float partial) {
        float s = e.getForm() == 1 ? 1.08f : 1.0f;
        ps.scale(s, s, s);
    }

    @Override
    protected float getFlipDegrees(Plant42Entity e) { return 0f; }
}
