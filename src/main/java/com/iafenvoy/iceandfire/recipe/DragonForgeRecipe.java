package com.iafenvoy.iceandfire.recipe;

import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.registry.IafRecipeSerializers;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class DragonForgeRecipe implements Recipe<DragonForgeBlockEntity.DragonForgeRecipeInput> {
    private final Ingredient input;
    private final Ingredient blood;
    private final ItemStackTemplate result;
    private final String dragonType;
    private final int cookTime;

    public DragonForgeRecipe(Ingredient input, Ingredient blood, ItemStackTemplate result, String dragonType, int cookTime) {
        this.input = input;
        this.blood = blood;
        this.result = result;
        this.dragonType = dragonType;
        this.cookTime = cookTime;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public Ingredient getBlood() {
        return this.blood;
    }

    public int getCookTime() {
        return this.cookTime;
    }

    public String getDragonType() {
        return this.dragonType;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(DragonForgeBlockEntity.DragonForgeRecipeInput inv, @NotNull Level worldIn) {
        return this.input.test(inv.getStack(0)) && this.blood.test(inv.getStack(1)) && this.dragonType.equals(inv.getTypeID());
    }

    @Override
    public @NotNull ItemStack assemble(DragonForgeBlockEntity.@NotNull DragonForgeRecipeInput input) {
        return this.result.create();
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    public boolean isValidBlood(ItemStack blood) {
        return this.blood.test(blood);
    }

    public ItemStack getResultItem() {
        return this.result.create();
    }

    @Override
    public @NotNull RecipeSerializer<DragonForgeRecipe> getSerializer() {
        return IafRecipeSerializers.DRAGONFORGE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<DragonForgeRecipe> getType() {
        return IafRecipes.DRAGON_FORGE_TYPE.get();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NonNull String group() {
        return "";
    }

    @Override
    public @NonNull PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public @NonNull RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static RecipeSerializer<DragonForgeRecipe> serializer() {
        MapCodec<DragonForgeRecipe> codec = RecordCodecBuilder.mapCodec(i -> i.group(
                Ingredient.CODEC.fieldOf("input").forGetter(DragonForgeRecipe::getInput),
                Ingredient.CODEC.fieldOf("blood").forGetter(DragonForgeRecipe::getBlood),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.STRING.fieldOf("dragonType").forGetter(DragonForgeRecipe::getDragonType),
                Codec.INT.fieldOf("cookTime").forGetter(DragonForgeRecipe::getCookTime)
        ).apply(i, DragonForgeRecipe::new));
        StreamCodec<RegistryFriendlyByteBuf, DragonForgeRecipe> streamCodec = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, DragonForgeRecipe::getInput,
                Ingredient.CONTENTS_STREAM_CODEC, DragonForgeRecipe::getBlood,
                ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
                ByteBufCodecs.STRING_UTF8, DragonForgeRecipe::getDragonType,
                ByteBufCodecs.INT, DragonForgeRecipe::getCookTime,
                DragonForgeRecipe::new
        );
        return new RecipeSerializer<>(codec, streamCodec);
    }
}
