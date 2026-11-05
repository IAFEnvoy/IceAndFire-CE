package com.iafenvoy.iceandfire.screen.gui.bestiary;

import com.iafenvoy.iceandfire.IceAndFire;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class BestiaryPageCache implements SynchronousResourceReloader {
    private static final Map<Identifier, List<String>> PAGES = new HashMap<>();

    public static List<String> get(String fileName) {
        var client = MinecraftClient.getInstance();
        String language = client.options.language.toLowerCase(Locale.ROOT);
        Identifier id = Identifier.of(IceAndFire.MOD_ID, "lang/bestiary/" + language + "_0/" + fileName);
        return PAGES.computeIfAbsent(id, key -> {
            var manager = client.getResourceManager();
            var resource = manager.getResource(key).or(() -> manager.getResource(
                    Identifier.of(IceAndFire.MOD_ID, "lang/bestiary/en_us_0/" + fileName)));
            if (resource.isEmpty()) return List.of();
            try (var reader = new BufferedReader(new InputStreamReader(resource.get().getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().map(String::trim).toList();
            } catch (Exception e) {
                IceAndFire.LOGGER.warn("Cannot read bestiary page {}", key, e);
                return List.of();
            }
        });
    }

    @Override
    public void reload(ResourceManager manager) {
        PAGES.clear();
    }
}
