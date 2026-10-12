package com.reevil.client.re2rzombie;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re2rZombieRenderer extends BioRenderer<ZombieEntity, Re2rZombieModel> {
    public Re2rZombieRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re2rZombieModel(ctx.bakeLayer(Re2rZombieModel.LAYER)), 0.6f, "re2r_zombie");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
