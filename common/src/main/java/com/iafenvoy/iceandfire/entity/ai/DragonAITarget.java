package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.uranus.object.item.FoodUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.function.Predicate;

public class DragonAITarget<T extends LivingEntity> extends ActiveTargetGoal<T> {
    private final EntityDragonBase dragon;

    public DragonAITarget(EntityDragonBase entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 3, checkSight, false, targetSelector);
        this.setControls(EnumSet.of(Control.TARGET));
        this.dragon = entityIn;
    }

    @Override
    public boolean canStart() {
        if (this.dragon.getCommand() == 1 || this.dragon.getCommand() == 2 || this.dragon.isSleeping())
            return false;
        if (!this.dragon.isTamed() && this.dragon.lookingForRoostAIFlag)
            return false;
        if (!super.canStart() || this.targetEntity == null) return false;
        if (!this.targetEntity.getClass().equals(this.dragon.getClass())) {

            final float dragonSize = Math.max(this.dragon.getWidth(), this.dragon.getWidth() * this.dragon.getRenderSize());
            if (dragonSize >= this.targetEntity.getWidth()) {
                if (this.targetEntity instanceof PlayerEntity && !this.dragon.isTamed()) return true;
                if (this.targetEntity instanceof EntityDragonBase d) {
                    if (d.getOwner() != null && this.dragon.getOwner() != null && this.dragon.isOwner(d.getOwner()))
                        return false;
                    return !d.isModelDead();
                }
                if (this.targetEntity instanceof PlayerEntity && this.dragon.isTamed()) return false;
                else {
                    if (!this.dragon.isOwner(this.targetEntity) && FoodUtils.getFoodPoints(this.targetEntity) > 0 && this.dragon.canMove() && (this.dragon.getHunger() < 90 || !this.dragon.isTamed() && this.targetEntity instanceof PlayerEntity)) {
                        if (this.dragon.isTamed())
                            return DragonUtils.canTameDragonAttack(this.dragon, this.targetEntity);
                        else return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected Box getSearchBox(double targetDistance) {
        return this.dragon.getBoundingBox().expand(targetDistance, targetDistance, targetDistance);
    }

    @Override
    protected double getFollowRange() {
        EntityAttributeInstance iattributeinstance = this.mob.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE);
        return iattributeinstance == null ? 64.0D : iattributeinstance.getValue();
    }
}
