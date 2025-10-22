package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.entity.SirenEntity;
import com.iafenvoy.iceandfire.entity.util.SirenAffectable;
import com.iafenvoy.iceandfire.impl.ComponentManager;
import com.iafenvoy.iceandfire.util.attachment.NeedUpdateData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SirenData extends NeedUpdateData<LivingEntity> {
    public static final Codec<SirenData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("charmTime").forGetter(SirenData::getCharmTime),
            Uuids.CODEC.optionalFieldOf("charmedByUUID").forGetter(SirenData::getCharmedByUUID)
    ).apply(i, SirenData::new));
    public static final PacketCodec<RegistryByteBuf, SirenData> PACKET_CODEC = PacketCodecs.registryCodec(CODEC);

    public int charmTime;
    private Optional<UUID> charmedByUUID = Optional.empty();

    public SirenData() {
    }

    private SirenData(int charmTime, Optional<UUID> charmedByUUID) {
        this.charmTime = charmTime;
        this.charmedByUUID = charmedByUUID;
    }

    @Override
    public void tick(LivingEntity living) {
        if (!(living instanceof PlayerEntity || living instanceof MerchantEntity || living instanceof SirenAffectable))
            return;
        if (this.charmedByUUID.isEmpty() || !(living.getWorld() instanceof ServerWorld world) || !(world.getEntity(this.charmedByUUID.get()) instanceof SirenEntity siren))
            return;

        if (siren.isActuallySinging()) {
            if (SirenEntity.isWearingEarplugs(living) || this.charmTime > IafCommonConfig.INSTANCE.siren.maxSingTime.getValue()) {
                siren.singCooldown = IafCommonConfig.INSTANCE.siren.timeBetweenSongs.getValue();
                this.clearCharm();
                return;
            }

            if (!siren.isAlive() || living.distanceTo(siren) > SirenEntity.SEARCH_RANGE * 2 || living instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) {
                this.clearCharm();
                siren.setAttacking(false);
                return;
            }

            if (living.distanceTo(siren) < 5) {
                siren.singCooldown = IafCommonConfig.INSTANCE.siren.timeBetweenSongs.getValue();
                siren.setSinging(false);
                siren.setTarget(living);
                siren.setAttacking(true);
                siren.triggerOtherSirens(living);
                this.clearCharm();
                return;
            }

            this.charmTime++;
            if (living.getRandom().nextInt(7) == 0)
                for (int i = 0; i < 5; i++)
                    living.getWorld().addParticle(ParticleTypes.HEART,
                            living.getX() + ((living.getRandom().nextDouble() - 0.5D) * 3),
                            living.getY() + ((living.getRandom().nextDouble() - 0.5D) * 3),
                            living.getZ() + ((living.getRandom().nextDouble() - 0.5D) * 3),
                            0, 0, 0);

            if (living.horizontalCollision) living.setJumping(true);

            Vec3d velocity = living.getVelocity();
            double vx = (Math.signum(siren.getX() - living.getX()) * 0.5D - velocity.x) * 0.1;
            double vy = (Math.signum(siren.getY() - living.getY() + 1) * 0.5D - velocity.y) * 0.1;
            double vz = (Math.signum(siren.getZ() - living.getZ()) * 0.5D - velocity.z) * 0.1;
            living.setVelocity(velocity.add(vx, vy, vz));
            living.velocityModified = true;
            if (living.hasVehicle()) living.stopRiding();
            if (!(living instanceof PlayerEntity)) {
                double x = siren.getX() - living.getX();
                double y = siren.getY() - 1 - living.getY();
                double z = siren.getZ() - living.getZ();
                double radius = Math.sqrt(x * x + z * z);
                float xRot = (float) (-(MathHelper.atan2(y, radius) * (180D / Math.PI)));
                float yRot = (float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
                living.setPitch(this.updateRotation(living.getPitch(), xRot));
                living.setYaw(this.updateRotation(living.getYaw(), yRot));
            }
        }
    }

    public boolean isCharmed() {
        return this.charmedByUUID.isPresent();
    }

    public int getCharmTime() {
        return this.charmTime;
    }

    public Optional<UUID> getCharmedByUUID() {
        return this.charmedByUUID;
    }

    public void setCharmed(SirenEntity siren) {
        this.charmedByUUID = Optional.of(siren.getUuid());
        this.markDirty();
    }

    public void clearCharm() {
        this.charmedByUUID = Optional.empty();
        this.charmTime = 0;
        this.markDirty();
    }

    private float updateRotation(float angle, float targetAngle) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        if (f > 30) f = 30f;
        if (f < -30) f = -30f;
        return angle + f;
    }

    public static SirenData get(LivingEntity living) {
        return ComponentManager.getSirenData(living);
    }
}
