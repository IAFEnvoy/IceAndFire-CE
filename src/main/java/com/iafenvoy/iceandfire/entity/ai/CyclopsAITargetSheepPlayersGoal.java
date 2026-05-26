package com.iafenvoy.iceandfire.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import java.util.EnumSet;

public class CyclopsAITargetSheepPlayersGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public CyclopsAITargetSheepPlayersGoal(Mob goalOwnerIn, Class<T> targetClassIn, boolean checkSight) {
        super(goalOwnerIn, targetClassIn, 0, checkSight, true, livingEntity -> {
            return false; //TODO Sheep hunt cyclops
        });
        this.setFlags(EnumSet.of(Flag.TARGET));
    }
}