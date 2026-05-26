package com.iafenvoy.iceandfire.registry;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.screen.gui.*;
import com.iafenvoy.iceandfire.screen.gui.bestiary.BestiaryScreen;
import com.iafenvoy.iceandfire.screen.menu.*;
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
public final class IafMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, IceAndFire.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<DragonMenu>> DRAGON_SCREEN = register("dragon", () -> IMenuTypeExtension.create(DragonMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<HippogryphMenu>> HIPPOGRYPH_SCREEN = register("hippogryph", () -> IMenuTypeExtension.create(HippogryphMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<HippocampusMenu>> HIPPOCAMPUS_SCREEN = register("hippocampus", () -> IMenuTypeExtension.create(HippocampusMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<DragonForgeMenu>> DRAGON_FORGE_SCREEN = register("dragon_forge", () -> IMenuTypeExtension.create(DragonForgeMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<PodiumMenu>> PODIUM_SCREEN = register("podium", () -> new MenuType<>(PodiumMenu::new, FeatureFlags.VANILLA_SET));
    public static final DeferredHolder<MenuType<?>, MenuType<LecternMenu>> IAF_LECTERN_SCREEN = register("iaf_lectern", () -> new MenuType<>(LecternMenu::new, FeatureFlags.VANILLA_SET));
    public static final DeferredHolder<MenuType<?>, MenuType<BestiaryMenu>> BESTIARY_SCREEN = register("bestiary", () -> IMenuTypeExtension.create(BestiaryMenu::new));

    private static <C extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<C>> register(String name, Supplier<MenuType<C>> type) {
        return REGISTRY.register(name, type);
    }

    @SubscribeEvent
    public static void registerGui(RegisterMenuScreensEvent event) {
        event.register(IafMenus.IAF_LECTERN_SCREEN.get(), LecternScreen::new);
        event.register(IafMenus.PODIUM_SCREEN.get(), PodiumScreen::new);
        event.register(IafMenus.DRAGON_SCREEN.get(), DragonScreen::new);
        event.register(IafMenus.HIPPOGRYPH_SCREEN.get(), HippogryphScreen::new);
        event.register(IafMenus.HIPPOCAMPUS_SCREEN.get(), HippocampusScreen::new);
        event.register(IafMenus.DRAGON_FORGE_SCREEN.get(), DragonForgeScreen::new);
        event.register(IafMenus.BESTIARY_SCREEN.get(), BestiaryScreen::new);
    }
}
