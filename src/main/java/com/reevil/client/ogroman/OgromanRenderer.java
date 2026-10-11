package com.reevil.client.ogroman;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class OgromanRenderer extends BioRenderer<MrXEntity, OgromanModel> {
    public OgromanRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new OgromanModel(ctx.bakeLayer(OgromanModel.LAYER)), 1.6f, "ogroman");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.85f, 1.85f, 1.85f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
