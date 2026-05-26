package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DragonEggItem extends Item {
    public static final Map<DragonColor, Item> EGGS = new HashMap<>();
    public final DragonColor type;

    public DragonEggItem(DragonColor type) {
        super(new Properties().stacksTo(1));
        this.type = type;
        EGGS.put(type, this);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "item.iceandfire.dragonegg";
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("dragon." + this.type.getName().toLowerCase(Locale.ROOT)).withStyle(this.type.getColorFormatting()));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getPlayer().getItemInHand(context.getHand());
        BlockPos offset = context.getClickedPos().relative(context.getClickedFace());
        DragonEggEntity egg = new DragonEggEntity(IafEntities.DRAGON_EGG.get(), context.getLevel());
        egg.setEggType(this.type);
        egg.moveTo(offset.getX() + 0.5, offset.getY(), offset.getZ() + 0.5, 0, 0);
        egg.onPlayerPlace(context.getPlayer());
        if (itemstack.has(DataComponents.CUSTOM_NAME))
            egg.setCustomName(itemstack.getHoverName());
        if (!context.getLevel().isClientSide)
            context.getLevel().addFreshEntity(egg);
        itemstack.shrink(1);
        return InteractionResult.SUCCESS;
    }
}
