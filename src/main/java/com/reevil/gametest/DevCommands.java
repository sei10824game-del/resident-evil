package com.reevil.gametest;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.reevil.ReEvilMod;
import com.reevil.entity.AttackMove;
import com.reevil.entity.BioMonster;
import net.minecraft.commands.Commands;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;

/** Dev only (excluded from the jar): /rmove <id> plays an attack of the nearest creature, /rform <n> sets its form, /rstop cancels. */
@Mod.EventBusSubscriber(modid = ReEvilMod.MODID)
public final class DevCommands {
    private static BioMonster nearest(net.minecraft.commands.CommandSourceStack s) {
        return s.getLevel().getEntitiesOfClass(BioMonster.class, new AABB(s.getPosition(), s.getPosition()).inflate(30)).stream()
            .min(Comparator.comparingDouble(e -> e.distanceToSqr(s.getPosition()))).orElse(null);
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal("rmove").requires(s -> s.hasPermission(2)).then(Commands.argument("id", IntegerArgumentType.integer()).executes(c -> {
            BioMonster m = nearest(c.getSource());
            int id = IntegerArgumentType.getInteger(c, "id");
            if (m == null) return 0;
            m.cancelMove();
            for (AttackMove mv : m.allMoves()) if (mv.id == id) { m.startMove(mv); return 1; }
            return 0;
        })));
        e.getDispatcher().register(Commands.literal("rform").requires(s -> s.hasPermission(2)).then(Commands.argument("n", IntegerArgumentType.integer()).executes(c -> {
            BioMonster m = nearest(c.getSource());
            if (m == null) return 0;
            m.setForm(IntegerArgumentType.getInteger(c, "n"));
            return 1;
        })));
        e.getDispatcher().register(Commands.literal("rstop").requires(s -> s.hasPermission(2)).executes(c -> { BioMonster m = nearest(c.getSource()); if (m != null) m.cancelMove(); return 1; }));
    }
}
