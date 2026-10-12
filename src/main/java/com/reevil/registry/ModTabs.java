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

    private static final Map<String, List<String>> GAMES = Map.of(
        "re1", List.of("zombie", "crimson_head", "cerberus", "crow", "hunter", "chimera", "giant_spider", "web_spinner", "neptune", "yawn", "plant42", "tyrant", "lisa_trevor"),
        "re2", List.of("licker", "mr_x", "birkin", "alligator", "ivy", "giant_moth", "larva", "zombie", "cerberus", "crow", "giant_spider"),
        "re3", List.of("nemesis", "grave_digger", "drain_deimos", "brain_sucker", "pale_head", "hunter_beta", "hunter_gamma", "cerberus"),
        "re4", List.of("ganado", "zealot", "salvador", "garrador", "colmillos", "novistador", "regenerador", "iron_maiden", "armadura", "verdugo", "krauser", "el_gigante", "mendez", "del_lago", "saddler", "salazar"),
        "re5", List.of("majini", "chainsaw_majini", "executioner_majini", "kipepeo", "reaper", "adjule", "ndesu", "gatling_majini", "wesker", "jill", "uroboros"),
        "re6", List.of("javo", "ustanak", "napad", "ogroman", "simmons", "haos", "gnezdo"),
        "re7", List.of("molded", "fat_molded", "jack_baker", "marguerite", "eveline"),
        "re8", List.of("lycan", "daughter", "dimitrescu", "heisenberg", "moreau", "sturm", "urias", "soldat", "miranda"));
    private static final Map<String, String> ICON = Map.of("re1", "zombie", "re2", "licker", "re3", "nemesis", "re4", "ganado", "re5", "majini", "re6", "javo", "re7", "molded", "re8", "lycan");

    static {
        for (String g : new String[]{"re1", "re2", "re3", "re4", "re5", "re6", "re7", "re8"}) {
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
