package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.impl.ComponentManager;
import com.iafenvoy.iceandfire.util.attachment.NeedUpdateData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class MiscData extends NeedUpdateData<LivingEntity> {
    public static final Codec<MiscData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("loveTicks").forGetter(MiscData::getLoveTicks),
            Codec.INT.fieldOf("lungeTicks").forGetter(MiscData::getLungeTicks),
            UUIDUtil.AUTHLIB_CODEC.listOf().fieldOf("targetedByScepters").forGetter(MiscData::getTargetedByScepters)
    ).apply(i, MiscData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MiscData> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public int loveTicks;
    public int lungeTicks;
    private final List<UUID> targetedByScepters = new LinkedList<>();

    public MiscData() {
    }

    private MiscData(int loveTicks, int lungeTicks, List<UUID> targetedByScepters) {
        this.loveTicks = loveTicks;
        this.lungeTicks = lungeTicks;
        this.targetedByScepters.addAll(targetedByScepters);
    }

    @Override
    public void tick(LivingEntity entity) {
        if (this.loveTicks > 0) {
            this.loveTicks--;
            if (this.loveTicks == 0) {
                this.markDirty();
                if (entity instanceof Mob mob) mob.getNavigation().recomputePath();
                return;
            }
            if (entity instanceof Mob mob) {
                mob.setLastHurtByPlayer(null);
                mob.setLastHurtByMob(null);
                mob.setTarget(null);
                mob.setAggressive(false);
                mob.getNavigation().stop();
            }
            this.createLoveParticles(entity);
        }
    }

    public void addScepterTarget(final LivingEntity target) {
        UUID uuid = target.getUUID();
        if (!this.targetedByScepters.contains(uuid)) {
            this.targetedByScepters.add(uuid);
            this.markDirty();
        }
    }

    public void checkScepterTarget(Function<UUID, Entity> entityGetter) {
        this.targetedByScepters.removeIf(uuid -> entityGetter.apply(uuid) instanceof LivingEntity living && (living.isRemoved() || living.getEffect(MobEffects.WITHER) == null || living.getEffect(MobEffects.WITHER).getDuration() <= 0));
    }

    public void setLoveTicks(int loveTicks) {
        this.loveTicks = loveTicks;
        this.markDirty();
    }

    public void setLungeTicks(int lungeTicks) {
        this.lungeTicks = lungeTicks;
        this.markDirty();
    }

    private void createLoveParticles(final LivingEntity entity) {
        if (entity.getRandom().nextInt(7) == 0) {
            for (int i = 0; i < 5; i++) {
                entity.level().addParticle(ParticleTypes.HEART,
                        entity.getX() + ((entity.getRandom().nextDouble() - 0.5D) * 3),
                        entity.getY() + ((entity.getRandom().nextDouble() - 0.5D) * 3),
                        entity.getZ() + ((entity.getRandom().nextDouble() - 0.5D) * 3), 0, 0, 0);
            }
        }
    }

    public int getLoveTicks() {
        return this.loveTicks;
    }

    public int getLungeTicks() {
        return this.lungeTicks;
    }

    public List<UUID> getTargetedByScepters() {
        return this.targetedByScepters;
    }

    public static MiscData get(LivingEntity living) {
        return ComponentManager.getMiscData(living);
    }
}
