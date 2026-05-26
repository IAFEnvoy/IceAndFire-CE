package com.iafenvoy.iceandfire.item.tool;

import com.iafenvoy.iceandfire.registry.tag.IafItemTags;
import com.iafenvoy.uranus.object.RegistryHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class DragonBowItem extends BowItem {
    private static final Predicate<ItemStack> DRAGON_ARROWS = stack -> stack.is(IafItemTags.DRAGON_ARROWS);

    public DragonBowItem() {
        super(new Properties().durability(584));
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return DRAGON_ARROWS.or(ARROW_ONLY);
    }

    //Copied from parent
    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            boolean bl = playerEntity.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(world.registryAccess(), Enchantments.INFINITY), stack) > 0;
            ItemStack itemStack = playerEntity.getProjectile(stack);
            if (!itemStack.isEmpty() || bl) {
                if (itemStack.isEmpty())
                    itemStack = new ItemStack(Items.ARROW);
                int i = this.getUseDuration(stack, user) - remainingUseTicks;
                float f = getPowerForTime(i);
                if (!((double) f < 0.1)) {
                    boolean bl2 = bl && this.getAllSupportedProjectiles().test(itemStack);
                    if (!world.isClientSide) {
                        ArrowItem arrowItem = (ArrowItem) (itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                        AbstractArrow persistentProjectileEntity = arrowItem.createArrow(world, itemStack, playerEntity, stack);
                        persistentProjectileEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        if (f == 1.0F) persistentProjectileEntity.setCritArrow(true);
                        int j = EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(world.registryAccess(), Enchantments.POWER), stack);
                        if (j > 0)
                            persistentProjectileEntity.setBaseDamage(persistentProjectileEntity.getBaseDamage() + (double) j * 0.5 + 0.5);
                        if (EnchantmentHelper.getItemEnchantmentLevel(RegistryHelper.getEnchantment(world.registryAccess(), Enchantments.FLAME), stack) > 0)
                            persistentProjectileEntity.igniteForSeconds(100);
                        stack.hurtAndBreak(1, playerEntity, LivingEntity.getSlotForHand(user.getUsedItemHand()));
                        if (bl2 || playerEntity.getAbilities().instabuild && (itemStack.is(Items.SPECTRAL_ARROW) || itemStack.is(Items.TIPPED_ARROW)))
                            persistentProjectileEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        world.addFreshEntity(persistentProjectileEntity);
                    }
                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!bl2 && !playerEntity.getAbilities().instabuild) {
                        itemStack.shrink(1);
                        if (itemStack.isEmpty())
                            playerEntity.getInventory().removeItem(itemStack);
                    }
                    playerEntity.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }
}
