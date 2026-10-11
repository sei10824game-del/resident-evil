package com.reevil.client.eveline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class EvelineRenderer extends BioRenderer<NemesisEntity, EvelineModel> {
    public EvelineRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new EvelineModel(ctx.bakeLayer(EvelineModel.LAYER)), 0.6f, "eveline");
    }

    @Override protected void scale(NemesisEntity e, PoseStack ps, float partial) { ps.scale(0.95f, 0.95f, 0.95f); }
    @Override protected float getFlipDegrees(NemesisEntity e) { return 0f; }
}
