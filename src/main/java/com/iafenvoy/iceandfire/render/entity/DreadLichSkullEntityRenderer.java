package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadLichSkullEntity;
import com.iafenvoy.iceandfire.render.model.DreadLichSkullModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class DreadLichSkullEntityRenderer extends EntityRenderer<DreadLichSkullEntity, LegacyEntityRenderState<DreadLichSkullEntity>> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_lich_skull.png");
    private static final DreadLichSkullModel MODEL_SPIRIT = new DreadLichSkullModel();

    public DreadLichSkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public LegacyEntityRenderState<DreadLichSkullEntity> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(DreadLichSkullEntity entity, LegacyEntityRenderState<DreadLichSkullEntity> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<DreadLichSkullEntity> state, @NonNull PoseStack matrixStackIn, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        DreadLichSkullEntity entity = state.entity;
        if (entity.tickCount > 3) {
            matrixStackIn.pushPose();
            matrixStackIn.scale(1.5F, -1.5F, 1.5F);
            float yaw = entity.yRotO + (entity.getYRot() - entity.yRotO) * state.partialTick;
            matrixStackIn.translate(0F, 0F, 0F);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw - 180.0F));
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.eyes(TEXTURE), (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                MODEL_SPIRIT.renderToBuffer(modelStack, buffer, 240, OverlayTexture.NO_OVERLAY, -1);
            });
            matrixStackIn.popPose();
        }
        super.submit(state, matrixStackIn, collector, camera);
    }

    public @NotNull Identifier getTextureLocation(@NotNull DreadLichSkullEntity entity) {
        return TEXTURE;
    }
}
