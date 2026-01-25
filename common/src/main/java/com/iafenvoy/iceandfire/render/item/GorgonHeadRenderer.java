package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.render.model.GorgonHeadActiveModel;
import com.iafenvoy.iceandfire.render.model.GorgonHeadModel;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class GorgonHeadRenderer implements DynamicItemRenderer {
    private static final RenderLayer ACTIVE_TEXTURE = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/gorgon/head_active.png"), false);
    private static final RenderLayer INACTIVE_TEXTURE = RenderLayer.getEntityCutoutNoCull(Identifier.of(IceAndFire.MOD_ID, "textures/entity/gorgon/head_inactive.png"), false);
    private static final AdvancedEntityModel<Entity> ACTIVE_MODEL = new GorgonHeadActiveModel();
    private static final AdvancedEntityModel<Entity> INACTIVE_MODEL = new GorgonHeadModel();

    @Override
    public void render(ItemStack stack, ModelTransformationMode type, MatrixStack stackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        boolean active = stack.getItem() == IafItems.GORGON_HEAD.get() && stack.contains(IafDataComponents.ACTIVE.get());
        AdvancedEntityModel<Entity> model = active ? ACTIVE_MODEL : INACTIVE_MODEL;
        stackIn.push();
        stackIn.translate(0.5F, active ? 1.5F : 1.25F, 0.5F);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(active ? ACTIVE_TEXTURE : INACTIVE_TEXTURE);
        model.render(stackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, -1);
        stackIn.pop();
    }
}
