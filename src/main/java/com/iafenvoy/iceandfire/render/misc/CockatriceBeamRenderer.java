package com.iafenvoy.iceandfire.render.misc;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.CockatriceEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class CockatriceBeamRenderer {
    public static final RenderType TEXTURE_BEAM = RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cockatrice/beam.png"));

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, PoseStack.Pose entry, float x, float y, float z, int red, int green, int blue, float u, float v) {
        consumer.addVertex(matrix4f, x, y, z).setColor(red, green, blue, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(entry, 0.0F, 1.0F, 0.0F);
    }

    public static void render(Entity entityIn, Entity targetEntity, PoseStack matrixStackIn, MultiBufferSource bufferIn, float partialTicks) {
        float f = 1;
        if (entityIn instanceof CockatriceEntity cockatrice)
            f = cockatrice.getAttackAnimationScale(partialTicks);

        float f1 = (float) entityIn.level().getGameTime() + partialTicks;
        float f2 = f1 * 0.5F % 1.0F;
        float f3 = entityIn.getEyeHeight();
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, f3, 0.0D);
        Vec3 Vector3d = getPosition(targetEntity, (double) targetEntity.getBbHeight() * 0.5D, partialTicks);
        Vec3 Vector3d1 = getPosition(entityIn, f3, partialTicks);
        Vec3 Vector3d2 = Vector3d.subtract(Vector3d1);
        float f4 = (float) (Vector3d2.length() + 1.0D);
        Vector3d2 = Vector3d2.normalize();
        float f5 = (float) Math.acos(Vector3d2.y);
        float f6 = (float) Math.atan2(Vector3d2.z, Vector3d2.x);
        matrixStackIn.mulPose(Axis.YP.rotation((float) Math.PI / 2.0F - f6));
        matrixStackIn.mulPose(Axis.XP.rotation(f5));
        float f7 = f1 * 0.05F * -1.5F;
        float f8 = f * f;
        int j = 64 + (int) (f8 * 191.0F);
        int k = 32 + (int) (f8 * 191.0F);
        int l = 128 - (int) (f8 * 64.0F);
        float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
        float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
        float f13 = Mth.cos(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f14 = Mth.sin(f7 + ((float) Math.PI / 4F)) * 0.282F;
        float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
        float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
        float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
        float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
        float f19 = Mth.cos(f7 + (float) Math.PI) * 0.2F;
        float f20 = Mth.sin(f7 + (float) Math.PI) * 0.2F;
        float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
        float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
        float f23 = Mth.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f24 = Mth.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f25 = Mth.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f26 = Mth.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f29 = -1.0F + f2;
        float f30 = f4 * 2.5F + f29;
        VertexConsumer buffer = bufferIn.getBuffer(TEXTURE_BEAM);
        PoseStack.Pose entry = matrixStackIn.last();
        Matrix4f matrix4f = entry.pose();
        vertex(buffer, matrix4f, entry, f19, f4, f20, j, k, l, 0.4999F, f30);
        vertex(buffer, matrix4f, entry, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
        vertex(buffer, matrix4f, entry, f21, 0.0F, f22, j, k, l, 0.0F, f29);
        vertex(buffer, matrix4f, entry, f21, f4, f22, j, k, l, 0.0F, f30);
        vertex(buffer, matrix4f, entry, f23, f4, f24, j, k, l, 0.4999F, f30);
        vertex(buffer, matrix4f, entry, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
        vertex(buffer, matrix4f, entry, f25, 0.0F, f26, j, k, l, 0.0F, f29);
        vertex(buffer, matrix4f, entry, f25, f4, f26, j, k, l, 0.0F, f30);
        float f31 = 0.0F;
        if (entityIn.tickCount % 2 == 0) f31 = 0.5F;

        vertex(buffer, matrix4f, entry, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
        vertex(buffer, matrix4f, entry, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
        vertex(buffer, matrix4f, entry, f17, f4, f18, j, k, l, 1.0F, f31);
        vertex(buffer, matrix4f, entry, f15, f4, f16, j, k, l, 0.5F, f31);
        matrixStackIn.popPose();
    }

    private static Vec3 getPosition(Entity LivingEntityIn, double p_177110_2_, float p_177110_4_) {
        double d0 = LivingEntityIn.xOld + (LivingEntityIn.getX() - LivingEntityIn.xOld) * (double) p_177110_4_;
        double d1 = p_177110_2_ + LivingEntityIn.yOld + (LivingEntityIn.getY() - LivingEntityIn.yOld) * (double) p_177110_4_;
        double d2 = LivingEntityIn.zOld + (LivingEntityIn.getZ() - LivingEntityIn.zOld) * (double) p_177110_4_;
        return new Vec3(d0, d1, d2);
    }
}
