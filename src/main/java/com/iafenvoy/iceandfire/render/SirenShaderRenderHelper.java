package com.iafenvoy.iceandfire.render;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.registry.IafMobEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class SirenShaderRenderHelper {
    private static final ResourceLocation SIREN_SHADER = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "shaders/post/siren.json");

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
        PostChain processor = renderer.currentEffect();
        return processor != null && SIREN_SHADER.toString().equals(processor.getName());
    }

    private static void enableShader(GameRenderer renderer) {
        if (enabled(renderer)) return;
        renderer.loadEffect(SIREN_SHADER);
    }

    private static void disableShader(GameRenderer renderer) {
        if (!enabled(renderer)) return;
        renderer.shutdownEffect();
    }
}
