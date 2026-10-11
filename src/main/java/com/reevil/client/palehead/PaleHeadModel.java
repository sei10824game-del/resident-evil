package com.reevil.client.palehead;

import com.reevil.ReEvilMod;
import com.reevil.client.zombie.ZombieModel;
import com.reevil.entity.zombie.ZombieEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

/** Zombie rig with a hugely swollen head. */
public class PaleHeadModel extends ZombieModel {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(ReEvilMod.MODID, "pale_head"), "main");
    public PaleHeadModel(ModelPart root) { super(root); }
    public static LayerDefinition createBodyLayer() { return ZombieModel.createBodyLayer(); }

    @Override
    protected void extraAnim(ZombieEntity e, float ls, float amt, float age, float yaw, float pitch) {
        super.extraAnim(e, ls, amt, age, yaw, pitch);
        ModelPart h = bone("head");
        h.xScale *= 1.55f; h.yScale *= 1.7f; h.zScale *= 1.55f;
    }
}
