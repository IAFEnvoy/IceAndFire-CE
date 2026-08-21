package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer.ModelPose;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class SeaSerpentAncientFeatureRenderer implements LegacyEntityFeature<SeaSerpentEntity> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/seaserpent/ancient_overlay.png");
    private static final Identifier TEXTURE_BLINK = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/seaserpent/ancient_overlay_blink.png");
    private final AdvancedEntityModel<SeaSerpentEntity> model;

    public SeaSerpentAncientFeatureRenderer(LegacyMobRenderer<SeaSerpentEntity, AdvancedEntityModel<SeaSerpentEntity>> renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(SeaSerpentEntity serpent, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int lightCoords, int outlineColor) {
        if (!serpent.isAncient()) return;
        ModelPose modelPose = ModelPose.capture(this.model);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(serpent.isBlinking() ? TEXTURE_BLINK : TEXTURE, false), (pose, buffer) -> {
            modelPose.apply();
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            this.model.renderToBuffer(modelStack, buffer, lightCoords, OverlayTexture.NO_OVERLAY, -1);
        });
    }
}
