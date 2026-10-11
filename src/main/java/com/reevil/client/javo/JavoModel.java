package com.reevil.client.javo;

import com.reevil.ReEvilMod;
import com.reevil.client.ganado.GanadoModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the GanadoModel rig and clips. */
public class JavoModel extends GanadoModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "javo"), "main");
    public JavoModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return GanadoModel.createBodyLayer(); }
}
