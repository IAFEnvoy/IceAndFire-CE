package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadThrallEntity;
import com.iafenvoy.iceandfire.render.entity.feature.BipedArmorFeatureRendererMultiple;
import com.iafenvoy.iceandfire.render.entity.feature.DreadItemFeatureRenderer;
import com.iafenvoy.iceandfire.render.entity.feature.GenericGlowingFeatureRenderer;
import com.iafenvoy.iceandfire.render.entity.feature.IHasArmorVariantResource;
import com.iafenvoy.iceandfire.render.model.DreadThrallModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class DreadThrallEntityRenderer extends LegacyMobRenderer<DreadThrallEntity, DreadThrallModel> implements IHasArmorVariantResource {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_thrall.png");
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_thrall_eyes.png");
    public static final Identifier TEXTURE_LEG_ARMOR = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_legs.png");
    public static final Identifier TEXTURE_ARMOR_0 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_1.png");
    public static final Identifier TEXTURE_ARMOR_1 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_2.png");
    public static final Identifier TEXTURE_ARMOR_2 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_3.png");
    public static final Identifier TEXTURE_ARMOR_3 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_4.png");
    public static final Identifier TEXTURE_ARMOR_4 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_5.png");
    public static final Identifier TEXTURE_ARMOR_5 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_6.png");
    public static final Identifier TEXTURE_ARMOR_6 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_7.png");
    public static final Identifier TEXTURE_ARMOR_7 = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/thrall_chest_8.png");
    public final DreadItemFeatureRenderer<DreadThrallEntity> itemLayer;

    public DreadThrallEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DreadThrallModel(0.0F, false), 0.6F);
        this.addLayer(new GenericGlowingFeatureRenderer<>(this, TEXTURE_EYES));
        this.itemLayer = new DreadItemFeatureRenderer<>(this);
        this.addLayer(this.itemLayer);
        this.addLayer(new BipedArmorFeatureRendererMultiple<>(this, this, new DreadThrallModel(0.5F, true), new DreadThrallModel(1.0F, true), TEXTURE_ARMOR_0, TEXTURE_LEG_ARMOR));
    }

    @Override
    public Identifier getArmorResource(int variant, EquipmentSlot equipmentSlotType) {
        if (equipmentSlotType == EquipmentSlot.LEGS) return TEXTURE_LEG_ARMOR;
        return switch (variant) {
            case 1 -> TEXTURE_ARMOR_1;
            case 2 -> TEXTURE_ARMOR_2;
            case 3 -> TEXTURE_ARMOR_3;
            case 4 -> TEXTURE_ARMOR_4;
            case 5 -> TEXTURE_ARMOR_5;
            case 6 -> TEXTURE_ARMOR_6;
            case 7 -> TEXTURE_ARMOR_7;
            default -> TEXTURE_ARMOR_0;
        };
    }

    @Override
    public void scale(DreadThrallEntity livingEntityIn, PoseStack stack, float partialTickTime) {
        stack.scale(0.95F, 0.95F, 0.95F);
        if (livingEntityIn.getAnimation() == this.getLegacyModel().getSpawnAnimation()) {
            this.itemLayer.hidden = livingEntityIn.getAnimationTick() <= this.getLegacyModel().getSpawnAnimation().getDuration() - 10;
            return;
        }
        this.itemLayer.hidden = false;

    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull DreadThrallEntity entity) {
        return TEXTURE;
    }
}
