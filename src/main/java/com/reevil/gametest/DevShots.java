package com.reevil.gametest;

import com.mojang.blaze3d.platform.NativeImage;
import com.reevil.ReEvilMod;
import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Dev only (excluded from the jar): real in-game screenshots. Run the client with -Dreevil.autoshot=<jobs file> -Dreevil.shotdir=<dir>
 * (e.g. under xvfb with LIBGL_ALWAYS_SOFTWARE=1). Each jobs line: "<id> <mut> <form> <moveId|0> <clipPercent> [healthFraction]"; the creature is spawned
 * in front of the player (AI off, the move played through the real server tick), photographed, removed; the game exits after the last job.
 */
@Mod.EventBusSubscriber(modid = ReEvilMod.MODID, value = Dist.CLIENT)
public final class DevShots {
    private static List<String[]> jobs;
    private static int idx, wait, phase;
    private static Entity cur;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent e) throws Exception {
        String file = System.getProperty("reevil.autoshot");
        Minecraft mc = Minecraft.getInstance();
        if (file == null || e.phase != TickEvent.Phase.END || mc.player == null || mc.getSingleplayerServer() == null) return;
        if (jobs == null) { jobs = new ArrayList<>(); for (String l : Files.readAllLines(Path.of(file))) if (!l.isBlank()) jobs.add(l.trim().split("\\s+")); wait = 60; }
        mc.options.hideGui = true;   // no boss bars / hotbar in the pictures
        if (wait-- > 0) return;
        if (idx >= jobs.size()) { mc.stop(); return; }
        String[] j = jobs.get(idx);
        var server = mc.getSingleplayerServer();
        if (phase == 0) {          // spawn + start the move
            server.execute(() -> {
                ServerPlayer p = server.getPlayerList().getPlayers().get(0);
                p.setGameMode(GameType.CREATIVE); p.setInvulnerable(true);
                var type = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(ReEvilMod.MODID, j[0]));
                Entity en = type.create(p.serverLevel());
                double d = Math.max(4.5, en.getBbHeight() * 1.5 + 2);
                var lv = p.serverLevel();
                lv.getEntitiesOfClass(BioMonster.class, p.getBoundingBox().inflate(96)).forEach(Entity::discard);   // natural spawns / earlier jobs
                double x = Math.floor(p.getX()) + 0.5, z = Math.floor(p.getZ()) + 0.5;
                double y = lv.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) z);
                double ey = lv.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x, (int) (z + d));
                en.moveTo(x, ey, z + d, 180f, 0f);
                if (en instanceof net.minecraft.world.entity.LivingEntity le) { le.yBodyRot = 180f; le.yBodyRotO = 180f; le.yHeadRot = 180f; le.yHeadRotO = 180f; }   // face the camera
                if (en instanceof BioMonster b) {
                    b.setNoAi(true); b.setMut(Integer.parseInt(j[1])); b.setForm(Integer.parseInt(j[2]));
                    b.addTag("reevil_iso");
                }
                lv.addFreshEntity(en);
                p.teleportTo(lv, x, y, z, 0f, (float) Math.toDegrees(Math.atan2(-(ey + en.getBbHeight() * 0.5 - (y + 1.62)), d)));
                if (j.length > 5 && en instanceof BioMonster b) b.setHealth(b.getMaxHealth() * Float.parseFloat(j[5]));   // optional 6th column: health fraction (passives such as mutations then trigger on the real tick)
                cur = en;
                int mv = Integer.parseInt(j[3]);
                if (mv > 0 && en instanceof BioMonster b) for (AttackMove m : b.allMoves()) if (m.id == mv) { b.startMove(m); break; }
            });
            phase = 1; wait = j.length > 5 ? 50 : 12;
        } else if (phase == 1) {   // let the move run to the requested point, then capture
            int pct = Integer.parseInt(j[4]);
            if (cur instanceof BioMonster b && b.isMoving() && b.getMoveDuration() > 0 && b.getMoveTick() * 100 < pct * b.getMoveDuration()) return;
            phase = 2;
            File dir = new File(System.getProperty("reevil.shotdir", "shots")); dir.mkdirs();
            String name = j[0] + "_m" + j[3] + "_u" + j[1] + "_f" + j[2] + (j.length > 5 ? "_h" + j[5] : "") + ".png";
            Screenshot.grab(dir, name, mc.getMainRenderTarget(), c -> {});
            wait = 6;
        } else {                   // clean up
            server.execute(() -> { if (cur != null) cur.discard(); });
            phase = 0; idx++; wait = 6;
        }
    }
}
