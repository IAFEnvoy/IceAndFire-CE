package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Entity {
    public ChickenMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyArg(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemLike layRottenEgg(ItemLike egg) {
        return IafCommonConfig.INSTANCE.cockatrice.chickensLayRottenEggs.getValue() && this.random.nextDouble() < IafCommonConfig.INSTANCE.cockatrice.eggChance.getValue() ? IafItems.ROTTEN_EGG : egg;
    }
}
