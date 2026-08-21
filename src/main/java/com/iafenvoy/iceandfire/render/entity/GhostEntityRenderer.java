package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.GhostEntity;
import com.iafenvoy.iceandfire.registry.IafRenderTypes;
import com.iafenvoy.iceandfire.render.model.GhostModel;
import com.iafenvoy.iceandfire.util.Color4i;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;

public class GhostEntityRenderer extends LegacyMobRenderer<GhostEntity, GhostModel> {

    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/ghost/ghost_white.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/ghost/ghost_blue.png");
    public static final Identifier TEXTURE_2 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/ghost/ghost_green.png");
    public static final Identifier TEXTURE_SHOPPING_LIST = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/ghost/haunted_shopping_list.png");

    public GhostEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GhostModel(0.0F), 0.55F);
    }

    public static Identifier getGhostOverlayForType(int ghost) {
        return switch (ghost) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case -1 -> TEXTURE_SHOPPING_LIST;
            default -> TEXTURE_0;
        };
    }

    @Override
    public void submit(LegacyEntityRenderState<GhostEntity> state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        GhostEntity ghost = state.entity;
        this.shadowRadius = 0.0F;
        if (ghost.isInvisible()) return;

        float partialTick = state.partialTick;
        float walkSpeed = ghost.isPassenger() || !ghost.isAlive() ? 0.0F : Math.min(ghost.walkAnimation.speed(), 1.0F);
        float walkPos = ghost.isPassenger() || !ghost.isAlive() ? 0.0F : ghost.walkAnimation.position();
        if (ghost.isBaby()) walkPos *= 3.0F;
        this.model.prepareMobModel(ghost, walkPos, walkSpeed, partialTick);
        this.model.setupAnim(ghost, walkPos, walkSpeed, state.ageInTicks, 0.0F, ghost.getViewXRot(partialTick));

        float alpha = this.getAlphaForRender(ghost, partialTick);
        int overlay = OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(ghost.hurtTime > 0));
        RenderType renderType = ghost.isDaytimeMode() ? IafRenderTypes.getGhostDaytime(this.getTextureLocation(ghost)) : IafRenderTypes.getGhost(this.getTextureLocation(ghost));
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0D, -1.501F, 0.0D);
        if (ghost.isHauntedShoppingList()) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.8F + Mth.sin((ghost.tickCount + partialTick) * 0.15F) * 0.1F, 0.0D);
            poseStack.scale(0.6F, 0.6F, 0.6F);
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            this.submitShoppingListFace(poseStack, collector, renderType, overlay, alpha, 0.5F, 1.0F, state.lightCoords);
            poseStack.popPose();
            this.submitShoppingListFace(poseStack, collector, renderType, overlay, alpha, 0.0F, 0.5F, state.lightCoords);
            poseStack.popPose();
        } else {
            collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                this.model.renderToBuffer(modelStack, buffer, state.lightCoords, overlay, new Color4i(1.0F, 1.0F, 1.0F, alpha).getIntValue());
            });
        }
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private void submitShoppingListFace(PoseStack poseStack, SubmitNodeCollector collector, RenderType renderType, int overlay, float alpha, float minU, float maxU, int light) {
        collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            Matrix4f matrix = pose.pose();
            this.drawVertex(matrix, pose, buffer, overlay, (int) (alpha * 255), -1, -2, 0, maxU, 0.0F, 0, 1, 0, light);
            this.drawVertex(matrix, pose, buffer, overlay, (int) (alpha * 255), 1, -2, 0, minU, 0.0F, 0, 1, 0, light);
            this.drawVertex(matrix, pose, buffer, overlay, (int) (alpha * 255), 1, 2, 0, minU, 1.0F, 0, 1, 0, light);
            this.drawVertex(matrix, pose, buffer, overlay, (int) (alpha * 255), -1, 2, 0, maxU, 1.0F, 0, 1, 0, light);
        });
    }

    public float getAlphaForRender(GhostEntity entityIn, float partialTicks) {
        if (entityIn.isDaytimeMode())
            return Mth.clamp((101 - Math.min(entityIn.getDaytimeCounter(), 100)) / 100F, 0, 1);
        return Mth.clamp((Mth.sin((entityIn.tickCount + partialTicks) * 0.1F) + 1F) * 0.5F + 0.1F, 0F, 1F);
    }

    @Override
    public void scale(@NotNull GhostEntity LivingEntityIn, @NotNull PoseStack stack, float partialTickTime) {
    }

    @Override
    public @NotNull Identifier getTextureLocation(GhostEntity ghost) {
        return switch (ghost.getColor()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case -1 -> TEXTURE_SHOPPING_LIST;
            default -> TEXTURE_0;
        };
    }

    public void drawVertex(Matrix4f stack, PoseStack.Pose entry, VertexConsumer builder, int packedRed, int alphaInt, int x, int y, int z, float u, float v, int lightmap, int lightmap3, int lightmap2, int lightmap4) {
        builder.addVertex(stack, (float) x, (float) y, (float) z).setColor(255, 255, 255, alphaInt).setUv(u, v).setOverlay(packedRed).setLight(lightmap4).setNormal(entry, lightmap, lightmap2, lightmap3);
    }
}
