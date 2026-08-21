package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadLichEntity;
import com.iafenvoy.iceandfire.render.entity.feature.DreadItemFeatureRenderer;
import com.iafenvoy.iceandfire.render.entity.feature.GenericGlowingFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.DreadLichModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class DreadLichEntityRenderer extends LegacyMobRenderer<DreadLichEntity, DreadLichModel> {
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_lich_eyes.png");
    public static final Identifier TEXTURE_0 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_lich_0.png");
    public static final Identifier TEXTURE_1 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_lich_1.png");
    public static final Identifier TEXTURE_2 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_lich_2.png");
    public static final Identifier TEXTURE_3 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_lich_3.png");
    public static final Identifier TEXTURE_4 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_lich_4.png");
    public final DreadItemFeatureRenderer<DreadLichEntity> itemLayer;

    public DreadLichEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DreadLichModel(0.0F), 0.6F);
        this.addLayer(new GenericGlowingFeatureRenderer<>(this, TEXTURE_EYES));
        this.itemLayer = new DreadItemFeatureRenderer<>(this);
        this.addLayer(this.itemLayer);
    }

    @Override
    protected void scale(DreadLichEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(0.95F, 0.95F, 0.95F);
        if (entity.getAnimation() == this.getLegacyModel().getSpawnAnimation()) {
            this.itemLayer.hidden = entity.getAnimationTick() <= this.getLegacyModel().getSpawnAnimation().getDuration() - 10;
            return;
        }
        this.itemLayer.hidden = false;
    }

    @Override
    public @NotNull Identifier getTextureLocation(DreadLichEntity entity) {
        return switch (entity.getVariant()) {
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            case 3 -> TEXTURE_3;
            case 4 -> TEXTURE_4;
            default -> TEXTURE_0;
        };
    }
}
