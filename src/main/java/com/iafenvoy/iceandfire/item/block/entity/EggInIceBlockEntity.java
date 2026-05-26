package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class EggInIceBlockEntity extends BlockEntity {
    public DragonColor type;
    public int age;
    public int ticksExisted;
    public UUID ownerUUID;
    // boolean to prevent time in a bottle shenanigans
    private boolean spawned;

    public EggInIceBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.EGG_IN_ICE.get(), pos, state);
    }

    public static void tickEgg(Level level, BlockPos pos, BlockState state, EggInIceBlockEntity entityEggInIce) {
        entityEggInIce.age++;
        if (entityEggInIce.age >= IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue() && entityEggInIce.type != null && !entityEggInIce.spawned)
            if (!level.isClientSide) {
                IceDragonEntity dragon = IafEntities.ICE_DRAGON.get().create(level);
                assert dragon != null;
                dragon.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                dragon.setVariant(entityEggInIce.type.getName());
                dragon.setGender(ThreadLocalRandom.current().nextBoolean());
                dragon.setTame(true, false);
                dragon.setHunger(50);
                dragon.setOwnerUUID(entityEggInIce.ownerUUID);
                level.addFreshEntity(dragon);
                entityEggInIce.spawned = true;
                level.destroyBlock(pos, false);
                level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }
        entityEggInIce.ticksExisted++;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        if (this.type != null) nbt.putString("Color", this.type.getName());
        else nbt.putByte("Color", (byte) 0);
        nbt.putInt("Age", this.age);
        if (this.ownerUUID == null) nbt.putString("OwnerUUID", "");
        else nbt.putUUID("OwnerUUID", this.ownerUUID);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.type = DragonColor.getById(nbt.getString("Color"));
        this.age = nbt.getInt("Age");
        UUID s = null;
        if (nbt.hasUUID("OwnerUUID"))
            s = nbt.getUUID("OwnerUUID");
        else
            try {
                String s1 = nbt.getString("OwnerUUID");
                assert this.level != null;
                s = OldUsersConverter.convertMobOwnerIfNecessary(this.level.getServer(), s1);
            } catch (Exception ignored) {
            }
        if (s != null) this.ownerUUID = s;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registryLookup) {
        CompoundTag nbtTagCompound = new CompoundTag();
        this.saveAdditional(nbtTagCompound, registryLookup);
        return nbtTagCompound;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTagCompound = new CompoundTag();
        this.saveAdditional(nbtTagCompound, null);
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void spawnEgg() {
        if (this.type != null) {
            DragonEggEntity egg = new DragonEggEntity(IafEntities.DRAGON_EGG.get(), this.level);
            egg.setEggType(this.type);
            egg.setPos(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5);
            egg.setOwnerId(this.ownerUUID);
            assert this.level != null;
            if (!this.level.isClientSide)
                this.level.addFreshEntity(egg);
        }
    }
}
