package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DragonEggItem extends Item {
    public static final Map<DragonColor, Item> EGGS = new HashMap<>();
    public final DragonColor type;

    public DragonEggItem(DragonColor type) {
        super(new Settings().maxCount(1));
        this.type = type;
        EGGS.put(type, this);
    }

    @Override
    public String getTranslationKey() {
        return "item.iceandfire.dragonegg";
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("dragon." + this.type.getName().toLowerCase(Locale.ROOT)).formatted(this.type.getColorFormatting()));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack itemstack = context.getPlayer().getStackInHand(context.getHand());
        BlockPos offset = context.getBlockPos().offset(context.getSide());
        DragonEggEntity egg = new DragonEggEntity(IafEntities.DRAGON_EGG.get(), context.getWorld());
        egg.setEggType(this.type);
        egg.refreshPositionAndAngles(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
        egg.onPlayerPlace(context.getPlayer());
        if (itemstack.contains(DataComponentTypes.CUSTOM_NAME))
            egg.setCustomName(itemstack.getName());
        if (!context.getWorld().isClient)
            context.getWorld().spawnEntity(egg);
        itemstack.decrement(1);
        return ActionResult.SUCCESS;
    }
}
