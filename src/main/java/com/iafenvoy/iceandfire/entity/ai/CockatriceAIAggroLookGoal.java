package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import com.iafenvoy.iceandfire.entity.util.IafEntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class CockatriceAIAggroLookGoal extends NearestAttackableTargetGoal<Player> {
    private final CockatriceEntity cockatrice;
    private Player player;

    public CockatriceAIAggroLookGoal(CockatriceEntity cockatriceIn) {
        super(cockatriceIn, Player.class, false);
        this.cockatrice = cockatriceIn;
    }

    /**
     * Returns whether the Goal should begin execution.
     */
    @Override
    public boolean canUse() {
        if (this.cockatrice.isTame()) return false;
        this.player = this.cockatrice.level().getNearestPlayer(
                this.cockatrice.getX(), this.cockatrice.getY(), this.cockatrice.getZ(), 25.0D,
                target -> target instanceof LivingEntity living
                        && IafEntityUtil.isEntityLookingAt(living, this.cockatrice, CockatriceEntity.VIEW_RADIUS)
                        && this.cockatrice.distanceTo(living) < this.getFollowDistance()
        );
        return this.player != null;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by
     * another one
     */
    @Override
    public void stop() {
        this.player = null;
        super.stop();
    }

    /**
     * Returns whether an in-progress Goal should continue executing
     */
    @Override
    public boolean canContinueToUse() {
        if (this.player != null && !this.player.isCreative() && !this.player.isSpectator()) {
            if (!IafEntityUtil.isEntityLookingAt(this.player, this.cockatrice, 0.4F))
                return false;
            else {
                this.cockatrice.lookAt(this.player, 10.0F, 10.0F);
                if (!this.cockatrice.isTame()) {
                    this.cockatrice.setTargetedEntity(this.player.getId());
                    this.cockatrice.setTarget(this.player);
                }
                return true;
            }
        } else return this.targetMob != null && this.targetMob.isAlive() || super.canContinueToUse();
    }
}
