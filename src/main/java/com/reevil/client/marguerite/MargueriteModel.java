package com.reevil.client.marguerite;

import com.reevil.ReEvilMod;
import com.reevil.client.giantmoth.GiantMothModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the GiantMothModel rig and clips. */
public class MargueriteModel extends GiantMothModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "marguerite"), "main");
    public MargueriteModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return GiantMothModel.createBodyLayer(); }
}
