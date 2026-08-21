package com.iafenvoy.iceandfire.mixin;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 26.1.2 exposes typed matching but not the full recipe collection.
 */
@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor("recipes")
    RecipeMap iceandfire$getRecipes();
}
