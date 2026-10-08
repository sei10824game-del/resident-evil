package com.reevil.client.crow;

import com.reevil.ReEvilMod;
import com.reevil.client.BioRenderer;
import com.reevil.entity.crow.CrowEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CrowRenderer extends BioRenderer<CrowEntity, CrowModel> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(ReEvilMod.MODID, "textures/entity/crow_eyes.png"));

    public CrowRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CrowModel(ctx.bakeLayer(CrowModel.LAYER)), 0.25f, "crow");
        addLayer(new EyesLayer<CrowEntity, CrowModel>(this) {
            @Override public RenderType renderType() { return EYES; }
        });
    }

    /** Death flop is authored in CrowModel; disable the vanilla sideways flip. */
    @Override
    protected float getFlipDegrees(CrowEntity e) { return 0f; }
}
