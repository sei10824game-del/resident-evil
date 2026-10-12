package com.reevil.client.re4rironmaiden;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re4rIronMaidenRenderer extends BioRenderer<ZombieEntity, Re4rIronMaidenModel> {
    public Re4rIronMaidenRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re4rIronMaidenModel(ctx.bakeLayer(Re4rIronMaidenModel.LAYER)), 0.6f, "re4r_iron_maiden");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
