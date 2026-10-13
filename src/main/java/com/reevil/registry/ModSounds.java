package com.reevil.registry;

import com.reevil.ReEvilMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Creature voices: six synthesised archetypes x (ambient, hurt, death, attack), see tools/synth_sounds.py and assets/reevil/sounds.json. */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ReEvilMod.MODID);
    public static final String[] KINDS = {"ambient", "hurt", "death", "attack"};
    private static final Map<String, RegistryObject<SoundEvent>> EVENTS = new HashMap<>();

    static {
        for (String a : new String[]{"groan", "growl", "hiss", "roar", "screech", "squelch"})
            for (String k : KINDS) {
                String id = "voice." + a + "." + k;
                EVENTS.put(a + "." + k, SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ReEvilMod.MODID, id))));
            }
    }

    public static final RegistryObject<SoundEvent> IMPACT = SOUNDS.register("misc.impact", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ReEvilMod.MODID, "misc.impact")));
    public static final RegistryObject<SoundEvent> STEP = SOUNDS.register("misc.step", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ReEvilMod.MODID, "misc.step")));

    private static final Set<String> GROAN = Set.of("zombie", "pale_head", "regenerador", "iron_maiden", "armadura", "fat_molded", "molded", "ganado", "majini", "zealot", "javo", "jack_baker", "lisa_trevor", "soldat");
    private static final Set<String> GROWL = Set.of("cerberus", "colmillos", "lycan", "hunter", "hunter_beta", "chimera", "crimson_head", "alligator");
    private static final Set<String> HISS = Set.of("licker", "giant_spider", "web_spinner", "hunter_gamma", "reaper", "napad", "daughter", "larva", "gnezdo");
    private static final Set<String> SCREECH = Set.of("crow", "giant_moth", "novistador", "kipepeo", "drain_deimos", "brain_sucker", "marguerite");
    private static final Set<String> SQUELCH = Set.of("plant42", "ivy", "salazar", "uroboros", "haos", "yawn", "grave_digger", "neptune", "del_lago", "moreau", "eveline", "miranda", "saddler", "simmons");

    public static String archetype(String entityId) {
        entityId = entityId.replaceFirst("^re[1-4]r_", "");   // remake variants sound like their originals
        if (GROAN.contains(entityId)) return "groan";
        if (GROWL.contains(entityId)) return "growl";
        if (HISS.contains(entityId)) return "hiss";
        if (SCREECH.contains(entityId)) return "screech";
        if (SQUELCH.contains(entityId)) return "squelch";
        return "roar";
    }

    public static SoundEvent get(String entityId, String kind) { return EVENTS.get(archetype(entityId) + "." + kind).get(); }

    private ModSounds() {}
}
