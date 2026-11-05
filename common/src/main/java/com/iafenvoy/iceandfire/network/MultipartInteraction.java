package com.iafenvoy.iceandfire.network;

import com.iafenvoy.iceandfire.entity.EntityMultipartPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class MultipartInteraction {
    private MultipartInteraction() {}

    public static boolean canInteract(PlayerEntity player, EntityMultipartPart part) {
        Entity parent = part.getParent();
        if (player.isSpectator() || !player.isAlive() || part.isRemoved() || parent == null
                || !parent.isAlive() || part.getWorld() != player.getWorld() || !player.canSee(part)) return false;
        Vec3d eye = player.getEyePos();
        var box = part.getBoundingBox();
        double reach = player.isCreative() ? 5 : 3;
        return eye.squaredDistanceTo(
                MathHelper.clamp(eye.x, box.minX, box.maxX),
                MathHelper.clamp(eye.y, box.minY, box.maxY),
                MathHelper.clamp(eye.z, box.minZ, box.maxZ)) <= reach * reach;
    }
}
