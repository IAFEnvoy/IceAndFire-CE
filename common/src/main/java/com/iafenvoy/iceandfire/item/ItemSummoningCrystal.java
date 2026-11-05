package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemSummoningCrystal extends Item {
    public ItemSummoningCrystal() {
        super(new Settings().maxCount(1));
    }

    public static boolean hasDragon(ItemStack stack) {
        return stack.getItem() instanceof ItemSummoningCrystal && stack.hasNbt()
                && stack.getNbt().contains("Dragon", net.minecraft.nbt.NbtElement.COMPOUND_TYPE);
    }

    @Override
    public void onCraft(ItemStack itemStack, World world, PlayerEntity player) {
        itemStack.setNbt(new NbtCompound());
    }

    public ItemStack onItemUseFinish(World worldIn, LivingEntity LivingEntity) {
        return new ItemStack(this);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        boolean flag = false;
        String desc = "entity.iceandfire.fire_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_ICE.get())
            desc = "entity.iceandfire.ice_dragon";
        if (stack.getItem() == IafItems.SUMMONING_CRYSTAL_LIGHTNING.get())
            desc = "entity.iceandfire.lightning_dragon";
        if (stack.getNbt() != null) {
            for (String tagInfo : stack.getNbt().getKeys()) {
                if (tagInfo.contains("Dragon")) {
                    NbtCompound dragonTag = stack.getNbt().getCompound(tagInfo);
                    String dragonName = I18n.translate(desc);
                    if (!dragonTag.getString("CustomName").isEmpty())
                        dragonName = dragonTag.getString("CustomName");
                    tooltip.add(Text.translatable("item.iceandfire.summoning_crystal.bound", dragonName).formatted(Formatting.GRAY));
                    flag = true;
                }
            }
        }
        if (!flag) {
            tooltip.add(Text.translatable("item.iceandfire.summoning_crystal.desc_0").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("item.iceandfire.summoning_crystal.desc_1").formatted(Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() == null || !hasDragon(context.getStack())) return ActionResult.PASS;
        if (context.getWorld().isClient) return ActionResult.SUCCESS;
        if (context.getPlayer() instanceof net.minecraft.server.network.ServerPlayerEntity player)
            com.iafenvoy.iceandfire.world.SummoningCrystalManager.request(player, context.getHand(),
                    context.getBlockPos().offset(context.getSide()));
        return ActionResult.CONSUME;
    }
}
