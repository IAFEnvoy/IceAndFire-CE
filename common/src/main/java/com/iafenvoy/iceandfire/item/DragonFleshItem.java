package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DragonFleshItem extends Item {
    private final DragonType type;

    public DragonFleshItem(DragonType type) {
        super(new Settings().food(new FoodComponent.Builder().nutrition(8).saturationModifier(0.8F).build()));
        this.type = type;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity living) {
        if (!world.isClient) {
            if (this.type == IafDragonTypes.FIRE) {
                // Keep existing fire dragon behavior (sets the eater on fire briefly)
                living.setOnFireFor(5);
            } else if (this.type == IafDragonTypes.ICE) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            } else if (this.type == IafDragonTypes.NETHER) {
                // Nether dragon meat sets you on fire for 7 seconds
                living.setOnFireFor(7);
            } else {
                LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(living.getWorld());
                assert lightning != null;
                lightning.refreshPositionAfterTeleport(living.getPos());
                living.getWorld().spawnEntity(lightning);
            }
        }
        return super.finishUsing(stack, world, living);
    }
}
