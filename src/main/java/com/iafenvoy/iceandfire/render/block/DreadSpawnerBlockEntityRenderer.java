package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.item.block.entity.DreadSpawnerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.jspecify.annotations.NonNull;

public class DreadSpawnerBlockEntityRenderer<T extends DreadSpawnerBlockEntity> implements BlockEntityRenderer<T, BlockEntityRenderState> {
    public DreadSpawnerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
    }
}
