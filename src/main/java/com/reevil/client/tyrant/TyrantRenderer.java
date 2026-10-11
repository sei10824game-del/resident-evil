package com.reevil.client.tyrant;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.tyrant.TyrantEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TyrantRenderer extends BioRenderer<TyrantEntity, TyrantModel> {
    public TyrantRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TyrantModel(ctx.bakeLayer(TyrantModel.LAYER)), 1.1f, "tyrant");
    }

    /** enraged form bulks up slightly. */
    @Override
    protected void scale(TyrantEntity e, PoseStack ps, float partial) {
        float s = e.getForm() == TyrantEntity.FORM_ENRAGED ? 1.06f : 1.0f;
        ps.scale(s, s, s);
    }
}
