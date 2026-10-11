package com.iafenvoy.iceandfire.entity.block;

import com.iafenvoy.iceandfire.entity.util.DreadSpawnerBaseLogic;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;

public class BlockEntityDreadSpawner extends MobSpawnerBlockEntity {
    private final BlockEntityType<?> type;
    private final DreadSpawnerBaseLogic spawner = new DreadSpawnerBaseLogic() {
        @Override
        public void sendStatus(World world, BlockPos pos, int status) {
            // Vanilla sends this for Blocks.SPAWNER, which ServerWorld rejects for this block, so the
            // client never gets the spawn effect. Send it for our own block instead.
            world.addSyncedBlockEvent(pos, IafBlocks.DREAD_SPAWNER.get(), status, 0);
        }

        @Override
        public void setSpawnEntry(World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
            super.setSpawnEntry(world, pos, spawnEntry);
            if (world != null) {
                BlockState blockstate = world.getBlockState(pos);
                world.updateListeners(pos, blockstate, blockstate, 4);
            }
        }
    };

    public BlockEntityDreadSpawner(BlockPos pos, BlockState state) {
        super(pos, state);
        this.type = IafBlockEntities.DREAD_SPAWNER.get();
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.spawner.readNbt(this.world, this.pos, compound);
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        // MobSpawnerBlockEntity only saves its own private logic, which is never ticked for this block,
        // so the dread logic has to be written on top of it to keep the spawner state.
        super.writeNbt(compound);
        this.spawner.writeNbt(compound);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound compoundtag = this.createNbt();
        compoundtag.remove("SpawnPotentials");
        return compoundtag;
    }

    @Override
    public boolean onSyncedBlockEvent(int p_59797_, int p_59798_) {
        return this.spawner.handleStatus(this.world, p_59797_) || super.onSyncedBlockEvent(p_59797_, p_59798_);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    @Override
    public MobSpawnerLogic getLogic() {
        return this.spawner;
    }

    @Override
    public BlockEntityType<?> getType() {
        return this.type != null ? this.type : super.getType();
    }

    @Override
    public void setEntityType(EntityType<?> type, Random random) {
        // MobSpawnerBlockEntity writes to the private logic, which does nothing here.
        this.spawner.setEntityId(type, this.world, random, this.pos);
        this.markDirty();
    }

    // Vanilla MobSpawnerBlockEntity#serverTick/clientTick tick their private logic, which is inert for
    // this block, so the dread logic is ticked directly instead.
    public static void clientTick(World world, BlockPos pos, BlockState state, BlockEntityDreadSpawner blockEntity) {
        blockEntity.spawner.clientTick(world, pos);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BlockEntityDreadSpawner blockEntity) {
        blockEntity.spawner.serverTick((ServerWorld) world, pos);
    }
}
