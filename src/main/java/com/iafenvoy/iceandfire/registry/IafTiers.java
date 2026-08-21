package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public final class IafTiers {
    public static final ToolMaterial SILVER_TOOL_MATERIAL = material("silver", 460, 11.0F, 1.0F, 18, BlockTags.INCORRECT_FOR_IRON_TOOL);
    public static final ToolMaterial COPPER_TOOL_MATERIAL = material("copper", 300, 3.0F, 0.0F, 10, BlockTags.INCORRECT_FOR_IRON_TOOL);
    public static final ToolMaterial DRAGONBONE_TOOL_MATERIAL = material("dragonbone", 1660, 10.0F, 4.0F, 22, BlockTags.INCORRECT_FOR_IRON_TOOL);
    public static final ToolMaterial BLOODED_DRAGONBONE_TOOL_MATERIAL = material("blooded_dragonbone", 2000, 10.0F, 5.5F, 22, BlockTags.INCORRECT_FOR_IRON_TOOL);
    public static final ToolMaterial TROLL_WEAPON_TOOL_MATERIAL = material("troll_weapon", 300, 10.0F, 1.0F, 1, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial HIPPOGRYPH_SWORD_TOOL_MATERIAL = material("hippogryph", 500, 10.0F, 2.5F, 10, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial STYMHALIAN_SWORD_TOOL_MATERIAL = material("stymphalian", 500, 10.0F, 2.0F, 10, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial AMPHITHERE_SWORD_TOOL_MATERIAL = material("amphithere", 500, 10.0F, 1.0F, 10, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial HIPPOCAMPUS_SWORD_TOOL_MATERIAL = material("hippocampus", 500, 0.0F, -2.0F, 50, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial DREAD_SWORD_TOOL_MATERIAL = material("dread", 100, 10.0F, 1.0F, 0, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial DREAD_KNIGHT_TOOL_MATERIAL = material("dread_knight", 1200, 0.0F, 13.0F, 10, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial GHOST_SWORD_TOOL_MATERIAL = material("ghost", 3000, 10.0F, 5.0F, 25, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final ToolMaterial DRAGONSTEEL_FIRE = material("dragonsteel_fire", IafCommonConfig.INSTANCE.armors.dragonSteelBaseDurability.getValue(), 10.0F, IafCommonConfig.INSTANCE.armors.dragonSteelBaseDamage.getValue().floatValue() - 1, 21, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);
    public static final ToolMaterial DRAGONSTEEL_ICE = material("dragonsteel_ice", IafCommonConfig.INSTANCE.armors.dragonSteelBaseDurability.getValue(), 10.0F, IafCommonConfig.INSTANCE.armors.dragonSteelBaseDamage.getValue().floatValue() - 1, 21, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);
    public static final ToolMaterial DRAGONSTEEL_LIGHTNING = material("dragonsteel_lightning", IafCommonConfig.INSTANCE.armors.dragonSteelBaseDurability.getValue(), 10.0F, IafCommonConfig.INSTANCE.armors.dragonSteelBaseDamage.getValue().floatValue() - 1, 21, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);
    public static final ToolMaterial DREAD_QUEEN = material("dread_queen", 4000, 10.0F, 4.0F, 21, BlockTags.INCORRECT_FOR_WOODEN_TOOL);

    private IafTiers() {
    }

    private static ToolMaterial material(String name, int durability, float speed, float attackDamage, int enchantmentValue, TagKey<Block> incorrectBlocks) {
        TagKey<Item> repairItems = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "repairable/" + name));
        return new ToolMaterial(incorrectBlocks, durability, speed, attackDamage, Math.max(1, enchantmentValue), repairItems);
    }

    public static void init() {
    }
}
