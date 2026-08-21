package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Small bridge for Uranus models that still animate from live entities.
 */
public abstract class LegacyMobRenderer<T extends Mob, M extends AdvancedEntityModel<T>> extends EntityRenderer<T, LegacyEntityRenderState<T>> {
    protected M model;
    private final Supplier<? extends M> modelSupplier;
    private boolean modelInitialized;
    private final List<LegacyEntityFeature<T>> legacyFeatures = new ArrayList<>();

    protected LegacyMobRenderer(EntityRendererProvider.Context context, M model, float shadowRadius) {
        this(context, () -> model, shadowRadius);
    }

    protected LegacyMobRenderer(EntityRendererProvider.Context context, Supplier<? extends M> modelSupplier, float shadowRadius) {
        super(context);
        this.modelSupplier = modelSupplier;
        this.model = modelSupplier.get();
        this.modelInitialized = this.model != null;
        this.shadowRadius = shadowRadius;
    }

    /**
     * Called once when a resource-backed model becomes available after startup reload.
     */
    protected void onModelAvailable() {
    }

    private void ensureModel() {
        if (this.model == null)
            this.model = this.modelSupplier.get();
        if (this.model != null && !this.modelInitialized) {
            this.modelInitialized = true;
            this.onModelAvailable();
        }
    }

    protected void scale(T entity, PoseStack poseStack, float partialTick) {
    }

    public abstract Identifier getTextureLocation(T entity);

    public final M getLegacyModel() {
        this.ensureModel();
        return this.model;
    }

    public final void addLayer(LegacyEntityFeature<T> feature) {
        this.legacyFeatures.add(feature);
    }

    @Override
    public LegacyEntityRenderState<T> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(T entity, LegacyEntityRenderState<T> state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.entity = entity;
        state.hasRedOverlay = entity.hurtTime > 0 || entity.deathTime > 0;
        if (entity.isAlive()) {
            state.walkAnimationPos = entity.walkAnimation.position(partialTicks);
            state.walkAnimationSpeed = entity.walkAnimation.speed(partialTicks);
        } else {
            state.walkAnimationPos = 0.0F;
            state.walkAnimationSpeed = 0.0F;
        }
    }

    @Override
    public void submit(LegacyEntityRenderState<T> state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        T entity = state.entity;
        this.ensureModel();
        if (this.model == null)
            return;
        poseStack.pushPose();
        // Match LivingEntityRenderer's model origin before handing rendering to the legacy model.
        float bodyRot = Mth.rotLerp(state.partialTick, entity.yBodyRotO, entity.yBodyRot);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, poseStack, state.partialTick);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        this.model.setupAnim(entity, state.walkAnimationPos, state.walkAnimationSpeed, state.ageInTicks, 0.0F, 0.0F);
        ModelPose modelPose = ModelPose.capture(this.model);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(this.getTextureLocation(entity), false), (pose, buffer) -> {
            modelPose.apply();
            PoseStack modelStack = new PoseStack();
            modelStack.last().set(pose);
            this.model.renderToBuffer(modelStack, buffer, state.lightCoords, OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(state.hasRedOverlay)), state.outlineColor == 0 ? -1 : state.outlineColor);
        });
        for (LegacyEntityFeature<T> feature : this.legacyFeatures)
            feature.submit(entity, state.partialTick, poseStack, collector, camera, state.lightCoords, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    public static final class ModelPose {
        private final List<ModelPartPose> parts;

        private ModelPose(List<ModelPartPose> parts) {
            this.parts = parts;
        }

        public static ModelPose capture(AdvancedEntityModel<?> model) {
            List<ModelPartPose> parts = new ArrayList<>();
            for (AdvancedModelBox part : model.getAllParts())
                parts.add(new ModelPartPose(part));
            return new ModelPose(parts);
        }

        public void apply() {
            this.parts.forEach(ModelPartPose::apply);
        }
    }

    private static final class ModelPartPose {
        private final AdvancedModelBox part;
        private final float rotationPointX, rotationPointY, rotationPointZ;
        private final float rotateAngleX, rotateAngleY, rotateAngleZ;
        private final float scaleX, scaleY, scaleZ;
        private final float offsetX, offsetY, offsetZ;
        private final boolean showModel;

        private ModelPartPose(AdvancedModelBox part) {
            this.part = part;
            this.rotationPointX = part.rotationPointX;
            this.rotationPointY = part.rotationPointY;
            this.rotationPointZ = part.rotationPointZ;
            this.rotateAngleX = part.rotateAngleX;
            this.rotateAngleY = part.rotateAngleY;
            this.rotateAngleZ = part.rotateAngleZ;
            this.scaleX = part.scaleX;
            this.scaleY = part.scaleY;
            this.scaleZ = part.scaleZ;
            this.offsetX = part.offsetX;
            this.offsetY = part.offsetY;
            this.offsetZ = part.offsetZ;
            this.showModel = part.showModel;
        }

        private void apply() {
            this.part.rotationPointX = this.rotationPointX;
            this.part.rotationPointY = this.rotationPointY;
            this.part.rotationPointZ = this.rotationPointZ;
            this.part.rotateAngleX = this.rotateAngleX;
            this.part.rotateAngleY = this.rotateAngleY;
            this.part.rotateAngleZ = this.rotateAngleZ;
            this.part.scaleX = this.scaleX;
            this.part.scaleY = this.scaleY;
            this.part.scaleZ = this.scaleZ;
            this.part.offsetX = this.offsetX;
            this.part.offsetY = this.offsetY;
            this.part.offsetZ = this.offsetZ;
            this.part.showModel = this.showModel;
        }
    }
}
