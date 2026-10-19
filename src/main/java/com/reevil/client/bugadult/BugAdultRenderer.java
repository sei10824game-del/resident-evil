package com.reevil.client.bugadult;

import com.mojang.blaze3d.vertex.PoseStack;
import com.reevil.client.BioRenderer;
import com.reevil.entity.larva.LarvaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BugAdultRenderer extends BioRenderer<LarvaEntity, BugAdultModel> {
    public BugAdultRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BugAdultModel(ctx.bakeLayer(BugAdultModel.LAYER)), 0.6f, "bug_adult");
    }

    @Override protected void scale(LarvaEntity e, PoseStack ps, float partial) { ps.scale(1.3f, 1.3f, 1.3f); }
    @Override protected float getFlipDegrees(LarvaEntity e) { return 0f; }
}
