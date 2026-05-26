package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.BestiaryPage;
import com.iafenvoy.iceandfire.item.component.BestiaryPageComponent;
import com.iafenvoy.iceandfire.registry.IafBestiaryPages;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.screen.menu.BestiaryMenu;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class BestiaryItem extends Item implements MenuProvider {
    public BestiaryItem() {
        super(new Properties().stacksTo(1).component(IafDataComponents.BESTIARY_PAGES.get(), new BestiaryPageComponent(List.of(IafBestiaryPages.INTRODUCTION))));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (playerIn instanceof ServerPlayer serverPlayer)
            serverPlayer.openMenu(this, buf -> {
                CompoundTag compound = new CompoundTag();
                compound.put("data", ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, playerIn.getItemInHand(handIn)).resultOrPartial(IceAndFire.LOGGER::error).orElse(new CompoundTag()));
                buf.writeNbt(compound);
            });
        return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344)) {
            tooltip.add(Component.translatable("bestiary.contains").withStyle(ChatFormatting.GRAY));
            BestiaryPageComponent component = stack.get(IafDataComponents.BESTIARY_PAGES.get());
            if (component != null)
                for (BestiaryPage page : component.pages())
                    tooltip.add(Component.literal(ChatFormatting.WHITE + "-").append(Component.translatable("bestiary." + page.name().toLowerCase(Locale.ROOT))).withStyle(ChatFormatting.GRAY));
        } else tooltip.add(Component.translatable("bestiary.hold_shift").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("bestiary_gui");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new BestiaryMenu(syncId, playerInventory);
    }
}
