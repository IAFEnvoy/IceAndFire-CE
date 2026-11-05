package com.iafenvoy.iceandfire.recipe;

import com.google.gson.JsonObject;
import com.iafenvoy.iceandfire.entity.block.BlockEntityDragonForge;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafRecipeSerializers;
import com.iafenvoy.iceandfire.registry.IafRecipes;
import com.iafenvoy.uranus.client.model.tabula.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DragonForgeRecipe implements Recipe<BlockEntityDragonForge> {
    private final Ingredient input;
    private final Ingredient blood;
    private final ItemStack result;
    private final String dragonType;
    private final int cookTime;
    private final Identifier recipeId;

    public DragonForgeRecipe(Identifier recipeId, Ingredient input, Ingredient blood, ItemStack result, String dragonType, int cookTime) {
        this.recipeId = recipeId;
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
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public boolean matches(BlockEntityDragonForge inv, World worldIn) {
        return this.input.test(inv.getStack(0)) && this.blood.test(inv.getStack(1)) && this.dragonType.equals(inv.getTypeID());
    }

    public boolean isValidInput(ItemStack stack) {
        return this.input.test(stack);
    }

    public boolean isValidBlood(ItemStack blood) {
        return this.blood.test(blood);
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryAccess) {
        return this.result.copy();
    }

    public ItemStack getResultItem() {
        return this.result.copy();
    }

    @Override
    public ItemStack craft(BlockEntityDragonForge dragonforge, DynamicRegistryManager registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public Identifier getId() {
        return this.recipeId;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(IafBlocks.DRAGONFORGE_FIRE_CORE.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return IafRecipeSerializers.DRAGONFORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return IafRecipes.DRAGON_FORGE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<DragonForgeRecipe> {
        @Override
        public DragonForgeRecipe read(Identifier recipeId, JsonObject json) {
            String dragonType = JsonUtils.getString(json, "dragon_type");
            Ingredient input = Ingredient.fromJson(JsonUtils.getJsonObject(json, "input"));
            Ingredient blood = Ingredient.fromJson(JsonUtils.getJsonObject(json, "blood"));
            int cookTime = JsonUtils.getInt(json, "cook_time");
            ItemStack result = ShapedRecipe.outputFromJson(JsonUtils.getJsonObject(json, "result"));
            return new DragonForgeRecipe(recipeId, input, blood, result, dragonType, cookTime);
        }

        @Override
        public DragonForgeRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            int cookTime = buffer.readInt();
            String dragonType = buffer.readString();
            Ingredient input = Ingredient.fromPacket(buffer);
            Ingredient blood = Ingredient.fromPacket(buffer);
            ItemStack result = buffer.readItemStack();
            return new DragonForgeRecipe(recipeId, input, blood, result, dragonType, cookTime);
        }

        @Override
        public void write(PacketByteBuf buffer, DragonForgeRecipe recipe) {
            buffer.writeInt(recipe.cookTime);
            buffer.writeString(recipe.dragonType);
            recipe.input.write(buffer);
            recipe.blood.write(buffer);
            buffer.writeItemStack(recipe.result);
        }
    }
}
