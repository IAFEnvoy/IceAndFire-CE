package com.iafenvoy.iceandfire;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.data.IafSkullType;
import com.iafenvoy.iceandfire.data.SeaSerpentType;
import com.iafenvoy.iceandfire.data.TrollType;
import com.iafenvoy.iceandfire.event.ServerEvents;
import com.iafenvoy.iceandfire.network.ServerNetworkHelper;
import com.iafenvoy.iceandfire.registry.*;
import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.uranus.event.EntityEvents;
import com.iafenvoy.uranus.event.LivingEntityEvents;
import com.iafenvoy.uranus.event.PlayerEvents;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IceAndFire {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "iceandfire";
    public static final String MOD_NAME = "Ice And Fire";
    public static final String VERSION;

    static {
        VERSION = Platform.getMod(IceAndFire.MOD_ID).getVersion();
    }

    //TODO: IceAndFire::id is a temporary fix to capable with old version, should be removed in later versions
    public static Identifier id(String path) {
        if (path.contains(":")) return Identifier.tryParse(path);
        else return Identifier.of(MOD_ID, path);
    }

    public static void init() {
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

        IafArmorMaterials.REGISTRY.register();
        IafSounds.REGISTRY.register();
        IafBlocks.REGISTRY.register();
        IafBlockEntities.REGISTRY.register();
        IafDataComponents.REGISTRY.register();
        IafEntities.REGISTRY.register();
        IafItemGroups.REGISTRY.register();
        IafItems.REGISTRY.register();
        IafLoots.REGISTRY.register();
        IafRecipes.REGISTRY.register();
        IafRecipeSerializers.REGISTRY.register();
        IafParticles.REGISTRY.register();
        IafProcessors.REGISTRY.register();
        IafFeatures.REGISTRY.register();
        IafScreenHandlers.REGISTRY.register();
        IafStructurePieces.REGISTRY.register();
        IafStructureTypes.REGISTRY.register();
        //Trade
        IafTrades.POI_REGISTRY.register();
        IafTrades.PROFESSION_REGISTRY.register();
        IafEntities.registerAttributes();
    }

    public static void process() {
        IafEntities.init();
        IafTrades.init();
        IafRecipes.init();
        IafFeatures.init();
        IafToolMaterials.init();

        BlockEvent.BREAK.register(ServerEvents::onBreakBlock);
        InteractionEvent.INTERACT_ENTITY.register(ServerEvents::onEntityInteract);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(ServerEvents::onPlayerRightClick);
        EntityEvent.LIVING_DEATH.register(ServerEvents::onEntityDie);
        PlayerEvent.ATTACK_ENTITY.register(ServerEvents::onPlayerAttack);
        PlayerEvents.LOGGED_OUT.register(ServerEvents::onPlayerLeaveEvent);
        EntityEvents.ON_JOIN_WORLD.register(ServerEvents::onEntityJoinWorld);
        EntityEvents.START_TRACKING_TAIL.register(ServerEvents::onLivingSetTarget);
        LivingEntityEvents.DAMAGE.register(ServerEvents::onEntityDamage);
        EntityEvent.LIVING_HURT.register(ServerEvents::onLivingHurt);

        ServerNetworkHelper.registerReceivers();
    }
}
