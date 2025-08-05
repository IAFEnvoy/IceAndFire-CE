package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.EntityDragonEgg;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class DragonAIMate extends Goal {
    private static final BlockState NEST = IafBlocks.NEST.get().getDefaultState();
    final World theWorld;
    final double moveSpeed;
    private final EntityDragonBase dragon;
    int spawnBabyDelay;
    private EntityDragonBase targetMate;

    public DragonAIMate(EntityDragonBase dragon, double speedIn) {
        this.dragon = dragon;
        this.theWorld = dragon.getWorld();
        this.moveSpeed = speedIn;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.dragon.isInLove() || !this.dragon.canMove()) return false;
        else {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    public boolean continueExecuting() {
        return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    /**
     * Resets the task
     */
    @Override
    public void stop() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    @Override
    public void tick() {
        this.dragon.getLookControl().lookAt(this.targetMate, 10.0F, this.dragon.getMaxLookPitchChange());
        this.dragon.getNavigation().startMovingTo(this.targetMate.getX(), this.targetMate.getY(), this.targetMate.getZ(), this.moveSpeed);
        this.dragon.setFlying(false);
        this.dragon.setHovering(false);
        ++this.spawnBabyDelay;
        if (this.spawnBabyDelay >= 60 && this.dragon.distanceTo(this.targetMate) < 35)
            this.spawnBaby();
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityDragonBase getNearbyMate() {
        List<? extends EntityDragonBase> list = this.theWorld.getNonSpectatingEntities(this.dragon.getClass(), this.dragon.getBoundingBox().expand(180.0D, 180.0D, 180.0D));
        double d0 = Double.MAX_VALUE;
        EntityDragonBase mate = null;
        for (EntityDragonBase partner : list)
            if (this.dragon.canBreedWith(partner)) {
                double d1 = this.dragon.squaredDistanceTo(partner);
                if (d1 < d0) { // find min distance
                    mate = partner;
                    d0 = d1;
                }
            }
        return mate;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby() {
        EntityDragonEgg egg = this.dragon.createEgg();
        if (egg != null) {

            this.dragon.setBreedingAge(6000);
            this.targetMate.setBreedingAge(6000);
            this.dragon.resetLoveTicks();
            this.targetMate.resetLoveTicks();
            int nestX = (int) (this.dragon.isMale() ? this.targetMate.getX() : this.dragon.getX());
            int nestY = (int) (this.dragon.isMale() ? this.targetMate.getY() : this.dragon.getY()) - 1;
            int nestZ = (int) (this.dragon.isMale() ? this.targetMate.getZ() : this.dragon.getZ());

            egg.refreshPositionAndAngles(nestX - 0.5F, nestY + 1F, nestZ - 0.5F, 0.0F, 0.0F);
            this.theWorld.spawnEntity(egg);
            Random random = this.dragon.getRandom();

            for (int i = 0; i < 17; ++i) {
                final double d0 = random.nextGaussian() * 0.02D;
                final double d1 = random.nextGaussian() * 0.02D;
                final double d2 = random.nextGaussian() * 0.02D;
                final double d3 = random.nextDouble() * this.dragon.getWidth() * 2.0D - this.dragon.getWidth();
                final double d4 = 0.5D + random.nextDouble() * this.dragon.getHeight();
                final double d5 = random.nextDouble() * this.dragon.getWidth() * 2.0D - this.dragon.getWidth();
                this.theWorld.addParticle(ParticleTypes.HEART, this.dragon.getX() + d3, this.dragon.getY() + d4, this.dragon.getZ() + d5, d0, d1, d2);
            }
            BlockPos eggPos = new BlockPos(nestX - 2, nestY, nestZ - 2);
            BlockPos dirtPos = eggPos.add(1, 0, 1);

            for (int x = 0; x < 3; x++)
                for (int z = 0; z < 3; z++) {
                    BlockPos add = eggPos.add(x, 0, z);
                    BlockState prevState = this.theWorld.getBlockState(add);
                    if (prevState.isReplaceable() || this.theWorld.getBlockState(add).isIn(BlockTags.DIRT) || this.theWorld.getBlockState(add).getHardness(this.theWorld, add) < 5F || this.theWorld.getBlockState(add).getHardness(this.theWorld, add) >= 0F)
                        this.theWorld.setBlockState(add, NEST);
                }
            if (this.theWorld.getBlockState(dirtPos).isReplaceable() || this.theWorld.getBlockState(dirtPos) == NEST)
                this.theWorld.setBlockState(dirtPos, Blocks.DIRT_PATH.getDefaultState());
            if (this.theWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))
                this.theWorld.spawnEntity(new ExperienceOrbEntity(this.theWorld, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), random.nextInt(15) + 10));
        }
    }
}