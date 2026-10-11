package com.reevil.client.gravedigger;

import com.reevil.ReEvilMod;
import com.reevil.client.yawn.YawnModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the Yawn serpent rig and clips. */
public class GraveDiggerModel extends YawnModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "grave_digger"), "main");
    public GraveDiggerModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return YawnModel.createBodyLayer(); }
}
