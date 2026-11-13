package com.iafenvoy.iceandfire.regression;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.nio.file.Files;

/** Opt-in client entry point; isolated from release output and normal client runs. */
@EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT)
public final class ClientArmorRegression {
    private static boolean finished;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("iceandfire.armorRegression") || finished) return;
        Minecraft client = Minecraft.getInstance();
        if (client.screen == null || client.getOverlay() != null) return;
        finished = true;
        try {
            int count = ArmorModelRegression.run();
            Files.writeString(client.gameDirectory.toPath().resolve("armor-regression-result.json"),
                    "{\"passed\":" + count + ",\"failed\":0}");
            System.out.println("IAF ARMOR REGRESSION: all " + count + " model cases passed");
        } catch (Throwable failure) {
            Files.writeString(client.gameDirectory.toPath().resolve("armor-regression-result.json"), "{\"failed\":1}");
            throw new AssertionError("Armor regression failed", failure);
        } finally {
            client.stop();
        }
    }
}
