package com.reevil.client.regenerador;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RegeneradorRenderer extends BioRenderer<ZombieEntity, RegeneradorModel> {
    public RegeneradorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RegeneradorModel(ctx.bakeLayer(RegeneradorModel.LAYER)), 0.6f, "regenerador");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.15f, 1.15f, 1.15f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
