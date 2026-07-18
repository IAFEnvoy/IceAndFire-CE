package com.iafenvoy.iceandfire.render.model;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DragonSkullEntity;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DragonSkullGeoModel extends GeoModel<DragonSkullEntity> {
    @Override
    public ResourceLocation getModelResource(DragonSkullEntity skull) {
        return IceAndFire.id("geo/" + skull.getDragonType() + "dragon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DragonSkullEntity skull) {
        return IafRegistries.DRAGON_TYPE.get(IceAndFire.id(skull.getDragonType())).getSkeletonTexture(skull.getDragonStage());
    }

    @Override
    public ResourceLocation getAnimationResource(DragonSkullEntity skull) {
        return IceAndFire.id("animations/" + skull.getDragonType() + "dragon.animation.json");
    }
}
