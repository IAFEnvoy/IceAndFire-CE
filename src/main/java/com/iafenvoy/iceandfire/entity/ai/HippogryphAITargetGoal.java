package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class HippogryphAITargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final HippogryphEntity hippogryph;

    public HippogryphAITargetGoal(HippogryphEntity entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetPredicate) {
        super(entityIn, classTarget, 20, checkSight, false, targetPredicate);
        this.hippogryph = entityIn;
    }

    public HippogryphAITargetGoal(HippogryphEntity entityIn, Class<T> classTarget, int i, boolean checkSight, Predicate<LivingEntity> targetPredicate) {
        super(entityIn, classTarget, i, checkSight, false, targetPredicate);
        this.hippogryph = entityIn;
    }


    @Override
    public boolean canUse() {
        if (super.canUse() && this.target != null && !this.target.getClass().equals(this.hippogryph.getClass())) {
            if (this.hippogryph.getBbWidth() >= this.target.getBbWidth()) {
                if (this.target instanceof Player)
                    return !this.hippogryph.isTame();
                else {
                    if (!this.hippogryph.isOwnedBy(this.target) && this.hippogryph.canMove() && this.target instanceof Animal) {
                        if (this.hippogryph.isTame())
                            return DragonUtils.canTameDragonAttack(this.hippogryph, this.target);
                        else return true;
                    }
                }
            }
        }
        return false;
    }
}