package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.MobSkullItem;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum IafSkullType implements SkullBlock.Type {
    HIPPOGRYPH,
    CYCLOPS,
    COCKATRICE,
    STYMPHALIAN,
    TROLL,
    AMPHITHERE,
    SEASERPENT,
    HYDRA;

    private final String itemResourceName;

    IafSkullType() {
        this.itemResourceName = this.name().toLowerCase(Locale.ROOT) + "_skull";
    }

    public static void initItems() {
        //FIXME::Move to registries
        for (IafSkullType skull : IafSkullType.values())
            IafItems.registerItem(skull.itemResourceName, () -> new MobSkullItem(skull));
    }

    public Item getSkullItem() {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, this.itemResourceName));
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.itemResourceName;
    }
}
