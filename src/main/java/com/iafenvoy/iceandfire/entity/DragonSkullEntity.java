package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.util.BlacklistedFromStatues;
import com.iafenvoy.iceandfire.entity.util.IDeadMob;
import com.iafenvoy.iceandfire.item.component.DragonSkullComponent;
import com.iafenvoy.iceandfire.registry.IafDataComponents;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import net.minecraft.core.Holder;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class DragonSkullEntity extends Animal implements BlacklistedFromStatues, IDeadMob {
    private static final EntityDataAccessor<String> DRAGON_TYPE = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DRAGON_AGE = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DRAGON_STAGE = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DRAGON_DIRECTION = SynchedEntityData.defineId(DragonSkullEntity.class, EntityDataSerializers.FLOAT);

    public final float minSize = 0.3F;
    public final float maxSize = 8.58F;

    public DragonSkullEntity(EntityType<DragonSkullEntity> type, Level worldIn) {
        super(type, worldIn);
        // setScale(this.getDragonAge());
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return createMobAttributes()
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
    public boolean isInvulnerableTo(@NonNull ServerLevel level, DamageSource source) {
        return source.getEntity() != null && super.isInvulnerableTo(level, source);
    }

    @Override
    public boolean isNoAi() {
        return true;
    }

    public boolean isOnWall() {
        return this.level().isEmptyBlock(this.blockPosition().below());
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
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float amount) {
        this.turnIntoItem(level);
        return true;
    }

    public void turnIntoItem(ServerLevel level) {
        if (this.isRemoved())
            return;
        this.remove(RemovalReason.DISCARDED);
        ItemStack stack = new ItemStack(this.getDragonSkullItem());
        stack.set(IafDataComponents.DRAGON_SKULL.get(), new DragonSkullComponent(this.getStage(), this.getDragonAge()));
        this.spawnAtLocation(level, stack);
    }

    public Item getDragonSkullItem() {
        return IafRegistries.DRAGON_TYPE.get(IceAndFire.id(this.getDragonType())).map(Holder.Reference::value).orElse(IafDragonTypes.FIRE).getSkullItem();
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
    public void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setDragonType(input.getStringOr("Type", IafDragonTypes.FIRE.name()));
        this.setStage(input.getIntOr("Stage", 0));
        this.setDragonAge(input.getIntOr("DragonAge", 0));
        this.setYRot(input.getFloatOr("DragonYaw", 0.0F));
    }

    @Override
    public void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Type", this.getDragonType());
        output.putInt("Stage", this.getStage());
        output.putInt("DragonAge", this.getDragonAge());
        output.putFloat("DragonYaw", this.getYRot());
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
