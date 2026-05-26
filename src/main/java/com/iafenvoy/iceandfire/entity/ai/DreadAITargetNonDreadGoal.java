package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.util.IDreadMob;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DreadAITargetNonDreadGoal extends NearestAttackableTargetGoal<LivingEntity> {
    public DreadAITargetNonDreadGoal(Mob entityIn, Class<LivingEntity> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 0, checkSight, false, targetSelector);
    }

    @Override
    protected boolean canAttack(LivingEntity target, @NotNull TargetingConditions targetPredicate) {
        if (super.canAttack(target, targetPredicate))
            return !(target instanceof IDreadMob) && DragonUtils.isAlive(target);
        return false;
    }
}
