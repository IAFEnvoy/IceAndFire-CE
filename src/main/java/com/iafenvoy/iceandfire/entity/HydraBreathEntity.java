package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.entity.util.dragon.IDragonProjectile;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class HydraBreathEntity extends Fireball implements IDragonProjectile {
    public HydraBreathEntity(EntityType<? extends Fireball> t, Level worldIn) {
        super(t, worldIn);
    }

    public HydraBreathEntity(EntityType<? extends Fireball> t, Level worldIn, HydraEntity shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, new Vec3(accelX, accelY, accelZ), worldIn);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
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
    public boolean isPickable() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        this.clearFire();
        if (this.tickCount > 30) this.remove(RemovalReason.DISCARDED);
        Entity shootingEntity = this.getOwner();
        if (this.level().isClientSide || (shootingEntity == null || shootingEntity.isAlive()) && this.level().hasChunkAt(this.blockPosition())) {
            this.baseTick();
            if (this.shouldBurn()) this.igniteForSeconds(1);
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) this.onHit(hitResult);

            Vec3 Vector3d = this.getDeltaMovement();
            double d0 = this.getX() + Vector3d.x;
            double d1 = this.getY() + Vector3d.y;
            double d2 = this.getZ() + Vector3d.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            if (this.level().isClientSide)
                for (int i = 0; i < 15; ++i)
                    this.level().addParticle(IafParticles.HYDRA_BREATH.get(), this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, this.getY() - 0.5D, this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5F, 0.1D, 1.0D, 0.1D);

            Vec3 vec3d = this.getDeltaMovement();
            this.setDeltaMovement(vec3d.add(vec3d.normalize().scale(this.accelerationPower)).scale(this.getInertia()));

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - this.getDeltaMovement().x * 0.25D, this.getY() - this.getDeltaMovement().y * 0.25D, this.getZ() - this.getDeltaMovement().z * 0.25D, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
                }
            }
            this.setPos(d0, d1, d2);
            this.setPos(this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    protected void onHit(@NotNull HitResult movingObject) {
        this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        Entity shootingEntity = this.getOwner();
        if (!this.level().isClientSide) {
            if (movingObject.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();

                if (entity instanceof HydraHeadEntity) {
                    return;
                }
                if (shootingEntity instanceof HydraEntity dragon) {
                    if (dragon.isAlliedTo(entity) || dragon.is(entity)) {
                        return;
                    }
                    entity.hurt(this.level().damageSources().mobAttack(dragon), 2.0F);
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
                    }

                }
            }
        }
    }
}

