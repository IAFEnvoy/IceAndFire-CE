package com.iafenvoy.iceandfire.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Mob;

/** A render-state bridge for Uranus model features that require the live entity. */
@FunctionalInterface
public interface LegacyEntityFeature<T extends Mob> {
    void submit(T entity, float partialTick, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, int lightCoords, int outlineColor);
}
