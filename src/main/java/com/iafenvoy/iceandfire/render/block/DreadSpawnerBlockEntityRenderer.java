package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.item.block.entity.DreadSpawnerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class DreadSpawnerBlockEntityRenderer<T extends DreadSpawnerBlockEntity> implements BlockEntityRenderer<T, SpawnerRenderState> {
    private final EntityRenderDispatcher entityRenderer;

    public DreadSpawnerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    @Override
    public @NonNull SpawnerRenderState createRenderState() {
        return new SpawnerRenderState();
    }

    @Override
    public void extractRenderState(@NonNull T blockEntity, @NonNull SpawnerRenderState state, float partialTick, @NonNull Vec3 cameraPos, ModelFeatureRenderer.@NonNull CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        Level level = blockEntity.getLevel();
        if (level == null) return;
        BaseSpawner spawner = blockEntity.getLogic();
        Entity entity = spawner.getOrCreateDisplayEntity(level, blockEntity.getBlockPos());
        if (entity == null) return;
        // Mirrors TrialSpawnerRenderer.extractSpawnerData, which is package private.
        state.displayEntity = this.entityRenderer.extractEntity(entity, partialTick);
        state.displayEntity.lightCoords = state.lightCoords;
        state.spin = (float) Mth.lerp(partialTick, spawner.getOSpin(), spawner.getSpin()) * 10.0F;
        state.scale = 0.53125F;
        float size = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if (size > 1.0F) state.scale /= size;
    }

    @Override
    public void submit(@NonNull SpawnerRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        if (state.displayEntity != null)
            SpawnerRenderer.submitEntityInSpawner(poseStack, submitNodeCollector, state.displayEntity, this.entityRenderer, state.spin, state.scale, camera);
    }
}
