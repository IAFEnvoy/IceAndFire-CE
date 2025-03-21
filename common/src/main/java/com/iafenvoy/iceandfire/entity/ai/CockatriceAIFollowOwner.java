package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.EntityCockatrice;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;

public class CockatriceAIFollowOwner extends FollowOwnerGoal {
    final EntityCockatrice cockatrice;

    public CockatriceAIFollowOwner(EntityCockatrice cockatrice, double speed, float minDist, float maxDist) {
        super(cockatrice, speed, minDist, maxDist);
        this.cockatrice = cockatrice;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.cockatrice.getCommand() == 2;
    }
}
