package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadHorseEntity;
import com.iafenvoy.iceandfire.render.entity.feature.GenericGlowingFeatureRenderer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DreadHorseEntityRenderer extends MobRenderer<DreadHorseEntity, HorseModel<DreadHorseEntity>> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_horse.png");
    public static final ResourceLocation TEXTURE_EYES = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_horse_eyes.png");

    public DreadHorseEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel<>(context.bakeLayer(ModelLayers.HORSE)), 0.75F);
        this.addLayer(new GenericGlowingFeatureRenderer<>(this, TEXTURE_EYES));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DreadHorseEntity entity) {
        return TEXTURE;
    }
}
