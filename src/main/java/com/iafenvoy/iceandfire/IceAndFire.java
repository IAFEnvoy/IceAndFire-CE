package com.iafenvoy.iceandfire;

import com.iafenvoy.iceandfire.compat.IceAndFireArsNouveauCompat;
import com.iafenvoy.iceandfire.compat.curios.CuriosRegistry;
import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.IafSkullType;
import com.iafenvoy.iceandfire.data.SeaSerpentType;
import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.integration.IntegrationExecutor;
import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.ServerConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(IceAndFire.MOD_ID)
@EventBusSubscriber
public final class IceAndFire {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "iceandfire";
    public static final String VERSION;

    static {
        VERSION = ModList.get().getModContainerById(IceAndFire.MOD_ID).map(ModContainer::getModInfo).map(IModInfo::getVersion).map(Object::toString).orElse("UNKNOWN");
    }

    //TODO: IceAndFire::id is a temporary fix to capable with old version, should be removed in later versions
    public static ResourceLocation id(String path) {
        if (path.contains(":")) return ResourceLocation.tryParse(path);
        else return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public IceAndFire(IEventBus bus) {
        ConfigManager.getInstance().registerConfigHandler(IafCommonConfig.INSTANCE);
        ServerConfigManager.registerServerConfig(IafCommonConfig.INSTANCE, ServerConfigManager.PermissionChecker.IS_OPERATOR);

        IafBestiaryPages.init();
        IafDragonColors.init();
        IafDragonTypes.init();
        IafHippogryphTypes.init();
        IafSeaSerpentTypes.init();
        IafTrollTypes.init();

        DragonColor.initArmors();
        SeaSerpentType.initArmors();
        IafSkullType.initItems();
        TrollType.initArmors();

        IafAttachments.REGISTRY.register(bus);
        IafAttributes.REGISTRY.register(bus);
        IafArmorMaterials.REGISTRY.register(bus);
        IafSounds.REGISTRY.register(bus);
        IafBlocks.REGISTRY.register(bus);
        IafBlockEntities.REGISTRY.register(bus);
        IafDataComponents.REGISTRY.register(bus);
        IafEntities.REGISTRY.register(bus);
        IafItemGroups.REGISTRY.register(bus);
        IafItems.REGISTRY.register(bus);
        IafLoots.REGISTRY.register(bus);
        IafRecipes.REGISTRY.register(bus);
        IafRecipeSerializers.REGISTRY.register(bus);
        IafParticles.REGISTRY.register(bus);
        IafProcessors.REGISTRY.register(bus);
        IafFeatures.REGISTRY.register(bus);
        IafScreenHandlers.REGISTRY.register(bus);
        IafStatusEffects.REGISTRY.register(bus);
        IafStructurePieces.REGISTRY.register(bus);
        IafStructureTypes.REGISTRY.register(bus);
        //Trade
        IafTrades.POI_REGISTRY.register(bus);
        IafTrades.PROFESSION_REGISTRY.register(bus);
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        IafTrades.init();
        IafRecipes.init();
        IafToolMaterials.init();

        IntegrationExecutor.runWhenLoad("ars_nouveau", () -> IceAndFireArsNouveauCompat::init);
        IntegrationExecutor.runWhenLoad("curios", () -> CuriosRegistry::registerItems);
    }

    @SubscribeEvent
    public static void registerBrewing(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addMix(Potions.WATER, IafItems.SHINY_SCALES.get(), Potions.WATER_BREATHING);
    }
}
