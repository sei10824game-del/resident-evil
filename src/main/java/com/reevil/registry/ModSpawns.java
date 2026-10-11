package com.reevil.registry;

import com.reevil.ReEvilMod;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Placement rules for natural spawning. Which biomes/weights: data/reevil/forge/biome_modifier/*.json (delete or override with a datapack to disable).
 * Bosses (Tyrant, Lisa, Birkin, Mr. X, Plant 42, Yawn) never spawn naturally.
 */
@Mod.EventBusSubscriber(modid = ReEvilMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModSpawns {
    @SubscribeEvent
    public static void register(SpawnPlacementRegisterEvent e) {
        for (EntityType<? extends Mob> t : new EntityType[]{ModEntities.ZOMBIE.get(), ModEntities.CRIMSON_HEAD.get(), ModEntities.CERBERUS.get(), ModEntities.HUNTER.get(),
            ModEntities.CHIMERA.get(), ModEntities.GIANT_SPIDER.get(), ModEntities.WEB_SPINNER.get(), ModEntities.LICKER.get(), ModEntities.LARVA.get(), ModEntities.IVY.get(), ModEntities.GRAVE_DIGGER.get(), ModEntities.PALE_HEAD.get(), ModEntities.HUNTER_BETA.get(), ModEntities.HUNTER_GAMMA.get(), ModEntities.GANADO.get(), ModEntities.SALVADOR.get(), ModEntities.GARRADOR.get(), ModEntities.COLMILLOS.get(), ModEntities.REGENERADOR.get(), ModEntities.IRON_MAIDEN.get(), ModEntities.MAJINI.get(), ModEntities.ZEALOT.get(), ModEntities.CHAINSAW_MAJINI.get(), ModEntities.ARMADURA.get(), ModEntities.REAPER.get(), ModEntities.ADJULE.get(), ModEntities.NDESU.get()}) {
            reg(e, t);
        }
        for (EntityType<? extends Mob> t : new EntityType[]{ModEntities.CROW.get(), ModEntities.GIANT_MOTH.get(), ModEntities.DRAIN_DEIMOS.get(), ModEntities.BRAIN_SUCKER.get(), ModEntities.NOVISTADOR.get(), ModEntities.KIPEPEO.get()}) {
            e.register(t, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING, (ty, l, r, p, rnd) -> p.getY() > l.getMinBuildHeight() && Monster.isDarkEnoughToSpawn(l, p, rnd), SpawnPlacementRegisterEvent.Operation.OR);
        }
        for (EntityType<? extends Mob> w : new EntityType[]{ModEntities.ALLIGATOR.get(), ModEntities.DEL_LAGO.get()}) e.register(w, SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (ty, l, r, p, rnd) -> l.getFluidState(p).is(FluidTags.WATER) || l.getFluidState(p.below()).is(FluidTags.WATER), SpawnPlacementRegisterEvent.Operation.OR);
        e.register(ModEntities.NEPTUNE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.OCEAN_FLOOR,
            (ty, l, r, p, rnd) -> l.getFluidState(p).is(FluidTags.WATER) && p.getY() < l.getSeaLevel() - 6, SpawnPlacementRegisterEvent.Operation.OR);
    }

    private static void reg(SpawnPlacementRegisterEvent e, EntityType<? extends Mob> t) {
        e.register(t, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (ty, l, r, p, rnd) -> Monster.isDarkEnoughToSpawn(l, p, rnd) && l.getBlockState(p.below()).isValidSpawn(l, p.below(), ty), SpawnPlacementRegisterEvent.Operation.OR);
    }

    private ModSpawns() {}
}
