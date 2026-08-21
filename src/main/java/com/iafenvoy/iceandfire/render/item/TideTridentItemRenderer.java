package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.render.entity.TideTridentEntityRenderer;
import com.iafenvoy.iceandfire.render.model.TideTridentModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.item.ItemStack;

public class TideTridentItemRenderer implements DynamicItemRenderer {
    private static final TideTridentModel MODEL = new TideTridentModel();

    @Override
    public void submit(ItemStack stack, PoseStack matrices, SubmitNodeCollector collector, int light, int overlay, boolean foil, int color) {
        matrices.translate(0.5F, 0.5f, 0.5f);
        matrices.pushPose();
        matrices.translate(0, 0.2F, -0.15F);
        matrices.mulPose(Axis.XP.rotationDegrees(160.0F));
        collector.submitCustomGeometry(matrices, RenderTypes.entityCutout(TideTridentEntityRenderer.TRIDENT, false), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            MODEL.renderToBuffer(modelStack, buffer, light, overlay, -1);
        });
        matrices.popPose();
    }
}
