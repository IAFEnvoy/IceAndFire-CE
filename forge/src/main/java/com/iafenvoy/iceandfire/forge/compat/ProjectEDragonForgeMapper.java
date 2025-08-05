package com.iafenvoy.iceandfire.forge.compat;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.recipe.DragonForgeRecipe;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RecipeTypeMapper(requiredMods = IceAndFire.MOD_ID, priority = 1)
public class ProjectEDragonForgeMapper implements IRecipeTypeMapper {
    @Override
    public String getName() {
        return "IceAndFireDragonForgeMapper";
    }

    @Override
    public String getDescription() {
        return "Recipe mapper for Ice and Fire DragonForge recipes";
    }

    @Override
    public boolean canHandle(RecipeType<?> recipeType) {
        return recipeType == IafRecipes.DRAGON_FORGE_TYPE.get();
    }

    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> collector, Recipe<?> recipe, DynamicRegistryManager registryManager, INSSFakeGroupManager inssFakeGroupManager) {
        if (recipe instanceof DragonForgeRecipe forgeRecipe) {
            ItemStack output = forgeRecipe.getResultItem();
            if (output == null || output.isEmpty()) return false;
            boolean successful = true;
            List<Pair<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap = new ArrayList<>();
            Object2IntMap<NormalizedSimpleStack> ingredientMap = new Object2IntLinkedOpenHashMap<>();
            for (Ingredient ingredient : List.of(forgeRecipe.getInput(), forgeRecipe.getBlood()))
                if (!convertIngredient(ingredient, ingredientMap, fakeGroupMap, inssFakeGroupManager, forgeRecipe.getId().toString())) {
                    successful = false;
                    break;
                }
            if (successful) collector.addConversion(output.getCount(), NSSItem.createItem(output), ingredientMap);
            for (Pair<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>> dummyGroupInfo : fakeGroupMap)
                for (Object2IntMap<NormalizedSimpleStack> groupObject2IntMap : dummyGroupInfo.getRight())
                    collector.addConversion(1, dummyGroupInfo.getLeft(), groupObject2IntMap);
            return true;
        }
        return false;
    }

    public static boolean convertIngredient(Ingredient ingredient, Object2IntMap<NormalizedSimpleStack> ingredientMap, List<Pair<NormalizedSimpleStack, List<Object2IntMap<NormalizedSimpleStack>>>> fakeGroupMap, INSSFakeGroupManager fakeGroupManager, String recipeID) {
        ItemStack[] matches = ingredient.getMatchingStacks();
        if (matches == null) return false;
        else if (matches.length == 1) return !addIngredient(ingredientMap, matches[0].copy());
        else if (matches.length > 0) {
            Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack match : matches)
                if (!match.isEmpty()) {
                    rawNSSMatches.add(NSSItem.createItem(match));
                    stacks.add(match);
                }

            int count = stacks.size();
            if (count == 1) {
                ItemStack item = stacks.get(0);
                return !addIngredient(ingredientMap, item.copy());
            } else if (count > 1) {
                Pair<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
                NormalizedSimpleStack dummy = group.getLeft();
                ingredientMap.put(dummy, Math.max(-1, 1));
                if (group.getRight()) {
                    List<Object2IntMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
                    for (ItemStack stack : stacks) {
                        Object2IntMap<NormalizedSimpleStack> groupIngredientMap = new Object2IntLinkedOpenHashMap<>();
                        if (addIngredient(groupIngredientMap, stack.copy())) return false;
                        groupIngredientMaps.add(groupIngredientMap);
                    }
                    fakeGroupMap.add(new Pair<>(dummy, groupIngredientMaps));
                }
            }
        }
        return true;
    }

    public static boolean addIngredient(Object2IntMap<NormalizedSimpleStack> ingredientMap, ItemStack stack) {
        Item item = stack.getItem();
        try {
            if (item.hasCraftingRemainingItem(stack))
                ingredientMap.put(NSSItem.createItem(item.getCraftingRemainingItem(stack)), -1);
        } catch (Exception e) {
            IceAndFire.LOGGER.error("Failed to mapping recipe", e);
            return true;
        }
        ingredientMap.put(NSSItem.createItem(stack), stack.getCount());
        return false;
    }
}
