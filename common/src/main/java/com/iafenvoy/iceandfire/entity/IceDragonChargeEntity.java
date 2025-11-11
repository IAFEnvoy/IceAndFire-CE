package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonDestructionManager;
import com.iafenvoy.iceandfire.particle.DragonFrostParticleType;
import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IceDragonChargeEntity extends DragonChargeEntity {
    public IceDragonChargeEntity(EntityType<? extends AbstractFireballEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public IceDragonChargeEntity(EntityType<? extends AbstractFireballEntity> type, World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(type, worldIn, posX, posY, posZ, accelX, accelY, accelZ);
    }

    public IceDragonChargeEntity(EntityType<? extends AbstractFireballEntity> type, World worldIn, DragonBaseEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
    }

    @Override
    public void tick() {
        for (int i = 0; i < 10; ++i)
            this.getWorld().addParticle(new DragonFrostParticleType(3), this.getX() + this.random.nextDouble() * 1 * (this.random.nextBoolean() ? -1 : 1), this.getY() + this.random.nextDouble() * 1 * (this.random.nextBoolean() ? -1 : 1), this.getZ() + this.random.nextDouble() * 1 * (this.random.nextBoolean() ? -1 : 1), 0.0D, 0.0D, 0.0D);
        super.tick();
    }

    @Override
    public DamageSource causeDamage(Entity cause) {
        return IafDamageTypes.causeDragonIceDamage(cause);
    }

    @Override
    public void destroyArea(World world, BlockPos center, DragonBaseEntity destroyer) {
        IafDragonDestructionManager.destroyAreaCharge(world, center, destroyer);
    }

    @Override
    public float getDamage() {
        return IafCommonConfig.INSTANCE.dragon.attackDamageIce.getValue().floatValue();
    }

    @Override
    protected boolean isBurning() {
        return false;
    }
}