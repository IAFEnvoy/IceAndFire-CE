package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class RecipeMap {
    public static final RecipeMap EMPTY = new RecipeMap(ImmutableMultimap.of(), Map.of());
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;
    private final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey;

    private RecipeMap(Multimap<RecipeType<?>, RecipeHolder<?>> byType, Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey) {
        this.byType = byType;
        this.byKey = byKey;
    }

    public static RecipeMap create(Iterable<RecipeHolder<?>> recipes) {
        Builder<RecipeType<?>, RecipeHolder<?>> byType = ImmutableMultimap.builder();
        com.google.common.collect.ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey = ImmutableMap.builder();

        for (RecipeHolder<?> recipe : recipes) {
            byType.put(recipe.value().getType(), recipe);
            byKey.put(recipe.id(), recipe);
        }

        return new RecipeMap(byType.build(), byKey.build());
    }

    // Neo: handle ordering according to any recipe priorities
    public void order(it.unimi.dsi.fastutil.objects.Object2IntMap<ResourceKey<net.minecraft.world.item.crafting.Recipe<?>>> recipePriorities) {
        it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap<java.util.List<RecipeHolder<?>>> priorityBuilder = new it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap<>();
        com.google.common.collect.LinkedListMultimap<RecipeType<?>, RecipeHolder<?>> finalBuilder = com.google.common.collect.LinkedListMultimap.create();

        for (RecipeHolder<?> recipeholder : this.byKey.values()) {
            int priority = recipePriorities.getOrDefault(recipeholder.id(), 0);
            priorityBuilder.computeIfAbsent(priority, p -> new java.util.ArrayList<>()).add(recipeholder);
        }

        for (var list : priorityBuilder.reversed().values()) {
            for (RecipeHolder<?> recipeHolder : list) {
                finalBuilder.put(recipeHolder.value().getType(), recipeHolder);
            }
        }

        this.byType = ImmutableMultimap.copyOf(finalBuilder);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> type) {
        return (Collection<RecipeHolder<T>>)(Collection<?>)this.byType.get(type);
    }

    public Collection<RecipeHolder<?>> values() {
        return this.byKey.values();
    }

    public @Nullable RecipeHolder<?> byKey(ResourceKey<Recipe<?>> recipeId) {
        return this.byKey.get(recipeId);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> getRecipesFor(RecipeType<T> type, I container, Level level) {
        return container.isEmpty() ? Stream.empty() : this.byType(type).stream().filter(r -> r.value().matches(container, level));
    }
}
