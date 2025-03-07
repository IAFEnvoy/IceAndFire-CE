package com.iafenvoy.iceandfire.render.entity.layer;

import com.iafenvoy.iceandfire.data.DragonArmorMaterial;
import com.iafenvoy.iceandfire.entity.EntityDragonBase;
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

import java.util.List;

public class LayerDragonArmor extends FeatureRenderer<EntityDragonBase, TabulaModel<EntityDragonBase>> {
    private static final List<EquipmentSlot> ARMOR_SLOTS = List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    public LayerDragonArmor(MobEntityRenderer<EntityDragonBase, TabulaModel<EntityDragonBase>> renderIn) {
        super(renderIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int light, EntityDragonBase dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityModel<EntityDragonBase> model = this.getContextModel();
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = dragon.getEquippedStack(slot);
            if (stack.isEmpty()) continue;
            Identifier texture = DragonArmorMaterial.getArmorTexture(stack, slot);
            VertexConsumer vertexConsumer = bufferIn.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));
            model.render(matrixStackIn, vertexConsumer, light, OverlayTexture.DEFAULT_UV, -1);
        }
    }
}