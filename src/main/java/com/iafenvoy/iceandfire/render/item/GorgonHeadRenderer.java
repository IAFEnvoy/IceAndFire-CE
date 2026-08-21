package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.render.model.GorgonHeadActiveModel;
import com.iafenvoy.iceandfire.render.model.GorgonHeadModel;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class GorgonHeadRenderer implements DynamicItemRenderer {
    private static final Identifier ACTIVE_TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/gorgon/head_active.png");
    private static final Identifier INACTIVE_TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/gorgon/head_inactive.png");
    private static final AdvancedEntityModel<Entity> ACTIVE_MODEL = new GorgonHeadActiveModel();
    private static final AdvancedEntityModel<Entity> INACTIVE_MODEL = new GorgonHeadModel();

    @Override
    public void submit(ItemStack stack, PoseStack stackIn, SubmitNodeCollector collector, int combinedLightIn, int combinedOverlayIn, boolean foil, int color) {
        boolean active = stack.getItem() == IafItems.GORGON_HEAD.get() && stack.has(IafDataComponents.ACTIVE.get());
        AdvancedEntityModel<Entity> model = active ? ACTIVE_MODEL : INACTIVE_MODEL;
        stackIn.pushPose();
        stackIn.translate(0.5F, active ? 1.5F : 1.25F, 0.5F);
        collector.submitCustomGeometry(stackIn, RenderTypes.entityCutout(active ? ACTIVE_TEXTURE : INACTIVE_TEXTURE, false), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            model.renderToBuffer(modelStack, buffer, combinedLightIn, combinedOverlayIn, -1);
        });
        stackIn.popPose();
    }
}
