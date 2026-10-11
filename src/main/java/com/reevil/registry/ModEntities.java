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
    }

    private ModEntities() {}
}
