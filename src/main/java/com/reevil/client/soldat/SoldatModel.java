package com.reevil.client.soldat;

import com.reevil.ReEvilMod;
import com.reevil.client.krauser.KrauserModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the KrauserModel rig and clips. */
public class SoldatModel extends KrauserModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "soldat"), "main");
    public SoldatModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return KrauserModel.createBodyLayer(); }
}
