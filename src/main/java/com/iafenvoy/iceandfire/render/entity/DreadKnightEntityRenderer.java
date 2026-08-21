package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadKnightEntity;
import com.iafenvoy.iceandfire.render.entity.feature.GenericGlowingFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.DreadKnightModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.iafenvoy.iceandfire.render.entity.feature.DreadItemFeatureRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class DreadKnightEntityRenderer extends LegacyMobRenderer<DreadKnightEntity, DreadKnightModel> {
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_eyes.png");
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_1.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_2.png");
    public static final Identifier TEXTURE_2 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_3.png");

    public DreadKnightEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DreadKnightModel(0.0F), 0.6F);
        this.addLayer(new GenericGlowingFeatureRenderer<>(this, TEXTURE_EYES));
        this.addLayer(new DreadItemFeatureRenderer<>(this));
    }

    @Override
    protected void scale(@NotNull DreadKnightEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.95F, 0.95F, 0.95F);
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadKnightEntity entity) {
        return switch (entity.getArmorVariant()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
    }
}
