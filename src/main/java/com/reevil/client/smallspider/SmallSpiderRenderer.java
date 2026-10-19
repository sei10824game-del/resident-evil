package com.reevil.client.smallspider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.giantspider.GiantSpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SmallSpiderRenderer extends BioRenderer<GiantSpiderEntity, SmallSpiderModel> {
    public SmallSpiderRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SmallSpiderModel(ctx.bakeLayer(SmallSpiderModel.LAYER)), 0.3f, "small_spider");
    }

    @Override protected void scale(GiantSpiderEntity e, PoseStack ps, float partial) { ps.scale(0.4f, 0.4f, 0.4f); }
    @Override protected float getFlipDegrees(GiantSpiderEntity e) { return 0f; }
}
