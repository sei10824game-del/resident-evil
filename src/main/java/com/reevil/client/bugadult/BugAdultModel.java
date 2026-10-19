package com.reevil.client.bugadult;

import com.reevil.ReEvilMod;
import com.reevil.client.larva.LarvaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the LarvaModel rig and clips. */
public class BugAdultModel extends LarvaModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "bug_adult"), "main");
    public BugAdultModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return LarvaModel.createBodyLayer(); }
}
