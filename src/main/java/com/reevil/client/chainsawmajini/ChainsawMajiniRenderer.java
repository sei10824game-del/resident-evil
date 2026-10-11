package com.reevil.client.chainsawmajini;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ChainsawMajiniRenderer extends BioRenderer<GanadoEntity, ChainsawMajiniModel> {
    public ChainsawMajiniRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ChainsawMajiniModel(ctx.bakeLayer(ChainsawMajiniModel.LAYER)), 0.6f, "chainsaw_majini");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.2f, 1.2f, 1.2f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
