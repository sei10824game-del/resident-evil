package com.reevil.client.fatmolded;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class FatMoldedRenderer extends BioRenderer<ZombieEntity, FatMoldedModel> {
    public FatMoldedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new FatMoldedModel(ctx.bakeLayer(FatMoldedModel.LAYER)), 1.0f, "fat_molded");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.4f, 1.4f, 1.4f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
