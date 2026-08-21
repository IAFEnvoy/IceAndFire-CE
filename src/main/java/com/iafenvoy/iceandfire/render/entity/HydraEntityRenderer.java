package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HydraEntity;
import com.iafenvoy.iceandfire.render.entity.feature.GenericGlowingFeatureRenderer;
import com.iafenvoy.iceandfire.render.entity.feature.HydraHeadFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.HydraBodyModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class HydraEntityRenderer extends LegacyMobRenderer<HydraEntity, HydraBodyModel> {
    public static final Identifier TEXUTURE_0 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_0.png");
    public static final Identifier TEXUTURE_1 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_1.png");
    public static final Identifier TEXUTURE_2 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_2.png");
    public static final Identifier TEXUTURE_EYES = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/hydra/hydra_eyes.png");

    public HydraEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HydraBodyModel(), 1.2F);
        this.addLayer(new HydraHeadFeatureRenderer(this));
        this.addLayer(new GenericGlowingFeatureRenderer<>(this, TEXUTURE_EYES));
    }

    @Override
    public void scale(@NotNull HydraEntity LivingEntityIn, PoseStack stack, float partialTickTime) {
        stack.scale(1.75F, 1.75F, 1.75F);
    }

    @Override
    public @NotNull Identifier getTextureLocation(HydraEntity gorgon) {
        return switch (gorgon.getVariant()) {
            case 1 -> TEXUTURE_1;
            case 2 -> TEXUTURE_2;
            default -> TEXUTURE_0;
        };
    }
}
