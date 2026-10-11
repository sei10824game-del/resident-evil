package com.reevil.client.dellago;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.neptune.NeptuneEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class DelLagoRenderer extends BioRenderer<NeptuneEntity, DelLagoModel> {
    public DelLagoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DelLagoModel(ctx.bakeLayer(DelLagoModel.LAYER)), 0f, "del_lago");
    }

    @Override protected void scale(NeptuneEntity e, PoseStack ps, float partial) { ps.scale(2.6f, 2.6f, 2.6f); }
    @Override protected float getFlipDegrees(NeptuneEntity e) { return 0f; }
}
