package com.iafenvoy.iceandfire.render.model;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.SeaSerpentEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Function;

public class SeaSerpentGeoModel<T extends GeoAnimatable> extends GeoModel<T> {
    private final Function<T, ResourceLocation> textureProvider;

    public SeaSerpentGeoModel(Function<T, ResourceLocation> textureProvider) {
        this.textureProvider = textureProvider;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return IceAndFire.id("geo/seaserpent.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.textureProvider.apply(animatable);
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return IceAndFire.id("animations/seaserpent.animation.json");
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        if (!(animatable instanceof SeaSerpentEntity serpent)) return;

        float partialTick = animationState.getPartialTick();
        float bodyYaw = Mth.rotLerp(partialTick, serpent.yBodyRotO, serpent.yBodyRot);
        boolean jumping = serpent.isJumpingOutOfWater() || serpent.jumpProgress > 0.0F || serpent.jumpRot > 0.0F;
        float jumpPitch = 0.0F;
        if (jumping) {
            // GeckoLib's imported X axis is inverted relative to the original Tabula pose data.
            // Pitch the head into the vertical velocity and counter-bend the tail to form the jump arc.
            float jumpRotation = Mth.lerp(partialTick, serpent.prevJumpRot, serpent.jumpRot);
            jumpPitch = (float) serpent.getDeltaMovement().y * 4.0F * jumpRotation;
            this.rotateBone("BodyUpper", Mth.DEG_TO_RAD * 22.5F * jumpPitch, 0.0F, 0.0F);
        }

        if (!jumping)
            this.rotateBone("BodyUpper", -serpent.getXRot() * Mth.DEG_TO_RAD, 0.0F, 0.0F);
        for (int index = 1; index <= 4; index++) {
            float yaw = (serpent.getPieceYaw(index, partialTick) - bodyYaw) * Mth.DEG_TO_RAD;
            float pitch = 0.0F;
            if (!jumping)
                pitch = -serpent.getPiecePitch(index, partialTick) * Mth.DEG_TO_RAD;
            else
                pitch = -jumpPitch * Mth.DEG_TO_RAD;
            this.rotateBone("Tail" + index, pitch, yaw, 0.0F);
        }

        if (serpent.breathProgress > 0.0F) {
            float progress = Mth.clamp(serpent.breathProgress / 20.0F, 0.0F, 1.0F);
            this.rotateBone("Head", Mth.DEG_TO_RAD * -15.0F * progress, 0.0F, 0.0F);
            this.rotateBone("HeadFront", Mth.DEG_TO_RAD * -20.0F * progress, 0.0F, 0.0F);
            this.rotateBone("Jaw", Mth.DEG_TO_RAD * 60.0F * progress, 0.0F, 0.0F);
        }
    }

    private void rotateBone(String name, float x, float y, float z) {
        GeoBone bone = this.getAnimationProcessor().getBone(name);
        if (bone != null) bone.updateRotation(x, y, z);
    }
}
