package com.reevil.client.zombie;

import com.reevil.client.BioRenderer;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** Textures: zombie_civilian / zombie_labcoat / zombie_stars (ZombieEntity.getTextureSuffix). */
public class ZombieRenderer extends BioRenderer<ZombieEntity, ZombieModel> {
    public ZombieRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ZombieModel(ctx.bakeLayer(ZombieModel.LAYER)), 0.5f, "zombie");
    }

    /** The model animates its own fall (FALL clip); disable the vanilla sideways death flip. */
    @Override
    protected float getFlipDegrees(ZombieEntity e) { return 0f; }
}
