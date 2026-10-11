package com.reevil.client.elgigante;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.mrx.MrXEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ElGiganteRenderer extends BioRenderer<MrXEntity, ElGiganteModel> {
    public ElGiganteRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ElGiganteModel(ctx.bakeLayer(ElGiganteModel.LAYER)), 1.6f, "el_gigante");
    }

    @Override protected void scale(MrXEntity e, PoseStack ps, float partial) { ps.scale(1.75f, 1.75f, 1.75f); }
    @Override protected float getFlipDegrees(MrXEntity e) { return 0f; }
}
