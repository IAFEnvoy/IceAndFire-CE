package com.iafenvoy.iceandfire.forge.component;

import com.iafenvoy.iceandfire.data.component.IafEntityData;
import com.iafenvoy.uranus.forge.component.ITickableCapability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public record EntityDataStorage(IafEntityData data) implements ITickableCapability {
    public EntityDataStorage(LivingEntity living) {
        this(new IafEntityData(living));
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound compound = new NbtCompound();
        this.data.serialize(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(NbtCompound compound) {
        this.data.deserialize(compound);
    }

    @Override
    public void tick() {
        this.data.tick();
    }

    @Override
    public boolean isDirty() {
        return this.data.isDirty();
    }
}
