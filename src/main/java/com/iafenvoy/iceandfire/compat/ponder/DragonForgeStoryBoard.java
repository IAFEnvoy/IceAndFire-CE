package com.iafenvoy.iceandfire.compat.ponder;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonType;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.item.block.DragonForgeBrickBlock;
import com.iafenvoy.uranus.util.RandomHelper;
import net.createmod.ponder.api.scene.PonderStoryBoard;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Function;

public class DragonForgeStoryBoard<T extends DragonBaseEntity> implements PonderStoryBoard {
    private final Function<Level, Entity> dragonFactory;
    private final Class<T> dragonClass;

    public DragonForgeStoryBoard(Function<Level, Entity> dragonFactory, Class<T> dragonClass) {
        this.dragonFactory = dragonFactory;
        this.dragonClass = dragonClass;
    }

    public static ResourceLocation id(DragonType type) {
        return ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, String.format(Locale.ROOT, "%s_dragon_forge", type.name()));
    }

    @Override
    public void program(@NotNull SceneBuilder scene, @NotNull SceneBuildingUtil util) {
        BlockPos center = new BlockPos(2, 2, 2);
        scene.title("dragon_forge", "Dragon Forge");
        scene.configureBasePlate(0, 0, 5);
        scene.removeShadow();
        scene.world().showSection(util.select().layer(0), Direction.UP);//Show floor
        scene.idle(20);
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(20);
        scene.world().showSection(util.select().layer(2), Direction.DOWN);
        setBrickDisplay(scene, center, false);
        scene.idle(15);
        setBrickDisplay(scene, center, false);
        scene.idle(5);
        scene.overlay().showText(50)
                .text("This is the core of forge, all items put in here")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(center, Direction.UP));
        scene.idle(60);
        scene.overlay().showText(50)
                .text("Dragon need to see this block to power the forge")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(center.north(), Direction.UP));
        scene.idle(60);
        scene.world().showSection(util.select().layer(3), Direction.DOWN);
        scene.idle(20);
        setBrickDisplay(scene, center, true);
        scene.rotateCameraY(-45);
        scene.idle(20);
        scene.world().createEntity(this.dragonFactory);
        scene.world().modifyEntities(this.dragonClass, dragon -> Minecraft.getInstance().execute(() -> {
            dragon.setGender(true);
            dragon.setVariant(RandomHelper.randomOne(dragon.dragonType.colors()).getName());
            dragon.setPosRaw(3, 0.5, -5);
            dragon.setAgeInDays(50);//Stage 3 is enough
            dragon.setOrderedToSit(true);
            dragon.setAnimation(DragonBaseEntity.ANIMATION_SPEAK);
        }));
        scene.overlay().showText(100)
                .text("You need the correct type of dragon to power the forge")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(new BlockPos(0, 2, -5), Direction.UP));
        scene.overlay().showText(100)
                .text("You can use blocks at the center of each face to open the forge")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(center.west(), Direction.WEST));
    }

    private static void setBrickDisplay(SceneBuilder scene, BlockPos center, boolean bl) {
        for (int i = 0; i < 4; i++)
            scene.world().modifyBlock(center.offset(Direction.from2DDataValue(i).getNormal()), state -> state.hasProperty(DragonForgeBrickBlock.GRILL) ? state.setValue(DragonForgeBrickBlock.GRILL, bl) : state, false);
    }
}
