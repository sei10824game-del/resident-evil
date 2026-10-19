package com.reevil.client.smallspider;

import com.reevil.ReEvilMod;
import com.reevil.client.giantspider.GiantSpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the GiantSpiderModel rig and clips. */
public class SmallSpiderModel extends GiantSpiderModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "small_spider"), "main");
    public SmallSpiderModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return GiantSpiderModel.createBodyLayer(); }
}
