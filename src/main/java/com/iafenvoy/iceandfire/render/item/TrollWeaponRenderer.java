package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.item.tool.TrollWeaponItem;
import com.iafenvoy.iceandfire.render.model.TrollWeaponModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.item.ItemStack;

public class TrollWeaponRenderer implements DynamicItemRenderer {
    private final TrollWeaponModel model = new TrollWeaponModel();

    @Override
    public void submit(ItemStack stack, PoseStack stackIn, SubmitNodeCollector collector, int combinedLightIn, int combinedOverlayIn, boolean foil, int color) {
        TrollType.ITrollWeapon weapon = TrollType.BuiltinWeapon.AXE;
        if (stack.getItem() instanceof TrollWeaponItem trollWeapon) weapon = trollWeapon.weapon;
        stackIn.pushPose();
        stackIn.translate(0.5F, -0.75F, 0.5F);
        collector.submitCustomGeometry(stackIn, RenderTypes.entityCutout(weapon.getTexture(), false), (pose, buffer) -> {
            PoseStack modelStack = new PoseStack(); modelStack.last().set(pose);
            this.model.renderToBuffer(modelStack, buffer, combinedLightIn, combinedOverlayIn, -1);
        });
        stackIn.popPose();
    }
}
