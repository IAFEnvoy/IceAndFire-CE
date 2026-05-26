package com.iafenvoy.iceandfire.recipe;

import com.iafenvoy.iceandfire.item.block.entity.DragonForgeBlockEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafRecipeSerializers;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DragonForgeRecipe implements Recipe<DragonForgeBlockEntity.DragonForgeRecipeInput> {
    private final Ingredient input;
    private final Ingredient blood;
    private final ItemStack result;
    private final String dragonType;
    private final int cookTime;

    public DragonForgeRecipe(Ingredient input, Ingredient blood, ItemStack result, String dragonType, int cookTime) {
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
    public ItemStack assemble(DragonForgeBlockEntity.DragonForgeRecipeInput input, HolderLookup.Provider lookup) {
        return this.result;
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    public boolean isValidBlood(ItemStack blood) {
        return this.blood.test(blood);
    }

    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registriesLookup) {
        return this.result;
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return new ItemStack(IafBlocks.DRAGONFORGE_FIRE_CORE.get());
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return IafRecipeSerializers.DRAGONFORGE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return IafRecipes.DRAGON_FORGE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<DragonForgeRecipe> {
        @Override
        public @NotNull MapCodec<DragonForgeRecipe> codec() {
            return RecordCodecBuilder.mapCodec(i -> i.group(
                    Ingredient.CODEC.fieldOf("input").forGetter(DragonForgeRecipe::getInput),
                    Ingredient.CODEC.fieldOf("blood").forGetter(DragonForgeRecipe::getBlood),
                    ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(DragonForgeRecipe::getResultItem),
                    Codec.STRING.fieldOf("dragonType").forGetter(DragonForgeRecipe::getDragonType),
                    Codec.INT.fieldOf("cookTime").forGetter(DragonForgeRecipe::getCookTime)
            ).apply(i, DragonForgeRecipe::new));
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, DragonForgeRecipe> streamCodec() {
            return StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, DragonForgeRecipe::getInput,
                    Ingredient.CONTENTS_STREAM_CODEC, DragonForgeRecipe::getBlood,
                    ItemStack.STREAM_CODEC, DragonForgeRecipe::getResultItem,
                    ByteBufCodecs.STRING_UTF8, DragonForgeRecipe::getDragonType,
                    ByteBufCodecs.INT, DragonForgeRecipe::getCookTime,
                    DragonForgeRecipe::new
            );
        }
    }
}
