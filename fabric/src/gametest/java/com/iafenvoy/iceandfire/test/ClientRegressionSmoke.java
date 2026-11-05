package com.iafenvoy.iceandfire.test;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.gui.bestiary.BestiaryPageCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Opt-in startup/resource reload check; never included in a release jar. */
public class ClientRegressionSmoke implements ClientModInitializer {
    private boolean started;

    @Override
    public void onInitializeClient() {
        if (!Boolean.getBoolean("iceandfire.client-smoke")) return;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (this.started || client.currentScreen == null || client.getOverlay() != null) return;
            this.started = true;
            String original = client.options.language;
            try {
                client.options.language = "en_us";
                List<String> first = BestiaryPageCache.get("introduction_0.txt");
                check(!first.isEmpty(), "English page missing");
                for (int i = 0; i < 1000; i++)
                    check(first == BestiaryPageCache.get("introduction_0.txt"), "Page was re-read");
                client.options.language = "zz_zz";
                check(first.equals(BestiaryPageCache.get("introduction_0.txt")), "English fallback failed");
                check(BestiaryPageCache.get("missing_regression_page.txt").isEmpty(), "Missing page failed");
                client.options.language = original;
                client.reloadResources().whenComplete((unused, error) -> client.execute(() -> {
                    try {
                        if (error != null) throw new IllegalStateException("Resource reload failed", error);
                        client.options.language = "en_us";
                        List<String> reloaded = BestiaryPageCache.get("introduction_0.txt");
                        check(reloaded != first && reloaded.equals(first), "Reload did not invalidate page cache");
                        finish(client, "PASS: client startup, renderer initialization, page reuse, language fallback, missing page, resource reload");
                    } catch (Throwable e) {
                        finish(client, "FAIL: " + e);
                    } finally {
                        client.options.language = original;
                    }
                }));
            } catch (Throwable e) {
                client.options.language = original;
                finish(client, "FAIL: " + e);
            }
        });
    }

    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static void finish(MinecraftClient client, String result) {
        IceAndFire.LOGGER.info("Client regression smoke: {}", result);
        try {
            Files.writeString(Path.of("client-smoke-result.txt"), result + System.lineSeparator());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            client.scheduleStop();
        }
    }
}
