package com.iafenvoy.iceandfire.client;

import com.iafenvoy.iceandfire.entity.util.IFlightStamina;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * Stamina bar of the flight rework, drawn like the vanilla jump bar above the hotbar. It is shared by every mount
 * implementing {@link IFlightStamina} and only shows up while the ridden mount has the stamina system enabled.
 */
@Environment(EnvType.CLIENT)
public final class FlightStaminaOverlay implements ClientGuiEvent.RenderHud {
    private static final Identifier ICONS = new Identifier("minecraft", "textures/gui/icons.png");
    private static final int BAR_WIDTH = 182;
    private static final int BAR_HEIGHT = 5;
    private static final int BACKGROUND_V = 84;
    private static final int PROGRESS_V = 89;

    @Override
    public void renderHud(DrawContext context, float tickDelta) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft.player == null) return;
        Entity vehicle = minecraft.player.getVehicle();
        if (!(vehicle instanceof IFlightStamina stamina) || !stamina.isFlightStaminaEnabled()) return;
        float maxStamina = stamina.getMaxFlightStamina();
        if (maxStamina <= 0F) return;
        float percentage = Math.max(0F, Math.min(1F, stamina.getFlightStamina() / maxStamina));

        int x = context.getScaledWindowWidth() / 2 - 91;
        int y = context.getScaledWindowHeight() - 25;

        context.drawTexture(ICONS, x, y, 0, BACKGROUND_V, BAR_WIDTH, BAR_HEIGHT);
        int fillWidth = Math.round(BAR_WIDTH * percentage);
        if (fillWidth > 0) {
            context.enableScissor(x, y, x + fillWidth, y + BAR_HEIGHT);
            context.drawTexture(ICONS, x, y, 0, PROGRESS_V, BAR_WIDTH, BAR_HEIGHT);
            context.disableScissor();
        }
    }
}
