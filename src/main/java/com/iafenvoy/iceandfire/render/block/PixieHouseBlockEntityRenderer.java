package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.PixieHouseBlock;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.render.model.PixieHouseModel;
import com.iafenvoy.iceandfire.render.model.PixieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class PixieHouseBlockEntityRenderer<T extends PixieHouseBlockEntity> implements BlockEntityRenderer<T, PixieHouseBlockEntityRenderer.State> {
    public static class State extends BlockEntityRenderState {
        private PixieHouseBlockEntity entity;
        private float partialTicks;
    }

    private static final PixieHouseModel HOUSE_MODEL = new PixieHouseModel();
    private static final PixieModel PIXIE_MODEL = new PixieModel();
    private static final RenderType[] HOUSE_TEXTURES = new RenderType[6];

    static {
        for (int i = 0; i < HOUSE_TEXTURES.length; i++)
            HOUSE_TEXTURES[i] = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_" + i + ".png"), false);
    }

    public PixieHouseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T entity, State state, float partialTicks, net.minecraft.world.phys.@NonNull Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTicks, cameraPosition, breakProgress);
        state.entity = entity;
        state.partialTicks = partialTicks;
    }

    @Override
    public void submit(State state, PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        T entity = (T) state.entity;
        int houseType = PixieHouseBlockEntity.getHouseTypeFromBlock(entity.getBlockState().getBlock());
        int rotation = entity.getBlockState().getValue(PixieHouseBlock.FACING).get2DDataValue() * 90;
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.501F, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        if (entity.hasPixie) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.95F, 0.0F);
            poseStack.scale(0.55F, 0.55F, 0.55F);
            PIXIE_MODEL.animateInHouse(entity);
            RenderType pixieTexture = switch (entity.pixieType) {
                case 1 -> JarBlockEntityRenderer.TEXTURE_1;
                case 2 -> JarBlockEntityRenderer.TEXTURE_2;
                case 3 -> JarBlockEntityRenderer.TEXTURE_3;
                case 4 -> JarBlockEntityRenderer.TEXTURE_4;
                case 5 -> JarBlockEntityRenderer.TEXTURE_5;
                default -> JarBlockEntityRenderer.TEXTURE_0;
            };
            RenderType pixieGlow = switch (entity.pixieType) {
                case 1 -> JarBlockEntityRenderer.TEXTURE_1_GLO;
                case 2 -> JarBlockEntityRenderer.TEXTURE_2_GLO;
                case 3 -> JarBlockEntityRenderer.TEXTURE_3_GLO;
                case 4 -> JarBlockEntityRenderer.TEXTURE_4_GLO;
                case 5 -> JarBlockEntityRenderer.TEXTURE_5_GLO;
                default -> JarBlockEntityRenderer.TEXTURE_0_GLO;
            };
            collector.submitCustomGeometry(poseStack, pixieTexture, (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                PIXIE_MODEL.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
            });
            collector.submitCustomGeometry(poseStack, pixieGlow, (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                PIXIE_MODEL.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
            });
            poseStack.popPose();
        }
        collector.submitCustomGeometry(poseStack, HOUSE_TEXTURES[houseType], (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            HOUSE_MODEL.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
        poseStack.popPose();
    }
}
