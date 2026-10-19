package com.reevil.client.bugsmall;

import com.reevil.ReEvilMod;
import com.reevil.client.giantmoth.GiantMothModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the GiantMothModel rig and clips. */
public class BugSmallModel extends GiantMothModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "bug_small"), "main");
    public BugSmallModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return GiantMothModel.createBodyLayer(); }
}
