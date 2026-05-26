package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadKnightEntity;
import com.iafenvoy.iceandfire.render.entity.feature.GenericGlowingFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.DreadKnightModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DreadKnightEntityRenderer extends MobRenderer<DreadKnightEntity, DreadKnightModel> {
    public static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_eyes.png");
    public static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_1.png");
    public static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_2.png");
    public static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_3.png");

    public DreadKnightEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DreadKnightModel(0.0F), 0.6F);
        this.addLayer(new GenericGlowingFeatureRenderer<>(this, TEXTURE_EYES));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    protected void scale(@NotNull DreadKnightEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.95F, 0.95F, 0.95F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DreadKnightEntity entity) {
        return switch (entity.getArmorVariant()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
    }
}
