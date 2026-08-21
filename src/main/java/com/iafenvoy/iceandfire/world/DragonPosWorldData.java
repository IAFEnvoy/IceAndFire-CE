package com.iafenvoy.iceandfire.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.*;

public class DragonPosWorldData extends SavedData {
    private static final Codec<DragonPosition> DRAGON_POSITION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("DragonUUID").forGetter(DragonPosition::uuid),
            Codec.INT.fieldOf("DragonPosX").forGetter(position -> position.pos().getX()),
            Codec.INT.fieldOf("DragonPosY").forGetter(position -> position.pos().getY()),
            Codec.INT.fieldOf("DragonPosZ").forGetter(position -> position.pos().getZ())
    ).apply(instance, (uuid, x, y, z) -> new DragonPosition(uuid, new BlockPos(x, y, z))));
    private static final Codec<DragonPosWorldData> CODEC = DRAGON_POSITION_CODEC.listOf().optionalFieldOf("DragonMap", List.of())
            .xmap(DragonPosWorldData::new, DragonPosWorldData::entries).codec();
    private static final SavedDataType<DragonPosWorldData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("iceandfire", "dragon_positions"), DragonPosWorldData::new, CODEC
    );
    protected final Map<UUID, BlockPos> lastDragonPositions = new HashMap<>();

    public DragonPosWorldData() {
    }

    private DragonPosWorldData(List<DragonPosition> positions) {
        for (DragonPosition position : positions) this.lastDragonPositions.put(position.uuid(), position.pos());
    }

    public static DragonPosWorldData get(Level world) {
        if (world instanceof ServerLevel serverWorld) {
            SavedDataStorage storage = serverWorld.getDataStorage();
            return storage.computeIfAbsent(TYPE);
        }
        return null;
    }

    public void addDragon(UUID uuid, BlockPos pos) {
        if (!pos.equals(this.lastDragonPositions.put(uuid, pos))) this.setDirty();
    }

    public void removeDragon(UUID uuid) {
        if (this.lastDragonPositions.remove(uuid) != null) this.setDirty();
    }

    public BlockPos getDragonPos(UUID uuid) {
        return this.lastDragonPositions.get(uuid);
    }

    private List<DragonPosition> entries() {
        List<DragonPosition> entries = new ArrayList<>(this.lastDragonPositions.size());
        this.lastDragonPositions.forEach((uuid, pos) -> entries.add(new DragonPosition(uuid, pos)));
        return entries;
    }

    private record DragonPosition(UUID uuid, BlockPos pos) {
    }
}
