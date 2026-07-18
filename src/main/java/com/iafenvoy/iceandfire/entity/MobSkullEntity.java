package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.data.IafSkullType;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.IDeadMob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MobSkullEntity extends Animal implements BlacklistedFromStatues, IDeadMob, GeoEntity {
    private static final EntityDataAccessor<Float> SKULL_DIRECTION = SynchedEntityData.defineId(MobSkullEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> SKULL_ENUM = SynchedEntityData.defineId(MobSkullEntity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache geckoLibCache = GeckoLibUtil.createInstanceCache(this);

    public MobSkullEntity(EntityType<? extends MobSkullEntity> t, Level worldIn) {
        super(t, worldIn);
        this.noCulling = true;
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 10.0D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        return i.getEntity() != null;
    }

    @Override
    public boolean isNoAi() {
        return true;
    }

    public boolean isOnWall() {
        return this.level().isEmptyBlock(this.blockPosition().below());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geckoLibCache;
    }

    public void onUpdate() {
        this.yBodyRotO = 0;
        this.yHeadRotO = 0;
        this.yBodyRot = 0;
        this.yHeadRot = 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SKULL_DIRECTION, 0F);
        builder.define(SKULL_ENUM, 0);
    }

    @Override
    public float getYRot() {
        return this.getEntityData().get(SKULL_DIRECTION);
    }

    @Override
    public void setYRot(float var1) {
        this.getEntityData().set(SKULL_DIRECTION, var1);
    }

    private int getEnumOrdinal() {
        return this.getEntityData().get(SKULL_ENUM);
    }

    private void setEnumOrdinal(int var1) {
        this.getEntityData().set(SKULL_ENUM, var1);
    }

    public IafSkullType getSkullType() {
        return IafSkullType.values()[Mth.clamp(this.getEnumOrdinal(), 0, IafSkullType.values().length - 1)];
    }

    public void setSkullType(IafSkullType skullType) {
        this.setEnumOrdinal(skullType.ordinal());
    }

    @Override
    public boolean hurt(@NotNull DamageSource var1, float var2) {
        this.turnIntoItem();
        return super.hurt(var1, var2);
    }

    public void turnIntoItem() {
        if (this.isRemoved())
            return;
        this.remove(RemovalReason.DISCARDED);
        ItemStack stack = new ItemStack(this.getSkullType().getSkullItem(), 1);
        if (!this.level().isClientSide)
            this.spawnAtLocation(stack, 0.0F);
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            this.setYRot(player.getYRot());
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setYRot(compound.getFloat("SkullYaw"));
        this.setEnumOrdinal(compound.getInt("SkullType"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("SkullYaw", this.getYRot());
        compound.putInt("SkullType", this.getEnumOrdinal());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull Entity entity) {
    }

    @Override
    public boolean canBeTurnedToStone() {
        return false;
    }

    @Override
    public boolean isMobDead() {
        return true;
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverWorld, @NotNull AgeableMob ageable) {
        return null;
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}
