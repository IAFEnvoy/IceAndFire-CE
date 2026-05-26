package com.iafenvoy.iceandfire.compat.ponder;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.FireDragonEntity;
import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.entity.LightningDragonEntity;
import com.iafenvoy.iceandfire.registry.IafBlocks;
import com.iafenvoy.iceandfire.registry.IafDragonTypes;
import com.iafenvoy.iceandfire.registry.IafEntities;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IceAndFirePonderPlugin implements PonderPlugin {
    private static final ResourceLocation DRAGON_TAG_ID = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "dragon_forge");

    @Override
    public @NotNull String getModId() {
        return IceAndFire.MOD_ID;
    }

    @Override
    public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.getId())
                .addStoryBoard(DragonForgeStoryBoard.id(IafDragonTypes.FIRE), new DragonForgeStoryBoard<>(IafEntities.FIRE_DRAGON.get()::create, FireDragonEntity.class));
        helper.forComponents(IafBlocks.DRAGONFORGE_ICE_CORE_DISABLED.getId())
                .addStoryBoard(DragonForgeStoryBoard.id(IafDragonTypes.ICE), new DragonForgeStoryBoard<>(IafEntities.ICE_DRAGON.get()::create, IceDragonEntity.class));
        helper.forComponents(IafBlocks.DRAGONFORGE_LIGHTNING_CORE_DISABLED.getId())
                .addStoryBoard(DragonForgeStoryBoard.id(IafDragonTypes.LIGHTNING), new DragonForgeStoryBoard<>(IafEntities.LIGHTNING_DRAGON.get()::create, LightningDragonEntity.class));
    }

    @Override
    public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(DRAGON_TAG_ID)
                .addToIndex()
                .item(IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.get(), true, false)
                .title("Dragon Forge")
                .description("Dragon Forge")
                .register();
        helper.addToTag(DRAGON_TAG_ID)
                .add(IafBlocks.DRAGONFORGE_FIRE_CORE_DISABLED.getId())
                .add(IafBlocks.DRAGONFORGE_ICE_CORE_DISABLED.getId())
                .add(IafBlocks.DRAGONFORGE_LIGHTNING_CORE_DISABLED.getId());
    }

    public static void init() {
        PonderIndex.addPlugin(new IceAndFirePonderPlugin());
    }
}
