package com.iafenvoy.iceandfire.entity.util;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntityStymphalianBird;
import com.iafenvoy.iceandfire.entity.ai.StymphalianBirdAIAirTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class StymphalianBirdFlock {
    private final int distance = 15;
    private EntityStymphalianBird leader;
    private ArrayList<EntityStymphalianBird> members = new ArrayList<>();
    private BlockPos leaderTarget;
    private Random random;
    private long lastUpdate = Long.MIN_VALUE;

    private StymphalianBirdFlock() {
    }

    public static StymphalianBirdFlock createFlock(EntityStymphalianBird bird) {
        StymphalianBirdFlock flock = new StymphalianBirdFlock();
        flock.leader = bird;
        flock.members = new ArrayList<>();
        flock.members.add(bird);
        flock.leaderTarget = bird.airTarget;
        flock.random = bird.getRandom();
        return flock;
    }

    public static StymphalianBirdFlock getNearbyFlock(EntityStymphalianBird bird) {
        float d0 = IafCommonConfig.INSTANCE.stymphalianBird.flockLength.getValue();
        List<Entity> list = bird.getWorld().getOtherEntities(bird, (new Box(bird.getX(), bird.getY(), bird.getZ(), bird.getX() + 1.0D, bird.getY() + 1.0D, bird.getZ() + 1.0D)).expand(d0, 10.0D, d0), EntityStymphalianBird.STYMPHALIAN_PREDICATE);
        if (!list.isEmpty())
            for (Entity entity : list)
                if (entity instanceof EntityStymphalianBird other)
                    if (other.flock != null)
                        return other.flock;
        return null;
    }

    public boolean isLeader(EntityStymphalianBird bird) {
        return this.leader != null && this.leader == bird;
    }

    public void addToFlock(EntityStymphalianBird bird) {
        if (!this.members.contains(bird)) {
            this.members.add(bird);
            this.lastUpdate = Long.MIN_VALUE;
        }
    }

    public void removeFromFlock(EntityStymphalianBird bird) {
        this.members.remove(bird);
        if (this.leader == bird) this.leader = null;
        this.lastUpdate = Long.MIN_VALUE;
    }

    public void update() {
        if (this.members.isEmpty()) { this.leader = null; return; }
        var world = this.members.get(0).getWorld();
        long now = world.getTime();
        if (now == this.lastUpdate) return;
        this.lastUpdate = now;
        this.members.removeIf(bird -> bird.isRemoved() || !bird.isAlive() || bird.getWorld() != world || bird.flock != this);
        if (this.leader == null || !this.members.contains(this.leader))
            this.leader = this.members.isEmpty() ? null : this.members.get(this.random.nextInt(this.members.size()));
        if (this.leader != null) this.leaderTarget = this.leader.airTarget;
    }

    public void onLeaderAttack(LivingEntity attackTarget) {
        for (EntityStymphalianBird bird : this.members)
            if (bird.getTarget() == null && !this.isLeader(bird))
                bird.setTarget(attackTarget);
    }

    public EntityStymphalianBird getLeader() {
        this.update();
        return this.leader;
    }


    public void setTarget(BlockPos target) {
        this.leaderTarget = target;
        for (EntityStymphalianBird bird : this.members)
            if (!this.isLeader(bird))
                bird.airTarget = StymphalianBirdAIAirTarget.getNearbyAirTarget(bird);
    }

    public void setFlying(boolean flying) {
        for (EntityStymphalianBird bird : this.members)
            if (!this.isLeader(bird))
                bird.setFlying(flying);
    }

    public void setFearTarget(LivingEntity living) {
        for (EntityStymphalianBird bird : this.members)
            bird.setVictor(living);
    }
}
