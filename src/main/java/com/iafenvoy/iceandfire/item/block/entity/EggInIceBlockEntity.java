package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonEggEntity;
import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
            if (!level.isClientSide()) {
                IceDragonEntity dragon = IafEntities.ICE_DRAGON.get().create(level, EntitySpawnReason.TRIGGERED);
                assert dragon != null;
                dragon.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                dragon.setVariant(entityEggInIce.type.getName());
                dragon.setGender(ThreadLocalRandom.current().nextBoolean());
                dragon.setTame(true, false);
                dragon.setHunger(50);
                if (entityEggInIce.ownerUUID != null) dragon.setOwnerReference(EntityReference.of(entityEggInIce.ownerUUID));
                level.addFreshEntity(dragon);
                entityEggInIce.spawned = true;
                level.destroyBlock(pos, false);
                level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
            }
        entityEggInIce.ticksExisted++;
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        if (this.type != null) output.putString("Color", this.type.getName());
        else output.putByte("Color", (byte) 0);
        output.putInt("Age", this.age);
        if (this.ownerUUID == null) output.putString("OwnerUUID", "");
        else output.store("OwnerUUID", UUIDUtil.CODEC, this.ownerUUID);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.type = DragonColor.getById(input.getStringOr("Color", ""));
        this.age = input.getIntOr("Age", 0);
        input.read("OwnerUUID", UUIDUtil.CODEC).ifPresent(uuid -> this.ownerUUID = uuid);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void spawnEgg() {
        if (this.type != null) {
            DragonEggEntity egg = new DragonEggEntity(IafEntities.DRAGON_EGG.get(), this.level);
            egg.setEggType(this.type);
            egg.setPos(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5);
            egg.setOwnerId(this.ownerUUID);
            assert this.level != null;
            if (!this.level.isClientSide())
                this.level.addFreshEntity(egg);
        }
    }
}
