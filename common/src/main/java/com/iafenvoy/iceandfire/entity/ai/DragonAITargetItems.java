package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.entity.EntityIceDragon;
import com.iafenvoy.iceandfire.util.IafMath;
import com.iafenvoy.uranus.object.item.FoodUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class DragonAITargetItems<T extends ItemEntity> extends TrackTargetGoal {
    private final SearchCooldown searchCooldown = new SearchCooldown();
    protected final Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    private final int targetChance;
    private final boolean prioritizeItems;
    private final boolean isIce;

    protected ItemEntity targetEntity;

    private List<ItemEntity> list = IafMath.emptyItemEntityList;

    public DragonAITargetItems(EntityDragonBase creature, boolean checkSight) {
        this(creature, 20, checkSight, false, false);
    }

    public DragonAITargetItems(EntityDragonBase creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 20, checkSight, onlyNearby, false);
    }

    public DragonAITargetItems(EntityDragonBase creature, int chance, boolean checkSight, boolean onlyNearby) {
        this(creature, chance, checkSight, onlyNearby, false);
    }

    public DragonAITargetItems(EntityDragonBase creature, int chance, boolean checkSight, boolean onlyNearby, boolean prioritizeItems) {
        super(creature, checkSight, onlyNearby);
        this.setControls(EnumSet.of(Control.TARGET));
        this.isIce = creature instanceof EntityIceDragon;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new Sorter(creature);
        this.setControls(EnumSet.of(Control.MOVE));
        this.targetEntitySelector = (Predicate<ItemEntity>) item -> item != null && !item.getStack().isEmpty() && FoodUtils.getFoodPoints(item.getStack(), true, this.isIce) > 0;
        this.prioritizeItems = prioritizeItems;
    }

    @Override
    public boolean canStart() {
        final EntityDragonBase dragon = (EntityDragonBase) this.mob;

        if (this.prioritizeItems && dragon.getHunger() >= 60) return false;

        if (dragon.getHunger() >= 100 || !dragon.canMove() || (this.targetChance > 0 && this.mob.getRandom().nextInt(10) != 0)) {
            this.list = IafMath.emptyItemEntityList;
            return false;
        } else return this.updateList();
    }

    private boolean updateList() {
        if (!this.searchCooldown.shouldSearch(this.mob.getWorld().getTime(), this.mob.getId())) return false;
        this.list = this.mob.getWorld().getEntitiesByClass(ItemEntity.class, this.getTargetableArea(this.getFollowRange()), this.targetEntitySelector);

        if (this.list.isEmpty()) return false;
        else {
            this.list.sort(this.theNearestAttackableTargetSorter);
            this.targetEntity = this.list.get(0);
            return true;
        }
    }

    protected Box getTargetableArea(double targetDistance) {
        return this.mob.getBoundingBox().expand(targetDistance, 4.0D, targetDistance);
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 1);
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || !this.targetEntity.isAlive()) this.stop();
        else if (this.mob.squaredDistanceTo(this.targetEntity) < this.mob.getWidth() * 2 + this.mob.getHeight() / 2 || (this.mob instanceof EntityDragonBase dragon && dragon.getHeadPosition().squaredDistanceTo(this.targetEntity.getPos()) < this.mob.getHeight())) {
            this.mob.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            final int hunger = FoodUtils.getFoodPoints(this.targetEntity.getStack(), true, this.isIce);
            final EntityDragonBase dragon = ((EntityDragonBase) this.mob);
            dragon.setHunger(Math.min(100, dragon.getHunger() + hunger));
            this.targetEntity.getStack();
            this.mob.setHealth(Math.min(this.mob.getMaxHealth(), (int) (this.mob.getHealth() + FoodUtils.getFoodPoints(this.targetEntity.getStack(), true, this.isIce))));
            if (EntityDragonBase.ANIMATION_EAT != null)
                dragon.setAnimation(EntityDragonBase.ANIMATION_EAT);
            for (int i = 0; i < 4; i++)
                dragon.spawnItemCrackParticles(this.targetEntity.getStack().getItem());
            this.targetEntity.getStack().decrement(1);
            this.stop();
        } else this.updateList();
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    public static class Sorter implements Comparator<Entity> {

        private final Entity theEntity;

        public Sorter(Entity theEntityIn) {
            this.theEntity = theEntityIn;
        }

        @Override
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            final double d0 = this.theEntity.squaredDistanceTo(p_compare_1_);
            final double d1 = this.theEntity.squaredDistanceTo(p_compare_2_);
            return Double.compare(d0, d1);
        }
    }
}
