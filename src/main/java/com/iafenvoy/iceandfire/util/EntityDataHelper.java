package com.iafenvoy.iceandfire.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

public final class EntityDataHelper {
    private EntityDataHelper() {
    }

    public static CompoundTag saveWithoutId(Entity entity) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
        entity.saveWithoutId(output);
        return output.buildResult();
    }

    public static void load(Entity entity, CompoundTag data) {
        entity.load(TagValueInput.create(ProblemReporter.DISCARDING, entity.registryAccess(), data));
    }
}
