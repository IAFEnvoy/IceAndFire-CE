package com.iafenvoy.iceandfire.data.component;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MiscData extends NeedUpdateData {
    public int loveTicks;
    public int lungeTicks;
    public boolean hasDismounted;

    // Transient data
    public List<LivingEntity> targetedByScepter;
    private List<Integer> targetedByScepterIds;

    private boolean isInitialized;

    public void tickMisc(final LivingEntity entity) {
        if (!this.isInitialized)
            this.initialize(entity.getWorld());

        if (this.loveTicks > 0) {
            this.loveTicks--;

            if (this.loveTicks == 0) {
                this.markDirty();
                if (entity instanceof MobEntity mob)
                    mob.getNavigation().recalculatePath();
                return;
            }

            if (entity instanceof MobEntity mob) {
                mob.setAttacking(null);
                mob.setAttacker(null);
                mob.setTarget(null);
                mob.setAttacking(false);
                mob.getNavigation().stop();
            }

            this.createLoveParticles(entity);
        }
    }

    public List<LivingEntity> getTargetedByScepter() {
        return Objects.requireNonNullElse(this.targetedByScepter, Collections.emptyList());
    }

    public void addScepterTarget(final LivingEntity target) {
        if (this.targetedByScepter == null) {
            this.targetedByScepter = new ArrayList<>();
        } else if (this.targetedByScepter.contains(target))
            return;

        this.targetedByScepter.add(target);
        this.markDirty();
    }

    public void removeScepterTarget(final LivingEntity target) {
        if (this.targetedByScepter == null) return;
        this.targetedByScepter.remove(target);
        this.markDirty();
    }

    public void checkScepterTarget() {
        if (this.targetedByScepter == null) return;
        this.targetedByScepter.removeIf(living -> living.isRemoved() || living.getStatusEffect(StatusEffects.WITHER) == null || living.getStatusEffect(StatusEffects.WITHER).getDuration() <= 0);
    }

    public void setLoveTicks(int loveTicks) {
        this.loveTicks = loveTicks;
        this.markDirty();
    }

    public void setLungeTicks(int lungeTicks) {
        this.lungeTicks = lungeTicks;
        this.markDirty();
    }

    public void setDismounted(boolean hasDismounted) {
        this.hasDismounted = hasDismounted;
        this.markDirty();
    }

    public void serialize(final NbtCompound tag) {
        NbtCompound miscData = new NbtCompound();
        miscData.putInt("loveTicks", this.loveTicks);
        miscData.putInt("lungeTicks", this.lungeTicks);
        miscData.putBoolean("hasDismounted", this.hasDismounted);

        if (this.targetedByScepter != null) {
            int[] ids = new int[this.targetedByScepter.size()];
            for (int i = 0; i < this.targetedByScepter.size(); i++)
                ids[i] = this.targetedByScepter.get(i).getId();
            tag.putIntArray("targetedByScepterIds", ids);
        }

        tag.put("miscData", miscData);
    }

    public void deserialize(final NbtCompound tag) {
        NbtCompound miscData = tag.getCompound("miscData");
        this.loveTicks = miscData.getInt("loveTicks");
        this.lungeTicks = miscData.getInt("lungeTicks");
        this.hasDismounted = miscData.getBoolean("hasDismounted");
        int[] loadedChainedToIds = miscData.getIntArray("targetedByScepterIds");

        this.isInitialized = false;

        if (loadedChainedToIds.length > 0) {
            this.targetedByScepterIds = new ArrayList<>();
            for (int loadedChainedToId : loadedChainedToIds)
                this.targetedByScepterIds.add(loadedChainedToId);
        }
    }

    private void createLoveParticles(final LivingEntity entity) {
        if (entity.getRandom().nextInt(7) == 0) {
            for (int i = 0; i < 5; i++) {
                entity.getWorld().addParticle(ParticleTypes.HEART,
                        entity.getX() + ((entity.getRandom().nextDouble() - 0.5D) * 3),
                        entity.getY() + ((entity.getRandom().nextDouble() - 0.5D) * 3),
                        entity.getZ() + ((entity.getRandom().nextDouble() - 0.5D) * 3), 0, 0, 0);
            }
        }
    }

    private void initialize(final World level) {
        List<LivingEntity> entities = new ArrayList<>();

        if (this.targetedByScepterIds != null) {
            for (int id : this.targetedByScepterIds) {
                if (id == -1) continue;
                Entity entity = level.getEntityById(id);
                if (entity instanceof LivingEntity livingEntity)
                    entities.add(livingEntity);
            }
        }

        this.targetedByScepter = !entities.isEmpty() ? entities : null;
        this.targetedByScepterIds = null;
        this.isInitialized = true;
    }
}
