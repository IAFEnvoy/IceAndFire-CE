package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.entity.util.IHasArmorVariant;
import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer;
import com.iafenvoy.iceandfire.render.model.BipedBaseModel;
import com.iafenvoy.uranus.animation.IAnimatedEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public class BipedArmorFeatureRendererMultiple<T extends Mob & IHasArmorVariant & IAnimatedEntity, M extends BipedBaseModel<T>, A extends BipedBaseModel<T>> extends BipedArmorFeatureRenderer<T, M, A> {
    private final IHasArmorVariantResource variants;

    public BipedArmorFeatureRendererMultiple(LegacyMobRenderer<T, M> renderer, IHasArmorVariantResource variants, A modelLeggings, A modelArmor, Identifier defaultArmor, Identifier defaultLegArmor) {
        super(renderer, modelLeggings, modelArmor, defaultArmor, defaultLegArmor);
        this.variants = variants;
    }

    @Override
    public Identifier getArmorResource(T entity, ItemStack stack, EquipmentSlot slot, String type) {
        return this.variants.getArmorResource(entity.getBodyArmorVariant(), slot);
    }
}
