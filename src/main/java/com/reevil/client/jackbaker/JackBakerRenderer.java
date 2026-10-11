package com.reevil.client.jackbaker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class JackBakerRenderer extends BioRenderer<MrXEntity, JackBakerModel> {
    public JackBakerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new JackBakerModel(ctx.bakeLayer(JackBakerModel.LAYER)), 0.6f, "jack_baker");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.05f, 1.05f, 1.05f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
