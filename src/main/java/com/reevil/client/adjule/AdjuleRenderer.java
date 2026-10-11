package com.reevil.client.adjule;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AdjuleRenderer extends BioRenderer<MrXEntity, AdjuleModel> {
    public AdjuleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AdjuleModel(ctx.bakeLayer(AdjuleModel.LAYER)), 0.7f, "adjule");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
