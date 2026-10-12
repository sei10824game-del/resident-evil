package com.reevil.gametest;

import com.reevil.entity.BioMonster;
import com.reevil.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.Set;
import java.util.TreeSet;

/** Dev-only smoke tests (excluded from the jar): every creature fights a mock player for 600 ticks; fails if no attack ever started or the mob dies of its own accord. */
@GameTestHolder("reevil")
@PrefixGameTestTemplate(false)
public class CreatureTests {
    private static void run(GameTestHelper h, EntityType<? extends BioMonster> type, boolean water, int dist) { run(h, type, water, dist, 1f); }

    private static void run(GameTestHelper h, EntityType<? extends BioMonster> type, boolean water, int dist, float hpFrac) {
        for (int x = 0; x < 24; x++) for (int z = 0; z < 24; z++) {
            h.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
            for (int y = 1; y <= 5; y++) h.setBlock(new BlockPos(x, y, z), water && y <= 4 ? Blocks.WATER : Blocks.AIR);
        }
        BioMonster mob = (BioMonster) h.spawn(type, new BlockPos(12, water ? 2 : 1, 12 + dist));
        Player p = h.makeMockPlayer();
        Vec3 ppos = h.absoluteVec(new Vec3(12, 1, 12));
        p.moveTo(ppos.x, ppos.y, ppos.z);
        Set<Integer> seen = new TreeSet<>();
        h.runAfterDelay(3, () -> { mob.hurt(mob.damageSources().generic(), 1f); if (hpFrac < 1f) mob.setHealth(Math.max(2f, mob.getMaxHealth() * hpFrac)); });
        for (int t = 1; t <= 600; t++) {
            final int tick = t;
            h.runAfterDelay(t, () -> {
                p.setHealth(20f);
                p.moveTo(ppos.x, ppos.y, ppos.z);
                if (mob.isAlive()) {
                    if (tick % 10 == 0) mob.setTarget(p);
                    if (mob.getMoveId() > 0) seen.add(mob.getMoveId());
                }
            });
        }
        h.runAfterDelay(601, () -> {
            System.out.println("[REEVIL-TEST] " + (hpFrac < 1f ? "LOWHP " : "") + type.builtInRegistryHolder().key().location() + " moves seen=" + seen + " alive=" + mob.isAlive() + " hp=" + mob.getHealth());
            if (!mob.isAlive()) h.fail("died unprovoked");
            else if (seen.isEmpty()) h.fail("never started an attack");
            else h.succeed();
        });
    }


    private static void flat(GameTestHelper h) {
        for (int x = 0; x < 24; x++) for (int z = 0; z < 24; z++) {
            h.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
            for (int y = 1; y <= 5; y++) h.setBlock(new BlockPos(x, y, z), Blocks.AIR);
        }
    }
    private static void fight(GameTestHelper h, net.minecraft.world.entity.LivingEntity a, net.minecraft.world.entity.LivingEntity b, boolean expectDamage) {
        float ha = a.getMaxHealth(), hb = b.getMaxHealth();
        h.runAfterDelay(400, () -> {
            boolean dmg = a.getHealth() < ha || b.getHealth() < hb || !a.isAlive() || !b.isAlive();
            System.out.println("[REEVIL-TEST] FACTION " + a.getType() + " vs " + b.getType() + " dmg=" + dmg);
            if (dmg == expectDamage) h.succeed(); else h.fail("dmg=" + dmg);
        });
    }
    @GameTest(template = "empty", timeoutTicks = 500) public static void factionCross(GameTestHelper h) {
        flat(h); fight(h, h.spawn(ModEntities.ZOMBIE.get(), new BlockPos(8, 1, 12)), h.spawn(ModEntities.GANADO.get(), new BlockPos(14, 1, 12)), true); }
    @GameTest(template = "empty", timeoutTicks = 500) public static void factionSame(GameTestHelper h) {
        flat(h); fight(h, h.spawn(ModEntities.ZOMBIE.get(), new BlockPos(8, 1, 12)), h.spawn(ModEntities.HUNTER.get(), new BlockPos(14, 1, 12)), false); }
    @GameTest(template = "empty", timeoutTicks = 500) public static void factionVanilla(GameTestHelper h) {
        flat(h); fight(h, h.spawn(ModEntities.ZOMBIE.get(), new BlockPos(8, 1, 12)), h.spawn(net.minecraft.world.entity.EntityType.COW, new BlockPos(10, 1, 12)), true); }
    @GameTest(template = "empty", timeoutTicks = 500) public static void factionVanillaAttacks(GameTestHelper h) {
        flat(h); fight(h, h.spawn(ModEntities.GANADO.get(), new BlockPos(8, 1, 12)), h.spawn(net.minecraft.world.entity.EntityType.IRON_GOLEM, new BlockPos(14, 1, 12)), true); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void zombie(GameTestHelper h) { run(h, ModEntities.ZOMBIE.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void crimsonHead(GameTestHelper h) { run(h, ModEntities.CRIMSON_HEAD.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void cerberus(GameTestHelper h) { run(h, ModEntities.CERBERUS.get(), false, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void crow(GameTestHelper h) { run(h, ModEntities.CROW.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void hunter(GameTestHelper h) { run(h, ModEntities.HUNTER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void chimera(GameTestHelper h) { run(h, ModEntities.CHIMERA.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void giantSpider(GameTestHelper h) { run(h, ModEntities.GIANT_SPIDER.get(), false, 7); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void webSpinner(GameTestHelper h) { run(h, ModEntities.WEB_SPINNER.get(), false, 5); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void neptune(GameTestHelper h) { run(h, ModEntities.NEPTUNE.get(), true, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void yawn(GameTestHelper h) { run(h, ModEntities.YAWN.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void plant42(GameTestHelper h) { run(h, ModEntities.PLANT42.get(), false, 5); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void tyrant(GameTestHelper h) { run(h, ModEntities.TYRANT.get(), false, 5); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void lisaTrevor(GameTestHelper h) { run(h, ModEntities.LISA_TREVOR.get(), false, 6); }

    @GameTest(template = "empty", timeoutTicks = 700) public static void hunterLow(GameTestHelper h) { run(h, ModEntities.HUNTER.get(), false, 6, 0.3f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void giantSpiderLow(GameTestHelper h) { run(h, ModEntities.GIANT_SPIDER.get(), false, 7, 0.25f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void yawnLow(GameTestHelper h) { run(h, ModEntities.YAWN.get(), false, 6, 0.4f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void plant42Low(GameTestHelper h) { run(h, ModEntities.PLANT42.get(), false, 5, 0.4f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void tyrantLow(GameTestHelper h) { run(h, ModEntities.TYRANT.get(), false, 5, 0.5f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void tyrantLow2(GameTestHelper h) { run(h, ModEntities.TYRANT.get(), false, 5, 0.2f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void lisaLow(GameTestHelper h) { run(h, ModEntities.LISA_TREVOR.get(), false, 6, 0.4f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void neptuneLow(GameTestHelper h) { run(h, ModEntities.NEPTUNE.get(), true, 6, 0.2f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void zombieLow(GameTestHelper h) { run(h, ModEntities.ZOMBIE.get(), false, 4, 0.2f); }

    @GameTest(template = "empty", timeoutTicks = 700) public static void licker(GameTestHelper h) { run(h, ModEntities.LICKER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void mrX(GameTestHelper h) { run(h, ModEntities.MR_X.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void mrXLow(GameTestHelper h) { run(h, ModEntities.MR_X.get(), false, 6, 0.45f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void birkin(GameTestHelper h) { run(h, ModEntities.BIRKIN.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void birkinG2(GameTestHelper h) { run(h, ModEntities.BIRKIN.get(), false, 6, 0.6f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void birkinG3(GameTestHelper h) { run(h, ModEntities.BIRKIN.get(), false, 6, 0.25f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void alligator(GameTestHelper h) { run(h, ModEntities.ALLIGATOR.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void alligatorWet(GameTestHelper h) { run(h, ModEntities.ALLIGATOR.get(), true, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ivy(GameTestHelper h) { run(h, ModEntities.IVY.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ivyLow(GameTestHelper h) { run(h, ModEntities.IVY.get(), false, 4, 0.4f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void giantMoth(GameTestHelper h) { run(h, ModEntities.GIANT_MOTH.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void larva(GameTestHelper h) { run(h, ModEntities.LARVA.get(), false, 5); }

    @GameTest(template = "empty", timeoutTicks = 700) public static void nemesis(GameTestHelper h) { run(h, ModEntities.NEMESIS.get(), false, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void nemesisF1(GameTestHelper h) { run(h, ModEntities.NEMESIS.get(), false, 6, 0.55f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void nemesisF2(GameTestHelper h) { run(h, ModEntities.NEMESIS.get(), false, 6, 0.2f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void graveDigger(GameTestHelper h) { run(h, ModEntities.GRAVE_DIGGER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void drainDeimos(GameTestHelper h) { run(h, ModEntities.DRAIN_DEIMOS.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void brainSucker(GameTestHelper h) { run(h, ModEntities.BRAIN_SUCKER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void paleHead(GameTestHelper h) { run(h, ModEntities.PALE_HEAD.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void hunterBeta(GameTestHelper h) { run(h, ModEntities.HUNTER_BETA.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void hunterGamma(GameTestHelper h) { run(h, ModEntities.HUNTER_GAMMA.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ganado(GameTestHelper h) { run(h, ModEntities.GANADO.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ganadoLow(GameTestHelper h) { run(h, ModEntities.GANADO.get(), false, 4, 0.45f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void salvador(GameTestHelper h) { run(h, ModEntities.SALVADOR.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void garrador(GameTestHelper h) { run(h, ModEntities.GARRADOR.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void colmillos(GameTestHelper h) { run(h, ModEntities.COLMILLOS.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void novistador(GameTestHelper h) { run(h, ModEntities.NOVISTADOR.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void regenerador(GameTestHelper h) { run(h, ModEntities.REGENERADOR.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ironMaiden(GameTestHelper h) { run(h, ModEntities.IRON_MAIDEN.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void elGigante(GameTestHelper h) { run(h, ModEntities.EL_GIGANTE.get(), false, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void delLago(GameTestHelper h) { run(h, ModEntities.DEL_LAGO.get(), true, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void majini(GameTestHelper h) { run(h, ModEntities.MAJINI.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void zealot(GameTestHelper h) { run(h, ModEntities.ZEALOT.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void chainsawMajini(GameTestHelper h) { run(h, ModEntities.CHAINSAW_MAJINI.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void armadura(GameTestHelper h) { run(h, ModEntities.ARMADURA.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void kipepeo(GameTestHelper h) { run(h, ModEntities.KIPEPEO.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void reaper(GameTestHelper h) { run(h, ModEntities.REAPER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void verdugo(GameTestHelper h) { run(h, ModEntities.VERDUGO.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void wesker(GameTestHelper h) { run(h, ModEntities.WESKER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void adjule(GameTestHelper h) { run(h, ModEntities.ADJULE.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ndesu(GameTestHelper h) { run(h, ModEntities.NDESU.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void executionerMajini(GameTestHelper h) { run(h, ModEntities.EXECUTIONER_MAJINI.get(), false, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void krauser(GameTestHelper h) { run(h, ModEntities.KRAUSER.get(), false, 9); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void saddler(GameTestHelper h) { run(h, ModEntities.SADDLER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void saddlerLow(GameTestHelper h) { run(h, ModEntities.SADDLER.get(), false, 6, 0.5f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void simmons(GameTestHelper h) { run(h, ModEntities.SIMMONS.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void salazar(GameTestHelper h) { run(h, ModEntities.SALAZAR.get(), false, 5); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void uroboros(GameTestHelper h) { run(h, ModEntities.UROBOROS.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void mendez(GameTestHelper h) { run(h, ModEntities.MENDEZ.get(), false, 7); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ogroman(GameTestHelper h) { run(h, ModEntities.OGROMAN.get(), false, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void jill(GameTestHelper h) { run(h, ModEntities.JILL.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void ustanak(GameTestHelper h) { run(h, ModEntities.USTANAK.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void napad(GameTestHelper h) { run(h, ModEntities.NAPAD.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void javo(GameTestHelper h) { run(h, ModEntities.JAVO.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void javoLow(GameTestHelper h) { run(h, ModEntities.JAVO.get(), false, 4, 0.5f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void gatlingMajini(GameTestHelper h) { run(h, ModEntities.GATLING_MAJINI.get(), false, 9); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void molded(GameTestHelper h) { run(h, ModEntities.MOLDED.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void moldedLow(GameTestHelper h) { run(h, ModEntities.MOLDED.get(), false, 4, 0.5f); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void fatMolded(GameTestHelper h) { run(h, ModEntities.FAT_MOLDED.get(), false, 4); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void jackBaker(GameTestHelper h) { run(h, ModEntities.JACK_BAKER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void eveline(GameTestHelper h) { run(h, ModEntities.EVELINE.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void lycan(GameTestHelper h) { run(h, ModEntities.LYCAN.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void dimitrescu(GameTestHelper h) { run(h, ModEntities.DIMITRESCU.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void daughter(GameTestHelper h) { run(h, ModEntities.DAUGHTER.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void heisenberg(GameTestHelper h) { run(h, ModEntities.HEISENBERG.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void sturm(GameTestHelper h) { run(h, ModEntities.STURM.get(), false, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void urias(GameTestHelper h) { run(h, ModEntities.URIAS.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void soldat(GameTestHelper h) { run(h, ModEntities.SOLDAT.get(), false, 9); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void moreau(GameTestHelper h) { run(h, ModEntities.MOREAU.get(), true, 8); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void marguerite(GameTestHelper h) { run(h, ModEntities.MARGUERITE.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void miranda(GameTestHelper h) { run(h, ModEntities.MIRANDA.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void haos(GameTestHelper h) { run(h, ModEntities.HAOS.get(), false, 6); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void gnezdo(GameTestHelper h) { run(h, ModEntities.GNEZDO.get(), false, 5); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbZombie(GameTestHelper h) { run(h, ModEntities.ZOMBIE.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbHunter(GameTestHelper h) { run(h, ModEntities.HUNTER.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbMrX(GameTestHelper h) { run(h, ModEntities.MR_X.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbPlant42(GameTestHelper h) { run(h, ModEntities.PLANT42.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbLicker(GameTestHelper h) { run(h, ModEntities.LICKER.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbNemesis(GameTestHelper h) { run(h, ModEntities.NEMESIS.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbCerberus(GameTestHelper h) { run(h, ModEntities.CERBERUS.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbYawn(GameTestHelper h) { run(h, ModEntities.YAWN.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbTyrant(GameTestHelper h) { run(h, ModEntities.TYRANT.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbGarrador(GameTestHelper h) { run(h, ModEntities.GARRADOR.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbElGigante(GameTestHelper h) { run(h, ModEntities.EL_GIGANTE.get(), false, 0); }
    @GameTest(template = "empty", timeoutTicks = 700) public static void pbWesker(GameTestHelper h) { run(h, ModEntities.WESKER.get(), false, 0); }
}
