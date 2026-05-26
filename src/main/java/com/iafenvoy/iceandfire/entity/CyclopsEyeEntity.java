package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CyclopsEyeEntity extends MultipartPartEntity {
    public CyclopsEyeEntity(EntityType<?> t, Level world) {
        super(t, world);
    }

    public CyclopsEyeEntity(LivingEntity parent, float radius, float angleYaw, float offsetY, float sizeX, float sizeY, float damageMultiplier) {
        super(IafEntities.CYCLOPS_MULTIPART.get(), parent, radius, angleYaw, offsetY, sizeX, sizeY,
                damageMultiplier);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        Entity parent = this.getParent();
        if (parent instanceof CyclopsEntity && source.is(DamageTypes.ARROW)) {
            ((CyclopsEntity) parent).onHitEye(source, damage);
            return true;
        } else {
            return parent != null && parent.hurt(source, damage);
        }
    }
}
