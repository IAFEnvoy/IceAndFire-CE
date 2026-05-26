package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.HydraEntity;
import com.iafenvoy.iceandfire.entity.StoneStatueEntity;
import com.iafenvoy.iceandfire.entity.TrollEntity;
import com.iafenvoy.iceandfire.registry.IafRenderLayers;
import com.iafenvoy.iceandfire.render.entity.feature.HydraHeadFeatureRenderer;
import com.iafenvoy.iceandfire.render.model.HydraBodyModel;
import com.iafenvoy.iceandfire.render.model.ICustomStatueModel;
import com.iafenvoy.iceandfire.render.model.StonePlayerModel;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StoneStatueEntityRenderer extends EntityRenderer<StoneStatueEntity> {
    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{
            ResourceLocation.withDefaultNamespace("textures/block/destroy_stage_0.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_1.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_2.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_3.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_4.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_5.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_6.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_7.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_8.png"),
            ResourceLocation.fromNamespaceAndPath(ResourceLocation.DEFAULT_NAMESPACE, "textures/block/destroy_stage_9.png")};
    private final Map<String, EntityModel> modelMap = new HashMap<>();
    private final Map<String, Entity> hollowEntityMap = new HashMap<>();
    private final EntityRendererProvider.Context context;

    public StoneStatueEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(StoneStatueEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    protected void preRenderCallback(StoneStatueEntity entity, PoseStack matrixStackIn, float partialTickTime) {
        float scale = entity.getAgeScale() < 0.01F ? 1F : entity.getAgeScale();
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public void render(StoneStatueEntity entityIn, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        EntityModel model = new PigModel<>(this.context.bakeLayer(ModelLayers.PIG));

        // Get the correct model
        if (this.modelMap.get(entityIn.getTrappedEntityTypeString()) != null)
            model = this.modelMap.get(entityIn.getTrappedEntityTypeString());
        else {
            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityIn.getTrappedEntityType());

            if (renderer instanceof RenderLayerParent)
                model = ((RenderLayerParent<?, ?>) renderer).getModel();
            else if (entityIn.getTrappedEntityType() == EntityType.PLAYER)
                model = new StonePlayerModel(this.context.bakeLayer(ModelLayers.PLAYER));
            this.modelMap.put(entityIn.getTrappedEntityTypeString(), model);
        }
        if (model == null) return;

        Entity fakeEntity;
        if (this.hollowEntityMap.get(entityIn.getTrappedEntityTypeString()) == null) {
            fakeEntity = entityIn.getTrappedEntityType().create(Minecraft.getInstance().level);
            if (fakeEntity != null) {
                try {
                    fakeEntity.load(entityIn.getTrappedTag());
                } catch (Exception e) {
                    IceAndFire.LOGGER.warn("Mob {} could not build statue NBT", entityIn.getTrappedEntityTypeString());
                }
                this.hollowEntityMap.putIfAbsent(entityIn.getTrappedEntityTypeString(), fakeEntity);
            }
        } else
            fakeEntity = this.hollowEntityMap.get(entityIn.getTrappedEntityTypeString());
        RenderType tex = IafRenderLayers.getStoneMobRenderType(200, 200);
        if (fakeEntity instanceof TrollEntity troll)
            tex = RenderType.entityCutout(troll.getTrollType().getStatueTexture());

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(tex);

        matrixStackIn.pushPose();
        float yaw = entityIn.yRotO + (entityIn.getYRot() - entityIn.yRotO) * partialTicks;
        model.young = entityIn.isBaby();
        model.riding = false;
        model.attackTime = entityIn.getAttackAnim(partialTicks);
        if (model instanceof AdvancedEntityModel advancedEntityModel)
            advancedEntityModel.resetToDefaultPose();
        else if (fakeEntity != null)
            model.setupAnim(fakeEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0, 1.5F, 0);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw));
        if (model instanceof ICustomStatueModel statueModel && fakeEntity != null) {
            statueModel.renderStatue(matrixStackIn, ivertexbuilder, packedLightIn, fakeEntity);
            if (model instanceof HydraBodyModel hydraBody && fakeEntity instanceof HydraEntity hydra)
                HydraHeadFeatureRenderer.renderHydraHeads(hydraBody, true, matrixStackIn, bufferIn, packedLightIn, hydra, 0, 0, partialTicks, 0, 0, 0);
        } else
            model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);

        matrixStackIn.popPose();

        if (entityIn.getCrackAmount() >= 1) {
            int i = Mth.clamp(entityIn.getCrackAmount() - 1, 0, DESTROY_STAGES.length - 1);
            RenderType crackTex = IafRenderLayers.getStoneCrackRenderType(DESTROY_STAGES[i]);
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(crackTex);
            matrixStackIn.pushPose();
            matrixStackIn.pushPose();
            this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
            matrixStackIn.translate(0, 1.5F, 0);
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw));
            if (model instanceof ICustomStatueModel statueModel)
                statueModel.renderStatue(matrixStackIn, ivertexbuilder2, packedLightIn, fakeEntity);
            else
                model.renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            matrixStackIn.popPose();
            matrixStackIn.popPose();
        }
        //super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}