package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.LightningDragonEntity;
import com.iafenvoy.iceandfire.render.misc.LightningBoltData;
import com.iafenvoy.iceandfire.render.misc.LightningRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class LightningDragonEntityRenderer extends DragonBaseEntityRenderer<LightningDragonEntity> {
    private final LightningRenderer lightningRenderer = new LightningRenderer();

    public LightningDragonEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static float getBoundedScale(float scale) {
        return (float) 0.5 + scale * ((float) 2 - (float) 0.5);
    }

    @Override
    public boolean shouldRender(@NotNull LightningDragonEntity livingEntityIn, @NotNull Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(livingEntityIn, camera, camX, camY, camZ)) return true;
        else {
            if (livingEntityIn.hasLightningTarget()) {
                Vec3 head = livingEntityIn.getHeadPosition();
                Vec3 target = new Vec3(livingEntityIn.getLightningTargetX(), livingEntityIn.getLightningTargetY(), livingEntityIn.getLightningTargetZ());
                return camera.isVisible(new AABB(head.x, head.y, head.z, target.x, target.y, target.z));
            }
            return false;
        }
    }

    @Override
    public void render(@NotNull LightningDragonEntity entityIn, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pushPose();
        if (entityIn.hasLightningTarget()) {
            Minecraft client = Minecraft.getInstance();
            assert client.player != null;
            double dist = client.player.distanceTo(entityIn);
            if (dist <= Math.max(256, client.options.renderDistance().get() * 16F)) {
                Vec3 Vector3d1 = entityIn.getHeadPosition();
                Vec3 Vector3d = new Vec3(entityIn.getLightningTargetX(), entityIn.getLightningTargetY(), entityIn.getLightningTargetZ());
                float energyScale = 0.4F * entityIn.getAgeScale();
                LightningBoltData bolt = new LightningBoltData(LightningBoltData.BoltRenderInfo.ELECTRICITY, Vector3d1, Vector3d, 15)
                        .size(0.05F * getBoundedScale(energyScale))
                        .lifespan(4)
                        .spawn(LightningBoltData.SpawnFunction.NO_DELAY);
                this.lightningRenderer.update(null, bolt, partialTicks);
                matrixStackIn.translate(-entityIn.getX(), -entityIn.getY(), -entityIn.getZ());
                this.lightningRenderer.render(partialTicks, matrixStackIn, bufferIn);
            }
        }
        matrixStackIn.popPose();
    }
}
