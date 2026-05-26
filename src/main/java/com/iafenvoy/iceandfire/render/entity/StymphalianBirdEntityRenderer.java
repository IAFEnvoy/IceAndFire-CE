package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.StymphalianBirdEntity;
import com.iafenvoy.iceandfire.render.model.StymphalianBirdModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class StymphalianBirdEntityRenderer extends MobRenderer<StymphalianBirdEntity, StymphalianBirdModel> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/stymphalianbird/stymphalian_bird.png");

    public StymphalianBirdEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new StymphalianBirdModel(), 0.6F);
    }

    @Override
    public void scale(@NotNull StymphalianBirdEntity LivingEntityIn, PoseStack stack, float partialTickTime) {
        stack.scale(0.75F, 0.75F, 0.75F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull StymphalianBirdEntity cyclops) {
        return TEXTURE;
    }
}
