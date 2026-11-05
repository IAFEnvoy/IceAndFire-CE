package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.entity.EntityHydra;
import com.iafenvoy.iceandfire.entity.EntityStoneStatue;
import com.iafenvoy.iceandfire.entity.EntityTroll;
import com.iafenvoy.iceandfire.registry.IafRenderLayers;
import com.iafenvoy.iceandfire.render.entity.layer.LayerHydraHead;
import com.iafenvoy.iceandfire.render.model.ICustomStatueModel;
import com.iafenvoy.iceandfire.render.model.ModelHydraBody;
import com.iafenvoy.iceandfire.render.model.ModelStonePlayer;
import com.iafenvoy.uranus.client.model.AdvancedEntityModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.HashMap;
import java.util.Map;

public class RenderStoneStatue extends EntityRenderer<EntityStoneStatue> {
    protected static final Identifier[] DESTROY_STAGES = new Identifier[]{
            Identifier.of(Identifier.DEFAULT_NAMESPACE, "textures/block/destroy_stage_0.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_1.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_2.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_3.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_4.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_5.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_6.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_7.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_8.png"),
            Identifier.of(Identifier.DEFAULT_NAMESPACE,"textures/block/destroy_stage_9.png")};
    private final Map<String, EntityModel> modelMap = new HashMap<>();
    private final EntityModel fallbackModel;
    private final EntityRendererFactory.Context context;

    public RenderStoneStatue(EntityRendererFactory.Context context) {
        super(context);
        this.context = context;
        this.fallbackModel = new PigEntityModel<>(context.getPart(EntityModelLayers.PIG));
    }

    @Override
    public Identifier getTexture(EntityStoneStatue entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    protected void preRenderCallback(EntityStoneStatue entity, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = entity.getScaleFactor() < 0.01F ? 1F : entity.getScaleFactor();
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public void render(EntityStoneStatue entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
        EntityModel model = this.fallbackModel;

        // Get the correct model
        if (this.modelMap.get(entityIn.getTrappedEntityTypeString()) != null)
            model = this.modelMap.get(entityIn.getTrappedEntityTypeString());
        else {
            EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().renderers.get(entityIn.getTrappedEntityType());

            if (renderer instanceof FeatureRendererContext)
                model = ((FeatureRendererContext<?, ?>) renderer).getModel();
            else if (entityIn.getTrappedEntityType() == EntityType.PLAYER)
                model = new ModelStonePlayer(this.context.getPart(EntityModelLayers.PLAYER));
            this.modelMap.put(entityIn.getTrappedEntityTypeString(), model);
        }
        if (model == null) return;

        Entity fakeEntity = entityIn.getOrCreateRenderEntity();
        RenderLayer tex = IafRenderLayers.getStoneMobRenderType(200, 200);
        if (fakeEntity instanceof EntityTroll troll)
            tex = RenderLayer.getEntityCutout(troll.getTrollType().getStatueTexture());

        VertexConsumer ivertexbuilder = bufferIn.getBuffer(tex);

        matrixStackIn.push();
        float yaw = entityIn.prevYaw + (entityIn.getYaw() - entityIn.prevYaw) * partialTicks;
        model.child = entityIn.isBaby();
        model.riding = false;
        model.handSwingProgress = entityIn.getHandSwingProgress(partialTicks);
        if (model instanceof AdvancedEntityModel advancedEntityModel)
            advancedEntityModel.resetToDefaultPose();
        else if (fakeEntity != null)
            model.setAngles(fakeEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
        matrixStackIn.translate(0, 1.5F, 0);
        matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        if (model instanceof ICustomStatueModel statueModel && fakeEntity != null) {
            statueModel.renderStatue(matrixStackIn, ivertexbuilder, packedLightIn, fakeEntity);
            if (model instanceof ModelHydraBody hydraBody && fakeEntity instanceof EntityHydra hydra)
                LayerHydraHead.renderHydraHeads(hydraBody, true, matrixStackIn, bufferIn, packedLightIn, hydra, 0, 0, partialTicks, 0, 0, 0);
        } else
            model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStackIn.pop();

        if (entityIn.getCrackAmount() >= 1) {
            int i = MathHelper.clamp(entityIn.getCrackAmount() - 1, 0, DESTROY_STAGES.length - 1);
            RenderLayer crackTex = IafRenderLayers.getStoneCrackRenderType(DESTROY_STAGES[i]);
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(crackTex);
            matrixStackIn.push();
            matrixStackIn.push();
            this.preRenderCallback(entityIn, matrixStackIn, partialTicks);
            matrixStackIn.translate(0, 1.5F, 0);
            matrixStackIn.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            matrixStackIn.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
            if (model instanceof ICustomStatueModel statueModel)
                statueModel.renderStatue(matrixStackIn, ivertexbuilder2, packedLightIn, fakeEntity);
            else
                model.render(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
        //super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}
