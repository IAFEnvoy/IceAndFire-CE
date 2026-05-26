package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SeaSerpentAncientFeatureRenderer extends RenderLayer<SeaSerpentEntity, AdvancedEntityModel<SeaSerpentEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/seaserpent/ancient_overlay.png");
    private static final ResourceLocation TEXTURE_BLINK = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/seaserpent/ancient_overlay_blink.png");

    public SeaSerpentAncientFeatureRenderer(MobRenderer<SeaSerpentEntity, AdvancedEntityModel<SeaSerpentEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, SeaSerpentEntity serpent, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (serpent.isAncient()) {
            RenderType tex = RenderType.entityNoOutline(serpent.isBlinking() ? TEXTURE_BLINK : TEXTURE);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(tex);
            this.getParentModel().renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}
