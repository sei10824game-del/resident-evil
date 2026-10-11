package com.reevil.client.ndesu;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NdesuRenderer extends BioRenderer<MrXEntity, NdesuModel> {
    public NdesuRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NdesuModel(ctx.bakeLayer(NdesuModel.LAYER)), 1.2f, "ndesu");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.45f, 1.45f, 1.45f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
