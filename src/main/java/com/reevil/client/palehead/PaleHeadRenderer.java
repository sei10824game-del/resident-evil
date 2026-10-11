package com.reevil.client.palehead;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PaleHeadRenderer extends BioRenderer<ZombieEntity, PaleHeadModel> {
    public PaleHeadRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PaleHeadModel(ctx.bakeLayer(PaleHeadModel.LAYER)), 0.6f, "pale_head");
    }

    @Override protected void scale(ZombieEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
