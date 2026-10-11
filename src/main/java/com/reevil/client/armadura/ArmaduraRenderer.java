package com.reevil.client.armadura;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ArmaduraRenderer extends BioRenderer<ZombieEntity, ArmaduraModel> {
    public ArmaduraRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ArmaduraModel(ctx.bakeLayer(ArmaduraModel.LAYER)), 0.6f, "armadura");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
