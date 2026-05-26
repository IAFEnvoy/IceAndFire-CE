package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.GhostEntity;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class GhostChestBlockEntity extends ChestBlockEntity {
    private boolean generatedGhost = false;

    public GhostChestBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.GHOST_CHEST.get(), pos, state);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.generatedGhost = nbt.getBoolean("generatedGhost");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putBoolean("generatedGhost", this.generatedGhost);
    }

    @Override
    public void startOpen(@NotNull Player player) {
        super.startOpen(player);
        assert this.level != null;
        if ((!this.generatedGhost || IafCommonConfig.INSTANCE.ghost.alwaysSpawnFromChest.getValue()) && this.level.getDifficulty() != Difficulty.PEACEFUL) {
            this.generatedGhost = true;
            GhostEntity ghost = IafEntities.GHOST.get().create(this.level);
            assert ghost != null;
            ghost.absMoveTo(this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 0.5F, this.worldPosition.getZ() + 0.5F, ThreadLocalRandom.current().nextFloat() * 360F, 0);
            if (this.level instanceof ServerLevel serverWorld) {
                ghost.finalizeSpawn(serverWorld, this.level.getCurrentDifficultyAt(this.worldPosition), MobSpawnType.SPAWNER, null);
                if (!player.isCreative()) ghost.setTarget(player);
                ghost.setPersistenceRequired();
                this.level.addFreshEntity(ghost);
            }
            ghost.setAnimation(GhostEntity.ANIMATION_SCARE);
            ghost.restrictTo(this.worldPosition, 4);
            ghost.setFromChest(true);
        }
    }

    @Override
    protected void signalOpenCount(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, int p_155336_, int p_155337_) {
        super.signalOpenCount(level, pos, state, p_155336_, p_155337_);
        level.updateNeighborsAt(pos.below(), state.getBlock());
    }
}
