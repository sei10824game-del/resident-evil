package com.reevil.client.chainsawmajini;

import com.reevil.ReEvilMod;
import com.reevil.client.salvador.SalvadorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the SalvadorModel rig and clips. */
public class ChainsawMajiniModel extends SalvadorModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "chainsaw_majini"), "main");
    public ChainsawMajiniModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return SalvadorModel.createBodyLayer(); }
}
