package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.GhostSwordEntity;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class GhostSwordEntityRenderer extends EntityRenderer<GhostSwordEntity, LegacyEntityRenderState<GhostSwordEntity>> {
    public GhostSwordEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    public @NotNull Identifier getTextureLocation(@NotNull GhostSwordEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public LegacyEntityRenderState<GhostSwordEntity> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(GhostSwordEntity entity, LegacyEntityRenderState<GhostSwordEntity> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<GhostSwordEntity> state, PoseStack matrixStackIn, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        GhostSwordEntity entityIn = state.entity;
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(state.partialTick, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(state.partialTick, entityIn.xRotO, entityIn.getXRot())));
        matrixStackIn.translate(0, 0.5F, 0);
        matrixStackIn.scale(2F, 2F, 2F);
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(0.0F));
        matrixStackIn.mulPose(Axis.ZN.rotationDegrees(state.ageInTicks * 30.0F));
        matrixStackIn.translate(0, -0.15F, 0);
        ItemStackRenderState itemState = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForNonLiving(itemState, new ItemStack(IafItems.GHOST_SWORD.get()), ItemDisplayContext.GROUND, entityIn);
        itemState.submit(matrixStackIn, collector, 240, OverlayTexture.NO_OVERLAY, state.outlineColor);
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, camera);
    }
}
