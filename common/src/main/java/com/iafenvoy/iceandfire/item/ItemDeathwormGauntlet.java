package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ItemDeathwormGauntlet extends Item {
    public ItemDeathwormGauntlet() {
        super(new Settings().maxDamage(500));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 10;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getStackInHand(hand);
        playerIn.setCurrentHand(hand);
        return new TypedActionResult<>(ActionResult.PASS, itemStackIn);
    }

    @Override
    public void usageTick(World level, LivingEntity entity, ItemStack stack, int remainingUseTicks) {
        NbtCompound tag = stack.getOrCreateNbt();
        if (tag.getInt("HolderID") != entity.getId()) tag.putInt("HolderID", entity.getId());
        IafEntityData.get(entity).miscData.setLungeTicks(this.getMaxUseTime(stack) - remainingUseTicks);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity LivingEntity, int timeLeft) {
        NbtCompound tag = stack.getOrCreateNbt();
        if (tag.getInt("HolderID") != -1) tag.putInt("HolderID", -1);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            Vec3d Vector3d = player.getRotationVec(1.0F).normalize();
            double range = 5;
            for (LivingEntity livingEntity : world.getEntitiesByClass(LivingEntity.class, new Box(player.getX() - range, player.getY() - range, player.getZ() - range, player.getX() + range, player.getY() + range, player.getZ() + range), livingEntity -> livingEntity != player)) {
                Vec3d delta = new Vec3d(livingEntity.getX() - player.getX(), livingEntity.getY() - player.getY(), livingEntity.getZ() - player.getZ());
                double d0 = delta.length();
                delta = delta.normalize();
                double d1 = Vector3d.dotProduct(delta);
                boolean canSee = d1 > 1.0D - 0.5D / d0 && player.canSee(livingEntity);
                if (canSee) {
                    livingEntity.damage(world.damageSources.playerAttack(player), 3F);
                    livingEntity.takeKnockback(0.5F, livingEntity.getX() - player.getX(), livingEntity.getZ() - player.getZ());
                }
            }
            player.getItemCooldownManager().set(this, 20);
        }
        user.playSound(IafSounds.DEATHWORM_ATTACK.get(), 1F, 1F);
        NbtCompound tag = stack.getOrCreateNbt();
        if (tag.getInt("HolderID") != -1) tag.putInt("HolderID", -1);

        return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        tooltip.add(Text.translatable("item.iceandfire.legendary_weapon.desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.deathworm_gauntlet.desc_0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.iceandfire.deathworm_gauntlet.desc_1").formatted(Formatting.GRAY));
    }
}
