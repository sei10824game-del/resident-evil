package com.reevil.client.huntergamma;

import com.reevil.ReEvilMod;
import com.reevil.client.licker.LickerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the Licker tongue-crawler rig and clips. */
public class HunterGammaModel extends LickerModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "hunter_gamma"), "main");
    public HunterGammaModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return LickerModel.createBodyLayer(); }
}
