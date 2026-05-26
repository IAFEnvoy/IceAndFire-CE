package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.entity.DreadPortalBlockEntity;
import com.iafenvoy.iceandfire.registry.IafRenderLayers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class DreadPortalBlockEntityRenderer<T extends DreadPortalBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation DREAD_PORTAL_BACKGROUND = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/environment/dread_portal_background.png");
    public static final ResourceLocation DREAD_PORTAL = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/environment/dread_portal.png");

    public DreadPortalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        Matrix4f matrix4f = matrices.last().pose();
        VertexConsumer consumer = vertexConsumers.getBuffer(this.renderType());
        // z = 1
        consumer.addVertex(matrix4f, 0, 0, 1).setColor(-1);
        consumer.addVertex(matrix4f, 1, 0, 1).setColor(-1);
        consumer.addVertex(matrix4f, 1, 1, 1).setColor(-1);
        consumer.addVertex(matrix4f, 0, 1, 1).setColor(-1);
        // z = 0
        consumer.addVertex(matrix4f, 0, 0, 0).setColor(-1);
        consumer.addVertex(matrix4f, 0, 1, 0).setColor(-1);
        consumer.addVertex(matrix4f, 1, 1, 0).setColor(-1);
        consumer.addVertex(matrix4f, 1, 0, 0).setColor(-1);
        // x = 0
        consumer.addVertex(matrix4f, 0, 0, 0).setColor(-1);
        consumer.addVertex(matrix4f, 0, 0, 1).setColor(-1);
        consumer.addVertex(matrix4f, 0, 1, 1).setColor(-1);
        consumer.addVertex(matrix4f, 0, 1, 0).setColor(-1);
        // x = 1
        consumer.addVertex(matrix4f, 1, 0, 1).setColor(-1);
        consumer.addVertex(matrix4f, 1, 0, 0).setColor(-1);
        consumer.addVertex(matrix4f, 1, 1, 0).setColor(-1);
        consumer.addVertex(matrix4f, 1, 1, 1).setColor(-1);
        // y = 1
        consumer.addVertex(matrix4f, 0, 1, 0).setColor(-1);
        consumer.addVertex(matrix4f, 0, 1, 1).setColor(-1);
        consumer.addVertex(matrix4f, 1, 1, 1).setColor(-1);
        consumer.addVertex(matrix4f, 1, 1, 0).setColor(-1);
        // y = 0
        consumer.addVertex(matrix4f, 0, 0, 0).setColor(-1);
        consumer.addVertex(matrix4f, 1, 0, 0).setColor(-1);
        consumer.addVertex(matrix4f, 1, 0, 1).setColor(-1);
        consumer.addVertex(matrix4f, 0, 0, 1).setColor(-1);
    }

    protected RenderType renderType() {
        return IafRenderLayers.getDreadlandsPortal();
    }
}