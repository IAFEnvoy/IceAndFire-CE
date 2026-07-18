package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class SeaSerpentAncientGeoLayer extends GeoRenderLayer<SeaSerpentEntity> {
    private static final ResourceLocation TEXTURE = IceAndFire.id("textures/entity/seaserpent/ancient_overlay.png");
    private static final ResourceLocation BLINK_TEXTURE = IceAndFire.id("textures/entity/seaserpent/ancient_overlay_blink.png");

    public SeaSerpentAncientGeoLayer(GeoRenderer<SeaSerpentEntity> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, SeaSerpentEntity serpent, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!serpent.isAncient()) return;
        ResourceLocation texture = serpent.isBlinking() ? BLINK_TEXTURE : TEXTURE;
        RenderType overlayType = RenderType.entityNoOutline(texture);
        this.renderer.reRender(bakedModel, poseStack, bufferSource, serpent, overlayType, bufferSource.getBuffer(overlayType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
    }
}
