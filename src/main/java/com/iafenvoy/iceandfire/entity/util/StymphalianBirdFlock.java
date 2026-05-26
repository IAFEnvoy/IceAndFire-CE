package com.iafenvoy.iceandfire.entity.util;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.StymphalianBirdEntity;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAIAirTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class StymphalianBirdFlock {
    private final int distance = 15;
    private StymphalianBirdEntity leader;
    private ArrayList<StymphalianBirdEntity> members = new ArrayList<>();
    private BlockPos leaderTarget;
    private RandomSource random;

    private StymphalianBirdFlock() {
    }

    public static StymphalianBirdFlock createFlock(StymphalianBirdEntity bird) {
        StymphalianBirdFlock flock = new StymphalianBirdFlock();
        flock.leader = bird;
        flock.members = new ArrayList<>();
        flock.members.add(bird);
        flock.leaderTarget = bird.airTarget;
        flock.random = bird.getRandom();
        return flock;
    }

    public static StymphalianBirdFlock getNearbyFlock(StymphalianBirdEntity bird) {
        float d0 = IafCommonConfig.INSTANCE.stymphalianBird.flockLength.getValue();
        List<Entity> list = bird.level().getEntities(bird, (new AABB(bird.getX(), bird.getY(), bird.getZ(), bird.getX() + 1.0D, bird.getY() + 1.0D, bird.getZ() + 1.0D)).inflate(d0, 10.0D, d0), StymphalianBirdEntity.STYMPHALIAN_PREDICATE);
        if (!list.isEmpty())
            for (Entity entity : list)
                if (entity instanceof StymphalianBirdEntity other)
                    if (other.flock != null)
                        return other.flock;
        return null;
    }

    public boolean isLeader(StymphalianBirdEntity bird) {
        return this.leader != null && this.leader == bird;
    }

    public void addToFlock(StymphalianBirdEntity bird) {
        this.members.add(bird);
    }

    public void update() {
        if (!this.members.isEmpty() && (this.leader == null || !this.leader.isAlive()))
            this.leader = this.members.get(this.random.nextInt(this.members.size()));
        if (this.leader != null && this.leader.isAlive()) {
            BlockPos prevLeaderTarget = this.leaderTarget;
            this.leaderTarget = this.leader.airTarget;
        }
    }

    public void onLeaderAttack(LivingEntity attackTarget) {
        for (StymphalianBirdEntity bird : this.members)
            if (bird.getTarget() == null && !this.isLeader(bird))
                bird.setTarget(attackTarget);
    }

    public StymphalianBirdEntity getLeader() {
        return this.leader;
    }


    public void setTarget(BlockPos target) {
        this.leaderTarget = target;
        for (StymphalianBirdEntity bird : this.members)
            if (!this.isLeader(bird))
                bird.airTarget = StymphalianBirdAIAirTargetGoal.getNearbyAirTarget(bird);
    }

    public void setFlying(boolean flying) {
        for (StymphalianBirdEntity bird : this.members)
            if (!this.isLeader(bird))
                bird.setFlying(flying);
    }

    public void setFearTarget(LivingEntity living) {
        for (StymphalianBirdEntity bird : this.members)
            bird.setVictor(living);
    }
}
