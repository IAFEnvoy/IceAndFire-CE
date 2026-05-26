package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.CyclopsEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import org.jetbrains.annotations.NotNull;

public class CyclopsAIAttackMeleeGoal extends MeleeAttackGoal {
    public CyclopsAIAttackMeleeGoal(CyclopsEntity creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected void checkAndPerformAttack(@NotNull LivingEntity entity) {
        float distance = this.mob.distanceTo(entity);
        final double d0 = Math.sqrt(this.getSquaredMaxAttackDistance(entity));
        if (this.isCyclopsBlinded() && distance >= 6) {
            this.stop();
            return;
        }
        if (distance <= d0) {
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(entity);
        }
    }

    private boolean isCyclopsBlinded() {
        return this.mob instanceof CyclopsEntity cyclops && cyclops.isBlinded();
    }

    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + entity.getBbWidth();
    }
}
