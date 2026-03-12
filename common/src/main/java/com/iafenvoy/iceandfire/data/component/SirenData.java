package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.EntitySiren;
import com.iafenvoy.iceandfire.entity.util.IHearsSiren;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public class SirenData extends NeedUpdateData {
    public EntitySiren charmedBy;
    public int charmTime;
    public boolean isCharmed;

    private UUID charmedByUUID;
    private int charmedById;
    private boolean isInitialized;

    public void tickCharmed(final LivingEntity holder) {
        if (!(holder instanceof PlayerEntity || holder instanceof MerchantEntity || holder instanceof IHearsSiren))
            return;

        if (!this.isInitialized)
            this.initialize(holder.getWorld());

        if (this.charmedBy == null) return;

        if (this.charmedBy.isActuallySinging()) {
            if (EntitySiren.isWearingEarplugs(holder) || this.charmTime > IafCommonConfig.INSTANCE.siren.maxSingTime.getValue()) {
                this.charmedBy.singCooldown = IafCommonConfig.INSTANCE.siren.timeBetweenSongs.getValue();
                this.clearCharm();
                return;
            }

            if (!this.charmedBy.isAlive() || holder.distanceTo(this.charmedBy) > EntitySiren.SEARCH_RANGE * 2 || holder instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) {
                this.clearCharm();
                return;
            }

            if (holder.distanceTo(this.charmedBy) < 5) {
                this.charmedBy.singCooldown = IafCommonConfig.INSTANCE.siren.timeBetweenSongs.getValue();
                this.charmedBy.setSinging(false);
                this.charmedBy.setTarget(holder);
                this.charmedBy.setAttacking(true);
                this.charmedBy.triggerOtherSirens(holder);
                this.clearCharm();
                return;
            }

            this.isCharmed = true;
            this.charmTime++;
            if (holder.getRandom().nextInt(7) == 0)
                for (int i = 0; i < 5; i++)
                    holder.getWorld().addParticle(ParticleTypes.HEART,
                            holder.getX() + ((holder.getRandom().nextDouble() - 0.5D) * 3),
                            holder.getY() + ((holder.getRandom().nextDouble() - 0.5D) * 3),
                            holder.getZ() + ((holder.getRandom().nextDouble() - 0.5D) * 3),
                            0, 0, 0);

            if (holder.horizontalCollision)
                holder.setJumping(true);

            double motionXAdd = (Math.signum(this.charmedBy.getX() - holder.getX()) * 0.5D - holder.getVelocity().x) * 0.100000000372529;
            double motionYAdd = (Math.signum(this.charmedBy.getY() - holder.getY() + 1) * 0.5D - holder.getVelocity().y) * 0.100000000372529;
            double motionZAdd = (Math.signum(this.charmedBy.getZ() - holder.getZ()) * 0.5D - holder.getVelocity().z) * 0.100000000372529;

            holder.setVelocity(holder.getVelocity().add(motionXAdd, motionYAdd, motionZAdd));

            if (holder.hasVehicle())
                holder.stopRiding();

            if (!(holder instanceof PlayerEntity)) {
                double x = this.charmedBy.getX() - holder.getX();
                double y = this.charmedBy.getY() - 1 - holder.getY();
                double z = this.charmedBy.getZ() - holder.getZ();
                double radius = Math.sqrt(x * x + z * z);
                float xRot = (float) (-(MathHelper.atan2(y, radius) * (180D / Math.PI)));
                float yRot = (float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
                holder.setPitch(this.updateRotation(holder.getPitch(), xRot));
                holder.setYaw(this.updateRotation(holder.getYaw(), yRot));
            }
        }
    }

    public void setCharmed(final Entity entity) {
        if (!(entity instanceof EntitySiren siren)) return;

        this.charmedBy = siren;
        this.isCharmed = true;
        this.markDirty();
    }

    public void clearCharm() {
        this.charmTime = 0;
        this.isCharmed = false;
        this.charmedBy = null;
        this.markDirty();
    }

    public void serialize(final NbtCompound tag) {
        NbtCompound sirenData = new NbtCompound();

        if (this.charmedBy != null) {
            sirenData.put("charmedByUUID", NbtHelper.fromUuid(this.charmedBy.getUuid()));
            sirenData.putInt("charmedById", this.charmedBy.getId());
        } else
            sirenData.putInt("charmedById", -1);

        sirenData.putInt("charmTime", this.charmTime);
        sirenData.putBoolean("isCharmed", this.isCharmed);

        tag.put("sirenData", sirenData);
    }

    public void deserialize(final NbtCompound tag) {
        NbtCompound sirenData = tag.getCompound("sirenData");
        NbtElement uuidTag = sirenData.get("charmedByUUID");

        if (uuidTag != null)
            this.charmedByUUID = NbtHelper.toUuid(uuidTag);

        this.charmedById = sirenData.getInt("charmedById");
        this.charmTime = sirenData.getInt("charmTime");
        this.isCharmed = sirenData.getBoolean("isCharmed");
        this.isInitialized = false;
    }

    private float updateRotation(float angle, float targetAngle) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        if (f > 30) f = 30f;
        if (f < -30) f = -30f;
        return angle + f;
    }

    private void initialize(final World level) {
        this.charmedBy = null;

        // Make sure server gets the new entity ids on re-join and syncs it to the client
        if (this.charmedByUUID != null && level instanceof ServerWorld serverLevel) {
            Entity entity = serverLevel.getEntity(this.charmedByUUID);

            if (entity instanceof EntitySiren siren) {
                this.markDirty();
                this.charmedByUUID = null;
                this.charmedBy = siren;
            }
        } else if (this.charmedById != -1) {
            Entity entity = level.getEntityById(this.charmedById);
            if (entity instanceof EntitySiren siren)
                this.charmedBy = siren;
        }

        this.isInitialized = true;
    }
}
