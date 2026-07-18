package com.iafenvoy.iceandfire.render.entity;

import com.google.common.collect.Maps;
import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.IafSkullType;
import com.iafenvoy.iceandfire.entity.MobSkullEntity;
import com.iafenvoy.iceandfire.render.model.*;
import com.iafenvoy.uranus.client.model.basic.BasicModelPart;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class MobSkullEntityRenderer extends EntityRenderer<MobSkullEntity> {
    private static final Map<String, ResourceLocation> SKULL_TEXTURE_CACHE = Maps.newHashMap();
    private final HippogryphModel hippogryphModel;
    private final CyclopsModel cyclopsModel;
    private final CockatriceModel cockatriceModel;
    private final StymphalianBirdModel stymphalianBirdModel;
    private final TrollModel trollModel;
    private final AmphithereModel amphithereModel;
    private final HydraHeadModel hydraModel;
    private final SeaSerpentSkullEntityRenderer seaSerpentRenderer;

    public MobSkullEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.hippogryphModel = new HippogryphModel();
        this.cyclopsModel = new CyclopsModel();
        this.cockatriceModel = new CockatriceModel();
        this.stymphalianBirdModel = new StymphalianBirdModel();
        this.trollModel = new TrollModel();
        this.amphithereModel = new AmphithereModel();
        this.seaSerpentRenderer = new SeaSerpentSkullEntityRenderer(context);
        this.hydraModel = new HydraHeadModel(0);
    }

    private static void setRotationAngles(BasicModelPart cube, float rotX) {
        cube.rotateAngleX = rotX;
        cube.rotateAngleY = (float) 0;
        cube.rotateAngleZ = (float) 0;
    }

    @Override
    public void render(@NotNull MobSkullEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        if (entity.getSkullType() == IafSkullType.SEASERPENT) {
            this.seaSerpentRenderer.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            return;
        }
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-180.0F));
        matrixStackIn.mulPose(Axis.YN.rotationDegrees(180.0F - entity.getYRot()));
        float f = 0.0625F;
        float size = 1.0F;
        matrixStackIn.scale(size, size, size);
        matrixStackIn.translate(0, entity.isOnWall() ? -0.24F : -0.12F, 0.5F);
        this.renderForEnum(entity.getSkullType(), entity.isOnWall(), matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    private void renderForEnum(IafSkullType skull, boolean onWall, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getSkullTexture(skull)));
        switch (skull) {
            case HIPPOGRYPH -> {
                matrixStackIn.translate(0, -0.0F, -0.2F);
                matrixStackIn.scale(1.2F, 1.2F, 1.2F);
                this.hippogryphModel.resetToDefaultPose();
                setRotationAngles(this.hippogryphModel.Head, onWall ? (float) Math.toRadians(50F) : (float) Math.toRadians(-5));
                this.hippogryphModel.Head.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            case CYCLOPS -> {
                matrixStackIn.translate(0, 1.8F, -0.5F);
                matrixStackIn.scale(2.25F, 2.25F, 2.25F);
                this.cyclopsModel.resetToDefaultPose();
                setRotationAngles(this.cyclopsModel.Head, onWall ? (float) Math.toRadians(50F) : 0F);
                this.cyclopsModel.Head.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            case COCKATRICE -> {
                if (onWall) matrixStackIn.translate(0, 0F, 0.35F);
                this.cockatriceModel.resetToDefaultPose();
                setRotationAngles(this.cockatriceModel.head, onWall ? (float) Math.toRadians(50F) : 0F);
                this.cockatriceModel.head.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            case STYMPHALIAN -> {
                if (!onWall) matrixStackIn.translate(0, 0F, -0.35F);
                this.stymphalianBirdModel.resetToDefaultPose();
                setRotationAngles(this.stymphalianBirdModel.HeadBase, onWall ? (float) Math.toRadians(50F) : 0F);
                this.stymphalianBirdModel.HeadBase.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            case TROLL -> {
                matrixStackIn.translate(0, 1F, -0.35F);
                if (onWall) matrixStackIn.translate(0, 0F, 0.35F);
                this.trollModel.resetToDefaultPose();
                setRotationAngles(this.trollModel.head, onWall ? (float) Math.toRadians(50F) : (float) Math.toRadians(-20));
                this.trollModel.head.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            case AMPHITHERE -> {
                matrixStackIn.translate(0, -0.2F, 0.7F);
                matrixStackIn.scale(2.0F, 2.0F, 2.0F);
                this.amphithereModel.resetToDefaultPose();
                setRotationAngles(this.amphithereModel.Head, onWall ? (float) Math.toRadians(50F) : 0F);
                this.amphithereModel.Head.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
            case HYDRA -> {
                matrixStackIn.translate(0, -0.2F, -0.1F);
                matrixStackIn.scale(2.0F, 2.0F, 2.0F);
                this.hydraModel.resetToDefaultPose();
                setRotationAngles(this.hydraModel.Head1, onWall ? (float) Math.toRadians(50F) : 0F);
                this.hydraModel.Head1.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, -1);
            }
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(MobSkullEntity entity) {
        return this.getSkullTexture(entity.getSkullType());
    }

    public ResourceLocation getSkullTexture(IafSkullType skull) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/skulls/skull_" + skull.name().toLowerCase(Locale.ROOT) + ".png");
        return SKULL_TEXTURE_CACHE.computeIfAbsent(id.toString(), k -> id);
    }

    public static ResourceLocation getSkullTexture(MobSkullEntity skull) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/skulls/skull_seaserpent.png");
        return SKULL_TEXTURE_CACHE.computeIfAbsent(id.toString(), k -> id);
    }

}
