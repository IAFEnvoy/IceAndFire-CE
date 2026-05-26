package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.item.tool.TrollWeaponItem;
import com.iafenvoy.iceandfire.render.model.TrollWeaponModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TrollWeaponRenderer implements DynamicItemRenderer {
    private final TrollWeaponModel model = new TrollWeaponModel();

    @Override
    public void render(ItemStack stack, ItemDisplayContext type, PoseStack stackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        TrollType.ITrollWeapon weapon = TrollType.BuiltinWeapon.AXE;
        if (stack.getItem() instanceof TrollWeaponItem trollWeapon) weapon = trollWeapon.weapon;
        stackIn.pushPose();
        stackIn.translate(0.5F, -0.75F, 0.5F);
        this.model.renderToBuffer(stackIn, bufferIn.getBuffer(RenderType.entityCutout(weapon.getTexture())), combinedLightIn, combinedOverlayIn, -1);
        stackIn.popPose();
    }
}
