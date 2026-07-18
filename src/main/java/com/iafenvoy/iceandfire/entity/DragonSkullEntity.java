package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.IDeadMob;
import com.iafenvoy.iceandfire.item.component.DragonSkullComponent;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DragonSkullEntity extends Animal implements BlacklistedFromStatues, IDeadMob, GeoEntity {
    private static final EntityDataAccessor<String> DRAGON_TYPE = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DRAGON_AGE = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DRAGON_STAGE = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DRAGON_DIRECTION = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache geckoLibCache = GeckoLibUtil.createInstanceCache(this);

    public final float minSize = 0.3F;
    public final float maxSize = 8.58F;

    public DragonSkullEntity(EntityType<DragonSkullEntity> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        // setScale(this.getDragonAge());
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return Mob.createMobAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 10)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0D);
    }

    @Override
    public boolean isFood(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource i) {
        return i.getEntity() != null && super.isInvulnerableTo(i);
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
        builder.define(DRAGON_TYPE, IafDragonTypes.FIRE.name());
        builder.define(DRAGON_AGE, 0);
        builder.define(DRAGON_STAGE, 0);
        builder.define(DRAGON_DIRECTION, 0F);
    }

    @Override
    public float getYRot() {
        return this.getEntityData().get(DRAGON_DIRECTION);
    }

    @Override
    public void setYRot(float var1) {
        this.getEntityData().set(DRAGON_DIRECTION, var1);
    }

    public String getDragonType() {
        return this.getEntityData().get(DRAGON_TYPE);
    }

    public void setDragonType(String var1) {
        this.getEntityData().set(DRAGON_TYPE, var1);
    }

    public int getStage() {
        return this.getEntityData().get(DRAGON_STAGE);
    }

    public void setStage(int var1) {
        this.getEntityData().set(DRAGON_STAGE, var1);
    }

    public int getDragonAge() {
        return this.getEntityData().get(DRAGON_AGE);
    }

    public void setDragonAge(int var1) {
        this.getEntityData().set(DRAGON_AGE, var1);
    }

    @Override
    public SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        return null;
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
        ItemStack stack = new ItemStack(this.getDragonSkullItem());
        stack.set(IafDataComponents.DRAGON_SKULL.get(), new DragonSkullComponent(this.getStage(), this.getDragonAge()));
        if (!this.level().isClientSide)
            this.spawnAtLocation(stack, 0.0F);
    }

    public Item getDragonSkullItem() {
        return IafRegistries.DRAGON_TYPE.get(IceAndFire.id(this.getDragonType())).getSkullItem();
    }

    @Override
    public AgeableMob getBreedOffspring(@NotNull ServerLevel serverWorld, @NotNull AgeableMob ageable) {
        return null;
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
        this.setDragonType(compound.getString("Type"));
        this.setStage(compound.getInt("Stage"));
        this.setDragonAge(compound.getInt("DragonAge"));
        this.setYRot(compound.getFloat("DragonYaw"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putString("Type", this.getDragonType());
        compound.putInt("Stage", this.getStage());
        compound.putInt("DragonAge", this.getDragonAge());
        compound.putFloat("DragonYaw", this.getYRot());
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

    public int getDragonStage() {
        return Math.max(this.getStage(), 1);
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
