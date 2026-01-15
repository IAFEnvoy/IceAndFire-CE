package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonArmorPart;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.DragonArmorItem;
import com.iafenvoy.uranus.client.model.TabulaModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class DragonArmorFeatureRenderer<T extends DragonBaseEntity> extends FeatureRenderer<T, TabulaModel<T>> {
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    public DragonArmorFeatureRenderer(MobEntityRenderer<T, TabulaModel<T>> renderIn) {
        super(renderIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int light, T dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityModel<T> model = this.getContextModel();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            Identifier texture = getArmorTexture(dragon.getEquippedStack(slot), slot);
            if (texture == null) continue;
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
            model.render(matrixStackIn, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);
        }
    }

    @Nullable
    public static Identifier getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        DragonArmorPart part = DragonArmorPart.fromSlot(slot);
        if (part != null && !stack.isEmpty() && stack.getItem() instanceof DragonArmorItem armorItem)
            return Identifier.of(IceAndFire.MOD_ID, String.format(Locale.ROOT, "textures/entity/dragon_armor/armor_%s_%s.png", part.getId(), armorItem.type.name()));
        else return null;
    }
}