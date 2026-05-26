package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.entity.util.dragon.IDragonProjectile;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SeaSerpentBubblesEntity extends Fireball implements IDragonProjectile {
    public SeaSerpentBubblesEntity(EntityType<? extends Fireball> t, Level worldIn) {
        super(t, worldIn);
    }

    public SeaSerpentBubblesEntity(EntityType<? extends Fireball> t, Level worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, new Vec3(accelX, accelY, accelZ), worldIn);
    }

    public SeaSerpentBubblesEntity(EntityType<? extends Fireball> t, Level worldIn, SeaSerpentEntity shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, new Vec3(accelX, accelY, accelZ), worldIn);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        Entity shootingEntity = this.getOwner();
        if (this.tickCount > 400) this.remove(RemovalReason.DISCARDED);
        this.autoTarget();

        if (this.level().isClientSide || (shootingEntity == null || !shootingEntity.isAlive()) && this.level().hasChunkAt(this.blockPosition())) {
            this.baseTick();
            HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (raytraceresult.getType() != HitResult.Type.MISS)
                this.onHit(raytraceresult);

            Vec3 vec3d = this.getDeltaMovement();
            double d0 = this.getX() + vec3d.x;
            double d1 = this.getY() + vec3d.y;
            double d2 = this.getZ() + vec3d.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float f = this.getInertia();
            if (this.level().isClientSide)
                for (int i = 0; i < 3; ++i)
                    this.level().addParticle(IafParticles.SERPENT_BUBBLE.get(), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() - 0.5D, this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, 0, 0, 0);

            this.setDeltaMovement(vec3d.add(vec3d.normalize().multiply(this.stuckSpeedMultiplier)).scale(f));
            this.setPos(d0, d1, d2);
            this.setPos(this.getX(), this.getY(), this.getZ());
        }
        this.setPos(this.getX(), this.getY(), this.getZ());
        if (this.tickCount > 20 && !this.isInWaterOrRain())
            this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entityIn) {
        return super.canHitEntity(entityIn) && !(entityIn instanceof MultipartPartEntity) && !(entityIn instanceof SeaSerpentBubblesEntity);
    }


    public void autoTarget() {
        if (this.level().isClientSide) {
            Entity shootingEntity = this.getOwner();
            if (shootingEntity instanceof SeaSerpentEntity seaSerpent && seaSerpent.getTarget() != null) {
            } else if (this.tickCount > 20)
                this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.BUBBLE;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public float getPickRadius() {
        return 0F;
    }

    @Override
    protected void onHit(@NotNull HitResult movingObject) {
        boolean flag = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        if (!this.level().isClientSide) {
            if (movingObject.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();

                if (entity instanceof SlowPartEntity) return;
                Entity shootingEntity = this.getOwner();
                if (shootingEntity instanceof SeaSerpentEntity dragon) {
                    if (dragon.isAlliedTo(entity) || dragon.is(entity)) return;
                    entity.hurt(this.level().damageSources().mobAttack(dragon), 6.0F);
                }
            }
        }
    }
}
