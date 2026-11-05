package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityCyclops;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class EntitySheepAIFollowCyclops extends Goal {
    final AnimalEntity childAnimal;
    final double moveSpeed;
    EntityCyclops cyclops;
    private int delayCounter;

    public EntitySheepAIFollowCyclops(AnimalEntity animal, double speed) {
        this.childAnimal = animal;
        this.moveSpeed = speed;
    }

    @Override
    public boolean canStart() {
        List<EntityCyclops> list = this.childAnimal.getWorld().getNonSpectatingEntities(EntityCyclops.class, this.childAnimal.getBoundingBox().expand(16.0D, 8.0D, 16.0D));
        EntityCyclops cyclops = null;
        double d0 = Double.MAX_VALUE;

        for (EntityCyclops cyclops1 : list) {
            final double d1 = this.childAnimal.squaredDistanceTo(cyclops1);
            if (d1 <= d0) {
                d0 = d1;
                cyclops = cyclops1;
            }
        }

        if (cyclops == null) return false;
        else if (d0 < 10.0D) return false;
        else {
            this.cyclops = cyclops;
            return true;
        }
    }


    @Override
    public boolean shouldContinue() {
        if (this.cyclops == null || !this.cyclops.isAlive()) return false;
        else {
            final double d0 = this.childAnimal.squaredDistanceTo(this.cyclops);
            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }


    @Override
    public void start() {
        this.delayCounter = 0;
    }

    @Override
    public void stop() {
        this.cyclops = null;
    }

    @Override
    public void tick() {
        if (--this.delayCounter <= 0) {
            this.delayCounter = this.getTickCount(10);
            if (this.childAnimal.squaredDistanceTo(this.cyclops) > 10) {
                Path path = this.getPathToLivingEntity(this.childAnimal, this.cyclops);
                if (path != null)
                    this.childAnimal.getNavigation().startMovingAlong(path, this.moveSpeed);
            }
        }
    }

    public Path getPathToLivingEntity(AnimalEntity entityIn, EntityCyclops cyclops) {
        EntityNavigation navi = entityIn.getNavigation();
        Vec3d Vector3d = NoPenaltyTargeting.findTo(entityIn, 2, 7, cyclops.getPos(), (float) Math.PI / 2F);
        if (Vector3d != null) {
            BlockPos blockpos = BlockPos.ofFloored(Vector3d);
            return navi.findPathTo(blockpos, 0);
        }
        return null;
    }
}
