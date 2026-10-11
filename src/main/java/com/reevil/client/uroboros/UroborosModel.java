package com.reevil.client.uroboros;

import com.reevil.ReEvilMod;
import com.reevil.client.plant42.Plant42Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the Plant42Model rig and clips. */
public class UroborosModel extends Plant42Model {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "uroboros"), "main");
    public UroborosModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return Plant42Model.createBodyLayer(); }
}
