package com.reevil.client.re1rzombie;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re1rZombieRenderer extends BioRenderer<ZombieEntity, Re1rZombieModel> {
    public Re1rZombieRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re1rZombieModel(ctx.bakeLayer(Re1rZombieModel.LAYER)), 0.6f, "re1r_zombie");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
