package com.iafenvoy.iceandfire.entity.block;

import com.iafenvoy.iceandfire.entity.EntityPixie;
import com.iafenvoy.iceandfire.registry.IafBlockEntities;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.iafenvoy.iceandfire.registry.IafParticles;
import com.iafenvoy.iceandfire.registry.IafSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class BlockEntityJar extends BlockEntity {

    private static final float PARTICLE_WIDTH = 0.3F;
    private static final float PARTICLE_HEIGHT = 0.6F;
    private final Random rand;
    public boolean hasPixie;
    public boolean prevHasProduced;
    public boolean hasProduced;
    public boolean tamedPixie;
    public UUID pixieOwnerUUID;
    public int pixieType;
    public int ticksExisted;
    private long nextProductionTick = -1;
    public DefaultedList<ItemStack> pixieItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
    public float rotationYaw;
    public float prevRotationYaw;

    public BlockEntityJar(BlockPos pos, BlockState state) {
        super(IafBlockEntities.PIXIE_JAR.get(), pos, state);
        this.rand = new Random();
        this.hasPixie = true;
    }

    public BlockEntityJar(BlockPos pos, BlockState state, boolean empty) {
        super(IafBlockEntities.PIXIE_JAR.get(), pos, state);
        this.rand = new Random();
        this.hasPixie = !empty;
    }

    public static void tick(World level, BlockPos pos, BlockState state, BlockEntityJar jar) {
        jar.ticksExisted++;
        if (!level.isClient) {
            if (jar.nextProductionTick < 0) {
                jar.nextProductionTick = level.getTime() + 24000 - Math.floorMod(jar.ticksExisted, 24000);
                jar.markDirty();
            }
            if (level.getTime() >= jar.nextProductionTick) {
                jar.nextProductionTick = level.getTime() + 24000;
                if (jar.hasPixie && !jar.hasProduced) {
                    jar.hasProduced = true;
                    BlockEntitySync.changed(jar);
                } else jar.markDirty();
            }
            return;
        }
        if (jar.hasPixie) {
            int color = Math.floorMod(jar.pixieType, EntityPixie.PARTICLE_RGB.length);
            level.addParticle(IafParticles.PIXIE_DUST.get(),
                    pos.getX() + 0.5 + (jar.rand.nextFloat() - 0.5) * PARTICLE_WIDTH * 2,
                    pos.getY() + jar.rand.nextFloat() * PARTICLE_HEIGHT,
                    pos.getZ() + 0.5 + (jar.rand.nextFloat() - 0.5) * PARTICLE_WIDTH * 2,
                    EntityPixie.PARTICLE_RGB[color][0], EntityPixie.PARTICLE_RGB[color][1], EntityPixie.PARTICLE_RGB[color][2]);
            if (jar.hasProduced != jar.prevHasProduced && jar.ticksExisted > 5)
                level.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, IafSounds.PIXIE_HURT.get(), SoundCategory.BLOCKS, 1, 1, false);
            if (jar.ticksExisted % 40 == 0 && jar.rand.nextInt(6) == 0)
                level.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, IafSounds.PIXIE_IDLE.get(), SoundCategory.BLOCKS, 1, 1, false);
        }
        jar.prevRotationYaw = jar.rotationYaw;
        if (jar.rand.nextInt(30) == 0) jar.rotationYaw = jar.rand.nextFloat() * 360 - 180;
        jar.prevHasProduced = jar.hasProduced;
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        compound.putBoolean("HasPixie", this.hasPixie);
        compound.putInt("PixieType", this.pixieType);
        compound.putBoolean("HasProduced", this.hasProduced);
        compound.putBoolean("TamedPixie", this.tamedPixie);
        if (this.pixieOwnerUUID != null)
            compound.putUuid("PixieOwnerUUID", this.pixieOwnerUUID);
        compound.putInt("TicksExisted", this.ticksExisted);
        compound.putLong("NextProductionTick", this.nextProductionTick);
        Inventories.writeNbt(compound, this.pixieItems);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbtWithIdentifyingData();
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        this.hasPixie = compound.getBoolean("HasPixie");
        this.pixieType = compound.getInt("PixieType");
        this.hasProduced = compound.getBoolean("HasProduced");
        this.ticksExisted = compound.getInt("TicksExisted");
        this.nextProductionTick = compound.contains("NextProductionTick") ? compound.getLong("NextProductionTick") : -1;
        this.tamedPixie = compound.getBoolean("TamedPixie");
        this.pixieOwnerUUID = compound.containsUuid("PixieOwnerUUID") ? compound.getUuid("PixieOwnerUUID") : null;
        this.pixieItems = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(compound, this.pixieItems);
        super.readNbt(compound);
    }

    public void releasePixie() {
        if (this.world == null || this.world.isClient || !this.hasPixie) return;
        EntityPixie pixie = new EntityPixie(IafEntities.PIXIE.get(), this.world);
        pixie.updatePositionAndAngles(this.pos.getX() + 0.5, this.pos.getY() + 1, this.pos.getZ() + 0.5, this.world.random.nextInt(360), 0);
        pixie.setStackInHand(Hand.MAIN_HAND, this.pixieItems.get(0).copy());
        pixie.setColor(this.pixieType);
        pixie.setTamed(this.tamedPixie);
        pixie.setOwnerUuid(this.pixieOwnerUUID);
        pixie.ticksUntilHouseAI = 500;
        if (!this.world.spawnEntity(pixie)) return;
        this.hasPixie = false;
        this.pixieType = 0;
        this.pixieItems.clear();
        this.tamedPixie = false;
        this.pixieOwnerUUID = null;
        this.hasProduced = false;
        BlockEntitySync.changed(this);
    }
}
