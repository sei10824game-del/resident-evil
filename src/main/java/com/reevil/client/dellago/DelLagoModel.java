package com.reevil.client.dellago;

import com.reevil.ReEvilMod;
import com.reevil.client.neptune.NeptuneModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the Neptune rig and clips. */
public class DelLagoModel extends NeptuneModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "del_lago"), "main");
    public DelLagoModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return NeptuneModel.createBodyLayer(); }
}
