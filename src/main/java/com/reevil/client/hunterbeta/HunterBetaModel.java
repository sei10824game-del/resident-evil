package com.reevil.client.hunterbeta;

import com.reevil.ReEvilMod;
import com.reevil.client.hunter.HunterModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the Hunter rig and clips. */
public class HunterBetaModel extends HunterModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "hunter_beta"), "main");
    public HunterBetaModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return HunterModel.createBodyLayer(); }
}
