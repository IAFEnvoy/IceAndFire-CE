package com.iafenvoy.iceandfire.render.model.armor;

import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SilverArmorModel extends ArmorModelBase {
    private static final ModelPart INNER_MODEL = createMesh(CubeDeformation.NONE.extend(INNER_MODEL_OFFSET), 0.0F).getRoot().bake(64, 64);
    private static final ModelPart OUTER_MODEL = createMesh(CubeDeformation.NONE.extend(OUTER_MODEL_OFFSET), 0.0F).getRoot().bake(64, 64);

    public SilverArmorModel(boolean inner) {
        super(getBakedModel(inner));
    }

    public static MeshDefinition createMesh(CubeDeformation deformation, float offset) {
        MeshDefinition modelData = HumanoidModel.createMesh(deformation, offset);
        PartDefinition root = modelData.getRoot();
        root.getChild("head").addOrReplaceChild("faceGuard", CubeListBuilder.create().texOffs(30, 47).addBox(-4.5F, -3.0F, -6.1F, 9, 9, 8), PartPose.offsetAndRotation(0.0F, -6.6F, 1.9F, -0.7285004297824331F, 0.0F, 0.0F));
        root.getChild("head").addOrReplaceChild("helmWingR", CubeListBuilder.create().texOffs(2, 37).addBox(-0.5F, -1.0F, 0.0F, 1, 4, 6), PartPose.offsetAndRotation(-3.0F, -6.3F, 1.3F, 0.5235987755982988F, -0.4363323129985824F, -0.05235987755982988F));
        root.getChild("head").addOrReplaceChild("helmWingL", CubeListBuilder.create().texOffs(2, 37).mirror().addBox(-0.5F, -1.0F, 0.0F, 1, 4, 6), PartPose.offsetAndRotation(3.0F, -6.3F, 1.3F, 0.5235987755982988F, 0.4363323129985824F, 0.05235987755982988F));

        root.getChild("hat").addOrReplaceChild("crest", CubeListBuilder.create().texOffs(18, 32).addBox(0.0F, -0.5F, 0.0F, 1, 9, 9), PartPose.offsetAndRotation(0.0F, -7.9F, -0.1F, 1.2292353921796064F, 0.0F, 0.0F));

        root.getChild("body").addOrReplaceChild("robeLowerBack", CubeListBuilder.create().texOffs(4, 55).mirror().addBox(-4.0F, 0.0F, -2.5F, 8, 8, 1), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 3.141592653589793F, 0.0F));
        root.getChild("body").addOrReplaceChild("robeLower", CubeListBuilder.create().texOffs(4, 55).addBox(-4.0F, 0.0F, -2.5F, 8, 8, 1), PartPose.offset(0.0F, 12.0F, 0.0F));
        return modelData;
    }

    public static ModelPart getBakedModel(boolean inner) {
        return inner ? INNER_MODEL : OUTER_MODEL;
    }
}
