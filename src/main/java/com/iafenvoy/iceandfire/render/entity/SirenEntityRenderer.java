package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SirenEntity;
import com.iafenvoy.iceandfire.render.model.SirenModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SirenEntityRenderer extends MobRenderer<SirenEntity, SirenModel> {
    public static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/siren/siren_0.png");
    public static final ResourceLocation TEXTURE_0_AGGRESSIVE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/siren/siren_0_aggressive.png");
    public static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/siren/siren_1.png");
    public static final ResourceLocation TEXTURE_1_AGGRESSIVE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/siren/siren_1_aggressive.png");
    public static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/siren/siren_2.png");
    public static final ResourceLocation TEXTURE_2_AGGRESSIVE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/siren/siren_2_aggressive.png");

    public SirenEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SirenModel(), 0.8F);
    }

    public static ResourceLocation getSirenOverlayTexture(int siren) {
        return switch (siren) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
    }

    @Override
    public void scale(@NotNull SirenEntity LivingEntityIn, PoseStack stack, float partialTickTime) {
        stack.translate(0, 0, -0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(SirenEntity siren) {
        return switch (siren.getHairColor()) {
            case 1 -> siren.isAgressive() ? TEXTURE_1_AGGRESSIVE : TEXTURE_1;
            case 2 -> siren.isAgressive() ? TEXTURE_2_AGGRESSIVE : TEXTURE_2;
            default -> siren.isAgressive() ? TEXTURE_0_AGGRESSIVE : TEXTURE_0;
        };
    }
}
