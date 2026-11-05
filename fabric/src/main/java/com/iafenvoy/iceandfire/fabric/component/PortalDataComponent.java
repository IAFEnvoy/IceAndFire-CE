package com.iafenvoy.iceandfire.fabric.component;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.component.PortalData;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class PortalDataComponent implements ComponentV3, AutoSyncedComponent, CommonTickingComponent {
    protected static final ComponentKey<PortalDataComponent> COMPONENT = ComponentRegistryV3.INSTANCE.getOrCreate(Identifier.of(IceAndFire.MOD_ID, "portal_data"), PortalDataComponent.class);
    private final PlayerEntity player;
    private final PortalData data;

    public PortalDataComponent(PlayerEntity player) {
        this.player = player;
        this.data = new PortalData(player);
    }

    @Override
    public void tick() {
        if (this.player.getWorld().isClient) return;
        this.data.tick();
        if (this.data.consumeDirty()) COMPONENT.sync(this.player);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.data.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        this.data.writeToNbt(tag);
    }

    public PortalData getData() {
        return this.data;
    }

    public static PortalDataComponent get(PlayerEntity player) {
        return COMPONENT.get(player);
    }
}
