package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadGhoulEntity;
import com.iafenvoy.iceandfire.render.entity.feature.GenericGlowingFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.DreadGhoulModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DreadGhoulEntityRenderer extends MobRenderer<DreadGhoulEntity, DreadGhoulModel> {
    public static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_eyes.png");

    public static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_closed_1.png");
    public static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_closed_2.png");
    public static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_closed_3.png");
    public static final ResourceLocation TEXTURE_0_MID = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_mid_1.png");
    public static final ResourceLocation TEXTURE_1_MID = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_mid_2.png");
    public static final ResourceLocation TEXTURE_2_MID = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_mid_3.png");
    public static final ResourceLocation TEXTURE_0_OPEN = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_open_1.png");
    public static final ResourceLocation TEXTURE_1_OPEN = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_open_2.png");
    public static final ResourceLocation TEXTURE_2_OPEN = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_ghoul_open_3.png");

    public DreadGhoulEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DreadGhoulModel(0.0F), 0.5F);
        this.addLayer(new GenericGlowingFeatureRenderer<>(this, TEXTURE_EYES));
    }

    @Override
    protected void scale(DreadGhoulEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        float scale = entity.getSize() < 0.01F ? 1F : entity.getSize();
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getTextureLocation(DreadGhoulEntity ghoul) {
        return switch (ghoul.getScreamStage()) {
            case 2 -> switch (ghoul.getVariant()) {
                case 1 -> TEXTURE_1_OPEN;
                case 2 -> TEXTURE_2_OPEN;
                default -> TEXTURE_0_OPEN;
            };
            case 1 -> switch (ghoul.getVariant()) {
                case 1 -> TEXTURE_1_MID;
                case 2 -> TEXTURE_2_MID;
                default -> TEXTURE_0_MID;
            };
            default -> switch (ghoul.getVariant()) {
                case 1 -> TEXTURE_1;
                case 2 -> TEXTURE_2;
                default -> TEXTURE_0;
            };
        };
    }
}
