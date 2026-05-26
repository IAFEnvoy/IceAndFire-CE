package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IDragonProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class DragonChargeEntity extends Fireball implements IDragonProjectile {
    public DragonChargeEntity(EntityType<? extends Fireball> type, Level worldIn) {
        super(type, worldIn);
    }

    public DragonChargeEntity(EntityType<? extends Fireball> type, Level worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(type, posX, posY, posZ, new Vec3(accelX, accelY, accelZ), worldIn);
    }

    public DragonChargeEntity(EntityType<? extends Fireball> type, Level worldIn, DragonBaseEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, shooter, new Vec3(accelX, accelY, accelZ), worldIn);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick() {
        Entity shootingEntity = this.getOwner();
        if (this.level().isClientSide || (shootingEntity == null || shootingEntity.isAlive()) && this.level().hasChunkAt(this.blockPosition())) {
            super.baseTick();

            HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitMob);

            if (raytraceresult.getType() != HitResult.Type.MISS) {
                this.onHit(raytraceresult);
            }

            this.checkInsideBlocks();
            Vec3 vector3d = this.getDeltaMovement();
            double d0 = this.getX() + vector3d.x;
            double d1 = this.getY() + vector3d.y;
            double d2 = this.getZ() + vector3d.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float f = this.getInertia();
            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.level().addParticle(ParticleTypes.BUBBLE, this.getX() - this.getDeltaMovement().x * 0.25D, this.getY() - this.getDeltaMovement().y * 0.25D, this.getZ() - this.getDeltaMovement().z * 0.25D, this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
                }
                f = 0.8F;
            }
            this.setDeltaMovement(vector3d.add(vector3d.normalize().scale(this.accelerationPower)).scale(f));
            this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            this.setPos(d0, d1, d2);
        } else
            this.remove(RemovalReason.DISCARDED);
        if (this.level().getBlockState(this.blockPosition()).isSolid())
            this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onHit(@NotNull HitResult movingObject) {
        Entity shootingEntity = this.getOwner();
        if (!this.level().isClientSide) {
            if (movingObject.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();

                if (entity instanceof IDragonProjectile)
                    return;
                if (shootingEntity instanceof DragonBaseEntity dragon)
                    if (dragon.isAlliedTo(entity) || dragon.is(entity) || dragon.isPart(entity))
                        return;
                if (entity == null || entity != shootingEntity && shootingEntity instanceof DragonBaseEntity) {
                    assert shootingEntity instanceof DragonBaseEntity;
                    DragonBaseEntity dragon = (DragonBaseEntity) shootingEntity;
                    if (entity instanceof TamableAnimal && dragon.isOwnedBy(((DragonBaseEntity) shootingEntity).getOwner()))
                        return;
                    dragon.randomizeAttacks();
                    this.remove(RemovalReason.DISCARDED);
                }
                if (entity != null && !entity.is(shootingEntity)) {
                    if (shootingEntity != null && (entity.is(shootingEntity) || (shootingEntity instanceof DragonBaseEntity && entity instanceof TamableAnimal && ((DragonBaseEntity) shootingEntity).getOwner() == ((TamableAnimal) entity).getOwner()))) {
                        return;
                    }
                    if (shootingEntity instanceof DragonBaseEntity shootingDragon) {
                        float damageAmount = this.getDamage() * shootingDragon.getDragonStage();

                        Entity cause = shootingDragon.getRidingPlayer() != null ? shootingDragon.getRidingPlayer() : shootingDragon;
                        DamageSource source = this.causeDamage(cause);

                        entity.hurt(source, damageAmount);
                        if (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() == 0) {
                            shootingDragon.randomizeAttacks();
                        }
                    }
                    this.remove(RemovalReason.DISCARDED);
                }
            }
            if (movingObject.getType() != HitResult.Type.MISS) {
                if (shootingEntity instanceof DragonBaseEntity dragon && DragonUtils.canGrief(dragon))
                    this.destroyArea(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()), dragon);
                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    public abstract DamageSource causeDamage(Entity cause);

    public abstract void destroyArea(Level world, BlockPos center, DragonBaseEntity destroyer);

    public abstract float getDamage();

    @Override
    public boolean isPickable() {
        return false;
    }

    protected boolean canHitMob(Entity hitMob) {
        Entity shooter = this.getOwner();
        return hitMob != this && super.canHitEntity(hitMob) && !(shooter == null || hitMob.isAlliedTo(shooter)) && !(hitMob instanceof DragonPartEntity);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }

    @Override
    public float getPickRadius() {
        return 0F;
    }
}
