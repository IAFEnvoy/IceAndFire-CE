package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.ChainTieEntity;
import com.iafenvoy.iceandfire.render.model.ChainTieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class ChainTieEntityRenderer extends EntityRenderer<ChainTieEntity, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/misc/chain_tie.png");
    private final ChainTieModel leashKnotModel = new ChainTieModel();

    public ChainTieEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE, false), (pose, buffer) -> {
            PoseStack modelPoseStack = new PoseStack();
            modelPoseStack.last().set(pose);
            this.leashKnotModel.renderToBuffer(modelPoseStack, buffer, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        });
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
