package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityPixieCharge extends AbstractFireballEntity {
    private final float[] rgb;
    public int ticksInAir;

    public EntityPixieCharge(EntityType<? extends AbstractFireballEntity> t, World worldIn) {
        super(t, worldIn);
        this.rgb = EntityPixie.PARTICLE_RGB[this.random.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }

    public EntityPixieCharge(EntityType<? extends AbstractFireballEntity> t, World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, accelX, accelY, accelZ, worldIn);
        double d0 = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.powerX = accelX / d0 * 0.07D;
        this.powerY = accelY / d0 * 0.07D;
        this.powerZ = accelZ / d0 * 0.07D;
        this.rgb = EntityPixie.PARTICLE_RGB[this.random.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }

    public EntityPixieCharge(EntityType<? extends AbstractFireballEntity> t, World worldIn, PlayerEntity shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, accelX, accelY, accelZ, worldIn);
        double d0 = Math.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.powerX = accelX / d0 * 0.07D;
        this.powerY = accelY / d0 * 0.07D;
        this.powerZ = accelZ / d0 * 0.07D;
        this.rgb = EntityPixie.PARTICLE_RGB[this.random.nextInt(EntityPixie.PARTICLE_RGB.length - 1)];
    }

    @Override
    protected boolean isBurning() {
        return false;
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    public void tick() {
        Entity shootingEntity = this.getOwner();
        if (this.getWorld().isClient)
            for (int i = 0; i < 5; ++i)
                this.getWorld().addParticle(IafParticles.PIXIE_DUST.get(), this.getX() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.getY() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.getZ() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.rgb[0], this.rgb[1], this.rgb[2]);
        this.extinguish();
        if (this.age > 30) this.remove(RemovalReason.DISCARDED);
        if (this.getWorld().isClient || (shootingEntity == null || shootingEntity.isAlive()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
            this.baseTick();
            if (this.isBurning()) this.setOnFireFor(1);

            ++this.ticksInAir;
            HitResult raytraceresult = ProjectileUtil.getCollision(this, this::canHit);
            if (raytraceresult.getType() != HitResult.Type.MISS)
                this.onCollision(raytraceresult);

            Vec3d vector3d = this.getVelocity();
            double d0 = this.getX() + vector3d.x;
            double d1 = this.getY() + vector3d.y;
            double d2 = this.getZ() + vector3d.z;
            ProjectileUtil.setRotationFromVelocity(this, 0.2F);
            float f = this.getDrag();
            this.setVelocity(vector3d.add(this.powerX, this.powerY, this.powerZ).multiply(f));

            this.powerX *= 0.95F;
            this.powerY *= 0.95F;
            this.powerZ *= 0.95F;
            this.addVelocity(this.powerX, this.powerY, this.powerZ);
            ++this.ticksInAir;

            if (this.isTouchingWater())
                for (int i = 0; i < 4; ++i)
                    this.getWorld().addParticle(ParticleTypes.BUBBLE, this.getX() - this.getVelocity().x * 0.25D, this.getY() - this.getVelocity().y * 0.25D, this.getZ() - this.getVelocity().z * 0.25D, this.getVelocity().x, this.getVelocity().y, this.getVelocity().z);
            this.setPosition(d0, d1, d2);
            this.setPosition(this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    protected void onCollision(HitResult movingObject) {
        boolean flag = false;
        Entity shootingEntity = this.getOwner();
        if (!this.getWorld().isClient) {
            if (movingObject.getType() == HitResult.Type.ENTITY && !((EntityHitResult) movingObject).getEntity().isPartOf(shootingEntity)) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();
                if (shootingEntity != null && shootingEntity.equals(entity)) flag = true;
                else {
                    if (entity instanceof LivingEntity living) {
                        int duration = IafCommonConfig.INSTANCE.tools.pixieChargeEffectDuration.getValue();
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, duration, 0));
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, duration, 0));
                        entity.damage(this.getWorld().getDamageSources().indirectMagic(shootingEntity, null), 5.0F);
                    }
                    if (this.getWorld().isClient)
                        for (int i = 0; i < 20; ++i)
                            this.getWorld().addParticle(IafParticles.PIXIE_DUST.get(), this.getX() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.getY() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.getZ() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.rgb[0], this.rgb[1], this.rgb[2]);
                    if (!(shootingEntity instanceof PlayerEntity) || !((PlayerEntity) shootingEntity).isCreative())
                        if (this.random.nextInt(3) == 0)
                            this.dropStack(new ItemStack(IafItems.PIXIE_DUST.get(), 1), 0.45F);
                }
                if (!flag && this.age > 4)
                    this.remove(RemovalReason.DISCARDED);
            }
        }
    }
}