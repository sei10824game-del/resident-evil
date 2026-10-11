package com.reevil.client.ivy;

import com.reevil.ReEvilMod;
import com.reevil.client.plant42.Plant42Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Ivy shares the Plant 42 rig and animation clips (the renderer shrinks it); only the layer key and texture differ. */
public class IvyModel extends Plant42Model {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "ivy"), "main");

    public IvyModel(ModelPart root) { super(root); }

    public static LayerDefinition createBodyLayer() { return Plant42Model.createBodyLayer(); }
}
