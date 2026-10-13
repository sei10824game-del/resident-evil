package com.reevil.client.re2rnemesis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re2rNemesisRenderer extends BioRenderer<NemesisEntity, Re2rNemesisModel> {
    public Re2rNemesisRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re2rNemesisModel(ctx.bakeLayer(Re2rNemesisModel.LAYER)), 0.6f, "re2r_nemesis");
    }

    @Override protected void scale(NemesisEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(NemesisEntity e) { return 0f; }
}
