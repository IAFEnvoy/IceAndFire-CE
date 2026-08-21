package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.util.IDreadMob;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import java.util.function.Predicate;

public class DreadAITargetNonDreadGoal extends NearestAttackableTargetGoal<LivingEntity> {
    public DreadAITargetNonDreadGoal(Mob entityIn, Class<LivingEntity> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 10, checkSight, false, (target, level) ->
                !(target instanceof IDreadMob) && DragonUtils.isAlive(target) && targetSelector.test(target));
    }
}
