package com.iafenvoy.iceandfire.forge.component;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityDataProvider implements ICapabilitySerializable<NbtCompound> {
    public static final Capability<EntityDataStorage> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private EntityDataStorage storage;
    private final LazyOptional<EntityDataStorage> storageLazyOptional = LazyOptional.of(this::getOrCreateStorage);
    private final LivingEntity living;

    public EntityDataProvider(LivingEntity living) {
        this.living = living;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        return CAPABILITY.orEmpty(capability, this.storageLazyOptional);
    }

    @Override
    public NbtCompound serializeNBT() {
        return this.getOrCreateStorage().serializeNBT();
    }

    @Override
    public void deserializeNBT(NbtCompound arg) {
        this.getOrCreateStorage().deserializeNBT(arg);
    }

    private EntityDataStorage getOrCreateStorage() {
        if (this.storage == null) this.storage = new EntityDataStorage(this.living);
        return this.storage;
    }
}
