package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.data.component.ChainData;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChainTieEntity extends HangingEntity {
    public ChainTieEntity(EntityType<? extends HangingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public ChainTieEntity(EntityType<? extends HangingEntity> type, Level worldIn, BlockPos hangingPositionIn) {
        super(type, worldIn, hangingPositionIn);
        this.setPos(hangingPositionIn.getX() + 0.5D, hangingPositionIn.getY(), hangingPositionIn.getZ() + 0.5D);
    }

    public static ChainTieEntity createTie(Level worldIn, BlockPos fence) {
        ChainTieEntity chainTieEntity = new ChainTieEntity(IafEntities.CHAIN_TIE.get(), worldIn, fence);
        worldIn.addFreshEntity(chainTieEntity);
        chainTieEntity.playPlacementSound();
        return chainTieEntity;
    }

    public static ChainTieEntity getKnotForPosition(Level worldIn, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (ChainTieEntity entityleashknot : worldIn.getEntitiesOfClass(ChainTieEntity.class, new AABB(i - 1.0D, j - 1.0D, k - 1.0D, i + 1.0D, j + 1.0D, k + 1.0D)))
            if (entityleashknot != null && entityleashknot.pos.equals(pos))
                return entityleashknot;
        return null;
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(Mth.floor(x) + 0.5D, Mth.floor(y) + 0.5D, Mth.floor(z) + 0.5D);
    }

    @Override
    protected @NotNull AABB calculateBoundingBox(@NotNull BlockPos pos, @NotNull Direction side) {
        this.setPosRaw(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D);
        double xSize = 0.3D;
        double ySize = 0.875D;
        AABB box = new AABB(this.getX() - xSize, this.getY() - 0.5, this.getZ() - xSize, this.getX() + xSize, this.getY() + ySize - 0.5, this.getZ() + xSize);
        this.setBoundingBox(box);
        return box;
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, DamageSource source, float amount) {
        if (source.getEntity() != null && source.getEntity() instanceof Player)
            return super.hurtServer(level, source, amount);
        return false;
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getInt("TileX").isPresent()) {
            this.pos = new BlockPos(input.getIntOr("TileX", this.pos.getX()), input.getIntOr("TileY", this.pos.getY()), input.getIntOr("TileZ", this.pos.getZ()));
            this.recalculateBoundingBox();
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0D;
    }

    @Override
    public void dropItem(@NonNull ServerLevel level, Entity brokenEntity) {
        this.playSound(SoundEvents.ARMOR_EQUIP_CHAIN.value(), 1.0F, 1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void remove(@NotNull RemovalReason removalReason) {
        super.remove(removalReason);
        double d0 = 30D;

        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - d0, this.getY() - d0, this.getZ() - d0, this.getX() + d0, this.getY() + d0, this.getZ() + d0));

        for (LivingEntity livingEntity : list) {
            ChainData chainData = ChainData.get(livingEntity);
            if (chainData.isChainedTo(this.getUUID())) {
                chainData.removeChain(this.getUUID());
                ItemEntity entityitem = new ItemEntity(this.level(), this.getX(), this.getY() + 1, this.getZ(), new ItemStack(IafItems.CHAIN.get()));
                entityitem.setNoPickUpDelay();
                this.level().addFreshEntity(entityitem);
            }
        }
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand, @NotNull Vec3 hitPos) {
        if (this.level().isClientSide())
            return InteractionResult.SUCCESS;
        else {
            AtomicBoolean flag = new AtomicBoolean(false);
            double radius = 30D;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + radius, this.getZ() + radius));

            for (LivingEntity livingEntity : list) {
                ChainData chainData = ChainData.get(livingEntity);
                if (chainData.isChainedTo(player.getUUID())) {
                    chainData.removeChain(player.getUUID());
                    chainData.attachChain(this.getUUID());
                    flag.set(true);
                }
            }

            if (!flag.get()) {
                this.remove(RemovalReason.DISCARDED);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public boolean survives() {
        return this.level().getBlockState(this.pos).getBlock() instanceof WallBlock;
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.ARMOR_EQUIP_CHAIN.value(), 1.0F, 1.0F);
    }
}
