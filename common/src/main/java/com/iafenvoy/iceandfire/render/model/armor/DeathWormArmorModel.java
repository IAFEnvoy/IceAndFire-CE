package com.iafenvoy.iceandfire.render.model.armor;

import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class DeathWormArmorModel extends ArmorModelBase {
    private static final ModelPart INNER_MODEL = getModelData(Dilation.NONE.add(INNER_MODEL_OFFSET), 0.0F).getRoot().createPart(64, 64);
    private static final ModelPart OUTER_MODEL = getModelData(Dilation.NONE.add(OUTER_MODEL_OFFSET), 0.0F).getRoot().createPart(64, 64);

    public DeathWormArmorModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static ModelData getModelData(Dilation deformation, float offset) {
        ModelData modelData = BipedEntityModel.getModelData(deformation, offset);
        ModelPartData root = modelData.getRoot();

        root.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation), ModelTransform.pivot(-1.9F, 12.0F + offset, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation), ModelTransform.pivot(1.9F, 12.0F + offset, 0.0F));

        root.getChild("right_arm").addChild("spineR1", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.7F, -0.5F, 1, 3, 1), ModelTransform.of(-1.0F, -2.7F, 0.0F, -0.4914847173616032F, 0.0F, 0.0F));
        root.getChild("left_arm").addChild("spineL1", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.7F, -0.5F, 1, 3, 1), ModelTransform.of(1.0F, -2.7F, 0.0F, -0.4914847173616032F, 0.0F, 0.0F));

        root.getChild("right_arm").addChild("spineR2", ModelPartBuilder.create().uv(32, 40).cuboid(-0.6F, -1.7F, -0.5F, 1, 2, 1), ModelTransform.of(-2.5F, -1.6F, 0.0F, -0.4914847173616032F, 0.0F, 0.0F));
        root.getChild("left_arm").addChild("spineL2", ModelPartBuilder.create().uv(32, 40).cuboid(-0.4F, -1.7F, -0.5F, 1, 2, 1), ModelTransform.of(2.5F, -1.6F, 0.0F, -0.4914847173616032F, 0.0F, 0.0F));

        root.getChild("hat").addChild("spineH1", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.7F, -0.5F, 1, 3, 1), ModelTransform.of(0.0F, -9.0F, -3.0F, -0.4914847173616032F, 0.0F, 0.0F));
        root.getChild("hat").addChild("spineH2", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -2.7F, -0.5F, 1, 4, 1), ModelTransform.of(0.0F, -9.0F, 0.0F, -0.4914847173616032F, 0.0F, 0.0F));
        root.getChild("hat").addChild("spineH3", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.7F, -0.5F, 1, 3, 1), ModelTransform.of(0.0F, -9.0F, 3.0F, -0.8651597102135892F, 0.0F, 0.0F));
        root.getChild("hat").addChild("spineH4", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -2.7F, -0.5F, 1, 4, 1), ModelTransform.of(0.0F, -8.0F, 5.0F, -1.5481070465189704F, 0.0F, 0.0F));
        root.getChild("hat").addChild("spineH5", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.7F, -0.5F, 1, 3, 1), ModelTransform.of(0.0F, -6.0F, 5.0F, -1.8212510744560826F, 0.0F, 0.0F));
        root.getChild("hat").addChild("spineH6", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -2.7F, -0.5F, 1, 5, 1), ModelTransform.of(0.0F, -3.5F, 5.0F, -2.0032889154390916F, 0.0F, 0.0F));
        root.getChild("hat").addChild("spineH7", ModelPartBuilder.create().uv(32, 40).cuboid(-0.5F, -1.7F, -0.5F, 1, 3, 1), ModelTransform.of(0.0F, -1.3F, 4.5F, -2.0032889154390916F, 0.0F, 0.0F));

        return modelData;
    }

    public static ModelPart getBakedModel(boolean inner) {
        return inner ? INNER_MODEL : OUTER_MODEL;
    }
}
