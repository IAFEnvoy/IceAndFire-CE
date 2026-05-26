package com.iafenvoy.iceandfire.render.model.animator;

import com.iafenvoy.iceandfire.entity.IceDragonEntity;
import com.iafenvoy.iceandfire.render.model.util.DragonModelTypes;
import com.iafenvoy.iceandfire.render.model.util.DragonPoses;
import com.iafenvoy.uranus.client.model.TabulaModel;

public class IceDragonTabulaModelAnimator extends DragonTabulaModelAnimator<IceDragonEntity> {
    public IceDragonTabulaModelAnimator() {
        super(DragonPoses.GROUND_POSE, DragonModelTypes.ICE_DRAGON_MODEL);
        this.walkPoses = new TabulaModel[]{this.getModel(DragonPoses.WALK1), this.getModel(DragonPoses.WALK2), this.getModel(DragonPoses.WALK3), this.getModel(DragonPoses.WALK4)};
        this.flyPoses = new TabulaModel[]{this.getModel(DragonPoses.FLIGHT1), this.getModel(DragonPoses.FLIGHT2), this.getModel(DragonPoses.FLIGHT3), this.getModel(DragonPoses.FLIGHT4), this.getModel(DragonPoses.FLIGHT5), this.getModel(DragonPoses.FLIGHT6)};
        this.swimPoses = new TabulaModel[]{this.getModel(DragonPoses.SWIM1), this.getModel(DragonPoses.SWIM2), this.getModel(DragonPoses.SWIM3), this.getModel(DragonPoses.SWIM4), this.getModel(DragonPoses.SWIM5)};
    }
}
