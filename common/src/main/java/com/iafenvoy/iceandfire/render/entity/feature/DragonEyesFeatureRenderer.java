package com.iafenvoy.iceandfire.render.entity.feature;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.entity.DragonBaseEntity;
import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.entity.LightningDragonEntity;
import com.iafenvoy.uranus.client.model.AdvancedModelBox;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.util.TabulaModelHandlerHelper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragonEyesFeatureRenderer<T extends DragonBaseEntity> extends FeatureRenderer<T, TabulaModel<T>> {
    private TabulaModel<DragonBaseEntity> fireHead;
    private TabulaModel<DragonBaseEntity> iceHead;
    private TabulaModel<DragonBaseEntity> lightningHead;

    public DragonEyesFeatureRenderer(MobEntityRenderer<T, TabulaModel<T>> renderIn) {
        super(renderIn);
        try {
            this.fireHead = this.onlyKeepCubes(TabulaModelHandlerHelper.getModel(Identifier.of(IceAndFire.MOD_ID, "firedragon/firedragon_ground"), null), Collections.singletonList("HeadFront"));
            this.iceHead = this.onlyKeepCubes(TabulaModelHandlerHelper.getModel(Identifier.of(IceAndFire.MOD_ID, "icedragon/icedragon_ground"), null), Collections.singletonList("HeadFront"));
            this.lightningHead = this.onlyKeepCubes(TabulaModelHandlerHelper.getModel(Identifier.of(IceAndFire.MOD_ID, "lightningdragon/lightningdragon_ground"), null), Collections.singletonList("HeadFront"));
        } catch (Exception e) {
            IceAndFire.LOGGER.error(e);
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, DragonBaseEntity dragon, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (dragon.shouldRenderEyes()) {
            RenderLayer eyes = RenderLayer.getEyes(DragonColor.getById(dragon.getVariant()).getEyesTexture(dragon.getDragonStage()));
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(eyes);
            if (dragon instanceof LightningDragonEntity && this.lightningHead != null) {
                this.copyPositions(this.lightningHead, this.getContextModel());
                this.lightningHead.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            } else if (dragon instanceof IceDragonEntity && this.iceHead != null) {
                this.copyPositions(this.iceHead, this.getContextModel());
                this.iceHead.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            } else if (this.fireHead != null) {
                this.copyPositions(this.fireHead, this.getContextModel());
                this.fireHead.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
            }
            //Fallback method
            else
                this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV, -1);
        }
    }

    @Override
    protected Identifier getTexture(DragonBaseEntity entityIn) {
        return null;
    }

    //TODO: do this with hideable/visble/showModel stuff instead
    //Removes all cubes except the cube names specified by the string list and their parents
    //We need to keep the parents to correctly render the head position
    private TabulaModel<DragonBaseEntity> onlyKeepCubes(TabulaModel<DragonBaseEntity> model, List<String> strings) {
        List<AdvancedModelBox> keepCubes = new ArrayList<>();
        for (String str : strings) {
            AdvancedModelBox cube = model.getCube(str);
            keepCubes.add(cube);
            while (cube.getParent() != null) {
                keepCubes.add(cube.getParent());
                cube = cube.getParent();
            }
        }
        this.removeChildren(model, keepCubes);
        model.getCubes().values().removeIf(advancedModelBox -> !keepCubes.contains(advancedModelBox));
        return model;
    }

    private void removeChildren(TabulaModel<DragonBaseEntity> model, List<AdvancedModelBox> keepCubes) {
        model.getRootBox().forEach(modelRenderer -> {
            modelRenderer.childModels.removeIf(child -> !keepCubes.contains(child));
            modelRenderer.childModels.forEach(childModel -> this.removeChildren((AdvancedModelBox) childModel, keepCubes));
        });
    }

    private void removeChildren(AdvancedModelBox modelBox, List<AdvancedModelBox> keepCubes) {
        modelBox.childModels.removeIf(modelRenderer -> !keepCubes.contains(modelRenderer));
        modelBox.childModels.forEach(modelRenderer -> this.removeChildren((AdvancedModelBox) modelRenderer, keepCubes));
    }

    public boolean isAngleEqual(AdvancedModelBox original, AdvancedModelBox pose) {
        return pose != null && pose.rotateAngleX == original.rotateAngleX && pose.rotateAngleY == original.rotateAngleY && pose.rotateAngleZ == original.rotateAngleZ;
    }

    public boolean isPositionEqual(AdvancedModelBox original, AdvancedModelBox pose) {
        return pose.rotationPointX == original.rotationPointX && pose.rotationPointY == original.rotationPointY && pose.rotationPointZ == original.rotationPointZ;
    }

    public void copyPositions(TabulaModel<DragonBaseEntity> model, TabulaModel<T> modelTo) {
        for (AdvancedModelBox cube : model.getCubes().values()) {
            AdvancedModelBox modelToCube = modelTo.getCube(cube.boxName);
            if (!this.isAngleEqual(cube, modelToCube)) {
                cube.rotateAngleX = modelToCube.rotateAngleX;
                cube.rotateAngleY = modelToCube.rotateAngleY;
                cube.rotateAngleZ = modelToCube.rotateAngleZ;
            }
            if (!this.isPositionEqual(cube, modelToCube)) {
                cube.rotationPointX = modelToCube.rotationPointX;
                cube.rotationPointY = modelToCube.rotationPointY;
                cube.rotationPointZ = modelToCube.rotationPointZ;
            }
        }
    }
}