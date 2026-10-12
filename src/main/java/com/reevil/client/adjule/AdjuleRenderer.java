package com.reevil.client.adjule;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.cerberus.CerberusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AdjuleRenderer extends BioRenderer<CerberusEntity, AdjuleModel> {
    public AdjuleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AdjuleModel(ctx.bakeLayer(AdjuleModel.LAYER)), 0.6f, "adjule");
    }

    @Override protected void scale(CerberusEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(CerberusEntity e) { return 0f; }
}
