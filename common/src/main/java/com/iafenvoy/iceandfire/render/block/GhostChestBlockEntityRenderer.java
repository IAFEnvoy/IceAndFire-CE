package com.iafenvoy.iceandfire.render.block;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.item.block.entity.GhostChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import static net.minecraft.client.render.TexturedRenderLayers.CHEST_ATLAS_TEXTURE;

public class GhostChestBlockEntityRenderer extends ChestBlockEntityRenderer<GhostChestBlockEntity> {
    private static final SpriteIdentifier GHOST_CHEST = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Identifier.of(IceAndFire.MOD_ID, "entity/ghost/ghost_chest"));
    private static final SpriteIdentifier GHOST_CHEST_LEFT = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Identifier.of(IceAndFire.MOD_ID, "entity/ghost/ghost_chest_left"));
    private static final SpriteIdentifier GHOST_CHEST_RIGHT = new SpriteIdentifier(CHEST_ATLAS_TEXTURE, Identifier.of(IceAndFire.MOD_ID, "entity/ghost/ghost_chest_right"));

    public GhostChestBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    private static SpriteIdentifier getChestMaterial(ChestType chestType, SpriteIdentifier doubleMaterial, SpriteIdentifier leftMaterial, SpriteIdentifier rightMaterial) {
        return switch (chestType) {
            case LEFT -> leftMaterial;
            case RIGHT -> rightMaterial;
            default -> doubleMaterial;
        };
    }

}
