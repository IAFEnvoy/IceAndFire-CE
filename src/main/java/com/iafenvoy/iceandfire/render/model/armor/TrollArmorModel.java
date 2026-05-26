package com.iafenvoy.iceandfire.render.model.armor;

import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class TrollArmorModel extends ArmorModelBase {
    private static final ModelPart INNER_MODEL = createMesh(CubeDeformation.NONE.extend(INNER_MODEL_OFFSET), 0.0F).getRoot().bake(64, 64);
    private static final ModelPart OUTER_MODEL = createMesh(CubeDeformation.NONE.extend(OUTER_MODEL_OFFSET), 0.0F).getRoot().bake(64, 64);

    public TrollArmorModel(boolean inner) {
        super(getBakedModel(inner));
    }

    public static MeshDefinition createMesh(CubeDeformation deformation, float offset) {
        MeshDefinition modelData = HumanoidModel.createMesh(deformation, offset);
        PartDefinition root = modelData.getRoot();

        root.getChild("head").addOrReplaceChild("hornL", CubeListBuilder.create().texOffs(3, 41).mirror().addBox(-1.0F, -0.5F, 0.0F, 1, 2, 5), PartPose.offsetAndRotation(3.0F, -2.2F, -3.0F, -0.7740535232594852F, 2.9595548126067843F, -0.27314402793711257F));
        root.getChild("head").getChild("hornL").addOrReplaceChild("hornL2", CubeListBuilder.create().texOffs(15, 50).mirror().addBox(-0.51F, -0.8F, 0.0F, 1, 2, 7), PartPose.offsetAndRotation(-0.4F, 1.3F, 4.5F, 1.2747884856566583F, 0.0F, 0.0F));

        root.getChild("head").addOrReplaceChild("hornR", CubeListBuilder.create().texOffs(4, 41).mirror().addBox(-0.5F, -0.5F, 0.0F, 1, 2, 5), PartPose.offsetAndRotation(-3.3F, -2.2F, -3.0F, -0.7740535232594852F, -2.9595548126067843F, 0.27314402793711257F));
        root.getChild("head").getChild("hornR").addOrReplaceChild("hornR2", CubeListBuilder.create().texOffs(15, 50).mirror().addBox(-0.01F, -0.8F, 0.0F, 1, 2, 7), PartPose.offsetAndRotation(-0.6F, 1.3F, 4.5F, 1.2747884856566583F, 0.0F, 0.0F));
        return modelData;
    }

    public static ModelPart getBakedModel(boolean inner) {
        return inner ? INNER_MODEL : OUTER_MODEL;
    }
}
