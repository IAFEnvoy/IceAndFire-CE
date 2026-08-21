package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonArmorPart;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.DragonArmorItem;
import com.iafenvoy.iceandfire.render.entity.LegacyEntityFeature;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class DragonArmorFeatureRenderer<T extends DragonBaseEntity> implements LegacyEntityFeature<T> {
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    private final TabulaModel<T> model;

    public DragonArmorFeatureRenderer(LegacyMobRenderer<T, TabulaModel<T>> renderer) {
        this.model = renderer.getLegacyModel();
    }

    @Override
    public void submit(T dragon, float partialTick, PoseStack matrixStackIn, SubmitNodeCollector collector, CameraRenderState camera, int light, int outlineColor) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            Identifier texture = getArmorTexture(dragon.getItemBySlot(slot), slot);
            if (texture == null) continue;
            collector.submitCustomGeometry(matrixStackIn, RenderTypes.entityCutout(texture, false), (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                this.model.renderToBuffer(modelStack, buffer, light, OverlayTexture.NO_OVERLAY, -1);
            });
        }
    }

    @Nullable
    public static Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        DragonArmorPart part = DragonArmorPart.fromSlot(slot);
        if (part != null && !stack.isEmpty() && stack.getItem() instanceof DragonArmorItem armorItem)
            return Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, String.format(Locale.ROOT, "textures/entity/dragon_armor/armor_%s_%s.png", part.getId(), armorItem.type.name()));
        else return null;
    }
}
