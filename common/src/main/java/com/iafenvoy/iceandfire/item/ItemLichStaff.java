package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.entity.EntityDreadLichSkull;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemLichStaff extends Item {
    public ItemLichStaff() {
        super(new Settings().maxDamage(100));
    }

    @Override
    public boolean canRepair(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == IafItems.DREAD_SHARD.get() || super.canRepair(toRepair, repair);
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getStackInHand(hand);
        if (!worldIn.isClient) {
            playerIn.setCurrentHand(hand);
            playerIn.swingHand(hand);
            double d2 = playerIn.getRotationVector().x;
            double d3 = playerIn.getRotationVector().y;
            double d4 = playerIn.getRotationVector().z;
            float inaccuracy = 1.0F;
            d2 = d2 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            d3 = d3 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            d4 = d4 + playerIn.getRandom().nextGaussian() * 0.007499999832361937D * inaccuracy;
            EntityDreadLichSkull charge = new EntityDreadLichSkull(IafEntities.DREAD_LICH_SKULL.get(), worldIn, playerIn, 6);
            charge.setVelocity(playerIn.getPitch(), playerIn.getYaw(), 0.0F, 7.0F, 1.0F);
            charge.setPosition(playerIn.getX(), playerIn.getY() + 1, playerIn.getZ());
            worldIn.spawnEntity(charge);
            charge.setVelocity(d2, d3, d4, 1, 1);
            playerIn.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1F, 0.75F + 0.5F * playerIn.getRandom().nextFloat());
            itemStackIn.damage(1, playerIn, LivingEntity.getSlotForHand(hand));
            playerIn.getItemCooldownManager().set(this, 4);
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, itemStackIn);
    }
}