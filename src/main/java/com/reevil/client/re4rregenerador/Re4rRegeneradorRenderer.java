package com.reevil.client.re4rregenerador;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re4rRegeneradorRenderer extends BioRenderer<ZombieEntity, Re4rRegeneradorModel> {
    public Re4rRegeneradorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re4rRegeneradorModel(ctx.bakeLayer(Re4rRegeneradorModel.LAYER)), 0.6f, "re4r_regenerador");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
