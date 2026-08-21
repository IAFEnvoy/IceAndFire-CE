package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface BlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState> extends net.neoforged.neoforge.client.extensions.IBlockEntityRendererExtension<T> {
    S createRenderState();

    default void extractRenderState(
        T blockEntity, S state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
    }

    void submit(final S state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera);

    default boolean shouldRenderOffScreen() {
        return false;
    }

    default int getViewDistance() {
        return 64;
    }

    default boolean shouldRender(T blockEntity, Vec3 cameraPosition) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(cameraPosition, this.getViewDistance());
    }
}
