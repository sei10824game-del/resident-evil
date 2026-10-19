package com.reevil.client.mia;

import com.reevil.ReEvilMod;
import com.reevil.client.jill.JillModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the JillModel rig and clips. */
public class MiaModel extends JillModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "mia"), "main");
    public MiaModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return JillModel.createBodyLayer(); }
}
