package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.TideTridentEntity;
import com.iafenvoy.iceandfire.render.model.TideTridentModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class TideTridentEntityRenderer extends EntityRenderer<TideTridentEntity, LegacyEntityRenderState<TideTridentEntity>> {
    public static final Identifier TRIDENT = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/misc/tide_trident.png");
    private final TideTridentModel tridentModel = new TideTridentModel();

    public TideTridentEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public LegacyEntityRenderState<TideTridentEntity> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(TideTridentEntity entity, LegacyEntityRenderState<TideTridentEntity> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
    }

    @Override
    public void submit(LegacyEntityRenderState<TideTridentEntity> state, PoseStack matrixStackIn, SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        TideTridentEntity entityIn = state.entity;
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(state.partialTick, entityIn.yRotO, entityIn.getYRot()) - 90.0F));
        matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(state.partialTick, entityIn.xRotO, entityIn.getXRot()) + 90.0F));
        collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(this.getTextureLocation(entityIn)), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            this.tridentModel.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
        matrixStackIn.popPose();
        super.submit(state, matrixStackIn, collector, camera);
    }

    public @NotNull Identifier getTextureLocation(@NotNull TideTridentEntity entity) {
        return TRIDENT;
    }
}
