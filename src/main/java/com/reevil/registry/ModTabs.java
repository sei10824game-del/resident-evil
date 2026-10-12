package com.reevil.registry;

import com.reevil.ReEvilMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;

/** One creative tab per game (Resident Evil 1..8) holding that game's spawn eggs. Named in lang as itemGroup.reevil.re1 .. re8. */
public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ReEvilMod.MODID);

    private static final Map<String, List<String>> GAMES = Map.ofEntries(
        Map.entry("re1", List.of("zombie", "crimson_head", "cerberus", "crow", "hunter", "chimera", "giant_spider", "web_spinner", "neptune", "yawn", "plant42", "tyrant", "lisa_trevor")),
        Map.entry("re2", List.of("licker", "mr_x", "birkin", "alligator", "ivy", "giant_moth", "larva", "zombie", "cerberus", "crow", "giant_spider")),
        Map.entry("re3", List.of("nemesis", "grave_digger", "drain_deimos", "brain_sucker", "pale_head", "hunter_beta", "hunter_gamma", "cerberus")),
        Map.entry("re4", List.of("ganado", "zealot", "salvador", "garrador", "colmillos", "novistador", "regenerador", "iron_maiden", "armadura", "verdugo", "krauser", "el_gigante", "mendez", "del_lago", "saddler", "salazar")),
        Map.entry("re5", List.of("majini", "chainsaw_majini", "executioner_majini", "kipepeo", "reaper", "adjule", "ndesu", "gatling_majini", "wesker", "jill", "uroboros")),
        Map.entry("re6", List.of("javo", "ustanak", "napad", "ogroman", "simmons", "haos", "gnezdo")),
        Map.entry("re7", List.of("molded", "fat_molded", "jack_baker", "marguerite", "eveline")),
        Map.entry("re8", List.of("lycan", "daughter", "dimitrescu", "heisenberg", "moreau", "sturm", "urias", "soldat", "miranda")),

        Map.entry("re1r", List.of("re1r_zombie", "re1r_hunter", "crimson_head", "lisa_trevor", "cerberus", "crow", "neptune", "yawn")),
        Map.entry("re2r", List.of("re2r_zombie", "re2r_licker", "re2r_mr_x", "birkin", "alligator", "ivy", "cerberus")),
        Map.entry("re3r", List.of("re3r_zombie", "re3r_nemesis", "re3r_pale_head", "re3r_hunter_beta", "drain_deimos", "grave_digger", "brain_sucker", "cerberus")),
        Map.entry("re4r", List.of("re4r_ganado", "re4r_el_gigante", "re4r_verdugo", "re4r_regenerador", "re4r_krauser", "re4r_salazar", "re4r_armadura", "re4r_iron_maiden", "garrador", "novistador", "del_lago")));
    private static final Map<String, String> ICON = Map.ofEntries(Map.entry("re1r", "re1r_zombie"), Map.entry("re2r", "re2r_licker"), Map.entry("re3r", "re3r_nemesis"), Map.entry("re4r", "re4r_ganado"), Map.entry("re1", "zombie"), Map.entry("re2", "licker"), Map.entry("re3", "nemesis"), Map.entry("re4", "ganado"), Map.entry("re5", "majini"), Map.entry("re6", "javo"), Map.entry("re7", "molded"), Map.entry("re8", "lycan"));

    static {
        for (String g : new String[]{"re1", "re1r", "re2", "re2r", "re3", "re3r", "re4", "re4r", "re5", "re6", "re7", "re8"}) {
            RegistryObject<CreativeModeTab> ro = TABS.register(g, () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.reevil." + g))
                .icon(() -> new ItemStack(egg(ICON.get(g))))
                .displayItems((params, out) -> { for (String id : GAMES.get(g)) out.accept(egg(id)); })
                .build());
        }
    }

    private static Item egg(String id) { return ForgeRegistries.ITEMS.getValue(new ResourceLocation(ReEvilMod.MODID, id + "_spawn_egg")); }

    private ModTabs() {}
}
