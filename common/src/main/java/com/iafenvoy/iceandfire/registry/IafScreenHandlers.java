package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.gui.*;
import com.iafenvoy.iceandfire.screen.gui.bestiary.BestiaryScreen;
import com.iafenvoy.iceandfire.screen.handler.*;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import java.util.function.Supplier;

public final class IafScreenHandlers {
    public static final DeferredRegister<ScreenHandlerType<?>> REGISTRY = DeferredRegister.create(IceAndFire.MOD_ID, RegistryKeys.SCREEN_HANDLER);

    public static final RegistrySupplier<ScreenHandlerType<DragonScreenHandler>> DRAGON_SCREEN = register("dragon", () -> new ScreenHandlerType<>(DragonScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    public static final RegistrySupplier<ScreenHandlerType<HippogryphScreenHandler>> HIPPOGRYPH_SCREEN = register("hippogryph", () -> new ScreenHandlerType<>(HippogryphScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    public static final RegistrySupplier<ScreenHandlerType<HippocampusScreenHandler>> HIPPOCAMPUS_SCREEN = register("hippocampus", () -> new ScreenHandlerType<>(HippocampusScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    public static final RegistrySupplier<ScreenHandlerType<DragonForgeScreenHandler>> DRAGON_FORGE_SCREEN = register("dragon_forge", () -> new ScreenHandlerType<>(DragonForgeScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    public static final RegistrySupplier<ScreenHandlerType<PodiumScreenHandler>> PODIUM_SCREEN = register("podium", () -> new ScreenHandlerType<>(PodiumScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    public static final RegistrySupplier<ScreenHandlerType<LecternScreenHandler>> IAF_LECTERN_SCREEN = register("iaf_lectern", () -> new ScreenHandlerType<>(LecternScreenHandler::new, FeatureFlags.VANILLA_FEATURES));
    public static final RegistrySupplier<ScreenHandlerType<BestiaryScreenHandler>> BESTIARY_SCREEN = register("bestiary", () -> MenuRegistry.ofExtended(BestiaryScreenHandler::new));

    private static <C extends ScreenHandler> RegistrySupplier<ScreenHandlerType<C>> register(String name, Supplier<ScreenHandlerType<C>> type) {
        return REGISTRY.register(name, type);
    }

    public static void registerGui() {
        MenuRegistry.registerScreenFactory(IafScreenHandlers.IAF_LECTERN_SCREEN.get(), LecternScreen::new);
        MenuRegistry.registerScreenFactory(IafScreenHandlers.PODIUM_SCREEN.get(), PodiumScreen::new);
        MenuRegistry.registerScreenFactory(IafScreenHandlers.DRAGON_SCREEN.get(), DragonScreen::new);
        MenuRegistry.registerScreenFactory(IafScreenHandlers.HIPPOGRYPH_SCREEN.get(), HippogryphScreen::new);
        MenuRegistry.registerScreenFactory(IafScreenHandlers.HIPPOCAMPUS_SCREEN.get(), HippocampusScreen::new);
        MenuRegistry.registerScreenFactory(IafScreenHandlers.DRAGON_FORGE_SCREEN.get(), DragonForgeScreen::new);
        MenuRegistry.registerScreenFactory(IafScreenHandlers.BESTIARY_SCREEN.get(), BestiaryScreen::new);
    }
}
