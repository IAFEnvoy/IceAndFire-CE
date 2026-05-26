package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.entity.PixieEntity;
import com.iafenvoy.iceandfire.network.payload.UpdatePixieHouseS2CPayload;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PixieHouseBlockEntity extends BlockEntity {
    private static final float PARTICLE_WIDTH = 0.3F;
    private static final float PARTICLE_HEIGHT = 0.6F;
    private final Random rand;
    public int houseType;
    public boolean hasPixie;
    public boolean tamedPixie;
    public UUID pixieOwnerUUID;
    public int pixieType;
    public NonNullList<ItemStack> pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);

    public PixieHouseBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.PIXIE_HOUSE.get(), pos, state);
        this.rand = new Random();
    }

    public static int getHouseTypeFromBlock(Block block) {
        if (block == IafBlocks.PIXIE_HOUSE_MUSHROOM_RED.get()) return 1;
        if (block == IafBlocks.PIXIE_HOUSE_MUSHROOM_BROWN.get()) return 0;
        if (block == IafBlocks.PIXIE_HOUSE_OAK.get()) return 3;
        if (block == IafBlocks.PIXIE_HOUSE_BIRCH.get()) return 2;
        if (block == IafBlocks.PIXIE_HOUSE_SPRUCE.get()) return 5;
        if (block == IafBlocks.PIXIE_HOUSE_DARK_OAK.get()) return 4;
        else return 0;
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, PixieHouseBlockEntity entityPixieHouse) {
        if (entityPixieHouse.hasPixie)
            level.addParticle(IafParticles.PIXIE_DUST.get(),
                    pos.getX() + 0.5F + (double) (entityPixieHouse.rand.nextFloat() * PARTICLE_WIDTH * 2F) - PARTICLE_WIDTH,
                    pos.getY() + (double) (entityPixieHouse.rand.nextFloat() * PARTICLE_HEIGHT),
                    pos.getZ() + 0.5F + (double) (entityPixieHouse.rand.nextFloat() * PARTICLE_WIDTH * 2F) - PARTICLE_WIDTH,
                    PixieEntity.PARTICLE_RGB[entityPixieHouse.pixieType][0], PixieEntity.PARTICLE_RGB[entityPixieHouse.pixieType][1],
                    PixieEntity.PARTICLE_RGB[entityPixieHouse.pixieType][2]);
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, PixieHouseBlockEntity entityPixieHouse) {
        if (entityPixieHouse.hasPixie && ThreadLocalRandom.current().nextInt(100) == 0)
            entityPixieHouse.releasePixie();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putInt("HouseType", this.houseType);
        nbt.putBoolean("HasPixie", this.hasPixie);
        nbt.putInt("PixieType", this.pixieType);
        nbt.putBoolean("TamedPixie", this.tamedPixie);
        if (this.pixieOwnerUUID != null)
            nbt.putUUID("PixieOwnerUUID", this.pixieOwnerUUID);
        ContainerHelper.saveAllItems(nbt, this.pixieItems, registryLookup);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registryLookup) {
        return this.saveWithFullMetadata(registryLookup);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.houseType = nbt.getInt("HouseType");
        this.hasPixie = nbt.getBoolean("HasPixie");
        this.pixieType = nbt.getInt("PixieType");
        this.tamedPixie = nbt.getBoolean("TamedPixie");
        if (nbt.hasUUID("PixieOwnerUUID"))
            this.pixieOwnerUUID = nbt.getUUID("PixieOwnerUUID");
        this.pixieItems = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.pixieItems, registryLookup);
    }

    public void releasePixie() {
        PixieEntity pixie = new PixieEntity(IafEntities.PIXIE.get(), this.level);
        pixie.absMoveTo(this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 1F, this.worldPosition.getZ() + 0.5F, ThreadLocalRandom.current().nextInt(360), 0);
        pixie.setItemInHand(InteractionHand.MAIN_HAND, this.pixieItems.getFirst());
        pixie.setColor(this.pixieType);
        pixie.ticksUntilHouseAI = 500;
        pixie.setTame(this.tamedPixie, true);
        pixie.setOwnerUUID(this.pixieOwnerUUID);
        assert this.level != null;
        if (!this.level.isClientSide)
            this.level.addFreshEntity(pixie);
        this.hasPixie = false;
        this.pixieType = 0;
        if (!this.level.isClientSide)
            PacketDistributor.sendToAllPlayers(new UpdatePixieHouseS2CPayload(this.worldPosition, false, 0));
    }
}
