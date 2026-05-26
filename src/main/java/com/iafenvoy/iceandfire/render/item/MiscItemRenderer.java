package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.item.block.PixieHouseBlock;
import com.iafenvoy.iceandfire.item.block.entity.DreadPortalBlockEntity;
import com.iafenvoy.iceandfire.item.block.entity.GhostChestBlockEntity;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.render.block.PixieHouseBlockEntityRenderer;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class MiscItemRenderer implements DynamicItemRenderer {
    private final PixieHouseBlockEntityRenderer<?> pixieHouseBlockEntityRenderer;
    private final GhostChestBlockEntity chest = new GhostChestBlockEntity(BlockPos.ZERO, IafBlocks.GHOST_CHEST.get().defaultBlockState());
    private final DreadPortalBlockEntity portal = new DreadPortalBlockEntity(BlockPos.ZERO, IafBlocks.DREAD_PORTAL.get().defaultBlockState());

    public MiscItemRenderer() {
        this.pixieHouseBlockEntityRenderer = new PixieHouseBlockEntityRenderer<>(null);
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext type, PoseStack stackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockEntityRenderDispatcher blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
        if (stack.getItem() == IafBlocks.GHOST_CHEST.get().asItem())
            blockEntityRenderDispatcher.renderItem(this.chest, stackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        if (stack.getItem() == IafBlocks.DREAD_PORTAL.get().asItem())
            blockEntityRenderDispatcher.renderItem(this.portal, stackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof PixieHouseBlock block) {
            this.pixieHouseBlockEntityRenderer.metaOverride = (BlockItem) stack.getItem();
            this.pixieHouseBlockEntityRenderer.render(null, 0, stackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        }
    }
}
