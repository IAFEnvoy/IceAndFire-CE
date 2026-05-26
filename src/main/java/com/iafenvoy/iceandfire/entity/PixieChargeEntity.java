package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PixieChargeEntity extends Fireball {
    private final float[] rgb;
    public int ticksInAir;

    public PixieChargeEntity(EntityType<? extends Fireball> t, Level worldIn) {
        super(t, worldIn);
        this.rgb = PixieEntity.PARTICLE_RGB[this.random.nextInt(PixieEntity.PARTICLE_RGB.length - 1)];
    }

    public PixieChargeEntity(EntityType<? extends Fireball> t, Level worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(t, posX, posY, posZ, new Vec3(accelX, accelY, accelZ), worldIn);
        this.rgb = PixieEntity.PARTICLE_RGB[this.random.nextInt(PixieEntity.PARTICLE_RGB.length - 1)];
    }

    public PixieChargeEntity(EntityType<? extends Fireball> t, Level worldIn, Player shooter, double accelX, double accelY, double accelZ) {
        super(t, shooter, new Vec3(accelX, accelY, accelZ), worldIn);
        this.rgb = PixieEntity.PARTICLE_RGB[this.random.nextInt(PixieEntity.PARTICLE_RGB.length - 1)];
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        if (this.level().isClientSide)
            for (int i = 0; i < 5; ++i)
                this.level().addParticle(IafParticles.PIXIE_DUST.get(), this.getX() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.getY() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.getZ() + this.random.nextDouble() * 0.15F * (this.random.nextBoolean() ? -1 : 1), this.rgb[0], this.rgb[1], this.rgb[2]);
        this.clearFire();
        if (this.tickCount > 30) this.remove(RemovalReason.DISCARDED);
        super.tick();
    }

    @Override
    protected float getInertia() {
        return 1.05f;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return IafParticles.PIXIE_DUST.get();
    }

    @Override
    protected void onHit(@NotNull HitResult movingObject) {
        boolean flag = false;
        Entity shootingEntity = this.getOwner();
        if (!this.level().isClientSide) {
            if (movingObject.getType() == HitResult.Type.ENTITY && !((EntityHitResult) movingObject).getEntity().is(shootingEntity)) {
                Entity entity = ((EntityHitResult) movingObject).getEntity();
                if (shootingEntity.equals(entity)) flag = true;
                else {
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0));
                        living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
                        entity.hurt(this.level().damageSources().indirectMagic(shootingEntity, null), 5.0F);
                    }
                    if (this.level().isClientSide)
                        for (int i = 0; i < 20; ++i)
                            this.level().addParticle(this.getTrailParticle(), this.getX() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.getY() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.getZ() + this.random.nextDouble() * 1F * (this.random.nextBoolean() ? -1 : 1), this.rgb[0], this.rgb[1], this.rgb[2]);
                    if (!(shootingEntity instanceof Player) || !((Player) shootingEntity).isCreative())
                        if (this.random.nextInt(3) == 0)
                            this.spawnAtLocation(new ItemStack(IafItems.PIXIE_DUST.get(), 1), 0.45F);
                }
                if (!flag && this.tickCount > 4)
                    this.remove(RemovalReason.DISCARDED);
            }
        }
    }
}