package com.iafenvoy.iceandfire.entity.util;

import com.iafenvoy.iceandfire.entity.util.dragon.DragonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class HomePosition {
    int x;
    int y;
    int z;
    BlockPos pos;
    String dimension;

    public HomePosition(CompoundTag compound) {
        this.read(compound);
    }

    public HomePosition(CompoundTag compound, Level world) {
        this.read(compound, world);
    }

    public HomePosition(BlockPos pos, Level world) {
        this(pos.getX(), pos.getY(), pos.getZ(), world);
    }

    public HomePosition(int x, int y, int z, Level world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pos = new BlockPos(x, y, z);
        this.dimension = DragonUtils.getDimensionName(world);
    }

    public BlockPos getPosition() {
        return this.pos;
    }

    public String getDimension() {
        return this.dimension == null ? "" : this.dimension;
    }

    public void write(CompoundTag compound) {
        compound.putInt("HomeAreaX", this.x);
        compound.putInt("HomeAreaY", this.y);
        compound.putInt("HomeAreaZ", this.z);
        if (this.dimension != null)
            compound.putString("HomeDimension", this.dimension);
    }

    public void read(CompoundTag compound, Level world) {
        this.read(compound);
        if (this.dimension == null)
            this.dimension = DragonUtils.getDimensionName(world);
    }

    public void read(CompoundTag compound) {
        if (compound.contains("HomeAreaX"))
            this.x = compound.getInt("HomeAreaX");
        if (compound.contains("HomeAreaY"))
            this.y = compound.getInt("HomeAreaY");
        if (compound.contains("HomeAreaZ"))
            this.z = compound.getInt("HomeAreaZ");
        this.pos = new BlockPos(this.x, this.y, this.z);
        if (compound.contains("HomeDimension"))
            this.dimension = compound.getString("HomeDimension");
    }
}

