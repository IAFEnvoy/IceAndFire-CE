package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityPixie;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PixieAIPickupItem<T extends ItemEntity> extends TrackTargetGoal {
    private final SearchCooldown searchCooldown = new SearchCooldown();
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected ItemEntity targetEntity;

    private List<ItemEntity> list = IafMath.emptyItemEntityList;

    public PixieAIPickupItem(EntityPixie creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public PixieAIPickupItem(EntityPixie creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 20, checkSight, onlyNearby, null);
    }

    public PixieAIPickupItem(EntityPixie creature, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);

        this.targetEntitySelector = (Predicate<ItemEntity>) item -> item != null && !item.getStack().isEmpty() && (item.getStack().getItem() == Items.CAKE
                && !creature.isTamed()
                || item.getStack().getItem() == Items.SUGAR && creature.isTamed()
                && creature.getHealth() < creature.getMaxHealth());
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        EntityPixie pixie = (EntityPixie) this.mob;
        if (pixie.isPixieSitting()) return false;

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
        return this.mob.getBoundingBox().expand(targetDistance, 4.0, targetDistance);
    }

    @Override
    public void start() {
        // behaviour changed to the same as AmphitereAITargetItems
        this.mob.getMoveControl().moveTo(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 0.25D);

        LivingEntity attackTarget = this.mob.getTarget();
        if (attackTarget == null)
            this.mob.getLookControl().lookAt(this.targetEntity.getX(), this.targetEntity.getY(), this.targetEntity.getZ(), 180.0F, 20.0F);
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.targetEntity == null || !this.targetEntity.isAlive())
            this.stop();
        else if (this.mob.squaredDistanceTo(this.targetEntity) < 1) {
            EntityPixie pixie = (EntityPixie) this.mob;
            if (this.targetEntity.getStack() != null && this.targetEntity.getStack().getItem() != null)
                if (this.targetEntity.getStack().isIn(IafItemTags.HEAL_PIXIE)) {
                    pixie.heal(5);
                } else if (this.targetEntity.getStack().isIn(IafItemTags.TAME_PIXIE))
                    if (!pixie.isTamed() && this.targetEntity.getOwner() instanceof PlayerEntity player) {
                        pixie.setOwner(player);
                        pixie.setPixieSitting(true);
                        pixie.setOnGround(true);  //  Entity.onGround = true
                    }

            pixie.setStackInHand(Hand.MAIN_HAND, this.targetEntity.getStack());
            this.targetEntity.getStack().decrement(1);
            pixie.playSound(IafSounds.PIXIE_TAUNT.get(), 1F, 1F);
            this.stop();
        }
    }

    @Override
    public boolean shouldContinue() {
        return true;
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
