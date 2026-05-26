package com.iafenvoy.iceandfire.entity.ai;

import com.google.common.base.Predicate;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.GorgonEntity;
import com.iafenvoy.iceandfire.entity.StymphalianBirdEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class StymphalianBirdAITargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final StymphalianBirdEntity bird;

    public StymphalianBirdAITargetGoal(StymphalianBirdEntity entityIn, Class<LivingEntity> classTarget, boolean checkSight) {
        super(entityIn, classTarget, 0, checkSight, false, (Predicate<LivingEntity>) entity -> {
            if (GorgonEntity.isStoneMob(entity)) return false;
            if (entity instanceof Player && !((Player) entity).isCreative() || entity instanceof AbstractVillager || entity instanceof AbstractGolem)
                return true;
            if (!(entity instanceof Animal)) return false;
            return IafCommonConfig.INSTANCE.stymphalianBird.attackAnimals.getValue();
        });
        this.bird = entityIn;
    }

    @Override
    public boolean canUse() {
        boolean supe = super.canUse();
        if (this.target != null && this.bird.getVictor() != null && this.bird.getVictor().getUUID().equals(this.target.getUUID()))
            return false;
        return supe && this.target != null && !this.target.getClass().equals(this.bird.getClass());
    }

    @Override
    protected @NotNull AABB getTargetSearchArea(double targetDistance) {
        return this.bird.getBoundingBox().inflate(targetDistance, targetDistance, targetDistance);
    }
}