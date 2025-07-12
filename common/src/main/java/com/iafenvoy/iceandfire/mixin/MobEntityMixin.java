package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends Entity {
    public MobEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private static boolean iceandfire$isSkeleton(Entity entity) {
        return WitherSkeletonEntity.class.isAssignableFrom(entity.getClass());
    }

    @Inject(method = "dropLoot", at = @At("HEAD"))
    public void dropHandler(DamageSource damageSource, boolean causedByPlayer, CallbackInfo ci) {
        if (causedByPlayer && damageSource.getSource() instanceof PlayerEntity player)
            if (iceandfire$isSkeleton(this))
                this.dropStack(new ItemStack(IafItems.WITHERBONE.get(), this.random.nextInt(2)));
    }
}
