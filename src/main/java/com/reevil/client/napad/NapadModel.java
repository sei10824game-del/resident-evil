package com.reevil.client.napad;

import com.reevil.ReEvilMod;
import com.reevil.client.huntergamma.HunterGammaModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the HunterGammaModel rig and clips. */
public class NapadModel extends HunterGammaModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "napad"), "main");
    public NapadModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return HunterGammaModel.createBodyLayer(); }
}
