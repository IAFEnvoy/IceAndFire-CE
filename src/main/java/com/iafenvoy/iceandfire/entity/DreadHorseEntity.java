package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.entity.util.IDreadMob;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public class DreadHorseEntity extends SkeletonHorse implements IDreadMob {
    protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> COMMANDER_REFERENCE = SynchedEntityData.defineId(DreadHorseEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    public DreadHorseEntity(EntityType<? extends DreadHorseEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier.Builder bakeAttributes() {
        return createBaseHorseAttributes()
                //HEALTH
                .add(Attributes.MAX_HEALTH, 25.0D)
                //SPEED
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                //ARMOR
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COMMANDER_REFERENCE, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference.store(this.entityData.get(COMMANDER_REFERENCE).orElse(null), output, "CommanderUUID");
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(COMMANDER_REFERENCE, Optional.ofNullable(EntityReference.readWithOldOwnerConversion(input, "CommanderUUID", this.level())));
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull EntitySpawnReason reason, SpawnGroupData spawnDataIn) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setAge(24000);
        return data;
    }

    @Override
    protected boolean considersEntityAsAlly(@NonNull Entity entityIn) {
        return entityIn instanceof IDreadMob || super.considersEntityAsAlly(entityIn);
    }

    public UUID getCommanderId() {
        return this.entityData.get(COMMANDER_REFERENCE).map(EntityReference::getUUID).orElse(null);
    }

    public void setCommanderId(UUID uuid) {
        this.entityData.set(COMMANDER_REFERENCE, Optional.ofNullable(uuid).map(EntityReference::of));
    }

    @Override
    public Entity getCommander() {
        return EntityReference.getLivingEntity(this.entityData.get(COMMANDER_REFERENCE).orElse(null), this.level());
    }
}
