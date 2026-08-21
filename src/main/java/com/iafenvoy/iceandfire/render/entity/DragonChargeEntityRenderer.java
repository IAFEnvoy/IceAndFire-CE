package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class DragonChargeEntityRenderer extends EntityRenderer<Fireball, LegacyEntityRenderState<Fireball>> {
    public final boolean isFire;

    public DragonChargeEntityRenderer(EntityRendererProvider.Context context, boolean isFire) {
        super(context);
        this.isFire = isFire;
    }

    @SuppressWarnings("deprecation")
    public @NotNull Identifier getTextureLocation(@NotNull Fireball entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public LegacyEntityRenderState<Fireball> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(Fireball entity, LegacyEntityRenderState<Fireball> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<Fireball> state, PoseStack matrixStackIn, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, 0.5D, 0.0D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrixStackIn.translate(-0.5D, -0.5D, 0.5D);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(90.0F));
        BlockModelRenderState blockState = new BlockModelRenderState();
        Minecraft.getInstance().getBlockModelResolver().update(blockState, this.isFire ? Blocks.MAGMA_BLOCK.defaultBlockState() : IafBlocks.DRAGON_ICE.get().defaultBlockState(), net.minecraft.client.renderer.block.model.BlockDisplayContext.create());
        blockState.submitMultiLayer(matrixStackIn, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, camera);
    }
}
