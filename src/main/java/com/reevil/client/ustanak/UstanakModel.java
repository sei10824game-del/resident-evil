package com.reevil.client.ustanak;

import com.reevil.ReEvilMod;
import com.reevil.client.mrx.MrXModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the MrXModel rig and clips. */
public class UstanakModel extends MrXModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "ustanak"), "main");
    public UstanakModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return MrXModel.createBodyLayer(); }
}
