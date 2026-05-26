package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;

public class CockatriceAIFollowOwnerGoal extends FollowOwnerGoal {
    final CockatriceEntity cockatrice;

    public CockatriceAIFollowOwnerGoal(CockatriceEntity cockatrice, double speed, float minDist, float maxDist) {
        super(cockatrice, speed, minDist, maxDist);
        this.cockatrice = cockatrice;
    }

    @Override
    public boolean canUse() {
        return super.canUse() && this.cockatrice.getCommand() == 2;
    }
}
