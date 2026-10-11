package com.reevil.client.mendez;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class MendezRenderer extends BioRenderer<MrXEntity, MendezModel> {
    public MendezRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MendezModel(ctx.bakeLayer(MendezModel.LAYER)), 1.2f, "mendez");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.3f, 1.3f, 1.3f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
