package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.render.entity.RenderDeathWorm;
import com.iafenvoy.iceandfire.render.model.ModelDeathWormGauntlet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class RenderDeathWormGauntlet extends BuiltinModelItemRenderer {
    private static final ModelDeathWormGauntlet MODEL = new ModelDeathWormGauntlet();

    public RenderDeathWormGauntlet(BlockEntityRenderDispatcher dispatcher, EntityModelLoader modelLoader) {
        super(dispatcher, modelLoader);
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode type, MatrixStack stackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        RenderLayer texture;
        if (stack.isOf(IafItems.DEATHWORM_GAUNTLET_RED.get()))
            texture = RenderLayer.getEntityCutout(RenderDeathWorm.TEXTURE_RED);
        else if (stack.isOf(IafItems.DEATHWORM_GAUNTLET_WHITE.get()))
            texture = RenderLayer.getEntityCutout(RenderDeathWorm.TEXTURE_WHITE);
        else
            texture = RenderLayer.getEntityCutout(RenderDeathWorm.TEXTURE_YELLOW);
        stackIn.push();
        stackIn.translate(0.5F, 0.5F, 0.5F);
        MODEL.animate(stack, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));
        MODEL.render(stackIn, bufferIn.getBuffer(texture), combinedLightIn, combinedOverlayIn, -1);
        stackIn.pop();
    }
}
