package com.iafenvoy.iceandfire.mixin.client;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {
    @Inject(method = "showHitboxes", at = @At("TAIL"))
    private void iceandfire$showMultipartHitboxes(Entity entity, float partialTick, boolean serverSide, CallbackInfo ci) {
        if (!entity.isMultipartEntity() || entity instanceof EnderDragon) return;

        for (PartEntity<?> part : entity.getParts()) {
            if (part == null) continue;
            Vec3 motion = part.getPosition(partialTick).subtract(part.position());
            AABB bounds = part.getBoundingBox().move(motion);
            Gizmos.cuboid(bounds, GizmoStyle.stroke(ARGB.colorFromFloat(1.0F, 0.25F, 1.0F, 0.0F)));
        }
    }
}
