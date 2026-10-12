package com.reevil.client.re3rnemesis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.nemesis.NemesisEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re3rNemesisRenderer extends BioRenderer<NemesisEntity, Re3rNemesisModel> {
    public Re3rNemesisRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re3rNemesisModel(ctx.bakeLayer(Re3rNemesisModel.LAYER)), 0.6f, "re3r_nemesis");
    }

    @Override protected void scale(NemesisEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(NemesisEntity e) { return 0f; }
}
