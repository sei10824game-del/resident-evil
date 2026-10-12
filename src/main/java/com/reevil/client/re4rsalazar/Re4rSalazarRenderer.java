package com.reevil.client.re4rsalazar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.plant42.Plant42Entity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re4rSalazarRenderer extends BioRenderer<Plant42Entity, Re4rSalazarModel> {
    public Re4rSalazarRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re4rSalazarModel(ctx.bakeLayer(Re4rSalazarModel.LAYER)), 1.4f, "re4r_salazar");
    }

    @Override protected void scale(Plant42Entity e, PoseStack ps, float partial) { ps.scale(1.15f, 1.15f, 1.15f); }
    @Override protected float getFlipDegrees(Plant42Entity e) { return 0f; }
}
