package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityAmphithere;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class AmphithereAITargetItems<T extends ItemEntity> extends TrackTargetGoal {
    private final SearchCooldown searchCooldown = new SearchCooldown();
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected final int targetChance;
    protected ItemEntity targetEntity;
    private List<ItemEntity> list = IafMath.emptyItemEntityList;

    public AmphithereAITargetItems(MobEntity creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public AmphithereAITargetItems(MobEntity creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 20, checkSight, onlyNearby);
    }

    public AmphithereAITargetItems(MobEntity creature, int chance, boolean checkSight, boolean onlyNearby) {
        super(creature, checkSight, onlyNearby);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
        this.targetChance = chance;
        this.targetEntitySelector = (Predicate<ItemEntity>) item -> item != null && !item.getStack().isEmpty() && item.getStack().isIn(IafItemTags.HEAL_AMPITHERE);
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0) return false;

        if (!((EntityAmphithere) this.mob).canMove()) {
            this.list = IafMath.emptyItemEntityList;
            return false;
        }

        // If the target entity already is what we want skip AABB
        if (this.targetEntitySelector.test(this.targetEntity)) return true;

        if (!this.searchCooldown.shouldSearch(this.mob.getWorld().getTime(), this.mob.getId())) return false;
        this.list = this.mob.getWorld().getEntitiesByClass(ItemEntity.class, this.getTargetableArea(this.getFollowRange()), this.targetEntitySelector);

        if (this.list.isEmpty()) return false;

        this.list.sort(this.theNearestAttackableTargetSorter);
        this.targetEntity = this.list.get(0);
        return true;
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
    public void stop() {
        this.targetEntity = null;
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || !this.targetEntity.isAlive()) this.stop();
        if (this.targetEntity != null && this.targetEntity.isAlive() && this.mob.squaredDistanceTo(this.targetEntity) < 1) {
            EntityAmphithere hippo = (EntityAmphithere) this.mob;
            this.targetEntity.getStack().decrement(1);
            this.mob.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            hippo.heal(5);
            this.stop();
        }
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
