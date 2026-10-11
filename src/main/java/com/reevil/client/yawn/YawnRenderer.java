package com.reevil.client.yawn;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.yawn.YawnEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class YawnRenderer extends BioRenderer<YawnEntity, YawnModel> {
    public YawnRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new YawnModel(ctx.bakeLayer(YawnModel.LAYER)), 1.4f, "yawn");
    }

    @Override
    protected void scale(YawnEntity e, PoseStack ps, float partial) {
        float s = e.bodyScale();
        ps.scale(s, s, s);
    }

    @Override
    protected float getFlipDegrees(YawnEntity e) { return 0f; }
}
