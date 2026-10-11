package com.reevil.client.crimsonhead;

import com.reevil.ReEvilMod;
import com.reevil.client.BioRenderer;
import com.reevil.entity.crimsonhead.CrimsonHeadEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CrimsonHeadRenderer extends BioRenderer<CrimsonHeadEntity, CrimsonHeadModel> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(ReEvilMod.MODID, "textures/entity/crimson_head_eyes.png"));

    public CrimsonHeadRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CrimsonHeadModel(ctx.bakeLayer(CrimsonHeadModel.LAYER)), 0.5f, "crimson_head");
        // glowing yellow eyes (full-bright overlay)
        addLayer(new EyesLayer<CrimsonHeadEntity, CrimsonHeadModel>(this) {
            @Override
            public RenderType renderType() { return EYES; }
        });
    }

    /** death fall is animated by the model (DEATH clip) */
    @Override
    protected float getFlipDegrees(CrimsonHeadEntity e) { return 0f; }
}
