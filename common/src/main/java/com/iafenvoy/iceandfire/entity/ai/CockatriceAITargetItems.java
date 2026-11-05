package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityCockatrice;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.function.Predicate;

public class CockatriceAITargetItems<T extends ItemEntity> extends TrackTargetGoal {
    private final SearchCooldown searchCooldown = new SearchCooldown();
    protected final DragonAITargetItems.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected final int targetChance;
    protected ItemEntity targetEntity;
    private List<ItemEntity> list = IafMath.emptyItemEntityList;

    public CockatriceAITargetItems(EntityCockatrice creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public CockatriceAITargetItems(EntityCockatrice creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 10, checkSight, onlyNearby, null);
    }

    public CockatriceAITargetItems(EntityCockatrice creature, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.theNearestAttackableTargetSorter = new DragonAITargetItems.Sorter(creature);
        this.targetChance = chance;
        this.targetEntitySelector = (Predicate<ItemEntity>) item -> item != null && !item.getStack().isEmpty() && item.getStack().isIn(IafItemTags.HEAL_COCKATRICE);
    }

    @Override
    public boolean canStart() {
        if (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0)
            return false;

        if ((!((EntityCockatrice) this.mob).canMove()) || this.mob.getHealth() >= this.mob.getMaxHealth()) {
            this.list = IafMath.emptyItemEntityList;
            return false;
        }

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
        if (this.targetEntity == null || !this.targetEntity.isAlive())
            this.stop();
        else if (this.mob.squaredDistanceTo(this.targetEntity) < 1) {
            EntityCockatrice cockatrice = (EntityCockatrice) this.mob;
            this.targetEntity.getStack().decrement(1);
            this.mob.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1, 1);
            cockatrice.heal(8);
            cockatrice.setAnimation(EntityCockatrice.ANIMATION_EAT);
            this.stop();
        }
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }
}
