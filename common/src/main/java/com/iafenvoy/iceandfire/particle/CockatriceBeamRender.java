package com.iafenvoy.iceandfire.particle;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityCockatrice;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class CockatriceBeamRender {
    public static final RenderLayer TEXTURE_BEAM = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/cockatrice/beam.png"));

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, MatrixStack.Entry entry, float x, float y, float z, int red, int green, int blue, float u, float v) {
        consumer.vertex(matrix4f, x, y, z).color(red, green, blue, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(entry, 0.0F, 1.0F, 0.0F);
    }

    public static void render(Entity entityIn, Entity targetEntity, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, float partialTicks) {
        float f = 1;
        if (entityIn instanceof EntityCockatrice cockatrice)
            f = cockatrice.getAttackAnimationScale(partialTicks);

        float f1 = (float) entityIn.getWorld().getTime() + partialTicks;
        float f2 = f1 * 0.5F % 1.0F;
        float f3 = entityIn.getStandingEyeHeight();
        matrixStackIn.push();
        matrixStackIn.translate(0.0D, f3, 0.0D);
        Vec3d Vector3d = getPosition(targetEntity, (double) targetEntity.getHeight() * 0.5D, partialTicks);
        Vec3d Vector3d1 = getPosition(entityIn, f3, partialTicks);
        Vec3d Vector3d2 = Vector3d.subtract(Vector3d1);
        float f4 = (float) (Vector3d2.length() + 1.0D);
        Vector3d2 = Vector3d2.normalize();
        float f5 = (float) Math.acos(Vector3d2.y);
        float f6 = (float) Math.atan2(Vector3d2.z, Vector3d2.x);
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotation((float) Math.PI / 2.0F - f6));
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotation(f5));
        float f7 = f1 * 0.05F * -1.5F;
        float f8 = f * f;
        int j = 64 + (int) (f8 * 191.0F);
        int k = 32 + (int) (f8 * 191.0F);
        int l = 128 - (int) (f8 * 64.0F);
        float f11 = MathHelper.cos(f7 + 2.3561945F) * 0.282F;
        float f12 = MathHelper.sin(f7 + 2.3561945F) * 0.282F;
        float f13 = MathHelper.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f14 = MathHelper.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f15 = MathHelper.cos(f7 + 3.926991F) * 0.282F;
        float f16 = MathHelper.sin(f7 + 3.926991F) * 0.282F;
        float f17 = MathHelper.cos(f7 + 5.4977875F) * 0.282F;
        float f18 = MathHelper.sin(f7 + 5.4977875F) * 0.282F;
        float f19 = MathHelper.cos(f7 + (float) Math.PI) * 0.2F;
        float f20 = MathHelper.sin(f7 + (float) Math.PI) * 0.2F;
        float f21 = MathHelper.cos(f7 + 0.0F) * 0.2F;
        float f22 = MathHelper.sin(f7 + 0.0F) * 0.2F;
        float f23 = MathHelper.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f24 = MathHelper.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f25 = MathHelper.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f26 = MathHelper.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f29 = -1.0F + f2;
        float f30 = f4 * 2.5F + f29;
        VertexConsumer buffer = bufferIn.getBuffer(TEXTURE_BEAM);
        MatrixStack.Entry entry = matrixStackIn.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        vertex(buffer, matrix4f, entry, f19, f4, f20, j, k, l, 0.4999F, f30);
        vertex(buffer, matrix4f, entry, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
        vertex(buffer, matrix4f, entry, f21, 0.0F, f22, j, k, l, 0.0F, f29);
        vertex(buffer, matrix4f, entry, f21, f4, f22, j, k, l, 0.0F, f30);
        vertex(buffer, matrix4f, entry, f23, f4, f24, j, k, l, 0.4999F, f30);
        vertex(buffer, matrix4f, entry, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
        vertex(buffer, matrix4f, entry, f25, 0.0F, f26, j, k, l, 0.0F, f29);
        vertex(buffer, matrix4f, entry, f25, f4, f26, j, k, l, 0.0F, f30);
        float f31 = 0.0F;
        if (entityIn.age % 2 == 0) f31 = 0.5F;

        vertex(buffer, matrix4f, entry, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
        vertex(buffer, matrix4f, entry, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
        vertex(buffer, matrix4f, entry, f17, f4, f18, j, k, l, 1.0F, f31);
        vertex(buffer, matrix4f, entry, f15, f4, f16, j, k, l, 0.5F, f31);
        matrixStackIn.pop();
    }

    private static Vec3d getPosition(Entity LivingEntityIn, double p_177110_2_, float p_177110_4_) {
        double d0 = LivingEntityIn.lastRenderX + (LivingEntityIn.getX() - LivingEntityIn.lastRenderX) * (double) p_177110_4_;
        double d1 = p_177110_2_ + LivingEntityIn.lastRenderY + (LivingEntityIn.getY() - LivingEntityIn.lastRenderY) * (double) p_177110_4_;
        double d2 = LivingEntityIn.lastRenderZ + (LivingEntityIn.getZ() - LivingEntityIn.lastRenderZ) * (double) p_177110_4_;
        return new Vec3d(d0, d1, d2);
    }
}
