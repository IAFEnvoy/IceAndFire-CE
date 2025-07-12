package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.HippogryphTypes;
import com.iafenvoy.iceandfire.entity.EntityHippogryphEgg;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ItemHippogryphEgg extends Item {
    public ItemHippogryphEgg() {
        super(new Settings().maxCount(1));
    }

    public static ItemStack createEggStack(HippogryphTypes parent1, HippogryphTypes parent2) {
        HippogryphTypes eggType = ThreadLocalRandom.current().nextBoolean() ? parent1 : parent2;
        ItemStack stack = new ItemStack(IafItems.HIPPOGRYPH_EGG.get());
        NbtCompound tag = new NbtCompound();
        tag.putString("EggType", eggType.getName());
        stack.setNbt(tag);
        return stack;
    }


    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getStackInHand(handIn);

        if (!playerIn.isCreative())
            itemstack.decrement(1);

        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClient) {
            EntityHippogryphEgg entityegg = new EntityHippogryphEgg(IafEntities.HIPPOGRYPH_EGG.get(), worldIn,
                    playerIn, itemstack);
            entityegg.setVelocity(playerIn, playerIn.getPitch(), playerIn.getYaw(), 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(entityegg);
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, itemstack);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        NbtCompound tag = stack.getNbt();
        String eggOrdinal = "";
        if (tag != null)
            eggOrdinal = tag.getString("EggType");
        tooltip.add(Text.translatable("entity.iceandfire.hippogryph." + eggOrdinal).formatted(Formatting.GRAY));
    }
}
