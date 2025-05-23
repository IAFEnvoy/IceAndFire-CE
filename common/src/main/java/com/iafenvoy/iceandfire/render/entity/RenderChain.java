package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.List;

public class RenderChain {
    private static final Identifier TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/misc/chain_link.png");

    public static void render(LivingEntity entityLivingIn, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int lightIn, List<Entity> chainedTo) {
        for (Entity chainTarget : chainedTo) {
            if (chainTarget == null) {
                IceAndFire.LOGGER.warn("Found null value in list of target entities");
                continue;
            }
            try {
                renderLink(entityLivingIn, matrixStackIn, bufferIn, lightIn, chainTarget);
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Could not render chain link for {} connected to {}", entityLivingIn.toString(), chainTarget.toString());
            }
        }
    }

    public static <E extends Entity> void renderLink(LivingEntity entityLivingIn, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int lightIn, E chainTarget) {
        // Most of this code stems from the guardian lasers
        float f3 = entityLivingIn.getHeight() * 0.4f;
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, f3, 0.0D);
        Vec3d vector3d = getPosition(chainTarget, (double) chainTarget.getHeight() * 0.5D);
        Vec3d vector3d1 = getPosition(entityLivingIn, f3);
        Vec3d vector3d2 = vector3d.subtract(vector3d1);
        float f4 = (float) (vector3d2.length() + 0.0D);
        vector3d2 = vector3d2.normalize();
        float f5 = (float) Math.acos(vector3d2.y);
        float f6 = (float) Math.atan2(vector3d2.z, vector3d2.x);
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.PI / 2.0F - f6));
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotation(f5));
        float f7 = -1.0F;
        int j = 255;
        int k = 255;
        int l = 255;
        float f19 = 0;
        float f20 = 0.2F;
        float f21 = 0F;
        float f22 = -0.2F;
        float f23 = MathHelper.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f24 = MathHelper.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f25 = MathHelper.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f26 = MathHelper.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f29 = 0;
        float f30 = f4 + f29;
        float f32 = 0.75F;
        float f31 = f4 + f32;

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture()));
        MatrixStack.Entry entry = matrixStackIn.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        matrixStackIn.push();
        vertex(ivertexbuilder, matrix4f, entry, f19, f4, f20, j, k, l, 0.4999F, f30, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f19, 0.0F, f20, j, k, l, 0.4999F, f29, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f21, 0.0F, f22, j, k, l, 0.0F, f29, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f21, f4, f22, j, k, l, 0.0F, f30, lightIn);

        vertex(ivertexbuilder, matrix4f, entry, f23, f4, f24, j, k, l, 0.4999F, f31, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f23, 0.0F, f24, j, k, l, 0.4999F, f32, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f25, 0.0F, f26, j, k, l, 0.0F, f32, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f25, f4, f26, j, k, l, 0.0F, f31, lightIn);
        matrixStackIn.pop();
        matrixStackIn.pop();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, MatrixStack.Entry entry, float x, float y, float z, int r, int g, int b, float u, float v, int packedLight) {
        consumer.vertex(matrix4f, x, y, z).color(r, g, b, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(packedLight).normal(entry, 0.0F, 1.0F, 0.0F);
    }

    private static Vec3d getPosition(Entity LivingEntityIn, double p_177110_2_) {
        return LivingEntityIn.getPos().add(0, p_177110_2_, 0);
    }

    public static Identifier getTexture() {
        return TEXTURE;
    }
}
