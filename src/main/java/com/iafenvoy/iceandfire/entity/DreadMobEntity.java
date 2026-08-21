package com.iafenvoy.iceandfire.entity;

import com.iafenvoy.iceandfire.entity.util.IDreadMob;
import com.iafenvoy.iceandfire.entity.util.IHumanoid;
import com.iafenvoy.iceandfire.registry.IafEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

public class DreadMobEntity extends Monster implements IDreadMob {
    protected static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> COMMANDER_REFERENCE = SynchedEntityData.defineId(DreadMobEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    public DreadMobEntity(EntityType<? extends Monster> t, Level worldIn) {
        super(t, worldIn);
    }

    public static Entity necromancyEntity(LivingEntity entity) {
        if (entity.getType().builtInRegistryHolder().is(EntityTypeTags.ARTHROPOD)) {
            DreadScuttlerEntity lichSummoned = new DreadScuttlerEntity(IafEntities.DREAD_SCUTTLER.get(), entity.level());
            float readInScale = (entity.getBbWidth() / 1.5F);
            if (entity.level() instanceof ServerLevelAccessor serverWorldAccess)
                lichSummoned.finalizeSpawn(serverWorldAccess, serverWorldAccess.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            lichSummoned.setSize(readInScale);
            return lichSummoned;
        }
        if (entity instanceof Zombie || entity instanceof IHumanoid) {
            DreadGhoulEntity lichSummoned = new DreadGhoulEntity(IafEntities.DREAD_GHOUL.get(), entity.level());
            float readInScale = (entity.getBbWidth() / 0.6F);
            if (entity.level() instanceof ServerLevelAccessor serverWorldAccess)
                lichSummoned.finalizeSpawn(serverWorldAccess, serverWorldAccess.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            lichSummoned.setSize(readInScale);
            return lichSummoned;
        }
        if (entity.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD) || entity instanceof AbstractSkeleton || entity instanceof Player) {
            DreadThrallEntity lichSummoned = new DreadThrallEntity(IafEntities.DREAD_THRALL.get(), entity.level());
            if (entity.level() instanceof ServerLevelAccessor serverWorldAccess) {
                lichSummoned.finalizeSpawn(serverWorldAccess, serverWorldAccess.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            }
            lichSummoned.setCustomArmorHead(false);
            lichSummoned.setCustomArmorChest(false);
            lichSummoned.setCustomArmorLegs(false);
            lichSummoned.setCustomArmorFeet(false);
            for (EquipmentSlot slot : EquipmentSlot.values())
                lichSummoned.setItemSlot(slot, entity.getItemBySlot(slot));
            return lichSummoned;
        }
        if (entity instanceof AbstractHorse)
            return new DreadHorseEntity(IafEntities.DREAD_HORSE.get(), entity.level());
        if (entity instanceof Animal) {
            DreadBeastEntity lichSummoned = new DreadBeastEntity(IafEntities.DREAD_BEAST.get(), entity.level());
            float readInScale = (entity.getBbWidth() / 1.2F);
            if (entity.level() instanceof ServerLevelAccessor serverWorldAccess)
                lichSummoned.finalizeSpawn(serverWorldAccess, serverWorldAccess.getCurrentDifficultyAt(entity.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
            lichSummoned.setSize(readInScale);
            return lichSummoned;
        }
        return null;
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
        CompoundTag extensionData = new CompoundTag();
        this.addAdditionalSaveData(extensionData);
        output.store(extensionData);
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(COMMANDER_REFERENCE, Optional.ofNullable(EntityReference.readWithOldOwnerConversion(input, "CommanderUUID", this.level())));
        this.readAdditionalSaveData(input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElse(new CompoundTag()));
    }

    /**
     * Compatibility hook for Dread mob subclasses that still store extension NBT.
     */
    protected void addAdditionalSaveData(CompoundTag compound) {
    }

    /**
     * Compatibility hook paired with {@link #addAdditionalSaveData(CompoundTag)}.
     */
    protected void readAdditionalSaveData(CompoundTag compound) {
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
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && this.getCommander() instanceof DreadLichEntity lich)
            if (lich.getTarget() != null && lich.getTarget().isAlive())
                this.setTarget(lich.getTarget());
    }

    @Override
    public Entity getCommander() {
        return EntityReference.getLivingEntity(this.entityData.get(COMMANDER_REFERENCE).orElse(null), this.level());
    }

    public void onKillEntity(LivingEntity LivingEntityIn) {
        Entity commander = this instanceof DreadLichEntity ? this : this.getCommander();
        if (commander != null && !(LivingEntityIn instanceof DragonBaseEntity)) {// zombie dragons!!!!
            Entity summoned = necromancyEntity(LivingEntityIn);
            if (summoned != null) {
                summoned.copyPosition(LivingEntityIn);
                if (!this.level().isClientSide())
                    this.level().addFreshEntity(summoned);
                if (commander instanceof DreadLichEntity lich)
                    lich.setMinionCount(lich.getMinionCount() + 1);
                if (summoned instanceof DreadMobEntity mob)
                    mob.setCommanderId(commander.getUUID());
            }
        }

    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (!this.isRemoved() && this.getCommander() != null && this.getCommander() instanceof DreadLichEntity lich)
            lich.setMinionCount(lich.getMinionCount() - 1);
        super.remove(reason);
    }
}
