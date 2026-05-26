package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.gui.*;
import com.iafenvoy.iceandfire.screen.gui.bestiary.BestiaryScreen;
import com.iafenvoy.iceandfire.screen.handler.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(Dist.CLIENT)
public final class IafScreenHandlers {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, IceAndFire.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<DragonScreenHandler>> DRAGON_SCREEN = register("dragon", () -> IMenuTypeExtension.create(DragonScreenHandler::new));
    public static final DeferredHolder<MenuType<?>, MenuType<HippogryphScreenHandler>> HIPPOGRYPH_SCREEN = register("hippogryph", () -> IMenuTypeExtension.create(HippogryphScreenHandler::new));
    public static final DeferredHolder<MenuType<?>, MenuType<HippocampusScreenHandler>> HIPPOCAMPUS_SCREEN = register("hippocampus", () -> IMenuTypeExtension.create(HippocampusScreenHandler::new));
    public static final DeferredHolder<MenuType<?>, MenuType<DragonForgeScreenHandler>> DRAGON_FORGE_SCREEN = register("dragon_forge", () -> IMenuTypeExtension.create(DragonForgeScreenHandler::new));
    public static final DeferredHolder<MenuType<?>, MenuType<PodiumScreenHandler>> PODIUM_SCREEN = register("podium", () -> new MenuType<>(PodiumScreenHandler::new, FeatureFlags.VANILLA_SET));
    public static final DeferredHolder<MenuType<?>, MenuType<LecternScreenHandler>> IAF_LECTERN_SCREEN = register("iaf_lectern", () -> new MenuType<>(LecternScreenHandler::new, FeatureFlags.VANILLA_SET));
    public static final DeferredHolder<MenuType<?>, MenuType<BestiaryScreenHandler>> BESTIARY_SCREEN = register("bestiary", () -> IMenuTypeExtension.create(BestiaryScreenHandler::new));

    private static <C extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<C>> register(String name, Supplier<MenuType<C>> type) {
        return REGISTRY.register(name, type);
    }

    @SubscribeEvent
    public static void registerGui(RegisterMenuScreensEvent event) {
        event.register(IafScreenHandlers.IAF_LECTERN_SCREEN.get(), LecternScreen::new);
        event.register(IafScreenHandlers.PODIUM_SCREEN.get(), PodiumScreen::new);
        event.register(IafScreenHandlers.DRAGON_SCREEN.get(), DragonScreen::new);
        event.register(IafScreenHandlers.HIPPOGRYPH_SCREEN.get(), HippogryphScreen::new);
        event.register(IafScreenHandlers.HIPPOCAMPUS_SCREEN.get(), HippocampusScreen::new);
        event.register(IafScreenHandlers.DRAGON_FORGE_SCREEN.get(), DragonForgeScreen::new);
        event.register(IafScreenHandlers.BESTIARY_SCREEN.get(), BestiaryScreen::new);
    }
}
