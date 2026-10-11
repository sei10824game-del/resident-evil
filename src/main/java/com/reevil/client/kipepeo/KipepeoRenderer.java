package com.reevil.client.kipepeo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantmoth.GiantMothEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class KipepeoRenderer extends BioRenderer<GiantMothEntity, KipepeoModel> {
    public KipepeoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new KipepeoModel(ctx.bakeLayer(KipepeoModel.LAYER)), 0.4f, "kipepeo");
    }

    @Override protected void scale(GiantMothEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(GiantMothEntity e) { return 0f; }
}
