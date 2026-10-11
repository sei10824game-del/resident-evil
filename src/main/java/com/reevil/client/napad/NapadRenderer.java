package com.reevil.client.napad;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.licker.LickerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NapadRenderer extends BioRenderer<LickerEntity, NapadModel> {
    public NapadRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new NapadModel(ctx.bakeLayer(NapadModel.LAYER)), 0.6f, "napad");
    }

    @Override protected void scale(LickerEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(LickerEntity e) { return 0f; }
}
