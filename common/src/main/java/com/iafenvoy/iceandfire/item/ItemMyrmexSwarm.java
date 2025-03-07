package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.entity.EntityMyrmexSwarmer;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class ItemMyrmexSwarm extends Item {
    private final boolean jungle;

    public ItemMyrmexSwarm(boolean jungle) {
        super(new Settings().maxCount(1));
        this.jungle = jungle;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getStackInHand(hand);
        playerIn.setCurrentHand(hand);
        playerIn.swingHand(hand);
        if (!playerIn.isCreative()) {
            itemStackIn.decrement(1);
            playerIn.getItemCooldownManager().set(this, 20);
        }
        for (int i = 0; i < 5; i++) {
            EntityMyrmexSwarmer myrmex = new EntityMyrmexSwarmer(IafEntities.MYRMEX_SWARMER.get(), worldIn);
            myrmex.setPosition(playerIn.getX(), playerIn.getY(), playerIn.getZ());
            myrmex.setJungleVariant(this.jungle);
            myrmex.setSummonedBy(playerIn);
            myrmex.setFlying(true);
            if (!worldIn.isClient)
                worldIn.spawnEntity(myrmex);
        }
        playerIn.getItemCooldownManager().set(this, 1800);
        return new TypedActionResult<>(ActionResult.PASS, itemStackIn);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.myrmex_swarm.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.myrmex_swarm.desc_1").formatted(Formatting.GRAY));
    }
}