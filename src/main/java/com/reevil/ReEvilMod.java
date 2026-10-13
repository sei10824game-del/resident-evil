package com.reevil;

import com.reevil.registry.ModEntities;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ReEvilMod.MODID)
public class ReEvilMod {
    public static final String MODID = "reevil";

    public ReEvilMod() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ReConfig.SPEC);
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(bus);
        ModEntities.ITEMS.register(bus);
        com.reevil.registry.ModSounds.SOUNDS.register(bus);
        com.reevil.registry.ModTabs.TABS.register(bus);
        bus.addListener(ModEntities::onAttributes);
    }
}
