package com.reevil.client.krauser;

import com.reevil.ReEvilMod;
import com.reevil.client.ganado.GanadoModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Ganado rig with the bow. */
public class KrauserModel extends GanadoModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "krauser"), "main");
    public KrauserModel(ModelPart root) { super(root); }
    public static net.minecraft.client.model.geom.builders.MeshDefinition mesh() { return GanadoModel.mesh(); }
    public static LayerDefinition createBodyLayer() { return GanadoModel.createBodyLayer(); }
}
