package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
    private static final float SHADOW_POWER_FALLOFF_Y = 0.5F;
    private static final float MAX_SHADOW_RADIUS = 32.0F;
    public static final float NAMETAG_SCALE = 0.025F;
    protected final EntityRenderDispatcher entityRenderDispatcher;
    private final Font font;
    protected float shadowRadius;
    protected float shadowStrength = 1.0F;

    protected EntityRenderer(EntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getEntityRenderDispatcher();
        this.font = context.getFont();
    }

    public final int getPackedLightCoords(T entity, float partialTickTime) {
        BlockPos blockPos = BlockPos.containing(entity.getLightProbePosition(partialTickTime));
        return LightCoordsUtil.pack(this.getBlockLightLevel(entity, blockPos), this.getSkyLightLevel(entity, blockPos));
    }

    protected int getSkyLightLevel(T entity, BlockPos blockPos) {
        return entity.level().getBrightness(LightLayer.SKY, blockPos);
    }

    protected int getBlockLightLevel(T entity, BlockPos blockPos) {
        return entity.isOnFire() ? 15 : entity.level().getBrightness(LightLayer.BLOCK, blockPos);
    }

    public boolean shouldRender(T entity, Frustum culler, double camX, double camY, double camZ) {
        if (!entity.shouldRender(camX, camY, camZ)) {
            return false;
        } else if (!this.affectedByCulling(entity)) {
            return true;
        } else {
            AABB boundingBox = this.getBoundingBoxForCulling(entity).inflate(0.5);
            if (boundingBox.hasNaN() || boundingBox.getSize() == 0.0) {
                boundingBox = new AABB(
                    entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0
                );
            }

            if (culler.isVisible(boundingBox)) {
                return true;
            } else {
                if (entity instanceof Leashable leashable) {
                    Entity leashHolder = leashable.getLeashHolder();
                    if (leashHolder != null) {
                        AABB leasherBox = this.entityRenderDispatcher.getRenderer(leashHolder).getBoundingBoxForCulling(leashHolder);
                        return culler.isVisible(leasherBox) || culler.isVisible(boundingBox.minmax(leasherBox));
                    }
                }

                return false;
            }
        }
    }

    protected AABB getBoundingBoxForCulling(T entity) {
        return entity.getBoundingBox();
    }

    protected boolean affectedByCulling(T entity) {
        return true;
    }

    public Vec3 getRenderOffset(S state) {
        return state.passengerOffset != null ? state.passengerOffset : Vec3.ZERO;
    }

    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.leashStates != null) {
            for (EntityRenderState.LeashState leashState : state.leashStates) {
                submitNodeCollector.submitLeash(poseStack, leashState);
            }
        }

        this.submitNameDisplay(state, poseStack, submitNodeCollector, camera);
    }

    protected boolean shouldShowName(T entity, double distanceToCameraSq) {
        return entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity;
    }

    public Font getFont() {
        return this.font;
    }

    protected void submitNameDisplay(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        this.submitNameDisplay(state, poseStack, submitNodeCollector, camera, 0);
    }

    protected final <S extends EntityRenderState> void submitNameDisplay(
        S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, int offset
    ) {
        poseStack.pushPose();
        if (state.scoreText != null) {
            var event = new net.neoforged.neoforge.client.event.RenderNameTagEvent.DoRender(state, state.scoreText, this, poseStack, submitNodeCollector, camera, state.partialTick);
            if (!net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled())
            submitNodeCollector.submitNameTag(
                poseStack, state.nameTagAttachment, offset, state.scoreText, !state.isDiscrete, state.lightCoords, state.distanceToCameraSq, camera
            );
            poseStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
        }

        if (state.nameTag != null) {
            var event = new net.neoforged.neoforge.client.event.RenderNameTagEvent.DoRender(state, state.nameTag, this, poseStack, submitNodeCollector, camera, state.partialTick);
            if (!net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled())
            submitNodeCollector.submitNameTag(
                poseStack, state.nameTagAttachment, offset, state.nameTag, !state.isDiscrete, state.lightCoords, state.distanceToCameraSq, camera
            );
        }

        poseStack.popPose();
    }

    protected @Nullable Component getNameTag(T entity) {
        return entity.getDisplayName();
    }

    protected float getShadowRadius(S state) {
        return this.shadowRadius;
    }

    protected float getShadowStrength(S state) {
        return this.shadowStrength;
    }

    public abstract S createRenderState();

    public final S createRenderState(T entity, float partialTicks) {
        S state = this.createRenderState();
        this.extractRenderState(entity, state, partialTicks);
        this.finalizeRenderState(entity, state);
        net.neoforged.neoforge.client.renderstate.RenderStateExtensions.onUpdateEntityRenderState(this, entity, state);
        return state;
    }

    public void extractRenderState(T entity, S state, float partialTicks) {
        state.entityType = entity.getType();
        state.x = Mth.lerp((double)partialTicks, entity.xOld, entity.getX());
        state.y = Mth.lerp((double)partialTicks, entity.yOld, entity.getY());
        state.z = Mth.lerp((double)partialTicks, entity.zOld, entity.getZ());
        state.isInvisible = entity.isInvisible();
        state.partialTick = partialTicks;
        state.ageInTicks = entity.tickCount + partialTicks;
        state.boundingBoxWidth = entity.getBbWidth();
        state.boundingBoxHeight = entity.getBbHeight();
        state.eyeHeight = entity.getEyeHeight();
        if (entity.isPassenger()
            && entity.getVehicle() instanceof AbstractMinecart minecart
            && minecart.getBehavior() instanceof NewMinecartBehavior behavior
            && behavior.cartHasPosRotLerp()) {
            double cartLerpX = Mth.lerp((double)partialTicks, minecart.xOld, minecart.getX());
            double cartLerpY = Mth.lerp((double)partialTicks, minecart.yOld, minecart.getY());
            double cartLerpZ = Mth.lerp((double)partialTicks, minecart.zOld, minecart.getZ());
            state.passengerOffset = behavior.getCartLerpPosition(partialTicks).subtract(new Vec3(cartLerpX, cartLerpY, cartLerpZ));
        } else {
            state.passengerOffset = null;
        }

        if (this.entityRenderDispatcher.camera != null) {
            state.distanceToCameraSq = this.entityRenderDispatcher.distanceToSqr(entity);
            var event = new net.neoforged.neoforge.client.event.RenderNameTagEvent.CanRender(entity, state, this.getNameTag(entity), this, partialTicks);
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
            boolean shouldShowName = event.canRender().isTrue() || (event.canRender().isDefault() && state.distanceToCameraSq < 4096.0 && this.shouldShowName(entity, state.distanceToCameraSq));
            if (shouldShowName) {
                state.nameTag = event.getContent();
                state.nameTagAttachment = entity.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, entity.getYRot(partialTicks));
            } else {
                state.nameTag = null;
            }

            if (state.distanceToCameraSq < 100.0) {
                state.scoreText = entity.belowNameDisplay();
            } else {
                state.scoreText = null;
            }
        }

        label77: {
            state.isDiscrete = entity.isDiscrete();
            Level level = entity.level();
            if (entity instanceof Leashable leashable) {
                Entity entityYRot = leashable.getLeashHolder();
                if (entityYRot instanceof Entity) {
                    float entityYRotx = entity.getPreciseBodyRotation(partialTicks) * (float) (Math.PI / 180.0);
                    Vec3 attachOffset = leashable.getLeashOffset(partialTicks);
                    BlockPos entityEyePos = BlockPos.containing(entity.getEyePosition(partialTicks));
                    BlockPos roperEyePos = BlockPos.containing(entityYRot.getEyePosition(partialTicks));
                    int startBlockLight = this.getBlockLightLevel(entity, entityEyePos);
                    int endBlockLight = this.entityRenderDispatcher.getRenderer(entityYRot).getBlockLightLevel(entityYRot, roperEyePos);
                    int startSkyLight = level.getBrightness(LightLayer.SKY, entityEyePos);
                    int endSkyLight = level.getBrightness(LightLayer.SKY, roperEyePos);
                    boolean quadConnection = entityYRot.supportQuadLeashAsHolder() && leashable.supportQuadLeash();
                    int leashCount = quadConnection ? 4 : 1;
                    if (state.leashStates == null || state.leashStates.size() != leashCount) {
                        state.leashStates = new ArrayList<>(leashCount);

                        for (int i = 0; i < leashCount; i++) {
                            state.leashStates.add(new EntityRenderState.LeashState());
                        }
                    }

                    if (quadConnection) {
                        float roperYRot = entityYRot.getPreciseBodyRotation(partialTicks) * (float) (Math.PI / 180.0);
                        Vec3 holderPos = entityYRot.getPosition(partialTicks);
                        Vec3[] leashableAttachmentPoints = leashable.getQuadLeashOffsets();
                        Vec3[] roperAttachmentPoints = entityYRot.getQuadLeashHolderOffsets();
                        int i = 0;

                        while (true) {
                            if (i >= leashCount) {
                                break label77;
                            }

                            EntityRenderState.LeashState leashState = state.leashStates.get(i);
                            leashState.offset = leashableAttachmentPoints[i].yRot(-entityYRotx);
                            leashState.start = entity.getPosition(partialTicks).add(leashState.offset);
                            leashState.end = holderPos.add(roperAttachmentPoints[i].yRot(-roperYRot));
                            leashState.startBlockLight = startBlockLight;
                            leashState.endBlockLight = endBlockLight;
                            leashState.startSkyLight = startSkyLight;
                            leashState.endSkyLight = endSkyLight;
                            leashState.slack = false;
                            i++;
                        }
                    } else {
                        Vec3 rotatedAttachOffset = attachOffset.yRot(-entityYRotx);
                        EntityRenderState.LeashState leashState = state.leashStates.getFirst();
                        leashState.offset = rotatedAttachOffset;
                        leashState.start = entity.getPosition(partialTicks).add(rotatedAttachOffset);
                        leashState.end = entityYRot.getRopeHoldPosition(partialTicks);
                        leashState.startBlockLight = startBlockLight;
                        leashState.endBlockLight = endBlockLight;
                        leashState.startSkyLight = startSkyLight;
                        leashState.endSkyLight = endSkyLight;
                        break label77;
                    }
                }
            }

            state.leashStates = null;
        }

        state.displayFireAnimation = entity.displayFireAnimation();
        Minecraft minecraft = Minecraft.getInstance();
        boolean appearsGlowing = minecraft.shouldEntityAppearGlowing(entity);
        state.outlineColor = appearsGlowing ? ARGB.opaque(entity.getTeamColor()) : 0;
        state.lightCoords = this.getPackedLightCoords(entity, partialTicks);
    }

    protected void finalizeRenderState(T entity, S state) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = entity.level();
        this.extractShadow(state, minecraft, level);
    }

    private void extractShadow(S state, Minecraft minecraft, Level level) {
        state.shadowPieces.clear();
        if (minecraft.options.entityShadows().get() && !state.isInvisible) {
            float shadowRadius = Math.min(this.getShadowRadius(state), 32.0F);
            state.shadowRadius = shadowRadius;
            if (shadowRadius > 0.0F) {
                double distSq = state.distanceToCameraSq;
                float pow = (float)((1.0 - distSq / 256.0) * this.getShadowStrength(state));
                if (pow > 0.0F) {
                    int x0 = Mth.floor(state.x - shadowRadius);
                    int x1 = Mth.floor(state.x + shadowRadius);
                    int z0 = Mth.floor(state.z - shadowRadius);
                    int z1 = Mth.floor(state.z + shadowRadius);
                    float depth = Math.min(pow / 0.5F - 1.0F, shadowRadius);
                    int y0 = Mth.floor(state.y - depth);
                    int y1 = Mth.floor(state.y);
                    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                    for (int z = z0; z <= z1; z++) {
                        for (int x = x0; x <= x1; x++) {
                            pos.set(x, 0, z);
                            ChunkAccess chunk = level.getChunk(pos);

                            for (int y = y0; y <= y1; y++) {
                                pos.setY(y);
                                this.extractShadowPiece(state, level, pow, pos, chunk);
                            }
                        }
                    }
                }
            }
        } else {
            state.shadowRadius = 0.0F;
        }
    }

    private void extractShadowPiece(S state, Level level, float pow, BlockPos.MutableBlockPos pos, ChunkAccess chunk) {
        float powerAtDepth = pow - (float)(state.y - pos.getY()) * 0.5F;
        BlockPos belowPos = pos.below();
        BlockState belowState = chunk.getBlockState(belowPos);
        if (belowState.getRenderShape() != RenderShape.INVISIBLE) {
            int brightness = level.getMaxLocalRawBrightness(pos);
            if (brightness > 3) {
                if (belowState.isCollisionShapeFullBlock(chunk, belowPos)) {
                    VoxelShape belowShape = belowState.getShape(chunk, belowPos);
                    if (!belowShape.isEmpty()) {
                        float alpha = Mth.clamp(powerAtDepth * 0.5F * Lightmap.getBrightness(level.dimensionType(), brightness), 0.0F, 1.0F);
                        float relativeX = (float)(pos.getX() - state.x);
                        float relativeY = (float)(pos.getY() - state.y);
                        float relativeZ = (float)(pos.getZ() - state.z);
                        state.shadowPieces.add(new EntityRenderState.ShadowPiece(relativeX, relativeY, relativeZ, belowShape, alpha));
                    }
                }
            }
        }
    }

    private static @Nullable Entity getServerSideEntity(Entity entity) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            ServerLevel level = server.getLevel(entity.level().dimension());
            if (level != null) {
                return level.getEntity(entity.getId());
            }
        }

        return null;
    }
}
