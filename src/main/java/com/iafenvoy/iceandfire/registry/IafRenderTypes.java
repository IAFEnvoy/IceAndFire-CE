package com.iafenvoy.iceandfire.registry;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/** Render type helpers backed by the 26.1 render-state pipeline. */
public final class IafRenderTypes {
    private static final Identifier STONE_TEXTURE = Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/stone.png");

    private IafRenderTypes() {
    }

    public static RenderType getGhost(Identifier texture) {
        return RenderTypes.entityTranslucent(texture, false);
    }

    public static RenderType getGhostDaytime(Identifier texture) {
        return RenderTypes.entityTranslucent(texture, false);
    }

    public static RenderType getStoneMobRenderType(float x, float y) {
        return RenderTypes.entityCutout(STONE_TEXTURE, false);
    }

    public static RenderType getIce(Identifier texture) {
        return RenderTypes.beaconBeam(texture, true);
    }

    public static RenderType getStoneCrackRenderType(Identifier texture) {
        return RenderTypes.entityTranslucent(texture, false);
    }
}
