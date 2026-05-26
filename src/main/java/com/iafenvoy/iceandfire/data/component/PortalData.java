package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.impl.ComponentManager;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafWorld;
import com.iafenvoy.iceandfire.util.attachment.NeedUpdateData;
import com.iafenvoy.iceandfire.world.processor.DreadPortalProcessor;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class PortalData extends NeedUpdateData<LivingEntity> {
    public static final Codec<PortalData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("teleported").forGetter(PortalData::isTeleported),
            Codec.INT.fieldOf("teleportTick").forGetter(PortalData::getTeleportTick)
    ).apply(i, PortalData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PortalData> PACKET_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);
    private boolean teleported = false;
    private int teleportTick = -1;

    public PortalData() {
    }

    private PortalData(boolean teleported, int teleportTick) {
        this.teleported = teleported;
        this.teleportTick = teleportTick;
    }

    @Override
    public void tick(LivingEntity living) {
        Level world = living.level();
        if (!this.isTeleported() && this.getTeleportTick() == 0 && world instanceof ServerLevel serverWorld) {
            this.setTeleported(true);
            MinecraftServer server = serverWorld.getServer();
            if (world.dimension().location().equals(IafWorld.DREAD_LAND.location()))
                living.changeDimension(new DimensionTransition(server.overworld(), living.position(), Vec3.ZERO, living.yHeadRot, living.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));
            else {
                ServerLevel dreadLand = server.getLevel(IafWorld.DREAD_LAND);
                if (dreadLand == null) return;
                living.changeDimension(new DimensionTransition(server.getLevel(IafWorld.DREAD_LAND), living.position(), Vec3.ZERO, living.yHeadRot, living.getXRot(), DimensionTransition.PLAY_PORTAL_SOUND));
                if (!dreadLand.getBlockState(living.blockPosition()).is(IafBlocks.DREAD_PORTAL.get()))
                    server.getStructureManager().get(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dread_exit_portal")).ifPresent(structureTemplate -> structureTemplate.placeInWorld(dreadLand, living.blockPosition().subtract(new BlockPos(2, 1, 2)), BlockPos.ZERO, new StructurePlaceSettings().addProcessor(new DreadPortalProcessor()), dreadLand.random, 2));
                living.sendSystemMessage(Component.translatable("warning.iceandfire.dreadland.not_complete"));
            }
        }
        if (world.getBlockState(living.blockPosition()).is(IafBlocks.DREAD_PORTAL.get())) {
            if (this.getTeleportTick() > 0) this.setTeleportTick(this.getTeleportTick() - 1);
            else if (this.getTeleportTick() == -1) this.setTeleportTick(100);
        } else {
            this.setTeleported(false);
            this.setTeleportTick(-1);
        }
    }

    public boolean isTeleported() {
        return this.teleported;
    }

    public int getTeleportTick() {
        return this.teleportTick;
    }

    public void setTeleported(boolean teleported) {
        if (this.teleported != teleported) this.markDirty();
        this.teleported = teleported;
    }

    public void setTeleportTick(int teleportTick) {
        if (this.teleportTick != teleportTick) this.markDirty();
        this.teleportTick = teleportTick;
    }

    public static PortalData get(Player player) {
        return ComponentManager.getPortalData(player);
    }
}
