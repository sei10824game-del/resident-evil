package com.reevil.client.insectnest;

import com.reevil.ReEvilMod;
import com.reevil.client.ivy.IvyModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the IvyModel rig and clips. */
public class InsectNestModel extends IvyModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "insect_nest"), "main");
    public InsectNestModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return IvyModel.createBodyLayer(); }
}
