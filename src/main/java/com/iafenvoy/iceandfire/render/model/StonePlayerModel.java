package com.iafenvoy.iceandfire.render.model;

import com.iafenvoy.iceandfire.entity.StoneStatueEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class StonePlayerModel extends HumanoidModel<StoneStatueEntity> {
    public StonePlayerModel(ModelPart p_170677_) {
        super(p_170677_);
    }

    @Override
    public void setupAnim(@NotNull StoneStatueEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
