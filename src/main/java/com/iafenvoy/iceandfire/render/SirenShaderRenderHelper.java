package com.iafenvoy.iceandfire.render;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.registry.IafMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(Dist.CLIENT)
public class SirenShaderRenderHelper {
    private static final Identifier SIREN_SHADER = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "shaders/post/siren.json");

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        if (IafClientConfig.INSTANCE.sirenShader.getValue() && player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(IafMobEffects.SIREN_CHARM.get())))
            enableShader(renderer);
        else disableShader(renderer);
    }

    private static boolean enabled(GameRenderer renderer) {
        return SIREN_SHADER.equals(renderer.currentPostEffect());
    }

    private static void enableShader(GameRenderer renderer) {
        if (enabled(renderer)) return;
        renderer.setPostEffect(SIREN_SHADER);
    }

    private static void disableShader(GameRenderer renderer) {
        if (!enabled(renderer)) return;
        renderer.clearPostEffect();
    }
}
