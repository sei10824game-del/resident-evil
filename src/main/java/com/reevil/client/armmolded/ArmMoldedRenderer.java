package com.reevil.client.armmolded;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.ganado.GanadoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ArmMoldedRenderer extends BioRenderer<GanadoEntity, ArmMoldedModel> {
    public ArmMoldedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ArmMoldedModel(ctx.bakeLayer(ArmMoldedModel.LAYER)), 0.6f, "arm_molded");
    }

    @Override protected void scale(GanadoEntity e, PoseStack ps, float partial) { ps.scale(1.1f, 1.1f, 1.1f); }
    @Override protected float getFlipDegrees(GanadoEntity e) { return 0f; }
}
