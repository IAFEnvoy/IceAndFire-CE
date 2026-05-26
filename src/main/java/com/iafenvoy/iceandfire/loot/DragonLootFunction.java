package com.iafenvoy.iceandfire.loot;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.DragonEggItem;
import com.iafenvoy.iceandfire.item.DragonFleshItem;
import com.iafenvoy.iceandfire.item.DragonScalesItem;
import com.iafenvoy.iceandfire.item.DragonSkullItem;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafLoots;
import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DragonLootFunction extends LootItemConditionalFunction {
    public static final MapCodec<DragonLootFunction> CODEC = RecordCodecBuilder.mapCodec((instance) -> commonFields(instance).apply(instance, DragonLootFunction::new));

    public DragonLootFunction(List<LootItemCondition> conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ItemStack run(final ItemStack stack, final @NotNull LootContext context) {
        if (!stack.isEmpty() && context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof DragonBaseEntity dragon) {
            if (stack.getItem() == IafItems.DRAGON_BONE.get()) {
                stack.setCount(1 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 25)));
                return stack;
            } else if (stack.getItem() instanceof DragonScalesItem) {
                stack.setCount(dragon.getAgeInDays() / 25 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 5)));
                return new ItemStack(DragonColor.getById(dragon.getVariant()).getScaleItem(), stack.getCount());
            } else if (stack.getItem() instanceof DragonEggItem) {
                if (dragon.isMature())
                    return new ItemStack(DragonColor.getById(dragon.getVariant()).getEggItem(), stack.getCount());
                else {
                    stack.setCount(1 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 5)));
                    return new ItemStack(DragonColor.getById(dragon.getVariant()).getScaleItem(), stack.getCount());
                }
            } else if (stack.getItem() instanceof DragonFleshItem)
                return new ItemStack(dragon.getFleshItem(), 1 + dragon.getRandom().nextInt(1 + (dragon.getAgeInDays() / 25)));
            else if (stack.getItem() instanceof DragonSkullItem)
                return stack.transmuteCopy(dragon.getSkull(), stack.getCount());
            else if (stack.is(IafItemTags.DRAGON_BLOODS))
                return new ItemStack(dragon.getBloodItem(), stack.getCount());
            else if (stack.is(IafItemTags.DRAGON_HEARTS))
                return new ItemStack(dragon.getHeartItem(), stack.getCount());
        }
        return stack;
    }

    @Override
    public @NotNull LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return IafLoots.DRAGON_LOOT.get();
    }
}