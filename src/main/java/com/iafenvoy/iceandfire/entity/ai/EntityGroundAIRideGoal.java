package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.DeathWormEntity;
import com.iafenvoy.iceandfire.entity.util.IGroundMount;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityGroundAIRideGoal<T extends Mob & IGroundMount> extends Goal {
    private final T dragon;
    private Player player;

    public EntityGroundAIRideGoal(T dragon) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.dragon = dragon;
    }

    @Override
    public boolean canUse() {
        this.player = this.dragon.getRidingPlayer();
        return this.player != null;
    }

    @Override
    public void start() {
        this.dragon.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.dragon.getNavigation().stop();
        this.dragon.setTarget(null);
        double x = this.dragon.getX();
        double y = this.dragon.getY();
        if (this.dragon instanceof DeathWormEntity worm)
            y = worm.processRiderY(y);
        double z = this.dragon.getZ();
        double speed = 1.8F * this.dragon.getRideSpeedModifier();
        if (this.player.xxa != 0 || this.player.zza != 0) {
            Vec3 lookVec = this.player.getLookAngle();
            if (this.player.zza < 0)
                lookVec = lookVec.yRot((float) Math.PI);
            else if (this.player.xxa > 0)
                lookVec = lookVec.yRot((float) Math.PI * 0.5f);
            else if (this.player.xxa < 0)
                lookVec = lookVec.yRot((float) Math.PI * -0.5f);
            if (Math.abs(this.player.xxa) > 0.0)
                speed *= 0.25D;
            if (this.player.zza < 0.0)
                speed *= 0.15D;
            x += lookVec.x * 10;
            z += lookVec.z * 10;
        }
        this.dragon.getMoveControl().setWantedPosition(x, y, z, speed);
    }
}
