package com.iafenvoy.iceandfire.data;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.MobSkullItem;
import com.iafenvoy.iceandfire.registry.IafItems;
import net.minecraft.block.SkullBlock;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Locale;

public enum IafSkullType implements SkullBlock.SkullType {
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
        return Registries.ITEM.get(Identifier.of(IceAndFire.MOD_ID, this.itemResourceName));
    }

    @Override
    public String asString() {
        return this.itemResourceName;
    }
}
