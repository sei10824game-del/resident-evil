package com.reevil.client.cerberus;

import com.reevil.ReEvilMod;
import com.reevil.client.BioRenderer;
import com.reevil.entity.cerberus.CerberusEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CerberusRenderer extends BioRenderer<CerberusEntity, CerberusModel> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(ReEvilMod.MODID, "textures/entity/cerberus_eyes.png"));

    public CerberusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CerberusModel(ctx.bakeLayer(CerberusModel.LAYER)), 0.45f, "cerberus");
        addLayer(new EyesLayer<CerberusEntity, CerberusModel>(this) {
            @Override public RenderType renderType() { return EYES; }
        });
    }

    /** Death roll is authored in CerberusModel; disable the vanilla sideways flip. */
    @Override
    protected float getFlipDegrees(CerberusEntity e) { return 0f; }
}
