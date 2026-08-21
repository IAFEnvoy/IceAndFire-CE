package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.GhostEntity;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class GhostChestBlockEntity extends ChestBlockEntity {
    private boolean generatedGhost = false;

    public GhostChestBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.GHOST_CHEST.get(), pos, state);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.generatedGhost = input.getBooleanOr("generatedGhost", false);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("generatedGhost", this.generatedGhost);
    }

    @Override
    public void startOpen(@NotNull ContainerUser user) {
        super.startOpen(user);
        assert this.level != null;
        if (this.level instanceof ServerLevel serverLevel && (!this.generatedGhost || IafCommonConfig.INSTANCE.ghost.alwaysSpawnFromChest.getValue()) && this.level.getDifficulty() != Difficulty.PEACEFUL) {
            this.generatedGhost = true;
            GhostEntity ghost = IafEntities.GHOST.get().create(serverLevel, EntitySpawnReason.SPAWNER);
            assert ghost != null;
            ghost.snapTo(this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 0.5F, this.worldPosition.getZ() + 0.5F, ThreadLocalRandom.current().nextFloat() * 360F, 0);
            ghost.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.worldPosition), EntitySpawnReason.SPAWNER, null);
            if (user.getLivingEntity() instanceof Player player && !player.isCreative()) ghost.setTarget(player);
            ghost.setPersistenceRequired();
            serverLevel.addFreshEntity(ghost);
            ghost.setAnimation(GhostEntity.ANIMATION_SCARE);
            ghost.setHomeTo(this.worldPosition, 4);
            ghost.setFromChest(true);
        }
    }

    @Override
    protected void signalOpenCount(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, int p_155336_, int p_155337_) {
        super.signalOpenCount(level, pos, state, p_155336_, p_155337_);
        level.updateNeighborsAt(pos.below(), state.getBlock());
    }
}
