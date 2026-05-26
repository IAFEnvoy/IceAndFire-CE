package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.util.dragon.IDragonProjectile;
import com.iafenvoy.iceandfire.entity.util.dragon.IafDragonDestructionManager;
import com.iafenvoy.iceandfire.registry.IafDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;

public class LightningDragonChargeEntity extends DragonChargeEntity implements IDragonProjectile {
    public LightningDragonChargeEntity(EntityType<? extends Fireball> type, Level worldIn) {
        super(type, worldIn);
    }

    public LightningDragonChargeEntity(EntityType<? extends Fireball> type, Level worldIn, double posX,
                                       double posY, double posZ, double accelX, double accelY, double accelZ) {
        super(type, worldIn, posX, posY, posZ, accelX, accelY, accelZ);
    }

    public LightningDragonChargeEntity(EntityType<? extends Fireball> type, Level worldIn,
                                       DragonBaseEntity shooter, double accelX, double accelY, double accelZ) {
        super(type, worldIn, shooter, accelX, accelY, accelZ);
    }

    @Override
    public DamageSource causeDamage(Entity cause) {
        return IafDamageTypes.causeDragonLightningDamage(cause);
    }

    @Override
    public void destroyArea(Level world, BlockPos center, DragonBaseEntity destroyer) {
        IafDragonDestructionManager.destroyAreaCharge(world, center, destroyer);
    }

    @Override
    public float getDamage() {
        return IafCommonConfig.INSTANCE.dragon.attackDamageLightning.getValue().floatValue();
    }


}