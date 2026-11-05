package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.world.DragonSummoning;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SummoningCrystalItem extends Item {
    public SummoningCrystalItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel world, @NotNull Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        //Data Fix
        if (stack.has(IafDataComponents.NBT_COMPOUND.get())) {
            stack.set(IafDataComponents.CRYSTAL_DRAGON_DATA.get(), stack.get(IafDataComponents.NBT_COMPOUND.get()));
            stack.remove(IafDataComponents.NBT_COMPOUND.get());
        }
    }

    public static boolean hasDragon(ItemStack stack) {
        CompoundTag nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (stack.getItem() instanceof SummoningCrystalItem && nbt != null)
            for (String tagInfo : nbt.keySet())
                if (tagInfo.contains("Dragon"))
                    return true;
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, display, tooltip, type);
        boolean flag = false;
        String desc = "entity.iceandfire.fire_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_ICE.get()) desc = "entity.iceandfire.ice_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_LIGHTNING.get()) desc = "entity.iceandfire.lightning_dragon";
        CompoundTag nbt = stack.get(IafDataComponents.CRYSTAL_DRAGON_DATA.get());
        if (nbt != null)
            for (String tagInfo : nbt.keySet())
                if (tagInfo.contains("Dragon")) {
                    CompoundTag dragonTag = nbt.getCompoundOrEmpty(tagInfo);
                    String dragonName = I18n.get(desc);
                    dragonName = dragonTag.getStringOr("CustomName", dragonName);
                    tooltip.accept(Component.translatable("item.iceandfire.summoning_crystal.bound", dragonName).withStyle(ChatFormatting.GRAY));
                    flag = true;
                }
        if (!flag) {
            tooltip.accept(Component.translatable("item.iceandfire.summoning_crystal.desc_0").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("item.iceandfire.summoning_crystal.desc_1").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null || !hasDragon(context.getItemInHand())) return InteractionResult.PASS;
        if (context.getPlayer() instanceof ServerPlayer player)
            DragonSummoning.request(player, context.getHand(), context.getClickedPos().relative(context.getClickedFace()));
        return InteractionResult.SUCCESS;
    }
}
