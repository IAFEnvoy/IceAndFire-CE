package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonArmorPart;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.DragonArmorItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

import java.util.List;
import java.util.Locale;

/** Renders dragon textures that use the same geometry as the base model. */
public class DragonGeoOverlayLayer<T extends DragonBaseEntity> extends GeoRenderLayer<T> {
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    public DragonGeoOverlayLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, T dragon, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ResourceLocation maleOverlay = DragonColor.getById(dragon.getVariant()).getTextureProvider().getMaleOverlay();
        if (dragon.isMale() && !dragon.isSkeletal() && maleOverlay != null)
            this.renderer.reRender(bakedModel, poseStack, bufferSource, dragon, RenderType.entityTranslucent(maleOverlay), bufferSource.getBuffer(RenderType.entityTranslucent(maleOverlay)), partialTick, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());

        if (dragon.shouldRenderEyes()) {
            ResourceLocation eyes = DragonColor.getById(dragon.getVariant()).getTextureProvider().getEyesTexture(dragon.getDragonStage());
            if (eyes != null)
                this.renderer.reRender(bakedModel, poseStack, bufferSource, dragon, RenderType.eyes(eyes), bufferSource.getBuffer(RenderType.eyes(eyes)), partialTick, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
        }

        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ResourceLocation armor = getArmorTexture(dragon.getItemBySlot(slot), slot);
            if (armor != null)
                this.renderer.reRender(bakedModel, poseStack, bufferSource, dragon, RenderType.entityCutoutNoCull(armor), bufferSource.getBuffer(RenderType.entityCutoutNoCull(armor)), partialTick, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
        }
    }

    private static ResourceLocation getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        DragonArmorPart part = DragonArmorPart.fromSlot(slot);
        if (part != null && !stack.isEmpty() && stack.getItem() instanceof DragonArmorItem armorItem)
            return IceAndFire.id(String.format(Locale.ROOT, "textures/entity/dragon_armor/armor_%s_%s.png", part.getId(), armorItem.type.name()));
        return null;
    }
}
