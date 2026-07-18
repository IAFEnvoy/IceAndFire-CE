package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends Entity {
    public MobMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Unique
    private static boolean iceandfire$isSkeleton(Entity entity) {
        return WitherSkeleton.class.isAssignableFrom(entity.getClass());
    }

    @Inject(method = "dropFromLootTable", at = @At("HEAD"))//FIXME::Loot table modifiers
    public void dropHandler(DamageSource damageSource, boolean attackedRecently, CallbackInfo ci) {
        if (attackedRecently && damageSource.getDirectEntity() instanceof Player)
            if (iceandfire$isSkeleton(this))
                this.spawnAtLocation(new ItemStack(IafItems.WITHERBONE.get(), this.random.nextInt(2)));
    }
}
