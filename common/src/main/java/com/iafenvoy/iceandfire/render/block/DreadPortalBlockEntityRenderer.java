package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.entity.DreadPortalBlockEntity;
import com.iafenvoy.iceandfire.registry.IafRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class DreadPortalBlockEntityRenderer<T extends DreadPortalBlockEntity> implements BlockEntityRenderer<T> {
    public static final Identifier DREAD_PORTAL_BACKGROUND = Identifier.of(IceAndFire.MOD_ID, "textures/environment/dread_portal_background.png");
    public static final Identifier DREAD_PORTAL = Identifier.of(IceAndFire.MOD_ID, "textures/environment/dread_portal.png");

    public DreadPortalBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Matrix4f matrix4f = matrixStackIn.peek().getPositionMatrix();
        VertexConsumer consumer = bufferIn.getBuffer(this.renderType());
        // z = 1
        consumer.vertex(matrix4f, 0, 0, 1).color(-1);
        consumer.vertex(matrix4f, 1, 0, 1).color(-1);
        consumer.vertex(matrix4f, 1, 1, 1).color(-1);
        consumer.vertex(matrix4f, 0, 1, 1).color(-1);

        // z = 0
        consumer.vertex(matrix4f, 0, 0, 0).color(-1);
        consumer.vertex(matrix4f, 0, 1, 0).color(-1);
        consumer.vertex(matrix4f, 1, 1, 0).color(-1);
        consumer.vertex(matrix4f, 1, 0, 0).color(-1);

        // x = 0
        consumer.vertex(matrix4f, 0, 0, 0).color(-1);
        consumer.vertex(matrix4f, 0, 0, 1).color(-1);
        consumer.vertex(matrix4f, 0, 1, 1).color(-1);
        consumer.vertex(matrix4f, 0, 1, 0).color(-1);

        // x = 1
        consumer.vertex(matrix4f, 1, 0, 1).color(-1);
        consumer.vertex(matrix4f, 1, 0, 0).color(-1);
        consumer.vertex(matrix4f, 1, 1, 0).color(-1);
        consumer.vertex(matrix4f, 1, 1, 1).color(-1);

        // y = 1
        consumer.vertex(matrix4f, 0, 1, 0).color(-1);
        consumer.vertex(matrix4f, 0, 1, 1).color(-1);
        consumer.vertex(matrix4f, 1, 1, 1).color(-1);
        consumer.vertex(matrix4f, 1, 1, 0).color(-1);

        // y = 0
        consumer.vertex(matrix4f, 0, 0, 0).color(-1);
        consumer.vertex(matrix4f, 1, 0, 0).color(-1);
        consumer.vertex(matrix4f, 1, 0, 1).color(-1);
        consumer.vertex(matrix4f, 0, 0, 1).color(-1);
    }

    protected RenderLayer renderType() {
        return IafRenderLayers.getDreadlandsPortal();
    }
}