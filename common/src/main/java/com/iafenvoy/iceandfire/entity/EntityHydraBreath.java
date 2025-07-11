package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.entity.util.dragon.IDragonProjectile;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityHydraBreath extends AbstractFireballEntity implements IDragonProjectile {
    public EntityHydraBreath(EntityType<? extends AbstractFireballEntity> t, World worldIn) {
        super(t, worldIn);
    }

    public EntityHydraBreath(EntityType<? extends AbstractFireballEntity> t, World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, new Vec3d(accelX, accelY, accelZ), worldIn);
    }

    public EntityHydraBreath(EntityType<? extends AbstractFireballEntity> t, World worldIn, EntityHydra shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, new Vec3d(accelX, accelY, accelZ), worldIn);
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }

    @Override
    public float getTargetingMargin() {
        return 0F;
    }

    @Override
    public boolean canHit() {
        return false;
    }


    @Override
    public void tick() {
        this.extinguish();
        if (this.age > 30) {
            this.remove(RemovalReason.DISCARDED);
        }
        Entity shootingEntity = this.getOwner();
        if (this.getWorld().isClient || (shootingEntity == null || shootingEntity.isAlive()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
            this.baseTick();
            if (this.isBurning()) {
                this.setOnFireFor(1);
            }

            HitResult raytraceresult = ProjectileUtil.getCollision(this, this::canHit);
            if (raytraceresult.getType() != HitResult.Type.MISS) {
                this.onCollision(raytraceresult);
            }

            Vec3d Vector3d = this.getVelocity();
            double d0 = this.getX() + Vector3d.x;
            double d1 = this.getY() + Vector3d.y;
            double d2 = this.getZ() + Vector3d.z;
            ProjectileUtil.setRotationFromVelocity(this, 0.2F);
            float f = this.getDrag();
            if (this.getWorld().isClient)
                for (int i = 0; i < 15; ++i)
                    this.getWorld().addParticle(IafParticles.HYDRA_BREATH.get(), this.getX() + (double) (this.random.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, this.getY() - 0.5D, this.getZ() + (double) (this.random.nextFloat() * this.getWidth()) - (double) this.getWidth() * 0.5F, 0.1D, 1.0D, 0.1D);

            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.add(vec3d.normalize().multiply(this.accelerationPower)).multiply(this.getDrag()));

            if (this.isTouchingWater()) {
                for (int i = 0; i < 4; ++i) {
                    this.getWorld().addParticle(ParticleTypes.BUBBLE, this.getX() - this.getVelocity().x * 0.25D, this.getY() - this.getVelocity().y * 0.25D, this.getZ() - this.getVelocity().z * 0.25D, this.getVelocity().x, this.getVelocity().y, this.getVelocity().z);
                }
            }
            this.setPosition(d0, d1, d2);
            this.setPosition(this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    protected void onCollision(HitResult movingObject) {
        this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
        Entity shootingEntity = this.getOwner();
        if (!this.getWorld().isClient) {
            if (movingObject.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();

                if (entity instanceof EntityHydraHead) {
                    return;
                }
                if (shootingEntity instanceof EntityHydra dragon) {
                    if (dragon.isTeammate(entity) || dragon.isPartOf(entity)) {
                        return;
                    }
                    entity.damage(this.getWorld().getDamageSources().mobAttack(dragon), 2.0F);
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 0));
                    }

                }
            }
        }
    }
}

