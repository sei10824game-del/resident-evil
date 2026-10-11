package com.reevil.client.heisenberg;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HeisenbergRenderer extends BioRenderer<MrXEntity, HeisenbergModel> {
    public HeisenbergRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HeisenbergModel(ctx.bakeLayer(HeisenbergModel.LAYER)), 0.7f, "heisenberg");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
