package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.uranus.object.item.FoodUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Predicate;

public class DragonAITargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final DragonBaseEntity dragon;

    public DragonAITargetGoal(DragonBaseEntity entityIn, Class<T> classTarget, boolean checkSight, Predicate<LivingEntity> targetSelector) {
        super(entityIn, classTarget, 3, checkSight, false, targetSelector);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.dragon = entityIn;
    }

    @Override
    public boolean canUse() {
        if (this.dragon.getCommand() == 1 || this.dragon.getCommand() == 2 || this.dragon.isSleeping())
            return false;
        if (!this.dragon.isTame() && this.dragon.lookingForRoostAIFlag)
            return false;
        if (this.target != null && !this.target.getClass().equals(this.dragon.getClass())) {
            if (!super.canUse())
                return false;

            final float dragonSize = Math.max(this.dragon.getBbWidth(), this.dragon.getBbWidth() * this.dragon.getRenderSize());
            if (dragonSize >= this.target.getBbWidth()) {
                switch (this.target) {
                    case Player ignored when !this.dragon.isTame() -> {
                        return true;
                    }
                    case DragonBaseEntity d -> {
                        if (d.getOwner() != null && this.dragon.getOwner() != null && this.dragon.isOwnedBy(d.getOwner()))
                            return false;
                        return !d.isModelDead();
                    }
                    case Player ignored when this.dragon.isTame() -> {
                        return false;
                    }
                    default -> {
                        if (!this.dragon.isOwnedBy(this.target) && FoodUtils.getFoodPoints(this.target) > 0 && this.dragon.canMove() && (this.dragon.getHunger() < 90 || !this.dragon.isTame() && this.target instanceof Player)) {
                            if (this.dragon.isTame())
                                return DragonUtils.canTameDragonAttack(this.dragon, this.target);
                            else return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected @NotNull AABB getTargetSearchArea(double targetDistance) {
        return this.dragon.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
    }

    @Override
    protected double getFollowDistance() {
        AttributeInstance iattributeinstance = this.mob.getAttribute(Attributes.FOLLOW_RANGE);
        return iattributeinstance == null ? 64.0D : iattributeinstance.getValue();
    }
}