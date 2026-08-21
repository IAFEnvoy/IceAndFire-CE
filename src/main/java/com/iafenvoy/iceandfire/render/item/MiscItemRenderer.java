package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.PixieHouseBlock;
import com.iafenvoy.iceandfire.item.block.entity.PixieHouseBlockEntity;
import com.iafenvoy.iceandfire.render.model.PixieHouseModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class MiscItemRenderer implements DynamicItemRenderer {
    private static final PixieHouseModel HOUSE_MODEL = new PixieHouseModel();
    private static final RenderType[] HOUSE_TEXTURES = new RenderType[6];

    static {
        for (int i = 0; i < HOUSE_TEXTURES.length; i++)
            HOUSE_TEXTURES[i] = RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/pixie/house/pixie_house_" + i + ".png"), false);
    }

    public MiscItemRenderer() {
    }

    @Override
    public void submit(ItemStack stack, PoseStack stackIn, SubmitNodeCollector collector, int light, int overlay, boolean foil, int color) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof PixieHouseBlock))
            return;
        int houseType = PixieHouseBlockEntity.getHouseTypeFromBlock(blockItem.getBlock());
        stackIn.pushPose();
        stackIn.translate(0.5F, 1.501F, 0.5F);
        stackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        collector.submitCustomGeometry(stackIn, HOUSE_TEXTURES[houseType], (pose, buffer) -> {
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            HOUSE_MODEL.renderToBuffer(modelStack, buffer, light, overlay, -1);
        });
        stackIn.popPose();
    }
}
