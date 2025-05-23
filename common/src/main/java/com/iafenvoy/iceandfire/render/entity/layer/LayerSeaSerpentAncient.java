package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntitySeaSerpent;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LayerSeaSerpentAncient extends FeatureRenderer<EntitySeaSerpent, AdvancedEntityModel<EntitySeaSerpent>> {
    private static final Identifier TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/seaserpent/ancient_overlay.png");
    private static final Identifier TEXTURE_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/entity/seaserpent/ancient_overlay_blink.png");

    public LayerSeaSerpentAncient(MobEntityRenderer<EntitySeaSerpent, AdvancedEntityModel<EntitySeaSerpent>> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, EntitySeaSerpent serpent, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (serpent.isAncient()) {
            RenderLayer tex = RenderLayer.getEntityNoOutline(serpent.isBlinking() ? TEXTURE_BLINK : TEXTURE);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(tex);
            this.getContextModel().render(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
        }
    }
}
