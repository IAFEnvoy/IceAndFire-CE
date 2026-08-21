package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;

public interface ValueInput extends net.neoforged.neoforge.common.extensions.ValueInputExtension {
    <T> Optional<T> read(String name, Codec<T> codec);

    @Deprecated
    <T> Optional<T> read(MapCodec<T> codec);

    Optional<ValueInput> child(String name);

    ValueInput childOrEmpty(String name);

    Optional<ValueInput.ValueInputList> childrenList(String name);

    ValueInput.ValueInputList childrenListOrEmpty(String name);

    <T> Optional<ValueInput.TypedInputList<T>> list(String name, Codec<T> codec);

    <T> ValueInput.TypedInputList<T> listOrEmpty(String name, Codec<T> codec);

    boolean getBooleanOr(String name, boolean defaultValue);

    byte getByteOr(String name, byte defaultValue);

    int getShortOr(String name, short defaultValue);

    Optional<Integer> getInt(String name);

    int getIntOr(String name, int defaultValue);

    long getLongOr(String name, long defaultValue);

    Optional<Long> getLong(String name);

    float getFloatOr(String name, float defaultValue);

    double getDoubleOr(String name, double defaultValue);

    Optional<String> getString(String name);

    String getStringOr(String name, String defaultValue);

    Optional<int[]> getIntArray(String name);

    @Deprecated
    HolderLookup.Provider lookup();

    public interface TypedInputList<T> extends Iterable<T> {
        boolean isEmpty();

        Stream<T> stream();
    }

    public interface ValueInputList extends Iterable<ValueInput> {
        boolean isEmpty();

        Stream<ValueInput> stream();
    }
}
