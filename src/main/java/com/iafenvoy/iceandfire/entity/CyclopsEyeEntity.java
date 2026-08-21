package com.iafenvoy.iceandfire.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import org.jetbrains.annotations.NotNull;

public class CyclopsEyeEntity extends MultipartPartEntity<CyclopsEntity> {
    public CyclopsEyeEntity(CyclopsEntity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(parent, radius, angleYaw, offsetY, sizeX, sizeY, damageMultiplier);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float damage) {
        CyclopsEntity parent = this.getParent();
        if (source.is(DamageTypes.ARROW)) {
            parent.onHitEye(source, damage);
            return true;
        }
        return parent.hurtServer(level, source, damage);
    }
}
