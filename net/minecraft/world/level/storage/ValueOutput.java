package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.jspecify.annotations.Nullable;

public interface ValueOutput extends net.neoforged.neoforge.common.extensions.ValueOutputExtension {
    <T> void store(String name, Codec<T> codec, T value);

    <T> void storeNullable(String name, Codec<T> codec, @Nullable T value);

    @Deprecated
    <T> void store(MapCodec<T> codec, T value);

    void putBoolean(String name, boolean value);

    void putByte(String name, byte value);

    void putShort(String name, short value);

    void putInt(String name, int value);

    void putLong(String name, long value);

    void putFloat(String name, float value);

    void putDouble(String name, double value);

    void putString(String name, String value);

    void putIntArray(String name, int[] value);

    ValueOutput child(String name);

    ValueOutput.ValueOutputList childrenList(String name);

    <T> ValueOutput.TypedOutputList<T> list(String name, Codec<T> codec);

    void discard(String name);

    boolean isEmpty();

    public interface TypedOutputList<T> {
        void add(T value);

        boolean isEmpty();
    }

    public interface ValueOutputList {
        ValueOutput addChild();

        void discardLast();

        boolean isEmpty();
    }
}
