package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.render.entity.DeathWormEntityRenderer;
import com.iafenvoy.iceandfire.render.model.DeathWormGauntletModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DeathwormGauntletRenderer implements DynamicItemRenderer {
    private static final DeathWormGauntletModel MODEL = new DeathWormGauntletModel();

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        RenderType texture;
        if (stack.is(IafItems.DEATHWORM_GAUNTLET_RED.get()))
            texture = RenderType.entityCutout(DeathWormEntityRenderer.TEXTURE_RED);
        else if (stack.is(IafItems.DEATHWORM_GAUNTLET_WHITE.get()))
            texture = RenderType.entityCutout(DeathWormEntityRenderer.TEXTURE_WHITE);
        else
            texture = RenderType.entityCutout(DeathWormEntityRenderer.TEXTURE_YELLOW);
        matrices.pushPose();
        matrices.translate(0.5F, 0.5F, 0.5F);
        MODEL.animate(stack, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
        MODEL.renderToBuffer(matrices, vertexConsumers.getBuffer(texture), light, overlay, -1);
        matrices.popPose();
    }
}
