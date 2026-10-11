package com.reevil.client.saddler;

import com.reevil.ReEvilMod;
import com.reevil.client.nemesis.NemesisModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the NemesisModel rig and clips. */
public class SaddlerModel extends NemesisModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "saddler"), "main");
    public SaddlerModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return NemesisModel.createBodyLayer(); }
}
