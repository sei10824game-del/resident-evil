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

    /** canonical size: extra scale on top of the creature's own renderer scale (heights and builds from CreatureSizes) */
    @Override
    public void render(T e, float yaw, float partial, com.mojang.blaze3d.vertex.PoseStack ps, net.minecraft.client.renderer.MultiBufferSource buf, int light) {
        float[] sz = e.canonSize();
        if (sz == null) { super.render(e, yaw, partial, ps, buf, light); return; }
        ps.pushPose();
        ps.scale(sz[2], sz[3], sz[2]);
        super.render(e, yaw, partial, ps, buf, light);
        ps.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(T e) {
        String suffix = e.getTextureSuffix();
        return new ResourceLocation(ReEvilMod.MODID, "textures/entity/" + tex + suffix + ".png");
    }
}
