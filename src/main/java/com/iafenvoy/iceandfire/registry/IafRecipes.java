package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IafRecipes {
    public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, IceAndFire.MOD_ID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<DragonForgeRecipe>> DRAGON_FORGE_TYPE = REGISTRY.register("dragonforge", () -> new RecipeType<>() {
        @Override
        public String toString() {
            return "dragonforge";
        }
    });

    public static void registerDispenser() {
        DispenserBlock.registerProjectileBehavior(IafItems.STYMPHALIAN_ARROW.get());
        DispenserBlock.registerProjectileBehavior(IafItems.AMPHITHERE_ARROW.get());
        DispenserBlock.registerProjectileBehavior(IafItems.SEA_SERPENT_ARROW.get());
        DispenserBlock.registerProjectileBehavior(IafItems.DRAGONBONE_ARROW.get());
        DispenserBlock.registerProjectileBehavior(IafItems.HYDRA_ARROW.get());
        DispenserBlock.registerProjectileBehavior(IafItems.HIPPOGRYPH_EGG.get());
        DispenserBlock.registerProjectileBehavior(IafItems.ROTTEN_EGG.get());
        DispenserBlock.registerProjectileBehavior(IafItems.DEATHWORM_EGG.get());
        DispenserBlock.registerProjectileBehavior(IafItems.DEATHWORM_EGG_GIGANTIC.get());
    }

    public static void init() {
        registerDispenser();
    }
}
