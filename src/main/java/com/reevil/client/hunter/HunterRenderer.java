package com.reevil.client.hunter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.hunter.HunterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HunterRenderer extends BioRenderer<HunterEntity, HunterModel> {
    public HunterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HunterModel(ctx.bakeLayer(HunterModel.LAYER)), 0.6f, "hunter");
    }

    /** bespoke collapse animation in the model replaces the vanilla sideways death flip */
    @Override
    protected float getFlipDegrees(HunterEntity e) { return 0f; }

    /** the enraged red Hunter is visibly bulkier */
    @Override
    protected void scale(HunterEntity e, PoseStack ps, float partial) {
        if (e.getForm() == 1) ps.scale(1.08f, 1.08f, 1.08f);
    }
}
