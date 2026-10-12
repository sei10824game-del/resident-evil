package com.reevil.client.re3rzombie;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re3rZombieRenderer extends BioRenderer<ZombieEntity, Re3rZombieModel> {
    public Re3rZombieRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re3rZombieModel(ctx.bakeLayer(Re3rZombieModel.LAYER)), 0.6f, "re3r_zombie");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
