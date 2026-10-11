package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class IafItemTags {
    public static final TagKey<Item> DRAGON_ARROWS = createKey("dragon_arrows");
    public static final TagKey<Item> DRAGON_BLOODS = createKey("dragon_bloods");
    public static final TagKey<Item> DRAGON_HEARTS = createKey("dragon_hearts");
    public static final TagKey<Item> BREED_AMPITHERE = createKey("breed_ampithere");
    public static final TagKey<Item> BREED_HIPPOCAMPUS = createKey("breed_hippocampus");
    public static final TagKey<Item> BREED_HIPPOGRYPH = createKey("breed_hippogryph");
    public static final TagKey<Item> HEAL_AMPITHERE = createKey("heal_ampithere");
    public static final TagKey<Item> HEAL_COCKATRICE = createKey("heal_cockatrice");
    public static final TagKey<Item> HEAL_HIPPOCAMPUS = createKey("heal_hippocampus");
    public static final TagKey<Item> HEAL_PIXIE = createKey("heal_pixie");
    public static final TagKey<Item> TAME_HIPPOGRYPH = createKey("tame_hippogryph");
    public static final TagKey<Item> TAME_PIXIE = createKey("tame_pixie");
    public static final TagKey<Item> TEMPT_DRAGON = createKey("tempt_dragon");
    public static final TagKey<Item> TEMPT_HIPPOCAMPUS = createKey("tempt_hippocampus");
    public static final TagKey<Item> TEMPT_HIPPOGRYPH = createKey("tempt_hippogryph");
    public static final TagKey<Item> INGOTS_SILVER = createKey("compat/ingots/silver");

    private static TagKey<Item> createKey(final String name) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of(IceAndFire.MOD_ID, name));
    }
}
