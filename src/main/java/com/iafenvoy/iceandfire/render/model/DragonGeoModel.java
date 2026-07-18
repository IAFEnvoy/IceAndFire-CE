package com.iafenvoy.iceandfire.render.model;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.FireDragonEntity;
import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class DragonGeoModel<T extends DragonBaseEntity> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T dragon) {
        return IceAndFire.id("geo/" + dragonModelName(dragon) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T dragon) {
        return DragonColor.getById(dragon.getVariant()).getTextureProvider().getTextureByEntity(dragon);
    }

    @Override
    public ResourceLocation getAnimationResource(T dragon) {
        return IceAndFire.id("animations/" + dragonModelName(dragon) + ".animation.json");
    }

    @Override
    public void setCustomAnimations(T dragon, long instanceId, AnimationState<T> animationState) {
        float sleepProgress = Mth.clamp(Mth.lerp(animationState.getPartialTick(), dragon.prevAnimationProgresses[1], dragon.sleepProgress) / 20.0F, 0.0F, 1.0F);
        if (!dragon.isSleeping() && sleepProgress <= 0.0F || sleepProgress >= 1.0F) return;

        for (GeoBone bone : this.getAnimationProcessor().getRegisteredBones()) {
            BoneSnapshot initialPose = bone.getInitialSnapshot();
            bone.setRotX(Mth.lerp(sleepProgress, initialPose.getRotX(), bone.getRotX()));
            bone.setRotY(Mth.lerp(sleepProgress, initialPose.getRotY(), bone.getRotY()));
            bone.setRotZ(Mth.lerp(sleepProgress, initialPose.getRotZ(), bone.getRotZ()));
            bone.setPosX(Mth.lerp(sleepProgress, initialPose.getOffsetX(), bone.getPosX()));
            bone.setPosY(Mth.lerp(sleepProgress, initialPose.getOffsetY(), bone.getPosY()));
            bone.setPosZ(Mth.lerp(sleepProgress, initialPose.getOffsetZ(), bone.getPosZ()));
            bone.setScaleX(Mth.lerp(sleepProgress, initialPose.getScaleX(), bone.getScaleX()));
            bone.setScaleY(Mth.lerp(sleepProgress, initialPose.getScaleY(), bone.getScaleY()));
            bone.setScaleZ(Mth.lerp(sleepProgress, initialPose.getScaleZ(), bone.getScaleZ()));
        }
    }

    public static String dragonModelName(DragonBaseEntity dragon) {
        if (dragon instanceof FireDragonEntity) return "firedragon";
        if (dragon instanceof IceDragonEntity) return "icedragon";
        return "lightningdragon";
    }
}
