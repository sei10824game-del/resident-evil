package com.reevil.client.ogroman;

import com.reevil.ReEvilMod;
import com.reevil.client.elgigante.ElGiganteModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Shares the ElGiganteModel rig and clips. */
public class OgromanModel extends ElGiganteModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "ogroman"), "main");
    public OgromanModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return ElGiganteModel.createBodyLayer(); }
}
