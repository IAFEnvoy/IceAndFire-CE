package com.iafenvoy.iceandfire.item.block.entity;

import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.block.DragonForgeInputBlock;
import com.iafenvoy.iceandfire.registry.IafAttributes;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.util.DragonTypeProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class DragonForgeInputBlockEntity extends BlockEntity {
    private static final int LURE_DISTANCE = 50;
    private int ticksSinceDragonFire;
    private DragonForgeBlockEntity core = null;

    public DragonForgeInputBlockEntity(BlockPos pos, BlockState state) {
        super(IafBlockEntities.DRAGONFORGE_INPUT.get(), pos, state);
    }

    public static void tick(final World level, final BlockPos position, final BlockState state, final DragonForgeInputBlockEntity forgeInput) {
        if (forgeInput.core == null)
            forgeInput.core = forgeInput.getConnectedTileEntity(position);

        if (forgeInput.ticksSinceDragonFire > 0)
            forgeInput.ticksSinceDragonFire--;

        if ((forgeInput.ticksSinceDragonFire == 0 || forgeInput.core == null) && forgeInput.isActive()) {
            BlockEntity tileentity = level.getBlockEntity(position);
            level.setBlockState(position, forgeInput.getDeactivatedState());
            if (tileentity != null) {
                tileentity.cancelRemoval();
                level.addBlockEntity(tileentity);
            }
        }

        if (forgeInput.isAssembled())
            forgeInput.lureDragons();
    }

    public void onHitWithFlame(LivingEntity entity) {
        this.onHitWithFlame(entity.getAttributeValue(IafAttributes.DRAGON_FORGE_SPEED));
    }

    public void onHitWithFlame(double amount) {
        if (this.core != null)
            this.core.transferPower(amount);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbtWithIdentifyingData(registryLookup);
    }

    protected void lureDragons() {
        Vec3d targetPosition = new Vec3d(
                this.getPos().getX() + 0.5F,
                this.getPos().getY() + 0.5F,
                this.getPos().getZ() + 0.5F
        );

        Box searchArea = new Box(
                (double) this.pos.getX() - LURE_DISTANCE,
                (double) this.pos.getY() - LURE_DISTANCE,
                (double) this.pos.getZ() - LURE_DISTANCE,
                (double) this.pos.getX() + LURE_DISTANCE,
                (double) this.pos.getY() + LURE_DISTANCE,
                (double) this.pos.getZ() + LURE_DISTANCE
        );

        boolean dragonSelected = false;

        assert this.world != null;
        for (DragonBaseEntity dragon : this.world.getNonSpectatingEntities(DragonBaseEntity.class, searchArea)) {
            if (!dragonSelected && /* Dragon Checks */ this.getDragonType() == dragon.dragonType && (dragon.isChained() || dragon.isTamed()) && this.canSeeInput(dragon, targetPosition)) {
                dragon.burningTarget = this.pos;
                dragonSelected = true;
            } else if (dragon.burningTarget == this.pos) {
                dragon.burningTarget = null;
                dragon.setBreathingFire(false);
            }
        }
    }

    public boolean isAssembled() {
        return (this.core != null && this.core.assembled() && this.core.canSmelt());
    }

    private boolean canSeeInput(DragonBaseEntity dragon, Vec3d target) {
        if (target != null) {
            assert this.world != null;
            HitResult rayTrace = this.world.raycast(new RaycastContext(dragon.getHeadPosition(), target, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, dragon));
            double distance = dragon.getHeadPosition().distanceTo(rayTrace.getPos());
            return distance < 10 + dragon.getWidth() * 2;
        }

        return false;
    }

    private BlockState getDeactivatedState() {
        return DragonForgeInputBlock.getBlockByType(this.getDragonType()).getDefaultState().with(DragonForgeInputBlock.ACTIVE, false);
    }

    private DragonType getDragonType() {
        return this.getCachedState().getBlock() instanceof DragonTypeProvider provider ? provider.getDragonType() : IafDragonTypes.FIRE;
    }

    private boolean isActive() {
        assert this.world != null;
        BlockState state = this.world.getBlockState(this.pos);
        return state.getBlock() instanceof DragonForgeInputBlock && state.get(DragonForgeInputBlock.ACTIVE);
    }

    private DragonForgeBlockEntity getConnectedTileEntity(final BlockPos position) {
        assert this.world != null;
        for (Direction facing : Direction.Type.HORIZONTAL)
            if (this.world.getBlockEntity(position.offset(facing)) instanceof DragonForgeBlockEntity forge)
                return forge;
        return null;
    }
}
