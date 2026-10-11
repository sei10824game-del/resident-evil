package com.reevil.client.colmillos;

import com.reevil.ReEvilMod;
import com.reevil.client.cerberus.CerberusModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the CerberusModel rig and clips. */
public class ColmillosModel extends CerberusModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "colmillos"), "main");
    public ColmillosModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return CerberusModel.createBodyLayer(); }
}
