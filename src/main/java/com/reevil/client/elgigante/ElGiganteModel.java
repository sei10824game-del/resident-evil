package com.reevil.client.elgigante;

import com.reevil.ReEvilMod;
import com.reevil.client.mrx.MrXModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Stalker rig (hat hidden by the texture's transparency). */
public class ElGiganteModel extends MrXModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "el_gigante"), "main");
    public ElGiganteModel(ModelPart root) { super(root); }
    public static net.minecraft.client.model.geom.builders.MeshDefinition mesh() { return MrXModel.mesh(); }
    public static LayerDefinition createBodyLayer() { return MrXModel.createBodyLayer(); }
}
