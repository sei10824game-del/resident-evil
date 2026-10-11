package com.reevil.client.nemesis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NemesisRenderer extends BioRenderer<NemesisEntity, NemesisModel> {
    public NemesisRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NemesisModel(ctx.bakeLayer(NemesisModel.LAYER)), 0.9f, "nemesis");
    }

    @Override
    protected void scale(NemesisEntity e, PoseStack ps, float partial) {
        float s = 1.08f + e.getForm() * 0.07f;
        ps.scale(s, s, s);
    }

    @Override protected float getFlipDegrees(NemesisEntity e) { return 0f; }
}
