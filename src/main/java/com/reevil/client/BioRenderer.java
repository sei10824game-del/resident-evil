package com.reevil.client;

import com.reevil.ReEvilMod;
import com.reevil.entity.BioMonster;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BioRenderer<T extends BioMonster, M extends BioModel<T>> extends MobRenderer<T, M> {
    private final String tex;

    /** texBase e.g. "zombie" -> textures/entity/zombie.png ; forms use getTextureName(). */
    public BioRenderer(EntityRendererProvider.Context ctx, M model, float shadow, String texBase) {
        super(ctx, model, shadow);
        this.tex = texBase;
    }

    @Override
    public ResourceLocation getTextureLocation(T e) {
        String suffix = e.getTextureSuffix();
        return new ResourceLocation(ReEvilMod.MODID, "textures/entity/" + tex + suffix + ".png");
    }
}
