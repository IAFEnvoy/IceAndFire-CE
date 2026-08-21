package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.render.entity.DeathWormEntityRenderer;
import com.iafenvoy.iceandfire.render.model.DeathWormGauntletModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class DeathwormGauntletRenderer implements DynamicItemRenderer {
    private static final DeathWormGauntletModel MODEL = new DeathWormGauntletModel();

    @Override
    public void submit(ItemStack stack, PoseStack matrices, SubmitNodeCollector collector, int light, int overlay, boolean foil, int color) {
        Identifier texture;
        if (stack.is(IafItems.DEATHWORM_GAUNTLET_RED.get()))
            texture = DeathWormEntityRenderer.TEXTURE_RED;
        else if (stack.is(IafItems.DEATHWORM_GAUNTLET_WHITE.get()))
            texture = DeathWormEntityRenderer.TEXTURE_WHITE;
        else
            texture = DeathWormEntityRenderer.TEXTURE_YELLOW;
        matrices.pushPose();
        matrices.translate(0.5F, 0.5F, 0.5F);
        MODEL.animate(stack, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
        collector.submitCustomGeometry(matrices, RenderTypes.entityCutout(texture, false), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            MODEL.renderToBuffer(modelStack, buffer, light, overlay, -1);
        });
        matrices.popPose();
    }
}
