package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.CyclopsEntity;
import com.iafenvoy.iceandfire.render.model.CyclopsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CyclopsEntityRenderer extends MobRenderer<CyclopsEntity, CyclopsModel> {
    public static final ResourceLocation TEXTURE_0 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_0.png");
    public static final ResourceLocation BLINK_0_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_0_blink.png");
    public static final ResourceLocation BLINDED_0_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_0_injured.png");
    public static final ResourceLocation TEXTURE_1 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_1.png");
    public static final ResourceLocation BLINK_1_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_1_blink.png");
    public static final ResourceLocation BLINDED_1_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_1_injured.png");
    public static final ResourceLocation TEXTURE_2 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_2.png");
    public static final ResourceLocation BLINK_2_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_2_blink.png");
    public static final ResourceLocation BLINDED_2_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_2_injured.png");
    public static final ResourceLocation TEXTURE_3 = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_3.png");
    public static final ResourceLocation BLINK_3_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_3_blink.png");
    public static final ResourceLocation BLINDED_3_TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/cyclops/cyclops_3_injured.png");

    public CyclopsEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new CyclopsModel(), 1.6F);
    }

    @Override
    protected void scale(@NotNull CyclopsEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(2.25F, 2.25F, 2.25F);
    }

    @Override
    public ResourceLocation getTextureLocation(CyclopsEntity cyclops) {
        switch (cyclops.getVariant()) {
            case 0 -> {
                if (cyclops.isBlinded()) return BLINDED_0_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_0_TEXTURE;
                else return TEXTURE_0;
            }
            case 1 -> {
                if (cyclops.isBlinded()) return BLINDED_1_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_1_TEXTURE;
                else return TEXTURE_1;
            }
            case 2 -> {
                if (cyclops.isBlinded()) return BLINDED_2_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_2_TEXTURE;
                else return TEXTURE_2;
            }
            case 3 -> {
                if (cyclops.isBlinded()) return BLINDED_3_TEXTURE;
                else if (cyclops.isBlinking()) return BLINK_3_TEXTURE;
                else return TEXTURE_3;
            }
        }
        return TEXTURE_0;
    }
}
