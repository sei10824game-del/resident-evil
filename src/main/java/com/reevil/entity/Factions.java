package com.reevil.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Who fights whom. Creatures of the same origin never fight each other; different origins are enemies, except pairs known to be allied.
 * Everything that is not a Resident Evil creature (players, vanilla and other-mod mobs) is an enemy of every creature, and vice versa.
 *  UMBRELLA: T-virus / Umbrella B.O.W.s (RE1-3).  G_VIRUS: G-Birkin (turns on Umbrella B.O.W.s).  PLAGA: Las Plagas / Los Illuminados / Uroboros (RE4-5).
 *  C_VIRUS: RE6.  MOLD: the Mold (RE7).  CADOU: Mother Miranda's cadou (RE8).
 */
public final class Factions {
    public enum F { UMBRELLA, G_VIRUS, PLAGA, C_VIRUS, MOLD, CADOU }

    private static final Map<String, F> OF = new HashMap<>();
    /** pairs that are allied despite different factions (none confirmed in canon between these groups). */
    private static final Set<String> ALLIES = Set.of();

    static {
        reg(F.UMBRELLA, "zombie", "crimson_head", "cerberus", "crow", "hunter", "chimera", "giant_spider", "web_spinner", "neptune", "yawn", "plant42", "tyrant", "lisa_trevor",
            "licker", "mr_x", "alligator", "ivy", "giant_moth", "larva", "nemesis", "grave_digger", "drain_deimos", "brain_sucker", "pale_head", "hunter_beta", "hunter_gamma");
        reg(F.UMBRELLA, "re1r_zombie", "re1r_hunter", "re2r_zombie", "re2r_licker", "re2r_mr_x", "re3r_zombie", "re3r_nemesis", "re3r_pale_head", "re3r_hunter_beta");
        reg(F.PLAGA, "re4r_ganado", "re4r_el_gigante", "re4r_verdugo", "re4r_regenerador", "re4r_krauser", "re4r_salazar", "re4r_armadura", "re4r_iron_maiden");
        reg(F.G_VIRUS, "birkin");
        reg(F.PLAGA, "ganado", "salvador", "garrador", "colmillos", "novistador", "regenerador", "iron_maiden", "armadura", "zealot", "verdugo", "krauser", "el_gigante", "mendez",
            "del_lago", "saddler", "salazar", "majini", "chainsaw_majini", "executioner_majini", "kipepeo", "reaper", "adjule", "ndesu", "gatling_majini", "wesker", "jill", "uroboros");
        reg(F.C_VIRUS, "javo", "ustanak", "napad", "ogroman", "simmons", "haos", "gnezdo");
        reg(F.MOLD, "molded", "fat_molded", "jack_baker", "eveline", "marguerite");
        reg(F.CADOU, "lycan", "daughter", "dimitrescu", "heisenberg", "moreau", "sturm", "urias", "soldat", "miranda");
    }

    private static void reg(F f, String... ids) { for (String i : ids) OF.put(i, f); }

    public static F of(LivingEntity e) {
        if (!(e instanceof BioMonster)) return null;
        return OF.get(BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getPath());
    }

    /** true if a and b should fight. Non-Resident-Evil entities are hostile to every creature (and vice versa). */
    public static boolean hostile(LivingEntity a, LivingEntity b) {
        if (a == b) return false;
        F fa = of(a), fb = of(b);
        if (fa == null || fb == null) return true;           // one side is not a bio creature
        if (fa == fb) return false;                          // same origin
        return !ALLIES.contains(fa.name() + ">" + fb.name()) && !ALLIES.contains(fb.name() + ">" + fa.name());
    }

    public static boolean isBio(LivingEntity e) { return e instanceof BioMonster; }
    public static boolean isPlayer(LivingEntity e) { return e instanceof Player; }

    private Factions() {}
}
