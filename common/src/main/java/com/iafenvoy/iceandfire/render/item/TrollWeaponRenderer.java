package com.iafenvoy.iceandfire.render.item;

import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.item.tool.TrollWeaponItem;
import com.iafenvoy.iceandfire.render.model.TrollWeaponModel;
import com.iafenvoy.uranus.client.render.DynamicItemRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class TrollWeaponRenderer implements DynamicItemRenderer {
    private static final TrollWeaponModel MODEL = new TrollWeaponModel();

    @Override
    public void render(ItemStack stack, ModelTransformationMode type, MatrixStack stackIn, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        TrollType.ITrollWeapon weapon = TrollType.BuiltinWeapon.AXE;
        if (stack.getItem() instanceof TrollWeaponItem trollWeapon)
            weapon = trollWeapon.weapon;
        stackIn.push();
        stackIn.translate(0.5F, -0.75F, 0.5F);
        MODEL.render(stackIn, bufferIn.getBuffer(RenderLayer.getEntityCutout(weapon.getTexture())), combinedLightIn, combinedOverlayIn, -1);
        stackIn.pop();
    }
}
