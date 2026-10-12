package com.reevil.client.soldat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SoldatRenderer extends BioRenderer<MrXEntity, SoldatModel> {
    public SoldatRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SoldatModel(ctx.bakeLayer(SoldatModel.LAYER)), 0.5f, "soldat");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { float k = e.getMut() == 3 ? 1.3f : 1.0f; ps.scale(k, k, k); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
