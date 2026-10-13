package com.reevil;

import com.reevil.entity.BioMonster;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Loot: toughness decides what a creature leaves behind (no custom items needed). */
@Mod.EventBusSubscriber(modid = ReEvilMod.MODID)
public final class CanonEvents {
    @SubscribeEvent
    public static void drops(LivingDropsEvent e) {
        if (!(e.getEntity() instanceof BioMonster m) || m.level().isClientSide || !ReConfig.DROPS.get()) return;
        float hp = m.getMaxHealth(); var r = m.getRandom();
        if (hp >= 300) {
            add(e, Items.EMERALD, 1 + r.nextInt(3)); add(e, Items.GOLD_INGOT, r.nextInt(3)); add(e, Items.GUNPOWDER, 2 + r.nextInt(4));
            if (r.nextInt(8) == 0) add(e, Items.DIAMOND, 1);
        } else if (hp >= 80) {
            add(e, Items.IRON_NUGGET, 1 + r.nextInt(4)); add(e, Items.GUNPOWDER, r.nextInt(3));
            if (r.nextInt(6) == 0) add(e, Items.GOLD_NUGGET, 1 + r.nextInt(3));
        } else {
            if (r.nextBoolean()) add(e, r.nextBoolean() ? Items.ROTTEN_FLESH : Items.BONE, 1);
            if (r.nextInt(12) == 0) add(e, Items.IRON_NUGGET, 1);
        }
    }
    @SubscribeEvent
    public static void impact(net.minecraftforge.event.entity.ProjectileImpactEvent e) {
        var pr = e.getProjectile();
        if (pr.level().isClientSide || !pr.getTags().contains("reevil_bomb")) return;
        pr.level().explode(pr.getOwner(), pr.getX(), pr.getY(), pr.getZ(), 2.4f, net.minecraft.world.level.Level.ExplosionInteraction.NONE);
        pr.discard(); e.setCanceled(true);
    }
    private static void add(LivingDropsEvent e, net.minecraft.world.item.Item item, int n) {
        if (n > 0) e.getDrops().add(new ItemEntity(e.getEntity().level(), e.getEntity().getX(), e.getEntity().getY() + 0.5, e.getEntity().getZ(), new ItemStack(item, n)));
    }
    private CanonEvents() {}
}
