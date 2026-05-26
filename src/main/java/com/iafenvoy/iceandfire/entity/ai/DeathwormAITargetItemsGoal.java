package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class DeathwormAITargetItemsGoal<T extends ItemEntity> extends TargetGoal {
    protected final DragonAITargetItemsGoal.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected final int targetChance;
    private final DeathWormEntity worm;
    private final List<ItemEntity> list = IafMath.emptyItemEntityList;
    protected ItemEntity targetEntity;

    public DeathwormAITargetItemsGoal(DeathWormEntity creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public DeathwormAITargetItemsGoal(DeathWormEntity creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 10, checkSight, onlyNearby, null);
    }

    public DeathwormAITargetItemsGoal(DeathWormEntity creature, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.worm = creature;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new DragonAITargetItemsGoal.Sorter(creature);
        this.targetEntitySelector = (Predicate<ItemEntity>) item -> item != null && !item.getItem().isEmpty() && item.getItem().getItem() == Blocks.TNT.asItem() && item.level().getBlockState(item.blockPosition().below()).is(BlockTags.SAND);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0)
            return false;
        List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);
        if (list.isEmpty()) return false;
        else {
            list.sort(this.theNearestAttackableTargetSorter);
            this.targetEntity = list.getFirst();
            return true;
        }
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1);
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        Entity itemTarget = this.targetEntity;

        if (itemTarget == null) return false;
        else if (!itemTarget.isAlive()) return false;
        else {
            Team team = this.mob.getTeam();
            Team team1 = itemTarget.getTeam();
            if (team != null && team1 == team) return false;
            else {
                double d0 = this.getFollowDistance();
                return !(this.mob.distanceToSqr(itemTarget) > d0 * d0);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || !this.targetEntity.isAlive()) this.stop();
        else if (this.mob.distanceToSqr(this.targetEntity) < 1) {
            DeathWormEntity deathWorm = (DeathWormEntity) this.mob;
            this.targetEntity.getItem().shrink(1);
            this.mob.playSound(SoundEvents.GENERIC_EAT, 1, 1);
            deathWorm.setAnimation(DeathWormEntity.ANIMATION_BITE);
            Player thrower = null;
            if (this.targetEntity.getOwner() != null)
                thrower = this.targetEntity.level().getPlayerByUUID(this.targetEntity.getOwner().getUUID());
            deathWorm.setExplosive(true, thrower);
            this.stop();
        }
        if (this.worm.getNavigation().isDone())
            this.worm.getNavigation().moveTo(this.targetEntity, 1.0F);
    }
}
