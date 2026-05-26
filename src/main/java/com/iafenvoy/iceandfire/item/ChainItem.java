package com.iafenvoy.iceandfire.item;

import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.entity.ChainTieEntity;
import com.iafenvoy.iceandfire.registry.tag.IafEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainItem extends Item {
    private final boolean sticky;

    public ChainItem(boolean sticky) {
        super(new Properties());
        this.sticky = sticky;
    }

    public static void attachToFence(Player player, Level worldIn, BlockPos fence) {
        double d0 = 30.0D;
        int i = fence.getX();
        int j = fence.getY();
        int k = fence.getZ();

        for (LivingEntity livingEntity : worldIn.getEntitiesOfClass(LivingEntity.class, new AABB((double) i - d0, (double) j - d0, (double) k - d0, (double) i + d0, (double) j + d0, (double) k + d0))) {
            ChainData chainData = ChainData.get(livingEntity);
            if (chainData.isChainedTo(player.getUUID())) {
                ChainTieEntity entityleashknot = ChainTieEntity.getKnotForPosition(worldIn, fence);
                if (entityleashknot == null)
                    entityleashknot = ChainTieEntity.createTie(worldIn, fence);
                chainData.removeChain(player.getUUID());
                chainData.attachChain(entityleashknot.getUUID());
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("item.iceandfire.chain.desc_0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.iceandfire.chain.desc_1").withStyle(ChatFormatting.GRAY));
        if (this.sticky) {
            tooltip.add(Component.translatable("item.iceandfire.chain_sticky.desc_2").withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("item.iceandfire.chain_sticky.desc_3").withStyle(ChatFormatting.GREEN));
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, @NotNull Player playerIn, LivingEntity target, @NotNull InteractionHand hand) {
        if (target.getType().is(IafEntityTags.CHAIN_UNTIEABLE)) return InteractionResult.PASS;

        ChainData targetData = ChainData.get(target);
        if (targetData.isChainedTo(playerIn.getUUID()))
            return InteractionResult.PASS;

        if (this.sticky) {
            double d0 = 60.0D;
            double i = playerIn.getX();
            double j = playerIn.getY();
            double k = playerIn.getZ();
            List<LivingEntity> nearbyEntities = playerIn.level().getEntitiesOfClass(LivingEntity.class, new AABB(i - d0, j - d0, k - d0, i + d0, j + d0, k + d0), livingEntity -> true);

            if (playerIn.isShiftKeyDown()) {
                targetData.clearChains();
                for (LivingEntity livingEntity : nearbyEntities)
                    ChainData.get(livingEntity).removeChain(target.getUUID());
                return InteractionResult.SUCCESS;
            }

            AtomicBoolean flag = new AtomicBoolean(false);

            for (LivingEntity livingEntity : nearbyEntities) {
                ChainData nearbyData = ChainData.get(livingEntity);
                if (nearbyData.isChainedTo(playerIn.getUUID())) {
                    targetData.removeChain(playerIn.getUUID());
                    nearbyData.removeChain(playerIn.getUUID());
                    nearbyData.attachChain(target.getUUID());
                    flag.set(true);
                }
            }
            if (!flag.get()) targetData.attachChain(playerIn.getUUID());
        } else targetData.attachChain(playerIn.getUUID());

        if (!playerIn.isCreative())
            stack.shrink(1);

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();

        if (!(block instanceof WallBlock)) {
            return InteractionResult.PASS;
        } else {
            if (!context.getLevel().isClientSide)
                attachToFence(context.getPlayer(), context.getLevel(), context.getClickedPos());
            return InteractionResult.SUCCESS;
        }
    }
}
