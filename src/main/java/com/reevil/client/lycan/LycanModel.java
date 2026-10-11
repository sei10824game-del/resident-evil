package com.reevil.client.lycan;

import com.reevil.ReEvilMod;
import com.reevil.client.hunter.HunterModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the HunterModel rig and clips. */
public class LycanModel extends HunterModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "lycan"), "main");
    public LycanModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return HunterModel.createBodyLayer(); }
}
