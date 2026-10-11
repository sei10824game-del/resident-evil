package com.reevil.client.salazar;

import com.reevil.ReEvilMod;
import com.reevil.client.plant42.Plant42Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the Plant42Model rig and clips. */
public class SalazarModel extends Plant42Model {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "salazar"), "main");
    public SalazarModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return Plant42Model.createBodyLayer(); }
}
