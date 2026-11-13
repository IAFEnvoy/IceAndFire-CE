package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.render.entity.LegacyMobRenderer.ModelPose;
import com.iafenvoy.iceandfire.render.model.*;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.util.HideableModelRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/** Runs only in the opt-in, isolated development client; never included in the release jar. */
@EventBusSubscriber(modid = "iceandfire", value = Dist.CLIENT)
public final class ClientRegressionTests {
    private static int ticks;
    private static boolean finished;
    private static boolean opening;
    private static int worldTicks;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) throws Exception {
        if (!Boolean.getBoolean("iceandfire.auditClientRegression")) return;
        Minecraft client = Minecraft.getInstance();
        if (++ticks > 1800) throw new AssertionError("Timed out opening client regression world");
        if (finished) {
            client.stop();
            return;
        }
        if (client.getOverlay() != null || ticks < 20) return;
        if (!opening) {
            if (client.screen == null) return;
            opening = true;
            client.options.pauseOnLostFocus = false;
            // 26.1 binds item components when loading a world's data packs, not at the title screen.
            client.createWorldOpenFlows().createFreshLevel("armor-regression-" + System.currentTimeMillis(),
                    new LevelSettings("Armor regression", GameType.CREATIVE,
                            LevelSettings.DifficultySettings.DEFAULT, true, WorldDataConfiguration.DEFAULT),
                    new WorldOptions(273, false, false),
                    registries -> registries.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.FLAT)
                            .value().createWorldDimensions(), client.screen);
            return;
        }
        if (client.level == null || client.player == null || ++worldTicks < 20) return;
        finished = true;
        List<String> passed = new ArrayList<>();
        try {
            DreadKnightModel knight = new DreadKnightModel(0);
            deferredPose("knight_nested_cloak", knight, knight.cloak, passed);
            deferredPose("egg", new DragonEggModel(), null, passed);
            deferredPose("hydra_head", new HydraHeadModel(0), null, passed);
            deferredPose("pixie", new PixieModel(), null, passed);
            deferredPose("gauntlet", new DeathWormGauntletModel(), null, passed);
            ArmorModelRegression.run(passed);
            String result = "{\"passed\":" + passed.size() + ",\"failed\":0,\"checks\":\"" + String.join(",", passed)
                    + "\",\"world\":\"loaded\"}";
            Files.writeString(client.gameDirectory.toPath().resolve("client-regression-result.json"), result);
            System.out.println("IAF CLIENT REGRESSION: all " + passed.size() + " geometry/model tests passed");
        } catch (Throwable failure) {
            Files.writeString(client.gameDirectory.toPath().resolve("client-regression-result.json"),
                    "{\"failed\":1,\"completed\":" + passed.size() + "}");
            throw new AssertionError("Client regression failed after " + passed, failure);
        }
    }

    private static void deferredPose(String name, AdvancedEntityModel<?> model, AdvancedModelBox nested, List<String> passed) {
        AdvancedModelBox part = nested == null ? model.getAllParts().iterator().next() : nested;
        List<SubmitNodeCollector.CustomGeometryRenderer> queue = new ArrayList<>();
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(
                SubmitNodeCollector.class.getClassLoader(), new Class<?>[]{SubmitNodeCollector.class}, (proxy, method, args) -> {
                    if (method.getName().equals("submitCustomGeometry")) {
                        queue.add((SubmitNodeCollector.CustomGeometryRenderer) args[2]);
                        return null;
                    }
                    throw new AssertionError("Unexpected collector method " + method);
                });
        for (int i = 0; i < 4; i++) {
            int index = i;
            mutate(part, i);
            ModelPose.submit(collector, new PoseStack(), RenderTypes.entityCutout(Identifier.withDefaultNamespace("textures/misc/white.png")),
                    model, (pose, buffer) -> {
                        assertPose(part, index, name);
                        int[] vertices = {0};
                        VertexConsumer sink = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                                new Class<?>[]{VertexConsumer.class}, (proxy, method, args) -> {
                                    if (method.getName().equals("addVertex")) vertices[0]++;
                                    return method.getReturnType() == VertexConsumer.class ? proxy : null;
                                });
                        // Verify restored model state is consumable by the real target-version mesh path.
                        part.showModel = true;
                        if (part instanceof HideableModelRenderer hideable) hideable.invisible = false;
                        model.renderToBuffer(new PoseStack(), sink, 0xF000F0, 0, -1);
                        if (vertices[0] == 0) throw new AssertionError(name + " emitted no vertices");
                    });
        }
        // Render-type batching can reorder callbacks. Check both directions and repeated application.
        for (int i = queue.size() - 1; i >= 0; i--) queue.get(i).render(new PoseStack().last(), null);
        for (var geometry : queue) geometry.render(new PoseStack().last(), null);
        passed.add(name);
    }

    private static void mutate(AdvancedModelBox part, int index) {
        part.rotationPointX = index + 1;
        part.rotationPointY = index + 2;
        part.rotationPointZ = index + 3;
        part.rotateAngleX = index + 4;
        part.rotateAngleY = index + 5;
        part.rotateAngleZ = index + 6;
        part.scaleX = index + 7;
        part.scaleY = index + 8;
        part.scaleZ = index + 9;
        part.offsetX = index + 10;
        part.offsetY = index + 11;
        part.offsetZ = index + 12;
        part.showModel = (index & 1) == 0;
        part.scaleChildren = (index & 1) != 0;
        if (part instanceof HideableModelRenderer hideable) hideable.invisible = index > 1;
    }

    private static void assertPose(AdvancedModelBox part, int index, String name) {
        boolean valid = part.rotationPointX == index + 1 && part.rotationPointY == index + 2 && part.rotationPointZ == index + 3
                && part.rotateAngleX == index + 4 && part.rotateAngleY == index + 5 && part.rotateAngleZ == index + 6
                && part.scaleX == index + 7 && part.scaleY == index + 8 && part.scaleZ == index + 9
                && part.offsetX == index + 10 && part.offsetY == index + 11 && part.offsetZ == index + 12
                && part.showModel == ((index & 1) == 0) && part.scaleChildren == ((index & 1) != 0);
        if (part instanceof HideableModelRenderer hideable) valid &= hideable.invisible == (index > 1);
        if (!valid) throw new AssertionError(name + " read another submission's pose or visibility at " + index);
    }
}
