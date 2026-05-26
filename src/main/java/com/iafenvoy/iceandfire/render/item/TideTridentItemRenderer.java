package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.render.entity.TideTridentEntityRenderer;
import com.iafenvoy.iceandfire.render.model.TideTridentModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TideTridentItemRenderer implements DynamicItemRenderer {
    private static final TideTridentModel MODEL = new TideTridentModel();

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.translate(0.5F, 0.5f, 0.5f);
        if (mode == ItemDisplayContext.GUI || mode == ItemDisplayContext.FIXED || mode == ItemDisplayContext.NONE || mode == ItemDisplayContext.GROUND) {
            ItemStack tridentInventory = new ItemStack(IafItems.TIDE_TRIDENT_INVENTORY.get());
            if (stack.isEnchanted())
                tridentInventory.set(DataComponents.ENCHANTMENTS, stack.get(DataComponents.ENCHANTMENTS));
            Minecraft.getInstance().getItemRenderer().renderStatic(tridentInventory, mode, mode == ItemDisplayContext.GROUND ? light : 240, overlay, matrices, vertexConsumers, Minecraft.getInstance().level, 0);
        } else {
            matrices.pushPose();
            matrices.translate(0, 0.2F, -0.15F);
            if (mode.firstPerson())
                matrices.translate(mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -0.3F : 0.3F, 0.2F, -0.2F);
            else matrices.translate(0, 0.6F, 0.0F);
            matrices.mulPose(Axis.XP.rotationDegrees(160.0F));
            VertexConsumer glintVertexBuilder = ItemRenderer.getFoilBufferDirect(vertexConsumers, RenderType.entityCutoutNoCull(TideTridentEntityRenderer.TRIDENT), false, stack.hasFoil());
            MODEL.renderToBuffer(matrices, glintVertexBuilder, light, overlay, -1);
            matrices.popPose();
        }
    }
}
