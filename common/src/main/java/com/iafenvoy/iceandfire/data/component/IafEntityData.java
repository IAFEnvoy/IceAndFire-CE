package com.iafenvoy.iceandfire.data.component;

import com.iafenvoy.iceandfire.impl.ComponentManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class IafEntityData {
    public final FrozenData frozenData = new FrozenData();
    public final ChainData chainData = new ChainData();
    public final SirenData sirenData = new SirenData();
    public final ChickenData chickenData = new ChickenData();
    public final MiscData miscData = new MiscData();
    private final LivingEntity entity;

    public IafEntityData(LivingEntity entity) {
        this.entity = entity;
    }

    public void deserialize(@NotNull NbtCompound tag) {
        this.frozenData.deserialize(tag);
        this.chainData.deserialize(tag);
        this.sirenData.deserialize(tag);
        this.chickenData.deserialize(tag);
        this.miscData.deserialize(tag);
    }

    public void serialize(@NotNull NbtCompound tag) {
        this.frozenData.serialize(tag);
        this.chainData.serialize(tag);
        this.sirenData.serialize(tag);
        this.chickenData.serialize(tag);
        this.miscData.serialize(tag);
    }

    public void tick() {
        this.frozenData.tickFrozen(this.entity);
        this.chainData.tickChain(this.entity);
        this.sirenData.tickCharmed(this.entity);
        this.chickenData.tickChicken(this.entity);
        this.miscData.tickMisc(this.entity);
    }

    public boolean isDirty() {
        return this.frozenData.isDirty() | this.chainData.isDirty() | this.sirenData.isDirty() | this.miscData.isDirty();
    }

    public static IafEntityData get(LivingEntity living) {
        return ComponentManager.getIafEntityData(living);
    }
}
