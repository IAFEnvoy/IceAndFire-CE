package com.iafenvoy.iceandfire.render.misc;

import com.iafenvoy.iceandfire.IceAndFire;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ChainRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/misc/chain_link.png");

    public static void render(LivingEntity entityLivingIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int lightIn, List<UUID> chainedTo) {
        for (UUID uuid : chainedTo) {
            if (uuid == null) {
                IceAndFire.LOGGER.warn("Found null value in list of target entities");
                continue;
            }
            if (Minecraft.getInstance().level == null) continue;
            Entity chainTarget = Minecraft.getInstance().level.entityStorage.getEntityGetter().get(uuid);
            if (chainTarget == null) continue;
            try {
                renderLink(entityLivingIn, matrixStackIn, bufferIn, lightIn, chainTarget);
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Could not render chain link for {} connected to {}", entityLivingIn.toString(), chainTarget.toString());
            }
        }
    }

    public static <E extends Entity> void renderLink(LivingEntity entityLivingIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int lightIn, E chainTarget) {
        // Most of this code stems from the guardian lasers
        float f3 = entityLivingIn.getBbHeight() * 0.4f;
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, f3, 0.0D);
        Vec3 vector3d = getPosition(chainTarget, (double) chainTarget.getBbHeight() * 0.5D);
        Vec3 vector3d1 = getPosition(entityLivingIn, f3);
        Vec3 vector3d2 = vector3d.subtract(vector3d1);
        float f4 = (float) (vector3d2.length() + 0.0D);
        vector3d2 = vector3d2.normalize();
        float f5 = (float) Math.acos(vector3d2.y);
        float f6 = (float) Math.atan2(vector3d2.z, vector3d2.x);
        matrixStackIn.mulPose(Axis.YP.rotation((float) Math.PI / 2.0F - f6));
        matrixStackIn.mulPose(Axis.XP.rotation(f5));
        float f7 = -1.0F;
        int j = 255;
        int k = 255;
        int l = 255;
        float f19 = 0;
        float f20 = 0.2F;
        float f21 = 0F;
        float f22 = -0.2F;
        float f23 = Mth.cos(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f24 = Mth.sin(f7 + ((float) Math.PI / 2F)) * 0.2F;
        float f25 = Mth.cos(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f26 = Mth.sin(f7 + ((float) Math.PI * 1.5F)) * 0.2F;
        float f29 = 0;
        float f30 = f4 + f29;
        float f32 = 0.75F;
        float f31 = f4 + f32;

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityCutoutNoCull(getTexture()));
        PoseStack.Pose entry = matrixStackIn.last();
        Matrix4f matrix4f = entry.pose();
        matrixStackIn.pushPose();
        vertex(ivertexbuilder, matrix4f, entry, f19, f4, f20, j, k, l, 0.4999F, f30, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f19, 0.0F, f20, j, k, l, 0.4999F, f29, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f21, 0.0F, f22, j, k, l, 0.0F, f29, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f21, f4, f22, j, k, l, 0.0F, f30, lightIn);

        vertex(ivertexbuilder, matrix4f, entry, f23, f4, f24, j, k, l, 0.4999F, f31, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f23, 0.0F, f24, j, k, l, 0.4999F, f32, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f25, 0.0F, f26, j, k, l, 0.0F, f32, lightIn);
        vertex(ivertexbuilder, matrix4f, entry, f25, f4, f26, j, k, l, 0.0F, f31, lightIn);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, PoseStack.Pose entry, float x, float y, float z, int r, int g, int b, float u, float v, int packedLight) {
        consumer.addVertex(matrix4f, x, y, z).setColor(r, g, b, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(entry, 0.0F, 1.0F, 0.0F);
    }

    private static Vec3 getPosition(Entity LivingEntityIn, double p_177110_2_) {
        return LivingEntityIn.position().add(0, p_177110_2_, 0);
    }

    public static ResourceLocation getTexture() {
        return TEXTURE;
    }
}
