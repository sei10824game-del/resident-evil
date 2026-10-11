package com.reevil.client.gatlingmajini;

import com.reevil.ReEvilMod;
import com.reevil.client.krauser.KrauserModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the KrauserModel rig and clips. */
public class GatlingMajiniModel extends KrauserModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "gatling_majini"), "main");
    public GatlingMajiniModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return KrauserModel.createBodyLayer(); }
}
