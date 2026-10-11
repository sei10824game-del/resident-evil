package com.reevil.client.ironmaiden;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class IronMaidenRenderer extends BioRenderer<ZombieEntity, IronMaidenModel> {
    public IronMaidenRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new IronMaidenModel(ctx.bakeLayer(IronMaidenModel.LAYER)), 0.6f, "iron_maiden");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.2f, 1.2f, 1.2f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
