package com.reevil.entity.huntergamma;

import com.reevil.entity.ai.BioCombatGoal;
import com.reevil.entity.licker.LickerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

/**
 * Hunter Gamma (RE3 remake): the amphibious frog-like Hunter with a long prehensile tongue. Uses the Licker's tongue lash / snare-and-reel / claw / pounce
 * move set, but SEES (no hearing-only targeting), swims fast, and does not hang from ceilings.
 */
public class HunterGammaEntity extends LickerEntity {
    public HunterGammaEntity(EntityType<HunterGammaEntity> type, Level level) { super(unchecked(type), level); xpReward = 22; }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static EntityType<LickerEntity> unchecked(EntityType<HunterGammaEntity> t) { return (EntityType) t; }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 70.0)
            .add(Attributes.MOVEMENT_SPEED, 0.32)
            .add(ForgeMod.SWIM_SPEED.get(), 2.0)
            .add(Attributes.ATTACK_DAMAGE, 6.0)
            .add(Attributes.FOLLOW_RANGE, 28.0)
            .add(Attributes.ARMOR, 3.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new BioCombatGoal(this, 1.0));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override protected boolean canBeta() { return false; }
    @Override protected boolean canCeiling() { return false; }
    @Override public String getTextureSuffix() { return ""; }
    @Override public boolean canBreatheUnderwater() { return true; }
}
