package com.reevil;

import com.reevil.entity.BioMonster;
import com.reevil.entity.Factions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Cross-faction + vanilla hostility. */
@Mod.EventBusSubscriber(modid = ReEvilMod.MODID)
public class FactionEvents {
    @SubscribeEvent
    public static void join(EntityJoinLevelEvent e) {
        if (e.getLevel().isClientSide || !(e.getEntity() instanceof Mob mob) || !ReConfig.FACTIONS.get()) return;
        if (mob instanceof BioMonster bio) {
            bio.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(bio, LivingEntity.class, 12, true, false,
                    t -> !(t instanceof Player) && Factions.hostile(bio, t)));
            return;
        }
        mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, BioMonster.class, 12, true, false, t -> true));
        if (mob instanceof PathfinderMob pm) {
            boolean armed = pm.goalSelector.getAvailableGoals().stream().anyMatch(g ->
                    g.getGoal() instanceof MeleeAttackGoal || g.getGoal() instanceof RangedAttackGoal);
            if (armed) return;
            if (pm.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) pm.goalSelector.addGoal(2, new MeleeAttackGoal(pm, 1.1, true));
            else pm.goalSelector.addGoal(2, new AvoidEntityGoal<>(pm, BioMonster.class, 10f, 1.1, 1.4));
        }
    }
}
