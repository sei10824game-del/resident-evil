package com.reevil;

import net.minecraftforge.common.ForgeConfigSpec;

/** config/reevil-common.toml */
public final class ReConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue FACTIONS, RETARGET, DROPS;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_SCALE;
    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        FACTIONS = b.comment("Creatures of different games / vanilla mobs fight each other. false = they only hunt players.").define("factionHostility", true);
        RETARGET = b.comment("Creatures switch to the nearest/most dangerous enemy instead of chasing a far target.").define("smartRetarget", true);
        DROPS = b.comment("Creatures drop loot scaled by their toughness.").define("drops", true);
        DAMAGE_SCALE = b.comment("Multiplier for all creature melee damage.").defineInRange("damageScale", 1.0, 0.1, 5.0);
        SPEC = b.build();
    }
    private ReConfig() {}
}
