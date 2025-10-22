package com.iafenvoy.iceandfire.registry.tag;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class IafEntityTags {
    public static final TagKey<EntityType<?>> DEATHWORM = create("deathworm");
    public static final TagKey<EntityType<?>> FIRE_DRAGON = create("fire_dragon");
    public static final TagKey<EntityType<?>> ICE_DRAGON = create("ice_dragon");
    public static final TagKey<EntityType<?>> LIGHTNING_DRAGON = create("lightning_dragon");
    public static final TagKey<EntityType<?>> IMMUNE_TO_GORGON_STONE = create("immune_to_gorgon_stone");
    public static final TagKey<EntityType<?>> CHICKENS = create("chickens");
    public static final TagKey<EntityType<?>> FEAR_DRAGONS = create("fear_dragons");
    public static final TagKey<EntityType<?>> SCARES_COCKATRICES = create("scares_cockatrices");
    public static final TagKey<EntityType<?>> SHEEP = create("sheep");
    public static final TagKey<EntityType<?>> VILLAGERS = create("villagers");
    public static final TagKey<EntityType<?>> ICE_DRAGON_TARGETS = create("ice_dragon_targets");
    public static final TagKey<EntityType<?>> FIRE_DRAGON_TARGETS = create("fire_dragon_targets");
    public static final TagKey<EntityType<?>> LIGHTNING_DRAGON_TARGETS = create("lightning_dragon_targets");
    public static final TagKey<EntityType<?>> COCKATRICE_TARGETS = create("cockatrice_targets");
    public static final TagKey<EntityType<?>> CYCLOPS_UNLIFTABLES = create("cyclops_unliftables");
    public static final TagKey<EntityType<?>> BLINDED = create("blinded");

    private static TagKey<EntityType<?>> create(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(IceAndFire.MOD_ID, id));
    }
}
