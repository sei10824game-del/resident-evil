package com.reevil.client.re4rarmadura;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re4rArmaduraRenderer extends BioRenderer<ZombieEntity, Re4rArmaduraModel> {
    public Re4rArmaduraRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re4rArmaduraModel(ctx.bakeLayer(Re4rArmaduraModel.LAYER)), 0.6f, "re4r_armadura");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
