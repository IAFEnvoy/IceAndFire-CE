package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.util.IHasArmorVariant;
import com.iafenvoy.iceandfire.render.model.BipedBaseModel;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class BipedArmorFeatureRendererMultiple<R extends MobRenderer<T, M> & IHasArmorVariantResource, T extends Mob & IHasArmorVariant & IAnimatedEntity, M extends BipedBaseModel<T>, A extends BipedBaseModel<T>> extends BipedArmorFeatureRenderer<T, M, A> {
    final R mobRenderer;

    public BipedArmorFeatureRendererMultiple(R mobRenderer, A modelLeggings, A modelArmor, ResourceLocation defaultArmor, ResourceLocation defaultLegArmor) {
        super(mobRenderer, modelLeggings, modelArmor, defaultArmor, defaultLegArmor);
        this.mobRenderer = mobRenderer;
    }

    @Override
    public ResourceLocation getArmorResource(T entity, ItemStack stack, EquipmentSlot slot, String type) {
        return this.mobRenderer.getArmorResource(entity.getBodyArmorVariant(), slot);
    }
}
