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
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DragonEggEntity extends LivingEntity implements BlacklistedFromStatues, IDeadMob {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID = SynchedEntityData.defineId(DragonEggEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final Map<DragonType, EggTicker> TICKERS = new LinkedHashMap<>();
    private static final EntityDataAccessor<String> DRAGON_TYPE = SynchedEntityData.defineId(DragonEggEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DRAGON_AGE = SynchedEntityData.defineId(DragonEggEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LOCATION_VALID = SynchedEntityData.defineId(DragonEggEntity.class, EntityDataSerializers.BOOLEAN);

    public DragonEggEntity(EntityType<DragonEggEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 10.0D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0D);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Color", this.getEggType().getName());
        tag.putInt("DragonAge", this.getDragonAge());
        try {
            if (this.getOwnerId() == null) tag.putString("OwnerUUID", "");
            else tag.putString("OwnerUUID", this.getOwnerId().toString());
        } catch (Exception e) {
            IceAndFire.LOGGER.error("An error occurred while trying to read the NBT data of a dragon egg", e);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setEggType(DragonColor.getById(tag.getString("Color")));
        this.setDragonAge(tag.getInt("DragonAge"));
        String s;

        if (tag.contains("OwnerUUID", 8)) s = tag.getString("OwnerUUID");
        else {
            String s1 = tag.getString("Owner");
            UUID converedUUID = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s1);
            s = converedUUID == null ? s1 : converedUUID.toString();
        }
        if (!s.isEmpty()) this.setOwnerId(UUID.fromString(s));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DRAGON_TYPE, IafDragonColors.RED.toString());
        builder.define(DRAGON_AGE, 0);
        builder.define(OWNER_UNIQUE_ID, Optional.empty());
        builder.define(LOCATION_VALID, false);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        return i.getEntity() != null && super.isInvulnerableTo(i);
    }

    public DragonColor getEggType() {
        return DragonColor.getById(this.getEntityData().get(DRAGON_TYPE));
    }

    public void setEggType(DragonColor color) {
        this.getEntityData().set(DRAGON_TYPE, color.getName());
    }

    public int getDragonAge() {
        return this.getEntityData().get(DRAGON_AGE);
    }

    public void setDragonAge(int i) {
        this.getEntityData().set(DRAGON_AGE, i);
    }

    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerId(UUID uuid) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    public boolean isLocationValid() {
        return this.entityData.get(LOCATION_VALID);
    }

    public void setLocationValid(boolean valid) {
        this.entityData.set(LOCATION_VALID, valid);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !this.isRemoved()) {
            this.setAirSupply(200);
            this.updateEggCondition();
        }
    }

    public void updateEggCondition() {
        DragonType dragonType = this.getEggType().getType();
        EggTicker ticker = TICKERS.get(dragonType);
        boolean hatched = this.getDragonAge() > IafCommonConfig.INSTANCE.dragon.eggBornTime.getValue();
        if (ticker != null) this.setLocationValid(ticker.tick(this, this.level(), this.blockPosition(), hatched));

        if (hatched) {
            this.level().setBlockAndUpdate(this.blockPosition(), Blocks.AIR.defaultBlockState());
            DragonBaseEntity dragon = dragonType.createEntity(this.level());
            assert dragon != null;
            dragon.setVariant(this.getEggType().getName());
            dragon.setGender(this.getRandom().nextBoolean());
            dragon.setPos(this.blockPosition().getX() + 0.5, this.blockPosition().getY() + 1, this.blockPosition().getZ() + 0.5);
            dragon.setHunger(50);
            if (!this.level().isClientSide()) this.level().addFreshEntity(dragon);
            if (this.hasCustomName()) dragon.setCustomName(this.getCustomName());
            dragon.setTame(true, true);
            dragon.setOwnerUUID(this.getOwnerId());
            this.level().playLocalSound(this.getX(), this.getY() + this.getEyeHeight(), this.getZ(), IafSounds.EGG_HATCH.get(), this.getSoundSource(), 2.5F, 1.0F, false);
            this.discard();
        }
    }

    @Override
    public SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return null;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return ImmutableList.of();
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {

    }

    @Override
    public boolean hurt(DamageSource var1, float var2) {
        if (var1.is(DamageTypeTags.IS_FIRE) && this.getEggType().getType() == IafDragonTypes.FIRE)
            return false;
        if (!this.level().isClientSide && !var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !this.isRemoved()) {
            this.spawnAtLocation(this.getItem().getItem(), 1);
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
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    public void onPlayerPlace(Player player) {
        this.setOwnerId(player.getUUID());
    }

    @Override
    public boolean isMobDead() {
        return true;
    }

    public static void register(DragonType type, EggTicker ticker) {
        TICKERS.put(type, ticker);
    }

    static {
        register(IafDragonTypes.FIRE, (entity, world, pos, hatched) -> {
            boolean valid = BlockUtil.isBurning(world.getBlockState(pos));
            if (valid) entity.setDragonAge(entity.getDragonAge() + 1);
            if (hatched)
                world.playLocalSound(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), SoundEvents.FIRE_EXTINGUISH, entity.getSoundSource(), 2.5F, 1.0F, false);
            return valid;
        });
        register(IafDragonTypes.ICE, (entity, world, pos, hatched) -> {
            BlockState state = world.getBlockState(pos);
            if (state.is(Blocks.WATER) && entity.getRandom().nextInt(500) == 0) {
                world.setBlockAndUpdate(pos, IafBlocks.EGG_IN_ICE.get().defaultBlockState());
                world.playLocalSound(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), SoundEvents.GLASS_BREAK, entity.getSoundSource(), 2.5F, 1.0F, false);
                if (world.getBlockEntity(pos) instanceof EggInIceBlockEntity eggInIce) {
                    eggInIce.type = entity.getEggType();
                    eggInIce.ownerUUID = entity.getOwnerId();
                }
                entity.remove(RemovalReason.DISCARDED);
            }
            return false;
        });
        register(IafDragonTypes.LIGHTNING, (entity, world, pos, hatched) -> {
            boolean isRainingAt = world.isRainingAt(pos) || world.isRainingAt(BlockPos.containing(entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ()));
            boolean valid = world.canSeeSky(pos.above()) && isRainingAt;
            if (valid) entity.setDragonAge(entity.getDragonAge() + 1);
            if (hatched) {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(world);
                assert bolt != null;
                bolt.setPos(entity.getX(), entity.getY(), entity.getZ());
                bolt.setVisualOnly(true);
                if (!world.isClientSide) world.addFreshEntity(bolt);
                world.playLocalSound(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, entity.getSoundSource(), 2.5F, 1.0F, false);
            }
            return valid;
        });
    }

    @FunctionalInterface
    public interface EggTicker {
        boolean tick(DragonEggEntity entity, Level world, BlockPos pos, boolean hatched);
    }
}
