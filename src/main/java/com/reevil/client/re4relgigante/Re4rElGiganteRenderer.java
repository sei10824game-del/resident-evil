package com.reevil.client.re4relgigante;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class Re4rElGiganteRenderer extends BioRenderer<MrXEntity, Re4rElGiganteModel> {
    public Re4rElGiganteRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new Re4rElGiganteModel(ctx.bakeLayer(Re4rElGiganteModel.LAYER)), 0.6f, "re4r_el_gigante");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.0f, 1.0f, 1.0f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
