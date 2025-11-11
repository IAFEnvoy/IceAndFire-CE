package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.HippogryphType;
import com.iafenvoy.iceandfire.entity.HippogryphEggEntity;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafHippogryphTypes;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HippogryphEggItem extends Item implements ProjectileItem {
    public HippogryphEggItem() {
        super(new Item.Settings().maxCount(1));
    }

    public static ItemStack createEggStack(HippogryphType parent1, HippogryphType parent2) {
        HippogryphType eggType = ThreadLocalRandom.current().nextBoolean() ? parent1 : parent2;
        ItemStack stack = new ItemStack(IafItems.HIPPOGRYPH_EGG.get());
        stack.set(IafDataComponents.HIPPOGRYPH_EGG.get(), eggType);
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getStackInHand(handIn);
        if (!playerIn.isCreative()) itemstack.decrement(1);
        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isClient) {
            HippogryphEggEntity entityegg = new HippogryphEggEntity(IafEntities.HIPPOGRYPH_EGG.get(), worldIn, playerIn, itemstack);
            entityegg.setVelocity(playerIn, playerIn.getPitch(), playerIn.getYaw(), 0.0F, 1.5F, 1.0F);
            worldIn.spawnEntity(entityegg);
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, itemstack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        HippogryphType eggOrdinal = stack.getOrDefault(IafDataComponents.HIPPOGRYPH_EGG.get(), IafHippogryphTypes.BLACK);
        tooltip.add(Text.translatable("entity.iceandfire.hippogryph." + eggOrdinal.name()).formatted(Formatting.GRAY));
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new HippogryphEggEntity(IafEntities.HIPPOGRYPH_EGG.get(), world, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
}
