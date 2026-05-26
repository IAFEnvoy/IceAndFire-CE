package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.PixieEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PixieAIMoveRandomGoal extends Goal {
    final PixieEntity pixie;
    final RandomSource random;
    BlockPos target;

    public PixieAIMoveRandomGoal(PixieEntity pixieEntityIn) {
        this.pixie = pixieEntityIn;
        this.random = pixieEntityIn.getRandom();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        this.target = PixieEntity.getPositionRelativetoGround(this.pixie, this.pixie.level(), this.pixie.getX() + this.random.nextInt(15) - 7, this.pixie.getZ() + this.random.nextInt(15) - 7, this.random);
        return !this.pixie.isOwnerClose() && !this.pixie.isPixieSitting() && this.isDirectPathBetweenPoints(this.pixie.blockPosition(), this.target) && !this.pixie.getMoveControl().hasWanted() && this.random.nextInt(4) == 0 && this.pixie.getHousePos() == null;
    }

    protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32) {
        return this.pixie.level().clip(
                new ClipContext(new Vec3(posVec31.getX() + 0.5D, posVec31.getY() + 0.5D, posVec31.getZ() + 0.5D),
                        new Vec3(posVec32.getX() + 0.5D, posVec32.getY() + this.pixie.getBbHeight() * 0.5D, posVec32.getZ() + 0.5D),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.pixie)).getType() == HitResult.Type.MISS;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void tick() {
        if (!this.isDirectPathBetweenPoints(this.pixie.blockPosition(), this.target))
            this.target = PixieEntity.getPositionRelativetoGround(this.pixie, this.pixie.level(), this.pixie.getX() + this.random.nextInt(15) - 7, this.pixie.getZ() + this.random.nextInt(15) - 7, this.random);
        if (this.pixie.level().isEmptyBlock(this.target)) {
            this.pixie.getMoveControl().setWantedPosition(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 0.25D);
            if (this.pixie.getTarget() == null)
                this.pixie.getLookControl().setLookAt(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 180.0F, 20.0F);
        }
    }
}
