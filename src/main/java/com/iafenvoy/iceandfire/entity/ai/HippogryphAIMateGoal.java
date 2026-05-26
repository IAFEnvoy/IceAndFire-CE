package com.iafenvoy.iceandfire.entity.ai;

import com.iafenvoy.iceandfire.entity.HippogryphEntity;
import com.iafenvoy.iceandfire.item.HippogryphEggItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.List;

public class HippogryphAIMateGoal extends Goal {
    final Level world;
    final double moveSpeed;
    private final HippogryphEntity hippo;
    int spawnBabyDelay;
    private HippogryphEntity targetMate;

    public HippogryphAIMateGoal(HippogryphEntity animal, double speedIn) {
        this(animal, speedIn, animal.getClass());
    }

    public HippogryphAIMateGoal(HippogryphEntity hippogryph, double speed, Class<? extends Animal> mate) {
        this.hippo = hippogryph;
        this.world = hippogryph.level();
        this.moveSpeed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.hippo.isInLove() || this.hippo.isOrderedToSit()) return false;
        else {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    @Override
    public void stop() {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    @Override
    public void tick() {
        this.hippo.getLookControl().setLookAt(this.targetMate, 10.0F, this.hippo.getMaxHeadXRot());
        this.hippo.getNavigation().moveTo(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if (this.spawnBabyDelay >= 60 && this.hippo.distanceToSqr(this.targetMate) < 9.0D)
            this.spawnBaby();
    }

    private HippogryphEntity getNearbyMate() {
        List<HippogryphEntity> list = this.world.getEntitiesOfClass(HippogryphEntity.class, this.hippo.getBoundingBox().inflate(8.0D));
        double d0 = Double.MAX_VALUE;
        HippogryphEntity entityanimal = null;

        for (HippogryphEntity entityanimal1 : list)
            if (this.hippo.canMate(entityanimal1) && this.hippo.distanceToSqr(entityanimal1) < d0) {
                entityanimal = entityanimal1;
                d0 = this.hippo.distanceToSqr(entityanimal1);
            }

        return entityanimal;
    }

    private void spawnBaby() {
        ItemEntity egg = new ItemEntity(this.world, this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), HippogryphEggItem.createEggStack(this.hippo.getEnumVariant(), this.targetMate.getEnumVariant()));
        this.hippo.setAge(6000);
        this.targetMate.setAge(6000);
        this.hippo.resetLove();
        this.targetMate.resetLove();
        egg.moveTo(this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), 0.0F, 0.0F);
        if (!this.world.isClientSide) this.world.addFreshEntity(egg);
        RandomSource random = this.hippo.getRandom();

        for (int i = 0; i < 7; ++i) {
            final double d0 = random.nextGaussian() * 0.02D;
            final double d1 = random.nextGaussian() * 0.02D;
            final double d2 = random.nextGaussian() * 0.02D;
            final double d3 = random.nextDouble() * this.hippo.getBbWidth() * 2.0D - this.hippo.getBbWidth();
            final double d4 = 0.5D + random.nextDouble() * this.hippo.getBbHeight();
            final double d5 = random.nextDouble() * this.hippo.getBbWidth() * 2.0D - this.hippo.getBbWidth();
            this.world.addParticle(ParticleTypes.HEART, this.hippo.getX() + d3, this.hippo.getY() + d4, this.hippo.getZ() + d5, d0, d1, d2);
        }

        if (this.world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
            this.world.addFreshEntity(new ExperienceOrb(this.world, this.hippo.getX(), this.hippo.getY(), this.hippo.getZ(), random.nextInt(7) + 1));
    }
}
