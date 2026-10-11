package com.reevil.client.moreau;

import com.reevil.ReEvilMod;
import com.reevil.client.neptune.NeptuneModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the NeptuneModel rig and clips. */
public class MoreauModel extends NeptuneModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "moreau"), "main");
    public MoreauModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return NeptuneModel.createBodyLayer(); }
}
