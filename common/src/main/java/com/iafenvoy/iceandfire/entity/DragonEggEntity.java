package com.iafenvoy.iceandfire.entity;

import com.google.common.collect.ImmutableList;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.IDeadMob;
import com.iafenvoy.iceandfire.item.DragonEggItem;
import com.iafenvoy.iceandfire.item.block.entity.EggInIceBlockEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafDragonColors;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafSounds;
import com.iafenvoy.uranus.object.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class DragonEggEntity extends LivingEntity implements BlacklistedFromStatues, IDeadMob {
    protected static final TrackedData<Optional<UUID>> OWNER_UNIQUE_ID = DataTracker.registerData(DragonEggEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<String> DRAGON_TYPE = DataTracker.registerData(DragonEggEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> DRAGON_AGE = DataTracker.registerData(DragonEggEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public DragonEggEntity(EntityType<DragonEggEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static DefaultAttributeContainer.Builder bakeAttributes() {
        return MobEntity.createMobAttributes()
                //HEALTH
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D)
                //SPEED
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0D);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);
        tag.putString("Color", this.getEggType().getName());
        tag.putInt("DragonAge", this.getDragonAge());
        try {
            if (this.getOwnerId() == null) {
                tag.putString("OwnerUUID", "");
            } else {
                tag.putString("OwnerUUID", this.getOwnerId().toString());
            }
        } catch (Exception e) {
            IceAndFire.LOGGER.error("An error occurred while trying to read the NBT data of a dragon egg", e);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound tag) {
        super.readCustomDataFromNbt(tag);
        this.setEggType(DragonColor.getById(tag.getString("Color")));
        this.setDragonAge(tag.getInt("DragonAge"));
        String s;

        if (tag.contains("OwnerUUID", 8)) {
            s = tag.getString("OwnerUUID");
        } else {
            String s1 = tag.getString("Owner");
            UUID converedUUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), s1);
            s = converedUUID == null ? s1 : converedUUID.toString();
        }
        if (!s.isEmpty()) {
            this.setOwnerId(UUID.fromString(s));
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DRAGON_TYPE, IafDragonColors.RED.toString());
        builder.add(DRAGON_AGE, 0);
        builder.add(OWNER_UNIQUE_ID, Optional.empty());
    }

    public UUID getOwnerId() {
        return this.dataTracker.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerId(UUID p_184754_1_) {
        this.dataTracker.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
    }

    public DragonColor getEggType() {
        return DragonColor.getById(this.getDataTracker().get(DRAGON_TYPE));
    }

    public void setEggType(DragonColor newtype) {
        this.getDataTracker().set(DRAGON_TYPE, newtype.getName());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        return i.getAttacker() != null && super.isInvulnerableTo(i);
    }

    public int getDragonAge() {
        return this.getDataTracker().get(DRAGON_AGE);
    }

    public void setDragonAge(int i) {
        this.getDataTracker().set(DRAGON_AGE, i);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && !this.isRemoved()) {
            this.setAir(200);
            this.updateEggCondition();
        }
    }

    public void updateEggCondition() {
        DragonType dragonType = this.getEggType().getType();

        if (dragonType == IafDragonTypes.FIRE) {
            if (BlockUtil.isBurning(this.getWorld().getBlockState(this.getBlockPos()))) {
                this.setDragonAge(this.getDragonAge() + 1);
            }
        } else if (dragonType == IafDragonTypes.ICE) {
            BlockState state = this.getWorld().getBlockState(this.getBlockPos());
            if (state.isOf(Blocks.WATER) && this.getRandom().nextInt(500) == 0) {
                this.getWorld().setBlockState(this.getBlockPos(), IafBlocks.EGG_IN_ICE.get().getDefaultState());
                this.getWorld().playSound(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ(), SoundEvents.BLOCK_GLASS_BREAK, this.getSoundCategory(), 2.5F, 1.0F, false);
                if (this.getWorld().getBlockEntity(this.getBlockPos()) instanceof EggInIceBlockEntity eggInIce) {
                    eggInIce.type = this.getEggType();
                    eggInIce.ownerUUID = this.getOwnerId();
                }
                this.remove(RemovalReason.DISCARDED);
            }
        } else if (dragonType == IafDragonTypes.LIGHTNING) {
            BlockPos.Mutable mutablePosition = new BlockPos.Mutable(this.getX(), this.getY(), this.getZ());
            boolean isRainingAt = this.getWorld().hasRain(mutablePosition) || this.getWorld().hasRain(mutablePosition.set(this.getX(), this.getY() + (double) this.getHeight(), this.getZ()));

            if (this.getWorld().isSkyVisible(this.getBlockPos().up()) && isRainingAt) {
                this.setDragonAge(this.getDragonAge() + 1);
            }
        } else if (dragonType == IafDragonTypes.NETHER) {
            // Nether dragons hatch when placed on soul fire (check if the egg is on soul fire)
            BlockState currentState = this.getWorld().getBlockState(this.getBlockPos());
            if (currentState.isOf(Blocks.SOUL_FIRE)) {
                this.setDragonAge(this.getDragonAge() + 1);
            }
        }

        if (this.getDragonAge() > IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue()) {
            this.getWorld().setBlockState(this.getBlockPos(), Blocks.AIR.getDefaultState());
            DragonBaseEntity dragon = dragonType.createEntity(this.getWorld());
            assert dragon != null;
            dragon.setVariant(this.getEggType().getName());
            dragon.setGender(this.getRandom().nextBoolean());
            dragon.setPosition(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 0.5);
            dragon.setHunger(50);
            if (!this.getWorld().isClient()) this.getWorld().spawnEntity(dragon);
            if (this.hasCustomName()) dragon.setCustomName(this.getCustomName());
            dragon.setTamed(true, true);
            dragon.setOwnerUuid(this.getOwnerId());
            if (dragonType == IafDragonTypes.LIGHTNING) {
                LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(this.getWorld());
                assert bolt != null;
                bolt.setPosition(this.getX(), this.getY(), this.getZ());
                bolt.setCosmetic(true);
                if (!this.getWorld().isClient()) this.getWorld().spawnEntity(bolt);
                this.getWorld().playSound(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, this.getSoundCategory(), 2.5F, 1.0F, false);
            } else if (dragonType == IafDragonTypes.FIRE)
                this.getWorld().playSound(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, this.getSoundCategory(), 2.5F, 1.0F, false);
            else if (dragonType == IafDragonTypes.NETHER)
                this.getWorld().playSound(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ(), SoundEvents.BLOCK_SOUL_SAND_STEP, this.getSoundCategory(), 2.5F, 0.5F, false);
            this.getWorld().playSound(this.getX(), this.getY() + this.getStandingEyeHeight(), this.getZ(), IafSounds.EGG_HATCH.get(), this.getSoundCategory(), 2.5F, 1.0F, false);
            this.discard();
        }
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return ImmutableList.of();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slotIn, ItemStack stack) {

    }

    @Override
    public boolean damage(DamageSource var1, float var2) {
        if (var1.isIn(DamageTypeTags.IS_FIRE) && (this.getEggType().getType() == IafDragonTypes.FIRE || this.getEggType().getType() == IafDragonTypes.NETHER))
            return false;
        if (!this.getWorld().isClient && !var1.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && !this.isRemoved()) {
            this.dropItem(this.getItem().getItem(), 1);
        }
        this.remove(RemovalReason.KILLED);
        return true;
    }

    private ItemStack getItem() {
        return new ItemStack(DragonEggItem.EGGS.getOrDefault(this.getEggType(), Items.AIR));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    protected void pushAway(Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    public void onPlayerPlace(PlayerEntity player) {
        this.setOwnerId(player.getUuid());
    }

    @Override
    public boolean isMobDead() {
        return true;
    }
}
