package com.reevil.client.ironmaiden;

import com.reevil.ReEvilMod;
import com.reevil.client.zombie.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the ZombieModel rig and clips. */
public class IronMaidenModel extends ZombieModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "iron_maiden"), "main");
    public IronMaidenModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return ZombieModel.createBodyLayer(); }
}
