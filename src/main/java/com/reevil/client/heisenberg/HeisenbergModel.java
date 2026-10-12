package com.reevil.client.heisenberg;

import com.reevil.ReEvilMod;
import com.reevil.client.mrx.MrXModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the MrXModel rig and clips. */
public class HeisenbergModel extends MrXModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "heisenberg"), "main");
    public HeisenbergModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return MrXModel.createBodyLayer(); }

    @Override
    protected com.reevil.anim.AnimClip clip(com.reevil.entity.mrx.MrXEntity e, int id) {
        com.reevil.anim.AnimClip c = com.reevil.client.mrx.SignatureClips.heisenberg(id);
        return c != null ? c : super.clip(e, id);
    }
}
