package com.iafenvoy.iceandfire.forge.component;

import com.iafenvoy.iceandfire.data.component.PortalData;
import com.iafenvoy.uranus.forge.component.ITickableCapability;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public record PortalDataStorage(PortalData data) implements ITickableCapability {
    public PortalDataStorage(PlayerEntity player) {
        this(new PortalData(player));
    }

    @Override
    public NbtCompound serializeNBT() {
        NbtCompound compound = new NbtCompound();
        this.data.writeToNbt(compound);
        return compound;
    }

    @Override
    public void deserializeNBT(NbtCompound compound) {
        this.data.readFromNbt(compound);
    }

    @Override
    public void tick() {
        this.data.tick();
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
