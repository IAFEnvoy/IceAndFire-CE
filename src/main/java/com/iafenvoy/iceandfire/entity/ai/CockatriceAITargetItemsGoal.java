package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.iceandfire.util.IafMath;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class CockatriceAITargetItemsGoal<T extends ItemEntity> extends TargetGoal {
    protected final DragonAITargetItemsGoal.Sorter theNearestAttackableTargetSorter;
    protected final Predicate<? super ItemEntity> targetEntitySelector;
    protected final int targetChance;
    protected ItemEntity targetEntity;
    private List<ItemEntity> list = IafMath.emptyItemEntityList;

    public CockatriceAITargetItemsGoal(CockatriceEntity creature, boolean checkSight) {
        this(creature, checkSight, false);
    }

    public CockatriceAITargetItemsGoal(CockatriceEntity creature, boolean checkSight, boolean onlyNearby) {
        this(creature, 10, checkSight, onlyNearby, null);
    }

    public CockatriceAITargetItemsGoal(CockatriceEntity creature, int chance, boolean checkSight, boolean onlyNearby, final Predicate<? super T> targetSelector) {
        super(creature, checkSight, onlyNearby);
        this.theNearestAttackableTargetSorter = new DragonAITargetItemsGoal.Sorter(creature);
        this.targetChance = chance;
        this.targetEntitySelector = (Predicate<ItemEntity>) item -> item != null && !item.getItem().isEmpty() && item.getItem().is(IafItemTags.HEAL_COCKATRICE);
    }

    @Override
    public boolean canUse() {
        if (this.targetChance > 0 && this.mob.getRandom().nextInt(this.targetChance) != 0)
            return false;

        if ((!((CockatriceEntity) this.mob).canMove()) || this.mob.getHealth() >= this.mob.getMaxHealth()) {
            this.list = IafMath.emptyItemEntityList;
            return false;
        }

        if (this.mob.level().getGameTime() % 4 == 0) // only update the list every 4 ticks
            this.list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.getTargetableArea(this.getFollowDistance()), this.targetEntitySelector);

        if (this.list.isEmpty()) return false;
        else {
            this.list.sort(this.theNearestAttackableTargetSorter);
            this.targetEntity = this.list.getFirst();
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
    public void tick() {
        super.tick();
        if (this.targetEntity == null || !this.targetEntity.isAlive())
            this.stop();
        else if (this.mob.distanceToSqr(this.targetEntity) < 1) {
            CockatriceEntity cockatrice = (CockatriceEntity) this.mob;
            this.targetEntity.getItem().shrink(1);
            this.mob.playSound(SoundEvents.GENERIC_EAT, 1, 1);
            cockatrice.heal(8);
            cockatrice.setAnimation(CockatriceEntity.ANIMATION_EAT);
            this.stop();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }
}
