package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.render.entity.DeathWormEntityRenderer;
import com.iafenvoy.iceandfire.render.model.DeathWormGauntletModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class DeathwormGauntletRenderer implements DynamicItemRenderer {
    private static final DeathWormGauntletModel MODEL = new DeathWormGauntletModel();

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        RenderLayer texture;
        if (stack.isOf(IafItems.DEATHWORM_GAUNTLET_RED.get()))
            texture = RenderLayer.getEntityCutout(DeathWormEntityRenderer.TEXTURE_RED);
        else if (stack.isOf(IafItems.DEATHWORM_GAUNTLET_WHITE.get()))
            texture = RenderLayer.getEntityCutout(DeathWormEntityRenderer.TEXTURE_WHITE);
        else
            texture = RenderLayer.getEntityCutout(DeathWormEntityRenderer.TEXTURE_YELLOW);
        matrices.push();
        matrices.translate(0.5F, 0.5F, 0.5F);
        MODEL.animate(stack, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));
        MODEL.render(matrices, vertexConsumers.getBuffer(texture), light, overlay, -1);
        matrices.pop();
    }
}
