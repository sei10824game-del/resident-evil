package com.reevil.client.re3rpalehead;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re3rPaleHeadRenderer extends BioRenderer<ZombieEntity, Re3rPaleHeadModel> {
    public Re3rPaleHeadRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re3rPaleHeadModel(ctx.bakeLayer(Re3rPaleHeadModel.LAYER)), 0.6f, "re3r_pale_head");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
