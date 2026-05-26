package com.iafenvoy.iceandfire.particle;

import com.iafenvoy.iceandfire.entity.GhostEntity;
import com.iafenvoy.iceandfire.registry.IafRenderTypes;
import com.iafenvoy.iceandfire.render.entity.GhostEntityRenderer;
import com.iafenvoy.iceandfire.render.model.GhostModel;
import com.iafenvoy.iceandfire.util.Color4i;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class GhostAppearanceParticle extends Particle {
    private final GhostModel model = new GhostModel(0.0F);
    private final int ghost;
    private final boolean fromLeft;

    protected GhostAppearanceParticle(ClientLevel world, double x, double y, double z, int ghost) {
        super(world, x, y, z);
        this.gravity = 0.0F;
        this.lifetime = 15;
        this.ghost = ghost;
        this.fromLeft = world.random.nextBoolean();
    }

    public static ParticleProvider<SimpleParticleType> factory() {
        return (parameters, world, x, y, z, velocityX, velocityY, velocityZ) -> new GhostAppearanceParticle(world, x, y, z, 1);
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float tickDelta) {
        float f = (this.age + tickDelta) / this.lifetime;
        float f1 = 0.05F + 0.5F * Mth.sin(f * (float) Math.PI);
        Entity entity = this.level.getEntity(this.ghost);
        if (entity instanceof GhostEntity ghostEntity && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
            PoseStack matrixstack = new PoseStack();
            matrixstack.mulPose(camera.rotation());
            if (this.fromLeft) {
                matrixstack.mulPose(Axis.YN.rotationDegrees(150 * f - 60));
                matrixstack.mulPose(Axis.ZN.rotationDegrees(150 * f - 60));
            } else {
                matrixstack.mulPose(Axis.YP.rotationDegrees(150 * f - 60));
                matrixstack.mulPose(Axis.ZP.rotationDegrees(150 * f - 60));
            }
            matrixstack.scale(-1.0F, -1.0F, 1.0F);
            matrixstack.translate(0.0D, 0.3F, 1.25D);
            MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer consumer1 = immediate.getBuffer(IafRenderTypes.getGhost(GhostEntityRenderer.getGhostOverlayForType(ghostEntity.getColor())));
            this.model.setupAnim(ghostEntity, 0, 0, entity.tickCount + tickDelta, 0, 0);
            this.model.renderToBuffer(matrixstack, consumer1, 240, OverlayTexture.NO_OVERLAY, new Color4i(1.0F, 1.0F, 1.0F, f1).getIntValue());
            immediate.endBatch();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}

