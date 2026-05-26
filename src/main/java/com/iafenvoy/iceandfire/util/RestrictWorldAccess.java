package com.iafenvoy.iceandfire.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class RestrictWorldAccess implements ServerLevelAccessor {
    private final ServerLevelAccessor origin;
    private final Predicate<BlockPos> checker;

    public RestrictWorldAccess(ServerLevelAccessor origin, Predicate<BlockPos> checker) {
        this.origin = origin;
        this.checker = checker;
    }

    @Override
    public @NotNull ServerLevel getLevel() {
        return this.origin.getLevel();
    }

    @Override
    public long nextSubTickCount() {
        return this.origin.nextSubTickCount();
    }

    @Override
    public @NotNull LevelTickAccess<Block> getBlockTicks() {
        return this.origin.getBlockTicks();
    }

    @Override
    public @NotNull LevelTickAccess<Fluid> getFluidTicks() {
        return this.origin.getFluidTicks();
    }

    @Override
    public @NotNull LevelData getLevelData() {
        return this.origin.getLevelData();
    }

    @Override
    public @NotNull DifficultyInstance getCurrentDifficultyAt(@NotNull BlockPos pos) {
        return this.origin.getCurrentDifficultyAt(pos);
    }

    @Override
    public @Nullable MinecraftServer getServer() {
        return this.origin.getServer();
    }

    @Override
    public @NotNull ChunkSource getChunkSource() {
        return this.origin.getChunkSource();
    }

    @Override
    public @NotNull RandomSource getRandom() {
        return this.origin.getRandom();
    }

    @Override
    public void playSound(@Nullable Player except, @NotNull BlockPos pos, @NotNull SoundEvent sound, @NotNull SoundSource category, float volume, float pitch) {
        this.origin.playSound(except, pos, sound, category, volume, pitch);
    }

    @Override
    public void addParticle(@NotNull ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this.origin.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void levelEvent(@Nullable Player player, int eventId, @NotNull BlockPos pos, int data) {
        this.origin.levelEvent(player, eventId, pos, data);
    }

    @Override
    public void gameEvent(@NotNull Holder<GameEvent> event, @NotNull Vec3 emitterPos, GameEvent.@NotNull Context emitter) {
        this.origin.gameEvent(event, emitterPos, emitter);
    }

    @Override
    public float getShade(@NotNull Direction direction, boolean shaded) {
        return this.origin.getShade(direction, shaded);
    }

    @Override
    public @NotNull LevelLightEngine getLightEngine() {
        return this.origin.getLightEngine();
    }

    @Override
    public @NotNull WorldBorder getWorldBorder() {
        return this.origin.getWorldBorder();
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(@NotNull BlockPos pos) {
        if (!this.checker.test(pos)) return null;
        return this.origin.getBlockEntity(pos);
    }

    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos pos) {
        if (!this.checker.test(pos)) return Blocks.AIR.defaultBlockState();
        return this.origin.getBlockState(pos);
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos pos) {
        if (!this.checker.test(pos)) return Fluids.EMPTY.defaultFluidState();
        return this.origin.getFluidState(pos);
    }

    @Override
    public @NotNull List<Entity> getEntities(@Nullable Entity except, @NotNull AABB box, @NotNull Predicate<? super Entity> predicate) {
        return this.origin.getEntities(except, box, predicate);
    }

    @Override
    public <T extends Entity> @NotNull List<T> getEntities(@NotNull EntityTypeTest<Entity, T> filter, @NotNull AABB box, @NotNull Predicate<? super T> predicate) {
        return this.origin.getEntities(filter, box, predicate);
    }

    @Override
    public @NotNull List<? extends Player> players() {
        return this.origin.players();
    }

    @Override
    public boolean setBlock(@NotNull BlockPos pos, @NotNull BlockState state, int flags, int maxUpdateDepth) {
        if (!this.checker.test(pos)) return false;
        return this.origin.setBlock(pos, state, flags, maxUpdateDepth);
    }

    @Override
    public boolean removeBlock(@NotNull BlockPos pos, boolean move) {
        return this.origin.removeBlock(pos, move);
    }

    @Override
    public boolean destroyBlock(@NotNull BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        return this.origin.destroyBlock(pos, drop, breakingEntity, maxUpdateDepth);
    }

    @Override
    public boolean isStateAtPosition(@NotNull BlockPos pos, @NotNull Predicate<BlockState> state) {
        return this.origin.isStateAtPosition(pos, state);
    }

    @Override
    public boolean isFluidAtPosition(@NotNull BlockPos pos, @NotNull Predicate<FluidState> state) {
        return this.origin.isFluidAtPosition(pos, state);
    }

    @Override
    public @Nullable ChunkAccess getChunk(int chunkX, int chunkZ, @NotNull ChunkStatus leastStatus, boolean create) {
        return this.origin.getChunk(chunkX, chunkZ, leastStatus, create);
    }

    @Override
    public int getHeight(Heightmap.@NotNull Types heightmap, int x, int z) {
        if (!this.checker.test(new BlockPos(x, 0, z))) return 64;
        return this.origin.getHeight(heightmap, x, z);
    }

    @Override
    public int getSkyDarken() {
        return this.origin.getSkyDarken();
    }

    @Override
    public @NotNull BiomeManager getBiomeManager() {
        return this.origin.getBiomeManager();
    }

    @Override
    public @NotNull Holder<Biome> getUncachedNoiseBiome(int biomeX, int biomeY, int biomeZ) {
        return this.origin.getUncachedNoiseBiome(biomeX, biomeY, biomeZ);
    }

    @Override
    public boolean isClientSide() {
        return this.origin.isClientSide();
    }

    @Deprecated
    @Override
    public int getSeaLevel() {
        return this.origin.getSeaLevel();
    }

    @Override
    public @NotNull DimensionType dimensionType() {
        return this.origin.dimensionType();
    }

    @Override
    public @NotNull RegistryAccess registryAccess() {
        return this.origin.registryAccess();
    }

    @Override
    public @NotNull FeatureFlagSet enabledFeatures() {
        return this.origin.enabledFeatures();
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        if (!this.checker.test(entity.blockPosition())) return false;
        return this.origin.addFreshEntity(entity);
    }
}
