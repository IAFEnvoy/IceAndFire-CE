package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.block.DragonForgeInputBlock;
import com.iafenvoy.iceandfire.registry.IafAttributes;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.util.DragonTypeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class DragonForgeInputBlockEntity extends BlockEntity {
    private static final int LURE_DISTANCE = 50;
    private int ticksSinceDragonFire;
    private DragonForgeBlockEntity core = null;

    public DragonForgeInputBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DRAGONFORGE_INPUT.get(), pos, state);
    }

    public static void tick(final Level level, final BlockPos position, final BlockState state, final DragonForgeInputBlockEntity forgeInput) {
        if (level.isClientSide) return;
        forgeInput.refreshCore();

        if (forgeInput.ticksSinceDragonFire > 0)
            forgeInput.ticksSinceDragonFire--;

        if ((forgeInput.ticksSinceDragonFire == 0 || forgeInput.core == null) && forgeInput.isActive()) {
            BlockEntity tileentity = level.getBlockEntity(position);
            level.setBlockAndUpdate(position, forgeInput.getDeactivatedState());
            if (tileentity != null) {
                tileentity.clearRemoved();
                level.setBlockEntity(tileentity);
            }
        }

        if (forgeInput.isAssembled())
            forgeInput.lureDragons();
    }

    public void onHitWithFlame(LivingEntity entity) {
        this.onHitWithFlame(entity.getAttributeValue(IafAttributes.DRAGON_FORGE_SPEED));
    }

    public void onHitWithFlame(double amount) {
        this.refreshCore();
        if (this.core != null)
            this.core.transferPower(amount);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registryLookup) {
        return this.saveWithFullMetadata(registryLookup);
    }

    protected void lureDragons() {
        AABB searchArea = new AABB(
                (double) this.worldPosition.getX() - LURE_DISTANCE,
                (double) this.worldPosition.getY() - LURE_DISTANCE,
                (double) this.worldPosition.getZ() - LURE_DISTANCE,
                (double) this.worldPosition.getX() + LURE_DISTANCE,
                (double) this.worldPosition.getY() + LURE_DISTANCE,
                (double) this.worldPosition.getZ() + LURE_DISTANCE
        );

        boolean dragonSelected = false;

        assert this.level != null;
        for (DragonBaseEntity dragon : this.level.getEntitiesOfClass(DragonBaseEntity.class, searchArea)) {
            if (!dragonSelected && this.canDragonFuel(dragon)) {
                dragon.burningTarget = this.worldPosition;
                dragonSelected = true;
            } else if (this.worldPosition.equals(dragon.burningTarget)) {
                dragon.burningTarget = null;
                dragon.setBreathingFire(false);
            }
        }
    }

    public boolean isAssembled() {
        this.refreshCore();
        return (this.core != null && this.core.assembled() && this.core.canSmelt());
    }

    private void refreshCore() {
        if (this.level == null) return;
        if (this.core == null || this.core.isRemoved() || this.core.getDragonType() != this.getDragonType() || this.level.getBlockEntity(this.core.getBlockPos()) != this.core)
            this.core = this.getConnectedTileEntity(this.worldPosition);
    }

    // Share the same eligibility, range and head-to-input ray with the dragon's breath logic.
    public boolean canDragonFuel(DragonBaseEntity dragon) {
        if (this.level == null || !dragon.isAlive() || dragon.isSleeping() || dragon.isBaby()
                || this.getDragonType() != dragon.dragonType
                || !(dragon.isChained() || dragon.isTame())) return false;
        Vec3 target = Vec3.atCenterOf(this.worldPosition);
        if (dragon.distanceToSqr(target) > LURE_DISTANCE * LURE_DISTANCE) return false;
        var result = this.level.clip(new ClipContext(dragon.getHeadPosition(), target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, dragon));
        return result.getType() == HitResult.Type.MISS
                || this.worldPosition.equals(result.getBlockPos());
    }

    private BlockState getDeactivatedState() {
        return DragonForgeInputBlock.getBlockByType(this.getDragonType()).defaultBlockState().setValue(DragonForgeInputBlock.ACTIVE, false);
    }

    private DragonType getDragonType() {
        return this.getBlockState().getBlock() instanceof DragonTypeProvider provider ? provider.getDragonType() : IafDragonTypes.FIRE;
    }

    private boolean isActive() {
        assert this.level != null;
        BlockState state = this.level.getBlockState(this.worldPosition);
        return state.getBlock() instanceof DragonForgeInputBlock && state.getValue(DragonForgeInputBlock.ACTIVE);
    }

    private DragonForgeBlockEntity getConnectedTileEntity(final BlockPos position) {
        assert this.level != null;
        for (Direction facing : Direction.Plane.HORIZONTAL)
            if (this.level.getBlockEntity(position.relative(facing)) instanceof DragonForgeBlockEntity forge && forge.getDragonType() == this.getDragonType())
                return forge;
        return null;
    }
}
