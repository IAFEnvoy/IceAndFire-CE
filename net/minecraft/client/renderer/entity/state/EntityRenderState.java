package net.minecraft.client.renderer.entity.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.CrashReportCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class EntityRenderState extends net.neoforged.neoforge.client.renderstate.BaseRenderState {
    public static final int NO_OUTLINE = 0;
    public EntityType<?> entityType;
    public double x;
    public double y;
    public double z;
    public float ageInTicks;
    public float boundingBoxWidth;
    public float boundingBoxHeight;
    public float eyeHeight;
    public double distanceToCameraSq;
    public boolean isInvisible;
    public boolean isDiscrete;
    public boolean displayFireAnimation;
    public int lightCoords = 15728880;
    public int outlineColor = 0;
    public @Nullable Vec3 passengerOffset;
    public @Nullable Component nameTag;
    public @Nullable Component scoreText;
    public @Nullable Vec3 nameTagAttachment;
    public @Nullable List<EntityRenderState.LeashState> leashStates;
    public float shadowRadius;
    public final List<EntityRenderState.ShadowPiece> shadowPieces = new ArrayList<>();
    public float partialTick;

    public boolean appearsGlowing() {
        return this.outlineColor != 0;
    }

    public void fillCrashReportCategory(CrashReportCategory category) {
        category.setDetail("EntityRenderState", this.getClass().getCanonicalName());
        category.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
    }

    @OnlyIn(Dist.CLIENT)
    public static class LeashState {
        public Vec3 offset = Vec3.ZERO;
        public Vec3 start = Vec3.ZERO;
        public Vec3 end = Vec3.ZERO;
        public int startBlockLight = 0;
        public int endBlockLight = 0;
        public int startSkyLight = 15;
        public int endSkyLight = 15;
        public boolean slack = true;
    }

    @OnlyIn(Dist.CLIENT)
    public record ShadowPiece(float relativeX, float relativeY, float relativeZ, VoxelShape shapeBelow, float alpha) {
    }
}
