package com.iafenvoy.iceandfire.render.model.armor;

import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class FireDragonScaleArmorModel extends ArmorModelBase {
    private static final ModelPart INNER_MODEL = getModelData(Dilation.NONE.add(INNER_MODEL_OFFSET), 0.0F).getRoot().createPart(64, 64);
    private static final ModelPart OUTER_MODEL = getModelData(Dilation.NONE.add(OUTER_MODEL_OFFSET), 0.0F).getRoot().createPart(64, 64);

    public FireDragonScaleArmorModel(boolean inner) {
        super(getBakedModel(inner));
    }

    public static ModelData getModelData(Dilation deformation, float offset) {
        ModelData modelData = BipedEntityModel.getModelData(deformation, offset);
        ModelPartData root = modelData.getRoot();

        root.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation), ModelTransform.pivot(-1.9F, 12.0F + offset, 0.0F));
        root.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deformation), ModelTransform.pivot(1.9F, 12.0F + offset, 0.0F));
        root.getChild("right_leg").addChild("RightLegSpike3", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, 0.0F, 1, 3, 1), ModelTransform.of(-0.8F, 0.0F, -0.8F, -1.2217304763960306F, 1.2217304763960306F, -0.17453292519943295F));
        root.getChild("right_leg").addChild("RightLegSpike2", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, 0.0F, 1, 3, 1), ModelTransform.of(-0.7F, 3.6F, -0.4F, -1.4114477660878142F, 0.0F, 0.0F));
        root.getChild("right_leg").addChild("RightLegSpike", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, 0.0F, 1, 3, 1), ModelTransform.of(0.0F, 5.0F, 0.4F, -1.4114477660878142F, 0.0F, 0.0F));
        root.getChild("left_leg").addChild("LeftLegSpike3", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, 0.0F, 1, 3, 1), ModelTransform.of(0.8F, 0.0F, -0.8F, -1.2217304763960306F, -1.2217304763960306F, 0.17453292519943295F));
        root.getChild("left_leg").addChild("LeftLegSpike2", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, 0.0F, 1, 3, 1), ModelTransform.of(0.7F, 3.6F, -0.4F, -1.4114477660878142F, 0.0F, 0.0F));
        root.getChild("left_leg").addChild("LeftLegSpike", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, 0.0F, 1, 3, 1), ModelTransform.of(0.0F, 5.0F, 0.4F, -1.4114477660878142F, 0.0F, 0.0F));

        root.getChild("head").addChild("HornR", ModelPartBuilder.create().uv(48, 44).cuboid(-1.0F, -0.5F, 0.0F, 2, 3, 5), ModelTransform.of(-3.6F, -8.0F, 1.0F, 0.3141592653589793F, -0.33161255787892263F, -0.19198621771937624F));
        root.getChild("head").addChild("HornL", ModelPartBuilder.create().uv(48, 44).mirrored().cuboid(-1.0F, -0.5F, 0.0F, 2, 3, 5), ModelTransform.of(3.6F, -8.0F, 1.0F, 0.3141592653589793F, 0.33161255787892263F, 0.19198621771937624F));

        root.getChild("head").addChild("HornR3", ModelPartBuilder.create().uv(46, 36).mirrored().cuboid(-0.5F, -0.8F, 0.0F, 1, 2, 5), ModelTransform.of(-4.0F, -4.0F, 0.7F, -0.06981317007977318F, -0.4886921905584123F, -0.08726646259971647F));
        root.getChild("head").addChild("HornL3", ModelPartBuilder.create().uv(46, 36).mirrored().cuboid(-0.5F, -0.8F, 0.0F, 1, 2, 5), ModelTransform.of(4.0F, -4.0F, 0.7F, -0.06981317007977318F, 0.4886921905584123F, 0.08726646259971647F));

        root.getChild("head").addChild("HeadFront", ModelPartBuilder.create().uv(6, 44).cuboid(-3.5F, -2.8F, -8.8F, 7, 2, 5), ModelTransform.of(0.0F, -5.6F, 0.0F, 0.045553093477052F, 0.0F, 0.0F));
        root.getChild("head").addChild("Jaw", ModelPartBuilder.create().uv(6, 51).cuboid(-3.5F, 4.0F, -7.4F, 7, 2, 5), ModelTransform.of(0.0F, -5.4F, 0.0F, -0.091106186954104F, 0.0F, 0.0F));

        root.getChild("right_arm").addChild("RightShoulderSpike1", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, -0.5F, 1, 3, 1), ModelTransform.of(-0.5F, -1.2F, 0.0F, -3.141592653589793F, 0.0F, -0.17453292519943295F));
        root.getChild("left_arm").addChild("LeftShoulderSpike1", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, -0.5F, 1, 3, 1), ModelTransform.of(0.5F, -1.2F, 0.0F, -3.141592653589793F, 0.0F, 0.17453292519943295F));

        root.getChild("right_arm").addChild("RightShoulderSpike2", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, -0.5F, 1, 3, 1), ModelTransform.of(-1.8F, -0.1F, 0.0F, -3.141592653589793F, 0.0F, -0.2617993877991494F));
        root.getChild("left_arm").addChild("LeftShoulderSpike2", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, -0.5F, 1, 3, 1), ModelTransform.of(1.8F, -0.1F, 0.0F, -3.141592653589793F, 0.0F, 0.2617993877991494F));

        root.getChild("body").addChild("BackSpike1", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, -0.5F, 1, 3, 1), ModelTransform.of(0.0F, 0.9F, 0.2F, 1.1838568316277536F, 0.0F, 0.0F));
        root.getChild("body").addChild("BackSpike2", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, -0.5F, 1, 3, 1), ModelTransform.of(0.0F, 3.5F, 0.6F, 1.1838568316277536F, 0.0F, 0.0F));
        root.getChild("body").addChild("BackSpike3", ModelPartBuilder.create().uv(0, 34).cuboid(-0.5F, 0.0F, -0.5F, 1, 3, 1), ModelTransform.of(0.0F, 6.4F, 0.0F, 1.1838568316277536F, 0.0F, 0.0F));

        root.getChild("head").getChild("HornR").addChild("HornR2", ModelPartBuilder.create().uv(46, 36).mirrored().cuboid(-0.5F, -0.8F, 0.0F, 1, 2, 5), ModelTransform.of(0.0F, 0.3F, 4.5F, -0.07504915783575616F, 0.0F, 0.0F));
        root.getChild("head").getChild("HornL").addChild("HornL2", ModelPartBuilder.create().uv(46, 36).mirrored().cuboid(-0.5F, -0.8F, 0.0F, 1, 2, 5), ModelTransform.of(0.0F, 0.3F, 4.5F, -0.07504915783575616F, 0.0F, 0.0F));

        root.getChild("head").getChild("HeadFront").addChild("Teeth1", ModelPartBuilder.create().uv(6, 34).cuboid(-3.6F, 0.1F, -8.9F, 4, 1, 5), ModelTransform.pivot(0.0F, -1.0F, 0.0F));
        root.getChild("head").getChild("HeadFront").addChild("Teeth2", ModelPartBuilder.create().uv(6, 34).mirrored().cuboid(-0.4F, 0.1F, -8.9F, 4, 1, 5), ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        return modelData;
    }

    public static ModelPart getBakedModel(boolean inner) {
        return inner ? INNER_MODEL : OUTER_MODEL;
    }
}
