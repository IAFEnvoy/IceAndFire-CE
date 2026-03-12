package com.iafenvoy.iceandfire.data.component;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.*;

public class ChainData extends NeedUpdateData {
    public List<Entity> chainedTo;

    // These lists are only for the sync (and therefor are cleared once it happened)
    private List<Integer> chainedToIds;
    private List<UUID> chainedToUUIDs;

    private boolean isInitialized;

    public void tickChain(final LivingEntity entity) {
        if (!this.isInitialized)
            this.initialize(entity.getWorld());
        if (this.chainedTo == null) return;
        for (Entity chain : this.chainedTo) {
            double distance = chain.distanceTo(entity);
            if (distance > 7) {
                double x = (chain.getX() - entity.getX()) / distance;
                double y = (chain.getY() - entity.getY()) / distance;
                double z = (chain.getZ() - entity.getZ()) / distance;
                entity.setVelocity(entity.getVelocity().add(x * Math.abs(x) * 0.4D, y * Math.abs(y) * 0.2D, z * Math.abs(z) * 0.4D));
            }
        }
    }

    public List<Entity> getChainedTo() {
        return Objects.requireNonNullElse(this.chainedTo, Collections.emptyList());
    }

    public void clearChains() {
        if (this.chainedTo == null) return;
        this.chainedTo = null;
        this.markDirty();
    }

    public void attachChain(final Entity chain) {
        if (this.chainedTo == null) {
            this.chainedTo = new ArrayList<>();
        } else if (this.chainedTo.contains(chain)) return;
        this.chainedTo.add(chain);
        this.markDirty();
    }

    public void removeChain(final Entity chain) {
        if (this.chainedTo == null) return;
        this.chainedTo.remove(chain);
        if (this.chainedTo.isEmpty())
            this.chainedTo = null;
        this.markDirty();
    }

    public boolean isChainedTo(final Entity toCheck) {
        if (this.chainedTo == null || this.chainedTo.isEmpty())
            return false;
        return this.chainedTo.contains(toCheck);
    }

    public void serialize(final NbtCompound tag) {
        NbtCompound chainedData = new NbtCompound();
        NbtList uuids = new NbtList();
        if (this.chainedTo != null) {
            int[] ids = new int[this.chainedTo.size()];
            for (int i = 0; i < this.chainedTo.size(); i++) {
                Entity entity = this.chainedTo.get(i);
                ids[i] = entity.getId();
                uuids.add(NbtHelper.fromUuid(entity.getUuid()));
            }
            chainedData.putIntArray("chainedToIds", ids);
            chainedData.put("chainedToUUIDs", uuids);
        }
        tag.put("chainedData", chainedData);
    }

    public void deserialize(final NbtCompound tag) {
        NbtCompound chainedData = tag.getCompound("chainedData");
        int[] loadedChainedToIds = chainedData.getIntArray("chainedToIds");
        NbtList uuids = chainedData.getList("chainedToUUIDs", NbtList.INT_ARRAY_TYPE);
        this.isInitialized = false;
        if (loadedChainedToIds.length > 0) {
            this.chainedToIds = new ArrayList<>();
            for (int loadedChainedToId : loadedChainedToIds)
                this.chainedToIds.add(loadedChainedToId);
        } else
            this.chainedToIds = null;
        if (!uuids.isEmpty()) {
            this.chainedToUUIDs = new ArrayList<>();
            for (NbtElement uuid : uuids)
                this.chainedToUUIDs.add(NbtHelper.toUuid(uuid));
        } else
            this.chainedToUUIDs = null;
    }

    private void initialize(final World level) {
        List<Entity> entities = new ArrayList<>();

        // Make sure server gets the new entity ids on re-join and syncs it to the client
        if (this.chainedToUUIDs != null && level instanceof ServerWorld serverLevel) {
            for (UUID uuid : this.chainedToUUIDs) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity != null)
                    entities.add(entity);
            }
            this.markDirty();
        } else if (this.chainedToIds != null)
            for (int id : this.chainedToIds) {
                if (id == -1) continue;
                Entity entity = level.getEntityById(id);
                if (entity != null) entities.add(entity);
            }

        this.chainedTo = !entities.isEmpty() ? entities : null;
        this.chainedToIds = null;
        this.chainedToUUIDs = null;
        this.isInitialized = true;
    }
}
