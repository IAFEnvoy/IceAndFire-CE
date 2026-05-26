package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.StymphalianFeatherEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class StymphalianFeatherEntityRenderer extends ArrowRenderer<StymphalianFeatherEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/stymphalianbird/feather.png");

    public StymphalianFeatherEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull StymphalianFeatherEntity entity) {
        return TEXTURE;
    }
}