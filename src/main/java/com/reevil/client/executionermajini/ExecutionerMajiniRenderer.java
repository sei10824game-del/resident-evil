package com.reevil.client.executionermajini;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ExecutionerMajiniRenderer extends BioRenderer<MrXEntity, ExecutionerMajiniModel> {
    public ExecutionerMajiniRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ExecutionerMajiniModel(ctx.bakeLayer(ExecutionerMajiniModel.LAYER)), 1.4f, "executioner_majini");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.6f, 1.6f, 1.6f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
