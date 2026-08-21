package com.iafenvoy.iceandfire.mixin;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.BiConsumer;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Entity {
    public ChickenMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyArg(
            method = "aiStep",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/chicken/Chicken;dropFromGiftLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/BiConsumer;)Z"),
            index = 2
    )
    private BiConsumer<ServerLevel, ItemStack> layRottenEgg(BiConsumer<ServerLevel, ItemStack> consumer) {
        return (level, stack) -> {
            if (IafCommonConfig.INSTANCE.cockatrice.chickensLayRottenEggs.getValue()
                    && this.random.nextDouble() < IafCommonConfig.INSTANCE.cockatrice.eggChance.getValue()) {
                consumer.accept(level, new ItemStack(IafItems.ROTTEN_EGG.get(), stack.getCount()));
            } else {
                consumer.accept(level, stack);
            }
        };
    }
}
