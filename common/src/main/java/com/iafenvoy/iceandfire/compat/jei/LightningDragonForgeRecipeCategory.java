package com.iafenvoy.iceandfire.compat.jei;

import com.iafenvoy.iceandfire.registry.IafBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.item.ItemStack;

//By jdkdigital
public class LightningDragonForgeRecipeCategory extends DragonForgeRecipeCategory {
    public LightningDragonForgeRecipeCategory(IGuiHelper guiHelper) {
        super(
                guiHelper,
                IceAndFireJeiPlugin.LIGHTNING,
                "lightning",
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(IafBlocks.DRAGONFORGE_LIGHTNING_CORE.get()))
        );
    }
}
