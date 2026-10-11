package com.reevil.registry;

import com.reevil.ReEvilMod;
import com.reevil.entity.zombie.ZombieEntity;
import com.reevil.entity.crimsonhead.CrimsonHeadEntity;
import com.reevil.entity.cerberus.CerberusEntity;
import com.reevil.entity.crow.CrowEntity;
import com.reevil.entity.hunter.HunterEntity;
import com.reevil.entity.chimera.ChimeraEntity;
import com.reevil.entity.giantspider.GiantSpiderEntity;
import com.reevil.entity.webspinner.WebSpinnerEntity;
import com.reevil.entity.neptune.NeptuneEntity;
import com.reevil.entity.yawn.YawnEntity;
import com.reevil.entity.plant42.Plant42Entity;
import com.reevil.entity.tyrant.TyrantEntity;
import com.reevil.entity.lisatrevor.LisaTrevorEntity;
import com.reevil.entity.licker.LickerEntity;
import com.reevil.entity.mrx.MrXEntity;
import com.reevil.entity.birkin.BirkinEntity;
import com.reevil.entity.alligator.AlligatorEntity;
import com.reevil.entity.ivy.IvyEntity;
import com.reevil.entity.giantmoth.GiantMothEntity;
import com.reevil.entity.larva.LarvaEntity;
import com.reevil.entity.nemesis.NemesisEntity;
import com.reevil.entity.gravedigger.GraveDiggerEntity;
import com.reevil.entity.draindeimos.DrainDeimosEntity;
import com.reevil.entity.brainsucker.BrainSuckerEntity;
import com.reevil.entity.palehead.PaleHeadEntity;
import com.reevil.entity.hunterbeta.HunterBetaEntity;
import com.reevil.entity.huntergamma.HunterGammaEntity;
import com.reevil.entity.ganado.GanadoEntity;
import com.reevil.entity.salvador.SalvadorEntity;
import com.reevil.entity.garrador.GarradorEntity;
import com.reevil.entity.colmillos.ColmillosEntity;
import com.reevil.entity.novistador.NovistadorEntity;
import com.reevil.entity.regenerador.RegeneradorEntity;
import com.reevil.entity.ironmaiden.IronMaidenEntity;
import com.reevil.entity.elgigante.ElGiganteEntity;
import com.reevil.entity.dellago.DelLagoEntity;
import com.reevil.entity.majini.MajiniEntity;
import com.reevil.entity.zealot.ZealotEntity;
import com.reevil.entity.chainsawmajini.ChainsawMajiniEntity;
import com.reevil.entity.armadura.ArmaduraEntity;
import com.reevil.entity.kipepeo.KipepeoEntity;
import com.reevil.entity.reaper.ReaperEntity;
import com.reevil.entity.verdugo.VerdugoEntity;
import com.reevil.entity.wesker.WeskerEntity;
import com.reevil.entity.adjule.AdjuleEntity;
import com.reevil.entity.ndesu.NdesuEntity;
import com.reevil.entity.executionermajini.ExecutionerMajiniEntity;
import com.reevil.entity.krauser.KrauserEntity;
import com.reevil.entity.saddler.SaddlerEntity;
import com.reevil.entity.simmons.SimmonsEntity;
import com.reevil.entity.salazar.SalazarEntity;
import com.reevil.entity.uroboros.UroborosEntity;
import com.reevil.entity.mendez.MendezEntity;
import com.reevil.entity.ogroman.OgromanEntity;
import com.reevil.entity.jill.JillEntity;
import com.reevil.entity.ustanak.UstanakEntity;
import com.reevil.entity.napad.NapadEntity;
import com.reevil.entity.javo.JavoEntity;
import com.reevil.entity.gatlingmajini.GatlingMajiniEntity;
import com.reevil.entity.molded.MoldedEntity;
import com.reevil.entity.fatmolded.FatMoldedEntity;
import com.reevil.entity.jackbaker.JackBakerEntity;
import com.reevil.entity.eveline.EvelineEntity;
import com.reevil.entity.lycan.LycanEntity;
import com.reevil.entity.dimitrescu.DimitrescuEntity;
import com.reevil.entity.daughter.DaughterEntity;
import com.reevil.entity.heisenberg.HeisenbergEntity;
import com.reevil.entity.moreau.MoreauEntity;
import com.reevil.entity.sturm.SturmEntity;
import com.reevil.entity.urias.UriasEntity;
import com.reevil.entity.soldat.SoldatEntity;
import com.reevil.entity.marguerite.MargueriteEntity;
import com.reevil.entity.miranda.MirandaEntity;
import com.reevil.entity.haos.HaosEntity;
import com.reevil.entity.gnezdo.GnezdoEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/** Entity types + spawn eggs. Each creature class lives in com.reevil.entity.<id without underscores>. */
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ReEvilMod.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ReEvilMod.MODID);

    private static <T extends Mob> RegistryObject<Item> egg(String id, Supplier<EntityType<T>> type, int a, int b) {
        return ITEMS.register(id + "_spawn_egg", () -> new ForgeSpawnEggItem(type, a, b, new Item.Properties()));
    }

    public static final RegistryObject<EntityType<ZombieEntity>> ZOMBIE = ENTITIES.register("zombie",
        () -> EntityType.Builder.of(ZombieEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build("zombie"));
    public static final RegistryObject<Item> ZOMBIE_EGG = egg("zombie", ZOMBIE, 0x6b7a5a, 0x5a1a1a);
    public static final RegistryObject<EntityType<CrimsonHeadEntity>> CRIMSON_HEAD = ENTITIES.register("crimson_head",
        () -> EntityType.Builder.of(CrimsonHeadEntity::new, MobCategory.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build("crimson_head"));
    public static final RegistryObject<Item> CRIMSON_HEAD_EGG = egg("crimson_head", CRIMSON_HEAD, 0x8a1c1c, 0x2a0a0a);
    public static final RegistryObject<EntityType<CerberusEntity>> CERBERUS = ENTITIES.register("cerberus",
        () -> EntityType.Builder.of(CerberusEntity::new, MobCategory.MONSTER).sized(0.8f, 0.95f).clientTrackingRange(10).build("cerberus"));
    public static final RegistryObject<Item> CERBERUS_EGG = egg("cerberus", CERBERUS, 0x2b2b2b, 0x7a1f1f);
    public static final RegistryObject<EntityType<CrowEntity>> CROW = ENTITIES.register("crow",
        () -> EntityType.Builder.of(CrowEntity::new, MobCategory.MONSTER).sized(0.5f, 0.6f).clientTrackingRange(10).build("crow"));
    public static final RegistryObject<Item> CROW_EGG = egg("crow", CROW, 0x151515, 0x9b1c1c);
    public static final RegistryObject<EntityType<HunterEntity>> HUNTER = ENTITIES.register("hunter",
        () -> EntityType.Builder.of(HunterEntity::new, MobCategory.MONSTER).sized(0.8f, 1.9f).clientTrackingRange(10).build("hunter"));
    public static final RegistryObject<Item> HUNTER_EGG = egg("hunter", HUNTER, 0x3d5a3a, 0xb0402a);
    public static final RegistryObject<EntityType<ChimeraEntity>> CHIMERA = ENTITIES.register("chimera",
        () -> EntityType.Builder.of(ChimeraEntity::new, MobCategory.MONSTER).sized(0.7f, 1.5f).clientTrackingRange(10).build("chimera"));
    public static final RegistryObject<Item> CHIMERA_EGG = egg("chimera", CHIMERA, 0x55633a, 0xb7b38a);
    public static final RegistryObject<EntityType<GiantSpiderEntity>> GIANT_SPIDER = ENTITIES.register("giant_spider",
        () -> EntityType.Builder.of(GiantSpiderEntity::new, MobCategory.MONSTER).sized(1.8f, 1.3f).clientTrackingRange(10).build("giant_spider"));
    public static final RegistryObject<Item> GIANT_SPIDER_EGG = egg("giant_spider", GIANT_SPIDER, 0x3a2a14, 0xd1a21f);
    public static final RegistryObject<EntityType<WebSpinnerEntity>> WEB_SPINNER = ENTITIES.register("web_spinner",
        () -> EntityType.Builder.of(WebSpinnerEntity::new, MobCategory.MONSTER).sized(0.9f, 0.7f).clientTrackingRange(10).build("web_spinner"));
    public static final RegistryObject<Item> WEB_SPINNER_EGG = egg("web_spinner", WEB_SPINNER, 0x2a1f33, 0xc0c040);
    public static final RegistryObject<EntityType<NeptuneEntity>> NEPTUNE = ENTITIES.register("neptune",
        () -> EntityType.Builder.of(NeptuneEntity::new, MobCategory.MONSTER).sized(1.6f, 1.2f).clientTrackingRange(10).build("neptune"));
    public static final RegistryObject<Item> NEPTUNE_EGG = egg("neptune", NEPTUNE, 0x3d5f73, 0xe0e0e0);
    public static final RegistryObject<EntityType<YawnEntity>> YAWN = ENTITIES.register("yawn",
        () -> EntityType.Builder.of(YawnEntity::new, MobCategory.MONSTER).sized(1.4f, 2.4f).clientTrackingRange(10).build("yawn"));
    public static final RegistryObject<Item> YAWN_EGG = egg("yawn", YAWN, 0x4a6b2a, 0xd0b030);
    public static final RegistryObject<EntityType<Plant42Entity>> PLANT42 = ENTITIES.register("plant42",
        () -> EntityType.Builder.of(Plant42Entity::new, MobCategory.MONSTER).sized(2.2f, 4.5f).clientTrackingRange(10).build("plant42"));
    public static final RegistryObject<Item> PLANT42_EGG = egg("plant42", PLANT42, 0x3e7a2e, 0xc03070);
    public static final RegistryObject<EntityType<TyrantEntity>> TYRANT = ENTITIES.register("tyrant",
        () -> EntityType.Builder.of(TyrantEntity::new, MobCategory.MONSTER).sized(1.1f, 2.9f).clientTrackingRange(10).build("tyrant"));
    public static final RegistryObject<Item> TYRANT_EGG = egg("tyrant", TYRANT, 0x5f6b4f, 0x8a1b1b);
    public static final RegistryObject<EntityType<LisaTrevorEntity>> LISA_TREVOR = ENTITIES.register("lisa_trevor",
        () -> EntityType.Builder.of(LisaTrevorEntity::new, MobCategory.MONSTER).sized(0.9f, 2.0f).clientTrackingRange(10).build("lisa_trevor"));
    public static final RegistryObject<Item> LISA_TREVOR_EGG = egg("lisa_trevor", LISA_TREVOR, 0x6b5a4a, 0xa07070);

    public static final RegistryObject<EntityType<LickerEntity>> LICKER = ENTITIES.register("licker",
        () -> EntityType.Builder.of(LickerEntity::new, MobCategory.MONSTER).sized(0.8f, 1.0f).clientTrackingRange(10).build("licker"));
    public static final RegistryObject<Item> LICKER_EGG = egg("licker", LICKER, 0xb04040, 0xd8a0a0);

    public static final RegistryObject<EntityType<MrXEntity>> MR_X = ENTITIES.register("mr_x",
        () -> EntityType.Builder.of(MrXEntity::new, MobCategory.MONSTER).sized(0.9f, 2.5f).clientTrackingRange(10).build("mr_x"));
    public static final RegistryObject<Item> MR_X_EGG = egg("mr_x", MR_X, 0x202020, 0x707070);

    public static final RegistryObject<EntityType<BirkinEntity>> BIRKIN = ENTITIES.register("birkin",
        () -> EntityType.Builder.of(BirkinEntity::new, MobCategory.MONSTER).sized(1.2f, 3.1f).clientTrackingRange(10).build("birkin"));
    public static final RegistryObject<Item> BIRKIN_EGG = egg("birkin", BIRKIN, 0x7a2a2a, 0xe0b060);

    public static final RegistryObject<EntityType<AlligatorEntity>> ALLIGATOR = ENTITIES.register("alligator",
        () -> EntityType.Builder.of(AlligatorEntity::new, MobCategory.MONSTER).sized(1.3f, 0.9f).clientTrackingRange(10).build("alligator"));
    public static final RegistryObject<Item> ALLIGATOR_EGG = egg("alligator", ALLIGATOR, 0x3c5a2c, 0xd8d0a0);

    public static final RegistryObject<EntityType<IvyEntity>> IVY = ENTITIES.register("ivy",
        () -> EntityType.Builder.of(IvyEntity::new, MobCategory.MONSTER).sized(1.3f, 2.7f).clientTrackingRange(10).build("ivy"));
    public static final RegistryObject<Item> IVY_EGG = egg("ivy", IVY, 0x2a5a30, 0x9030a0);

    public static final RegistryObject<EntityType<GiantMothEntity>> GIANT_MOTH = ENTITIES.register("giant_moth",
        () -> EntityType.Builder.of(GiantMothEntity::new, MobCategory.MONSTER).sized(1.4f, 1.2f).clientTrackingRange(10).build("giant_moth"));
    public static final RegistryObject<Item> GIANT_MOTH_EGG = egg("giant_moth", GIANT_MOTH, 0xc8b890, 0x504030);

    public static final RegistryObject<EntityType<LarvaEntity>> LARVA = ENTITIES.register("larva",
        () -> EntityType.Builder.of(LarvaEntity::new, MobCategory.MONSTER).sized(1.0f, 0.7f).clientTrackingRange(10).build("larva"));
    public static final RegistryObject<Item> LARVA_EGG = egg("larva", LARVA, 0xb8c070, 0x403020);

    public static final RegistryObject<EntityType<NemesisEntity>> NEMESIS = ENTITIES.register("nemesis",
        () -> EntityType.Builder.of(NemesisEntity::new, MobCategory.MONSTER).sized(1.0f, 2.7f).clientTrackingRange(10).build("nemesis"));
    public static final RegistryObject<Item> NEMESIS_EGG = egg("nemesis", NEMESIS, 0x181818, 0xa02020);

    public static final RegistryObject<EntityType<GraveDiggerEntity>> GRAVE_DIGGER = ENTITIES.register("grave_digger",
        () -> EntityType.Builder.of(GraveDiggerEntity::new, MobCategory.MONSTER).sized(1.8f, 1.6f).clientTrackingRange(10).build("grave_digger"));
    public static final RegistryObject<Item> GRAVE_DIGGER_EGG = egg("grave_digger", GRAVE_DIGGER, 0x6a6256, 0xc8b890);

    public static final RegistryObject<EntityType<DrainDeimosEntity>> DRAIN_DEIMOS = ENTITIES.register("drain_deimos",
        () -> EntityType.Builder.of(DrainDeimosEntity::new, MobCategory.MONSTER).sized(1.0f, 1.0f).clientTrackingRange(10).build("drain_deimos"));
    public static final RegistryObject<Item> DRAIN_DEIMOS_EGG = egg("drain_deimos", DRAIN_DEIMOS, 0x405040, 0xc03030);

    public static final RegistryObject<EntityType<BrainSuckerEntity>> BRAIN_SUCKER = ENTITIES.register("brain_sucker",
        () -> EntityType.Builder.of(BrainSuckerEntity::new, MobCategory.MONSTER).sized(0.8f, 0.8f).clientTrackingRange(10).build("brain_sucker"));
    public static final RegistryObject<Item> BRAIN_SUCKER_EGG = egg("brain_sucker", BRAIN_SUCKER, 0x504050, 0xc05080);

    public static final RegistryObject<EntityType<PaleHeadEntity>> PALE_HEAD = ENTITIES.register("pale_head",
        () -> EntityType.Builder.of(PaleHeadEntity::new, MobCategory.MONSTER).sized(0.7f, 2.0f).clientTrackingRange(10).build("pale_head"));
    public static final RegistryObject<Item> PALE_HEAD_EGG = egg("pale_head", PALE_HEAD, 0xc8c0b0, 0x605850);

    public static final RegistryObject<EntityType<HunterBetaEntity>> HUNTER_BETA = ENTITIES.register("hunter_beta",
        () -> EntityType.Builder.of(HunterBetaEntity::new, MobCategory.MONSTER).sized(0.9f, 2.1f).clientTrackingRange(10).build("hunter_beta"));
    public static final RegistryObject<Item> HUNTER_BETA_EGG = egg("hunter_beta", HUNTER_BETA, 0xa08a50, 0x402c20);

    public static final RegistryObject<EntityType<HunterGammaEntity>> HUNTER_GAMMA = ENTITIES.register("hunter_gamma",
        () -> EntityType.Builder.of(HunterGammaEntity::new, MobCategory.MONSTER).sized(1.0f, 1.2f).clientTrackingRange(10).build("hunter_gamma"));
    public static final RegistryObject<Item> HUNTER_GAMMA_EGG = egg("hunter_gamma", HUNTER_GAMMA, 0x6a8a30, 0xd0c040);

    public static final RegistryObject<EntityType<GanadoEntity>> GANADO = ENTITIES.register("ganado",
        () -> EntityType.Builder.of(GanadoEntity::new, MobCategory.MONSTER).sized(0.6f, 1.9f).clientTrackingRange(10).build("ganado"));
    public static final RegistryObject<Item> GANADO_EGG = egg("ganado", GANADO, 0x7a5a3a, 0x8a2020);

    public static final RegistryObject<EntityType<SalvadorEntity>> SALVADOR = ENTITIES.register("salvador",
        () -> EntityType.Builder.of(SalvadorEntity::new, MobCategory.MONSTER).sized(0.8f, 2.3f).clientTrackingRange(10).build("salvador"));
    public static final RegistryObject<Item> SALVADOR_EGG = egg("salvador", SALVADOR, 0xa09070, 0x802020);

    public static final RegistryObject<EntityType<GarradorEntity>> GARRADOR = ENTITIES.register("garrador",
        () -> EntityType.Builder.of(GarradorEntity::new, MobCategory.MONSTER).sized(1.0f, 2.5f).clientTrackingRange(10).build("garrador"));
    public static final RegistryObject<Item> GARRADOR_EGG = egg("garrador", GARRADOR, 0x504030, 0xc0a050);

    public static final RegistryObject<EntityType<ColmillosEntity>> COLMILLOS = ENTITIES.register("colmillos",
        () -> EntityType.Builder.of(ColmillosEntity::new, MobCategory.MONSTER).sized(0.8f, 0.95f).clientTrackingRange(10).build("colmillos"));
    public static final RegistryObject<Item> COLMILLOS_EGG = egg("colmillos", COLMILLOS, 0x707068, 0xc0c0b0);

    public static final RegistryObject<EntityType<NovistadorEntity>> NOVISTADOR = ENTITIES.register("novistador",
        () -> EntityType.Builder.of(NovistadorEntity::new, MobCategory.MONSTER).sized(1.0f, 1.0f).clientTrackingRange(10).build("novistador"));
    public static final RegistryObject<Item> NOVISTADOR_EGG = egg("novistador", NOVISTADOR, 0x405848, 0xa0c0a0);

    public static final RegistryObject<EntityType<RegeneradorEntity>> REGENERADOR = ENTITIES.register("regenerador",
        () -> EntityType.Builder.of(RegeneradorEntity::new, MobCategory.MONSTER).sized(0.7f, 2.1f).clientTrackingRange(10).build("regenerador"));
    public static final RegistryObject<Item> REGENERADOR_EGG = egg("regenerador", REGENERADOR, 0x8a8070, 0x305070);

    public static final RegistryObject<EntityType<IronMaidenEntity>> IRON_MAIDEN = ENTITIES.register("iron_maiden",
        () -> EntityType.Builder.of(IronMaidenEntity::new, MobCategory.MONSTER).sized(0.8f, 2.2f).clientTrackingRange(10).build("iron_maiden"));
    public static final RegistryObject<Item> IRON_MAIDEN_EGG = egg("iron_maiden", IRON_MAIDEN, 0x707070, 0xa03020);

    public static final RegistryObject<EntityType<ElGiganteEntity>> EL_GIGANTE = ENTITIES.register("el_gigante",
        () -> EntityType.Builder.of(ElGiganteEntity::new, MobCategory.MONSTER).sized(1.9f, 4.6f).clientTrackingRange(10).build("el_gigante"));
    public static final RegistryObject<Item> EL_GIGANTE_EGG = egg("el_gigante", EL_GIGANTE, 0x706050, 0xc03030);

    public static final RegistryObject<EntityType<DelLagoEntity>> DEL_LAGO = ENTITIES.register("del_lago",
        () -> EntityType.Builder.of(DelLagoEntity::new, MobCategory.MONSTER).sized(3.2f, 2.4f).clientTrackingRange(10).build("del_lago"));
    public static final RegistryObject<Item> DEL_LAGO_EGG = egg("del_lago", DEL_LAGO, 0x305048, 0xc0a060);

    public static final RegistryObject<EntityType<MajiniEntity>> MAJINI = ENTITIES.register("majini",
        () -> EntityType.Builder.of(MajiniEntity::new, MobCategory.MONSTER).sized(0.6f, 1.9f).clientTrackingRange(10).build("majini"));
    public static final RegistryObject<Item> MAJINI_EGG = egg("majini", MAJINI, 0x4a3424, 0xa03020);

    public static final RegistryObject<EntityType<ZealotEntity>> ZEALOT = ENTITIES.register("zealot",
        () -> EntityType.Builder.of(ZealotEntity::new, MobCategory.MONSTER).sized(0.6f, 1.9f).clientTrackingRange(10).build("zealot"));
    public static final RegistryObject<Item> ZEALOT_EGG = egg("zealot", ZEALOT, 0x602020, 0xc0a040);

    public static final RegistryObject<EntityType<ChainsawMajiniEntity>> CHAINSAW_MAJINI = ENTITIES.register("chainsaw_majini",
        () -> EntityType.Builder.of(ChainsawMajiniEntity::new, MobCategory.MONSTER).sized(0.8f, 2.3f).clientTrackingRange(10).build("chainsaw_majini"));
    public static final RegistryObject<Item> CHAINSAW_MAJINI_EGG = egg("chainsaw_majini", CHAINSAW_MAJINI, 0x3a2c20, 0xc0a040);

    public static final RegistryObject<EntityType<ArmaduraEntity>> ARMADURA = ENTITIES.register("armadura",
        () -> EntityType.Builder.of(ArmaduraEntity::new, MobCategory.MONSTER).sized(0.8f, 2.2f).clientTrackingRange(10).build("armadura"));
    public static final RegistryObject<Item> ARMADURA_EGG = egg("armadura", ARMADURA, 0x80848c, 0x303030);

    public static final RegistryObject<EntityType<KipepeoEntity>> KIPEPEO = ENTITIES.register("kipepeo",
        () -> EntityType.Builder.of(KipepeoEntity::new, MobCategory.MONSTER).sized(1.4f, 1.2f).clientTrackingRange(10).build("kipepeo"));
    public static final RegistryObject<Item> KIPEPEO_EGG = egg("kipepeo", KIPEPEO, 0x607040, 0xe0d080);

    public static final RegistryObject<EntityType<ReaperEntity>> REAPER = ENTITIES.register("reaper",
        () -> EntityType.Builder.of(ReaperEntity::new, MobCategory.MONSTER).sized(1.0f, 1.6f).clientTrackingRange(10).build("reaper"));
    public static final RegistryObject<Item> REAPER_EGG = egg("reaper", REAPER, 0x302030, 0xc03060);

    public static final RegistryObject<EntityType<VerdugoEntity>> VERDUGO = ENTITIES.register("verdugo",
        () -> EntityType.Builder.of(VerdugoEntity::new, MobCategory.MONSTER).sized(1.0f, 2.6f).clientTrackingRange(10).build("verdugo"));
    public static final RegistryObject<Item> VERDUGO_EGG = egg("verdugo", VERDUGO, 0x806050, 0xa0a0a8);

    public static final RegistryObject<EntityType<WeskerEntity>> WESKER = ENTITIES.register("wesker",
        () -> EntityType.Builder.of(WeskerEntity::new, MobCategory.MONSTER).sized(0.7f, 2.0f).clientTrackingRange(10).build("wesker"));
    public static final RegistryObject<Item> WESKER_EGG = egg("wesker", WESKER, 0x101010, 0xe0c040);

    public static final RegistryObject<EntityType<AdjuleEntity>> ADJULE = ENTITIES.register("adjule",
        () -> EntityType.Builder.of(AdjuleEntity::new, MobCategory.MONSTER).sized(0.9f, 2.5f).clientTrackingRange(10).build("adjule"));
    public static final RegistryObject<Item> ADJULE_EGG = egg("adjule", ADJULE, 0x706050, 0xa04040);

    public static final RegistryObject<EntityType<NdesuEntity>> NDESU = ENTITIES.register("ndesu",
        () -> EntityType.Builder.of(NdesuEntity::new, MobCategory.MONSTER).sized(1.4f, 3.4f).clientTrackingRange(10).build("ndesu"));
    public static final RegistryObject<Item> NDESU_EGG = egg("ndesu", NDESU, 0x605040, 0x80a040);

    public static final RegistryObject<EntityType<ExecutionerMajiniEntity>> EXECUTIONER_MAJINI = ENTITIES.register("executioner_majini",
        () -> EntityType.Builder.of(ExecutionerMajiniEntity::new, MobCategory.MONSTER).sized(1.5f, 3.8f).clientTrackingRange(10).build("executioner_majini"));
    public static final RegistryObject<Item> EXECUTIONER_MAJINI_EGG = egg("executioner_majini", EXECUTIONER_MAJINI, 0x403028, 0xc03030);

    public static final RegistryObject<EntityType<KrauserEntity>> KRAUSER = ENTITIES.register("krauser",
        () -> EntityType.Builder.of(KrauserEntity::new, MobCategory.MONSTER).sized(0.7f, 2.0f).clientTrackingRange(10).build("krauser"));
    public static final RegistryObject<Item> KRAUSER_EGG = egg("krauser", KRAUSER, 0x4a5a40, 0xc0a060);

    public static final RegistryObject<EntityType<SaddlerEntity>> SADDLER = ENTITIES.register("saddler",
        () -> EntityType.Builder.of(SaddlerEntity::new, MobCategory.MONSTER).sized(0.7f, 2.1f).clientTrackingRange(10).build("saddler"));
    public static final RegistryObject<Item> SADDLER_EGG = egg("saddler", SADDLER, 0xe0e0e0, 0x704020);

    public static final RegistryObject<EntityType<SimmonsEntity>> SIMMONS = ENTITIES.register("simmons",
        () -> EntityType.Builder.of(SimmonsEntity::new, MobCategory.MONSTER).sized(0.9f, 2.5f).clientTrackingRange(10).build("simmons"));
    public static final RegistryObject<Item> SIMMONS_EGG = egg("simmons", SIMMONS, 0x404040, 0xc02020);

    public static final RegistryObject<EntityType<SalazarEntity>> SALAZAR = ENTITIES.register("salazar",
        () -> EntityType.Builder.of(SalazarEntity::new, MobCategory.MONSTER).sized(2.4f, 4.0f).clientTrackingRange(10).build("salazar"));
    public static final RegistryObject<Item> SALAZAR_EGG = egg("salazar", SALAZAR, 0xc0a0b0, 0x704050);

    public static final RegistryObject<EntityType<UroborosEntity>> UROBOROS = ENTITIES.register("uroboros",
        () -> EntityType.Builder.of(UroborosEntity::new, MobCategory.MONSTER).sized(2.8f, 4.8f).clientTrackingRange(10).build("uroboros"));
    public static final RegistryObject<Item> UROBOROS_EGG = egg("uroboros", UROBOROS, 0x201010, 0xe0a020);

    public static final RegistryObject<EntityType<MendezEntity>> MENDEZ = ENTITIES.register("mendez",
        () -> EntityType.Builder.of(MendezEntity::new, MobCategory.MONSTER).sized(1.3f, 3.2f).clientTrackingRange(10).build("mendez"));
    public static final RegistryObject<Item> MENDEZ_EGG = egg("mendez", MENDEZ, 0x706040, 0x802020);

    public static final RegistryObject<EntityType<OgromanEntity>> OGROMAN = ENTITIES.register("ogroman",
        () -> EntityType.Builder.of(OgromanEntity::new, MobCategory.MONSTER).sized(1.9f, 4.6f).clientTrackingRange(10).build("ogroman"));
    public static final RegistryObject<Item> OGROMAN_EGG = egg("ogroman", OGROMAN, 0x504038, 0xa04030);

    public static final RegistryObject<EntityType<JillEntity>> JILL = ENTITIES.register("jill",
        () -> EntityType.Builder.of(JillEntity::new, MobCategory.MONSTER).sized(0.6f, 1.9f).clientTrackingRange(10).build("jill"));
    public static final RegistryObject<Item> JILL_EGG = egg("jill", JILL, 0x101010, 0xe0c070);

    public static final RegistryObject<EntityType<UstanakEntity>> USTANAK = ENTITIES.register("ustanak",
        () -> EntityType.Builder.of(UstanakEntity::new, MobCategory.MONSTER).sized(1.1f, 2.9f).clientTrackingRange(10).build("ustanak"));
    public static final RegistryObject<Item> USTANAK_EGG = egg("ustanak", USTANAK, 0x706860, 0xa02020);

    public static final RegistryObject<EntityType<NapadEntity>> NAPAD = ENTITIES.register("napad",
        () -> EntityType.Builder.of(NapadEntity::new, MobCategory.MONSTER).sized(0.8f, 1.2f).clientTrackingRange(10).build("napad"));
    public static final RegistryObject<Item> NAPAD_EGG = egg("napad", NAPAD, 0x405030, 0xc0b060);

    public static final RegistryObject<EntityType<JavoEntity>> JAVO = ENTITIES.register("javo",
        () -> EntityType.Builder.of(JavoEntity::new, MobCategory.MONSTER).sized(0.6f, 1.9f).clientTrackingRange(10).build("javo"));
    public static final RegistryObject<Item> JAVO_EGG = egg("javo", JAVO, 0x404850, 0xc03030);

    public static final RegistryObject<EntityType<GatlingMajiniEntity>> GATLING_MAJINI = ENTITIES.register("gatling_majini",
        () -> EntityType.Builder.of(GatlingMajiniEntity::new, MobCategory.MONSTER).sized(0.8f, 2.1f).clientTrackingRange(10).build("gatling_majini"));
    public static final RegistryObject<Item> GATLING_MAJINI_EGG = egg("gatling_majini", GATLING_MAJINI, 0x3a2c20, 0x808080);

    public static final RegistryObject<EntityType<MoldedEntity>> MOLDED = ENTITIES.register("molded",
        () -> EntityType.Builder.of(MoldedEntity::new, MobCategory.MONSTER).sized(0.6f, 1.9f).clientTrackingRange(10).build("molded"));
    public static final RegistryObject<Item> MOLDED_EGG = egg("molded", MOLDED, 0x101010, 0x606060);

    public static final RegistryObject<EntityType<FatMoldedEntity>> FAT_MOLDED = ENTITIES.register("fat_molded",
        () -> EntityType.Builder.of(FatMoldedEntity::new, MobCategory.MONSTER).sized(1.0f, 2.2f).clientTrackingRange(10).build("fat_molded"));
    public static final RegistryObject<Item> FAT_MOLDED_EGG = egg("fat_molded", FAT_MOLDED, 0x181818, 0x707070);

    public static final RegistryObject<EntityType<JackBakerEntity>> JACK_BAKER = ENTITIES.register("jack_baker",
        () -> EntityType.Builder.of(JackBakerEntity::new, MobCategory.MONSTER).sized(0.8f, 2.1f).clientTrackingRange(10).build("jack_baker"));
    public static final RegistryObject<Item> JACK_BAKER_EGG = egg("jack_baker", JACK_BAKER, 0x403830, 0x907040);

    public static final RegistryObject<EntityType<EvelineEntity>> EVELINE = ENTITIES.register("eveline",
        () -> EntityType.Builder.of(EvelineEntity::new, MobCategory.MONSTER).sized(0.7f, 2.0f).clientTrackingRange(10).build("eveline"));
    public static final RegistryObject<Item> EVELINE_EGG = egg("eveline", EVELINE, 0x203040, 0xc0d0e0);

    public static final RegistryObject<EntityType<LycanEntity>> LYCAN = ENTITIES.register("lycan",
        () -> EntityType.Builder.of(LycanEntity::new, MobCategory.MONSTER).sized(0.8f, 2.0f).clientTrackingRange(10).build("lycan"));
    public static final RegistryObject<Item> LYCAN_EGG = egg("lycan", LYCAN, 0x504030, 0xc0b090);

    public static final RegistryObject<EntityType<DimitrescuEntity>> DIMITRESCU = ENTITIES.register("dimitrescu",
        () -> EntityType.Builder.of(DimitrescuEntity::new, MobCategory.MONSTER).sized(1.1f, 3.4f).clientTrackingRange(10).build("dimitrescu"));
    public static final RegistryObject<Item> DIMITRESCU_EGG = egg("dimitrescu", DIMITRESCU, 0xe0e0e0, 0x601020);

    public static final RegistryObject<EntityType<DaughterEntity>> DAUGHTER = ENTITIES.register("daughter",
        () -> EntityType.Builder.of(DaughterEntity::new, MobCategory.MONSTER).sized(0.7f, 1.6f).clientTrackingRange(10).build("daughter"));
    public static final RegistryObject<Item> DAUGHTER_EGG = egg("daughter", DAUGHTER, 0xd0d0d0, 0x501018);

    public static final RegistryObject<EntityType<HeisenbergEntity>> HEISENBERG = ENTITIES.register("heisenberg",
        () -> EntityType.Builder.of(HeisenbergEntity::new, MobCategory.MONSTER).sized(0.8f, 2.3f).clientTrackingRange(10).build("heisenberg"));
    public static final RegistryObject<Item> HEISENBERG_EGG = egg("heisenberg", HEISENBERG, 0x503020, 0x909090);

    public static final RegistryObject<EntityType<MoreauEntity>> MOREAU = ENTITIES.register("moreau",
        () -> EntityType.Builder.of(MoreauEntity::new, MobCategory.MONSTER).sized(2.4f, 2.0f).clientTrackingRange(10).build("moreau"));
    public static final RegistryObject<Item> MOREAU_EGG = egg("moreau", MOREAU, 0x305030, 0xa0c070);

    public static final RegistryObject<EntityType<SturmEntity>> STURM = ENTITIES.register("sturm",
        () -> EntityType.Builder.of(SturmEntity::new, MobCategory.MONSTER).sized(1.8f, 4.4f).clientTrackingRange(10).build("sturm"));
    public static final RegistryObject<Item> STURM_EGG = egg("sturm", STURM, 0x404040, 0xa08040);

    public static final RegistryObject<EntityType<UriasEntity>> URIAS = ENTITIES.register("urias",
        () -> EntityType.Builder.of(UriasEntity::new, MobCategory.MONSTER).sized(1.2f, 3.0f).clientTrackingRange(10).build("urias"));
    public static final RegistryObject<Item> URIAS_EGG = egg("urias", URIAS, 0x302828, 0xa04020);

    public static final RegistryObject<EntityType<SoldatEntity>> SOLDAT = ENTITIES.register("soldat",
        () -> EntityType.Builder.of(SoldatEntity::new, MobCategory.MONSTER).sized(0.7f, 2.0f).clientTrackingRange(10).build("soldat"));
    public static final RegistryObject<Item> SOLDAT_EGG = egg("soldat", SOLDAT, 0x404840, 0xa0a0a0);

    public static final RegistryObject<EntityType<MargueriteEntity>> MARGUERITE = ENTITIES.register("marguerite",
        () -> EntityType.Builder.of(MargueriteEntity::new, MobCategory.MONSTER).sized(1.6f, 1.8f).clientTrackingRange(10).build("marguerite"));
    public static final RegistryObject<Item> MARGUERITE_EGG = egg("marguerite", MARGUERITE, 0x806040, 0xd0c080);

    public static final RegistryObject<EntityType<MirandaEntity>> MIRANDA = ENTITIES.register("miranda",
        () -> EntityType.Builder.of(MirandaEntity::new, MobCategory.MONSTER).sized(0.7f, 2.0f).clientTrackingRange(10).build("miranda"));
    public static final RegistryObject<Item> MIRANDA_EGG = egg("miranda", MIRANDA, 0x302040, 0xc0c0e0);

    public static final RegistryObject<EntityType<HaosEntity>> HAOS = ENTITIES.register("haos",
        () -> EntityType.Builder.of(HaosEntity::new, MobCategory.MONSTER).sized(2.6f, 4.4f).clientTrackingRange(10).build("haos"));
    public static final RegistryObject<Item> HAOS_EGG = egg("haos", HAOS, 0x402028, 0xc08040);

    public static final RegistryObject<EntityType<GnezdoEntity>> GNEZDO = ENTITIES.register("gnezdo",
        () -> EntityType.Builder.of(GnezdoEntity::new, MobCategory.MONSTER).sized(1.2f, 0.9f).clientTrackingRange(10).build("gnezdo"));
    public static final RegistryObject<Item> GNEZDO_EGG = egg("gnezdo", GNEZDO, 0x606030, 0xc0c060);

    public static void onAttributes(EntityAttributeCreationEvent e) {
        e.put(ZOMBIE.get(), ZombieEntity.createAttributes().build());
        e.put(CRIMSON_HEAD.get(), CrimsonHeadEntity.createAttributes().build());
        e.put(CERBERUS.get(), CerberusEntity.createAttributes().build());
        e.put(CROW.get(), CrowEntity.createAttributes().build());
        e.put(HUNTER.get(), HunterEntity.createAttributes().build());
        e.put(CHIMERA.get(), ChimeraEntity.createAttributes().build());
        e.put(GIANT_SPIDER.get(), GiantSpiderEntity.createAttributes().build());
        e.put(WEB_SPINNER.get(), WebSpinnerEntity.createAttributes().build());
        e.put(NEPTUNE.get(), NeptuneEntity.createAttributes().build());
        e.put(YAWN.get(), YawnEntity.createAttributes().build());
        e.put(PLANT42.get(), Plant42Entity.createAttributes().build());
        e.put(TYRANT.get(), TyrantEntity.createAttributes().build());
        e.put(LISA_TREVOR.get(), LisaTrevorEntity.createAttributes().build());
        e.put(LICKER.get(), LickerEntity.createAttributes().build());
        e.put(MR_X.get(), MrXEntity.createAttributes().build());
        e.put(BIRKIN.get(), BirkinEntity.createAttributes().build());
        e.put(ALLIGATOR.get(), AlligatorEntity.createAttributes().build());
        e.put(IVY.get(), IvyEntity.createAttributes().build());
        e.put(GIANT_MOTH.get(), GiantMothEntity.createAttributes().build());
        e.put(LARVA.get(), LarvaEntity.createAttributes().build());
        e.put(NEMESIS.get(), NemesisEntity.createAttributes().build());
        e.put(GRAVE_DIGGER.get(), GraveDiggerEntity.createAttributes().build());
        e.put(DRAIN_DEIMOS.get(), DrainDeimosEntity.createAttributes().build());
        e.put(BRAIN_SUCKER.get(), BrainSuckerEntity.createAttributes().build());
        e.put(PALE_HEAD.get(), PaleHeadEntity.createAttributes().build());
        e.put(HUNTER_BETA.get(), HunterBetaEntity.createAttributes().build());
        e.put(HUNTER_GAMMA.get(), HunterGammaEntity.createAttributes().build());
        e.put(GANADO.get(), GanadoEntity.createAttributes().build());
        e.put(SALVADOR.get(), SalvadorEntity.createAttributes().build());
        e.put(GARRADOR.get(), GarradorEntity.createAttributes().build());
        e.put(COLMILLOS.get(), ColmillosEntity.createAttributes().build());
        e.put(NOVISTADOR.get(), NovistadorEntity.createAttributes().build());
        e.put(REGENERADOR.get(), RegeneradorEntity.createAttributes().build());
        e.put(IRON_MAIDEN.get(), IronMaidenEntity.createAttributes().build());
        e.put(EL_GIGANTE.get(), ElGiganteEntity.createAttributes().build());
        e.put(DEL_LAGO.get(), DelLagoEntity.createAttributes().build());
        e.put(MAJINI.get(), MajiniEntity.createAttributes().build());
        e.put(ZEALOT.get(), ZealotEntity.createAttributes().build());
        e.put(CHAINSAW_MAJINI.get(), ChainsawMajiniEntity.createAttributes().build());
        e.put(ARMADURA.get(), ArmaduraEntity.createAttributes().build());
        e.put(KIPEPEO.get(), KipepeoEntity.createAttributes().build());
        e.put(REAPER.get(), ReaperEntity.createAttributes().build());
        e.put(VERDUGO.get(), VerdugoEntity.createAttributes().build());
        e.put(WESKER.get(), WeskerEntity.createAttributes().build());
        e.put(ADJULE.get(), AdjuleEntity.createAttributes().build());
        e.put(NDESU.get(), NdesuEntity.createAttributes().build());
        e.put(EXECUTIONER_MAJINI.get(), ExecutionerMajiniEntity.createAttributes().build());
        e.put(KRAUSER.get(), KrauserEntity.createAttributes().build());
        e.put(SADDLER.get(), SaddlerEntity.createAttributes().build());
        e.put(SIMMONS.get(), SimmonsEntity.createAttributes().build());
        e.put(SALAZAR.get(), SalazarEntity.createAttributes().build());
        e.put(UROBOROS.get(), UroborosEntity.createAttributes().build());
        e.put(MENDEZ.get(), MendezEntity.createAttributes().build());
        e.put(OGROMAN.get(), OgromanEntity.createAttributes().build());
        e.put(JILL.get(), JillEntity.createAttributes().build());
        e.put(USTANAK.get(), UstanakEntity.createAttributes().build());
        e.put(NAPAD.get(), NapadEntity.createAttributes().build());
        e.put(JAVO.get(), JavoEntity.createAttributes().build());
        e.put(GATLING_MAJINI.get(), GatlingMajiniEntity.createAttributes().build());
        e.put(MOLDED.get(), MoldedEntity.createAttributes().build());
        e.put(FAT_MOLDED.get(), FatMoldedEntity.createAttributes().build());
        e.put(JACK_BAKER.get(), JackBakerEntity.createAttributes().build());
        e.put(EVELINE.get(), EvelineEntity.createAttributes().build());
        e.put(LYCAN.get(), LycanEntity.createAttributes().build());
        e.put(DIMITRESCU.get(), DimitrescuEntity.createAttributes().build());
        e.put(DAUGHTER.get(), DaughterEntity.createAttributes().build());
        e.put(HEISENBERG.get(), HeisenbergEntity.createAttributes().build());
        e.put(MOREAU.get(), MoreauEntity.createAttributes().build());
        e.put(STURM.get(), SturmEntity.createAttributes().build());
        e.put(URIAS.get(), UriasEntity.createAttributes().build());
        e.put(SOLDAT.get(), SoldatEntity.createAttributes().build());
        e.put(MARGUERITE.get(), MargueriteEntity.createAttributes().build());
        e.put(MIRANDA.get(), MirandaEntity.createAttributes().build());
        e.put(HAOS.get(), HaosEntity.createAttributes().build());
        e.put(GNEZDO.get(), GnezdoEntity.createAttributes().build());
    }

    private ModEntities() {}
}
