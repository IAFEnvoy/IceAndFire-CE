package com.iafenvoy.iceandfire.render;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.config.IafClientConfig;
import com.iafenvoy.iceandfire.data.component.SirenData;
import com.iafenvoy.iceandfire.entity.SirenEntity;
import com.iafenvoy.iceandfire.registry.IafParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SirenShaderRenderHelper {
    private static final Identifier SIREN_SHADER = Identifier.of(IceAndFire.MOD_ID, "shaders/post/siren.json");

    public static void tick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        GameRenderer renderer = MinecraftClient.getInstance().gameRenderer;
        if (!IafClientConfig.INSTANCE.sirenShader.getValue()) {
            disableShader(renderer);
            return;
        }

        SirenData sirenData = SirenData.get(player);
        Optional<UUID> optional = sirenData.getCharmedByUUID();
        if (optional.isPresent()) {
            enableShader(renderer);
            if (player.getRandom().nextInt(40) == 0) {
                Entity e = client.world.entityManager.getLookup().get(optional.get());
                if (e instanceof SirenEntity siren)
                    player.getWorld().addParticle(IafParticles.SIREN_APPEARANCE.get(), player.getX(), player.getY(), player.getZ(), siren.getHairColor(), 0, 0);
            }
        } else disableShader(renderer);
    }

    private static boolean enabled(GameRenderer renderer) {
        PostEffectProcessor processor = renderer.getPostProcessor();
        return processor != null && SIREN_SHADER.toString().equals(processor.getName());
    }

    private static void enableShader(GameRenderer renderer) {
        if (enabled(renderer)) return;
        renderer.loadPostProcessor(SIREN_SHADER);
    }

    private static void disableShader(GameRenderer renderer) {
        if (!enabled(renderer)) return;
        renderer.disablePostProcessor();
    }
}
