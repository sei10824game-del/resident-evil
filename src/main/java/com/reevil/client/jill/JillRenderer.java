package com.reevil.client.jill;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class JillRenderer extends BioRenderer<MrXEntity, JillModel> {
    public JillRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new JillModel(ctx.bakeLayer(JillModel.LAYER)), 0.5f, "jill");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(0.95f, 0.95f, 0.95f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
