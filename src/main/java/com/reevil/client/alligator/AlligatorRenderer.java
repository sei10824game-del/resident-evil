package com.reevil.client.alligator;

import com.reevil.client.BioRenderer;
import com.reevil.entity.alligator.AlligatorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AlligatorRenderer extends BioRenderer<AlligatorEntity, AlligatorModel> {
    public AlligatorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new AlligatorModel(ctx.bakeLayer(AlligatorModel.LAYER)), 1.0f, "alligator");
    }

    @Override
    protected float getFlipDegrees(AlligatorEntity e) { return 0f; }
}
