package com.reevil.client.majini;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MajiniRenderer extends BioRenderer<GanadoEntity, MajiniModel> {
    public MajiniRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MajiniModel(ctx.bakeLayer(MajiniModel.LAYER)), 0.5f, "majini");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
