package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.function.Predicate;

public class CockatriceAITargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final CockatriceEntity cockatrice;

    public CockatriceAITargetGoal(CockatriceEntity entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 0, checkSight, false, targetSelector);
        this.cockatrice = entityIn;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(20) != 0 || this.cockatrice.level().getDifficulty() == Difficulty.PEACEFUL)
            return false;
        if (super.canUse() && this.target != null && !this.target.getClass().equals(this.cockatrice.getClass())) {
            if (this.target instanceof Player && !this.cockatrice.isOwnedBy(this.target))
                return !this.cockatrice.isTame();
            else
                return !this.cockatrice.isOwnedBy(this.target) && this.cockatrice.canMove();
        }
        return false;
    }
}