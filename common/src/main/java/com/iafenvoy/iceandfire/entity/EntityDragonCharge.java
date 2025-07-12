package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import com.iafenvoy.iceandfire.entity.util.dragon.IDragonProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityDragonCharge extends AbstractFireballEntity implements IDragonProjectile {
    public EntityDragonCharge(EntityType<? extends AbstractFireballEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public EntityDragonCharge(EntityType<? extends AbstractFireballEntity> type, World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(type, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
        double d0 = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.powerX = accelX / d0 * 0.07D;
        this.powerY = accelY / d0 * 0.07D;
        this.powerZ = accelZ / d0 * 0.07D;
    }

    public EntityDragonCharge(EntityType<? extends AbstractFireballEntity> type, World worldIn,
                              EntityDragonBase shooter, double accelX, double accelY, double accelZ) {
        super(type, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.powerX = accelX / d0 * 0.07D;
        this.powerY = accelY / d0 * 0.07D;
        this.powerZ = accelZ / d0 * 0.07D;
    }

    @Override
    public void tick() {
        Entity shootingEntity = this.getOwner();
        if (this.getWorld().isClient || (shootingEntity == null || shootingEntity.isAlive()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
            super.baseTick();

            HitResult raytraceresult = ProjectileUtil.getCollision(this, this::canHitMob);

            if (raytraceresult.getType() != HitResult.Type.MISS) {
                this.onCollision(raytraceresult);
            }

            this.checkBlockCollision();
            Vec3d vector3d = this.getVelocity();
            double d0 = this.getX() + vector3d.x;
            double d1 = this.getY() + vector3d.y;
            double d2 = this.getZ() + vector3d.z;
            ProjectileUtil.setRotationFromVelocity(this, 0.2F);
            float f = this.getDrag();
            if (this.isTouchingWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.getWorld().addParticle(ParticleTypes.BUBBLE, this.getX() - this.getVelocity().x * 0.25D, this.getY() - this.getVelocity().y * 0.25D, this.getZ() - this.getVelocity().z * 0.25D, this.getVelocity().x, this.getVelocity().y, this.getVelocity().z);
                }
                f = 0.8F;
            }
            this.setVelocity(vector3d.add(this.powerX, this.powerY, this.powerZ).multiply(f));
            this.getWorld().addParticle(this.getParticleType(), this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            this.setPosition(d0, d1, d2);
        } else
            this.remove(RemovalReason.DISCARDED);
        if (this.getWorld().getBlockState(this.getBlockPos()).isSolid())
            this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onCollision(HitResult movingObject) {
        Entity shootingEntity = this.getOwner();
        if (!this.getWorld().isClient) {
            if (movingObject.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();

                if (entity instanceof IDragonProjectile) {
                    return;
                }
                if (shootingEntity instanceof EntityDragonBase dragon) {
                    if (dragon.isTeammate(entity) || dragon.isPartOf(entity) || dragon.isPart(entity)) {
                        return;
                    }
                }
                if (entity == null || entity != shootingEntity && shootingEntity instanceof EntityDragonBase) {
                    assert shootingEntity instanceof EntityDragonBase;
                    EntityDragonBase dragon = (EntityDragonBase) shootingEntity;
                    if (entity instanceof TameableEntity && ((EntityDragonBase) shootingEntity).isOwner(((EntityDragonBase) shootingEntity).getOwner())) {
                        return;
                    }
                    dragon.randomizeAttacks();
                    this.remove(RemovalReason.DISCARDED);
                }
                if (entity != null && !entity.isPartOf(shootingEntity)) {
                    if (shootingEntity != null && (entity.isPartOf(shootingEntity) || (shootingEntity instanceof EntityDragonBase && entity instanceof TameableEntity && ((EntityDragonBase) shootingEntity).getOwner() == ((TameableEntity) entity).getOwner()))) {
                        return;
                    }
                    if (shootingEntity instanceof EntityDragonBase shootingDragon) {
                        float damageAmount = this.getDamage() * shootingDragon.getDragonStage();

                        Entity cause = shootingDragon.getRidingPlayer() != null ? shootingDragon.getRidingPlayer() : shootingDragon;
                        DamageSource source = this.causeDamage(cause);

                        entity.damage(source, damageAmount);
                        if (entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() == 0) {
                            shootingDragon.randomizeAttacks();
                        }
                    }
                    if (shootingEntity instanceof LivingEntity) {
                        this.applyDamageEffects((LivingEntity) shootingEntity, entity);
                    }
                    this.remove(RemovalReason.DISCARDED);
                }
            }
            if (movingObject.getType() != HitResult.Type.MISS) {
                if (shootingEntity instanceof EntityDragonBase && DragonUtils.canGrief((EntityDragonBase) shootingEntity)) {
                    this.destroyArea(this.getWorld(), BlockPos.ofFloored(this.getX(), this.getY(), this.getZ()), ((EntityDragonBase) shootingEntity));
                }
                this.remove(RemovalReason.DISCARDED);
            }
        }

    }

    public abstract DamageSource causeDamage(Entity cause);

    public abstract void destroyArea(World world, BlockPos center, EntityDragonBase destroyer);

    public abstract float getDamage();

    @Override
    public boolean canHit() {
        return false;
    }

    protected boolean canHitMob(Entity hitMob) {
        Entity shooter = this.getOwner();
        return hitMob != this && super.canHit(hitMob) && !(shooter == null || hitMob.isTeammate(shooter)) && !(hitMob instanceof EntityDragonPart);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public float getTargetingMargin() {
        return 0F;
    }
}
