package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.event.ClientEvents;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(Dist.CLIENT)
public final class IafKeybindings {
    public static final KeyMapping DRAGON_BREATH = new KeyMapping("key.dragon_fireAttack", GLFW.GLFW_KEY_R, "key.categories.gameplay");
    public static final KeyMapping DRAGON_STRIKE = new KeyMapping("key.dragon_strike", GLFW.GLFW_KEY_G, "key.categories.gameplay");
    public static final KeyMapping DRAGON_DOWN = new KeyMapping("key.dragon_down", GLFW.GLFW_KEY_X, "key.categories.gameplay");
    public static final KeyMapping DRAGON_CHANGE_VIEW = new KeyMapping("key.dragon_change_view", GLFW.GLFW_KEY_F7, "key.categories.gameplay");

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(DRAGON_BREATH);
        event.register(DRAGON_STRIKE);
        event.register(DRAGON_DOWN);
        event.register(DRAGON_CHANGE_VIEW);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (DRAGON_CHANGE_VIEW.consumeClick()) {
            if (ClientEvents.currentView + 1 > 3) ClientEvents.currentView = 0;
            else ClientEvents.currentView++;
        }
    }
}
