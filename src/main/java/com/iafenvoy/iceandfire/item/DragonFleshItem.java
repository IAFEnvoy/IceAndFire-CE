package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DragonFleshItem extends Item {
    private final DragonType type;

    public DragonFleshItem(DragonType type) {
        super(new Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()));
        this.type = type;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level world, @NotNull LivingEntity living) {
        if (!world.isClientSide) {
            if (this.type == IafDragonTypes.FIRE)
                living.igniteForSeconds(5);
            else if (this.type == IafDragonTypes.ICE)
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            else {
                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(living.level());
                assert lightning != null;
                lightning.moveTo(living.position());
                living.level().addFreshEntity(lightning);
            }
        }
        return super.finishUsingItem(stack, world, living);
    }
}
