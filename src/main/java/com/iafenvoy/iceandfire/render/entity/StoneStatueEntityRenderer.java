package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HydraEntity;
import com.iafenvoy.iceandfire.entity.StoneStatueEntity;
import com.iafenvoy.iceandfire.entity.TrollEntity;
import com.iafenvoy.iceandfire.registry.IafRenderTypes;
import com.iafenvoy.iceandfire.render.entity.feature.HydraHeadFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.HydraBodyModel;
import com.iafenvoy.iceandfire.render.model.ICustomStatueModel;
import com.iafenvoy.iceandfire.render.model.StonePlayerModel;
import com.iafenvoy.iceandfire.util.EntityDataHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/** Renders a frozen copy using the trapped entity's current model and render state. */
public class StoneStatueEntityRenderer extends EntityRenderer<StoneStatueEntity, LegacyEntityRenderState<StoneStatueEntity>> {
    protected static final Identifier[] DESTROY_STAGES = new Identifier[]{
            Identifier.withDefaultNamespace("textures/block/destroy_stage_0.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_1.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_2.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_3.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_4.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_5.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_6.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_7.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_8.png"),
            Identifier.fromNamespaceAndPath(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_9.png")};
    private final Map<String, Object> modelMap = new HashMap<>();
    private final Map<String, Entity> hollowEntityMap = new HashMap<>();
    private final EntityRendererProvider.Context context;

    public StoneStatueEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.context = context;
    }

    public @NotNull Identifier getTextureLocation(@NotNull StoneStatueEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public LegacyEntityRenderState<StoneStatueEntity> createRenderState() {
        return new LegacyEntityRenderState<>();
    }

    @Override
    public void extractRenderState(StoneStatueEntity entity, LegacyEntityRenderState<StoneStatueEntity> state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entity = entity;
    }

    private Entity getHollowEntity(StoneStatueEntity statue) {
        String typeId = statue.getTrappedEntityTypeString();
        Entity existing = this.hollowEntityMap.get(typeId);
        if (existing != null) return existing;
        if (Minecraft.getInstance().level == null) return null;
        Entity entity = statue.getTrappedEntityType().create(Minecraft.getInstance().level, EntitySpawnReason.TRIGGERED);
        if (entity == null) return null;
        try {
            EntityDataHelper.load(entity, statue.getTrappedTag());
        } catch (Exception exception) {
            IceAndFire.LOGGER.warn("Mob {} could not build statue NBT", typeId);
        }
        this.hollowEntityMap.put(typeId, entity);
        return entity;
    }

    private Object getModel(StoneStatueEntity statue, Entity fakeEntity) {
        String typeId = statue.getTrappedEntityTypeString();
        Object cached = this.modelMap.get(typeId);
        if (cached != null) return cached;
        Object model = new PigModel(this.context.bakeLayer(ModelLayers.PIG));
        if (fakeEntity != null) {
            EntityRenderer<?, ?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(fakeEntity);
            switch (renderer) {
                case RenderLayerParent<?, ?> parent -> model = parent.getModel();
                case LegacyMobRenderer<?, ?> legacyRenderer -> model = legacyRenderer.getLegacyModel();
                case LegacyEntityModelRenderer<?, ?> legacyRenderer -> model = legacyRenderer.model;
                default -> {
                }
            }
        }
        if (statue.getTrappedEntityType() == EntityType.PLAYER) model = new StonePlayerModel(this.context.bakeLayer(ModelLayers.PLAYER));
        this.modelMap.put(typeId, model);
        return model;
    }

    private static void preparePose(StoneStatueEntity statue, PoseStack poseStack, float partialTick) {
        float scale = statue.getAgeScale() < 0.01F ? 1.0F : statue.getAgeScale();
        float yaw = Mth.lerp(partialTick, statue.yRotO, statue.getYRot());
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void submitModel(Object model, Entity fakeEntity, LegacyEntityRenderState<StoneStatueEntity> state, PoseStack poseStack, SubmitNodeCollector collector, RenderType renderType) {
        if (model instanceof ICustomStatueModel statueModel && fakeEntity != null) {
            collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
                PoseStack modelStack = new PoseStack();
                modelStack.last().set(pose);
                statueModel.renderStatue(modelStack, buffer, state.lightCoords, fakeEntity);
            });
            if (model instanceof HydraBodyModel hydraBody && fakeEntity instanceof HydraEntity hydra)
                HydraHeadFeatureRenderer.submitHydraHeads(hydraBody, true, poseStack, collector, state.lightCoords, hydra, state.ageInTicks, state.outlineColor);
            return;
        }
        if (!(model instanceof EntityModel entityModel)) return;
        EntityRenderState modelState = fakeEntity == null || model instanceof StonePlayerModel
                ? state
                : Minecraft.getInstance().getEntityRenderDispatcher().extractEntity(fakeEntity, state.partialTick);
        collector.submitModel(entityModel, modelState, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
    }

    @Override
    public void submit(LegacyEntityRenderState<StoneStatueEntity> state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        StoneStatueEntity statue = state.entity;
        Entity fakeEntity = this.getHollowEntity(statue);
        Object model = this.getModel(statue, fakeEntity);
        RenderType stoneType = IafRenderTypes.getStoneMobRenderType(200, 200);
        if (fakeEntity instanceof TrollEntity troll) stoneType = RenderTypes.entityCutout(troll.getTrollType().getStatueTexture());
        poseStack.pushPose();
        preparePose(statue, poseStack, state.partialTick);
        this.submitModel(model, fakeEntity, state, poseStack, collector, stoneType);
        poseStack.popPose();
        if (statue.getCrackAmount() >= 1) {
            int stage = Mth.clamp(statue.getCrackAmount() - 1, 0, DESTROY_STAGES.length - 1);
            poseStack.pushPose();
            preparePose(statue, poseStack, state.partialTick);
            this.submitModel(model, fakeEntity, state, poseStack, collector, IafRenderTypes.getStoneCrackRenderType(DESTROY_STAGES[stage]));
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, camera);
    }
}
