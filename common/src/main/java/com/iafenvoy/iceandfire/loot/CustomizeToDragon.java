package com.iafenvoy.iceandfire.loot;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
import com.iafenvoy.iceandfire.item.ItemDragonEgg;
import com.iafenvoy.iceandfire.item.ItemDragonScales;
import com.iafenvoy.iceandfire.item.ItemDragonSkull;
import com.iafenvoy.iceandfire.item.food.ItemDragonFlesh;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafLoots;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;

import java.util.List;

public class CustomizeToDragon extends ConditionalLootFunction {
    public static final MapCodec<CustomizeToDragon> CODEC = RecordCodecBuilder.mapCodec((instance) -> addConditionsField(instance).apply(instance, CustomizeToDragon::new));

    public CustomizeToDragon(List<LootCondition> conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ItemStack process(final ItemStack stack, final LootContext context) {
        if (!stack.isEmpty() && context.get(LootContextParameters.THIS_ENTITY) instanceof EntityDragonBase dragon) {
            if (stack.getItem() == IafItems.DRAGON_BONE.get()) {
                stack.setCount(1 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 25)));
                return stack;
            } else if (stack.getItem() instanceof ItemDragonScales) {
                stack.setCount(dragon.getAgeInDays() / 25 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 5)));
                return new ItemStack(DragonColor.getById(dragon.getVariant()).getScaleItem(), stack.getCount());
            } else if (stack.getItem() instanceof ItemDragonEgg) {
                if (dragon.isMature())
                    return new ItemStack(DragonColor.getById(dragon.getVariant()).getEggItem(), stack.getCount());
                else {
                    stack.setCount(1 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 5)));
                    return new ItemStack(DragonColor.getById(dragon.getVariant()).getScaleItem(), stack.getCount());
                }
            } else if (stack.getItem() instanceof ItemDragonFlesh)
                return new ItemStack(dragon.getFleshItem(), 1 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 25)));
            else if (stack.getItem() instanceof ItemDragonSkull)
                return stack.copyComponentsToNewStack(dragon.getSkull(), stack.getCount());
            else if (stack.isIn(IafItemTags.DRAGON_BLOODS))
                return new ItemStack(dragon.getBloodItem(), stack.getCount());
            else if (stack.isIn(IafItemTags.DRAGON_HEARTS))
                return new ItemStack(dragon.getHeartItem(), stack.getCount());
        }
        return stack;
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return IafLoots.CUSTOMIZE_TO_DRAGON.get();
    }
}