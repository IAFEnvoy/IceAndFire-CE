package com.iafenvoy.iceandfire.render.model.armor;

import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DragonSteelLightningArmorModel extends ArmorModelBase {
    private static final ModelPart INNER_MODEL = createMesh(CubeDeformation.NONE.extend(INNER_MODEL_OFFSET), 0.0F, true).getRoot().bake(64, 64);
    private static final ModelPart OUTER_MODEL = createMesh(CubeDeformation.NONE.extend(OUTER_MODEL_OFFSET), 0.0F, false).getRoot().bake(64, 64);

    public DragonSteelLightningArmorModel(boolean inner) {
        super(getBakedModel(inner));
    }

    public static MeshDefinition createMesh(CubeDeformation deformation, float offset, boolean inner) {
        MeshDefinition modelData = HumanoidModel.createMesh(deformation, offset);
        PartDefinition root = modelData.getRoot();
        float legYOffset = inner ? 0.0F : 2.0F;

        root.getChild("head").addOrReplaceChild("HornR", CubeListBuilder.create().texOffs(9, 39).addBox(-1.0F, -0.5F, 0.0F, 2, 2, 4), PartPose.offsetAndRotation(-2.8F, -7.9F, -4.2F, 0.27314402793711257F, -0.24434609527920614F, 0.0F));
        root.getChild("head").addOrReplaceChild("HornL", CubeListBuilder.create().texOffs(9, 39).mirror().addBox(-1.0F, -0.5F, 0.0F, 2, 2, 4), PartPose.offsetAndRotation(2.8F, -7.9F, -4.2F, 0.27314402793711257F, 0.24434609527920614F, 0.0F));

        root.getChild("head").addOrReplaceChild("HornR2", CubeListBuilder.create().texOffs(9, 38).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5), PartPose.offsetAndRotation(-3.2F, -5.4F, -4.0F, -0.25185101106278174F, -0.296705972839036F, 0.0F));
        root.getChild("head").addOrReplaceChild("HornL2", CubeListBuilder.create().texOffs(9, 38).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5), PartPose.offsetAndRotation(3.2F, -5.4F, -4.0F, -0.25185101106278174F, 0.296705972839036F, 0.0F));

        root.getChild("head").addOrReplaceChild("HornR4", CubeListBuilder.create().texOffs(9, 38).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5), PartPose.offsetAndRotation(-3.2F, -6.4F, -3.0F, 0.0F, -0.296705972839036F, 0.0F));
        root.getChild("head").addOrReplaceChild("HornL4", CubeListBuilder.create().texOffs(9, 38).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5), PartPose.offsetAndRotation(3.2F, -6.4F, -3.0F, 0.0F, 0.296705972839036F, 0.0F));

        root.getChild("head").addOrReplaceChild("visor1", CubeListBuilder.create().texOffs(27, 50).addBox(-4.7F, -13.3F, -4.9F, 4, 5, 8), PartPose.offset(0.0F, 9.0F, 0.2F));
        root.getChild("head").addOrReplaceChild("visor2", CubeListBuilder.create().texOffs(27, 50).mirror().addBox(0.8F, -13.3F, -4.9F, 4, 5, 8), PartPose.offset(-0.1F, 9.0F, 0.2F));

        root.getChild("right_arm").addOrReplaceChild("sleeveRight", CubeListBuilder.create().texOffs(36, 33).addBox(-4.5F, -2.1F, -2.4F, 5, 4, 5), PartPose.offsetAndRotation(0.3F, -0.3F, 0.0F, 0.0F, 0.0F, -0.12217304763960307F));
        root.getChild("left_arm").addOrReplaceChild("sleeveLeft", CubeListBuilder.create().texOffs(36, 33).mirror().addBox(-0.5F, -2.1F, -2.4F, 5, 4, 5), PartPose.offsetAndRotation(-0.7F, -0.3F, 0.0F, 0.0F, 0.0F, 0.12217304763960307F));

        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -2.0F + legYOffset, -2.0F, 4.0F, 12.0F, 4.0F, deformation), PartPose.offset(-1.9F, 12.0F + offset, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, -2.0F + legYOffset, -2.0F, 4.0F, 12.0F, 4.0F, deformation), PartPose.offset(1.9F, 12.0F + offset, 0.0F));
        root.getChild("right_leg").addOrReplaceChild("robeLowerRight", CubeListBuilder.create().texOffs(4, 51).mirror().addBox(-2.1F, 0.0F, -2.5F, 4, 7, 5), PartPose.offset(0.0F, -0.2F, 0.0F));
        root.getChild("left_leg").addOrReplaceChild("robeLowerLeft", CubeListBuilder.create().texOffs(4, 51).addBox(-1.9F, 0.0F, -2.5F, 4, 7, 5), PartPose.offset(0.0F, -0.2F, 0.0F));

        root.getChild("head").getChild("HornR").addOrReplaceChild("HornR2_1", CubeListBuilder.create().texOffs(9, 38).mirror().addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5), PartPose.offsetAndRotation(0.0F, 0.3F, 3.6F, -0.08726646259971647F, 0.0F, 0.0F));
        root.getChild("head").getChild("HornL").addOrReplaceChild("HornL2_1", CubeListBuilder.create().texOffs(9, 38).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 5), PartPose.offsetAndRotation(0.0F, 0.3F, 3.6F, -0.08726646259971647F, 0.0F, 0.0F));

        root.getChild("head").getChild("HornR").getChild("HornR2_1").addOrReplaceChild("HornR3", CubeListBuilder.create().texOffs(24, 44).mirror().addBox(-1.0F, -0.8F, 0.0F, 2, 2, 4), PartPose.offsetAndRotation(0.0F, 0.0F, 4.3F, 0.17453292519943295F, 0.0F, 0.0F));
        root.getChild("head").getChild("HornL").getChild("HornL2_1").addOrReplaceChild("HornL3", CubeListBuilder.create().texOffs(24, 44).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 4), PartPose.offsetAndRotation(0.0F, 0.0F, 4.3F, 0.17453292519943295F, 0.0F, 0.0F));

        root.getChild("head").getChild("HornR2").addOrReplaceChild("HornR3_1", CubeListBuilder.create().texOffs(25, 45).mirror().addBox(-1.0F, -0.8F, 0.0F, 2, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 4.3F, 0.13962634015954636F, 0.0F, 0.0F));
        root.getChild("head").getChild("HornL2").addOrReplaceChild("HornL3_1", CubeListBuilder.create().texOffs(25, 45).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 4.3F, 0.13962634015954636F, 0.0F, 0.0F));

        root.getChild("head").getChild("HornR4").addOrReplaceChild("HornR5", CubeListBuilder.create().texOffs(25, 45).mirror().addBox(-1.0F, -0.8F, 0.0F, 2, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 4.3F, 0.13962634015954636F, 0.0F, 0.0F));
        root.getChild("head").getChild("HornL4").addOrReplaceChild("HornL5", CubeListBuilder.create().texOffs(25, 45).addBox(-1.0F, -0.8F, 0.0F, 2, 2, 3), PartPose.offsetAndRotation(0.0F, 0.0F, 4.3F, 0.13962634015954636F, 0.0F, 0.0F));

        return modelData;
    }

    public static ModelPart getBakedModel(boolean inner) {
        return inner ? INNER_MODEL : OUTER_MODEL;
    }
}
