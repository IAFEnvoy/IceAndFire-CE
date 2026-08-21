package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.entity.GhostEntity;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class GraveyardSoilBlock extends Block {
    public GraveyardSoilBlock() {
        super(Properties.of().mapColor(MapColor.DIRT).sound(SoundType.GRAVEL).strength(5, 1F).randomTicks());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel worldIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (!worldIn.isClientSide()) {
            if (!worldIn.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) return;
            if (worldIn.getOverworldClockTime() % 24000L >= 12000L && !worldIn.getBlockState(pos.above()).canOcclude() && rand.nextInt(9) == 0 && worldIn.getDifficulty() != Difficulty.PEACEFUL) {
                int checkRange = 32;
                int k = worldIn.getEntitiesOfClass(GhostEntity.class, (new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).inflate(checkRange)).size();
                if (k < 10) {
                    GhostEntity ghost = IafEntities.GHOST.get().create(worldIn, EntitySpawnReason.SPAWNER);
                    assert ghost != null;
                    ghost.snapTo(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, ThreadLocalRandom.current().nextFloat() * 360F, 0);
                    ghost.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(pos), EntitySpawnReason.SPAWNER, null);
                    worldIn.addFreshEntity(ghost);
                    ghost.setAnimation(GhostEntity.ANIMATION_SCARE);
                    ghost.setHomeTo(pos, 16);
                }
            }
        }
    }
}
