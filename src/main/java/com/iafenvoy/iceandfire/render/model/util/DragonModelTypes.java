package com.iafenvoy.iceandfire.render.model.util;

public enum DragonModelTypes implements IEnumDragonModelTypes {
    FIRE_DRAGON_MODEL("fire"),
    ICE_DRAGON_MODEL("ice"),
    LIGHTNING_DRAGON_MODEL("lightning");

    final String modelType;

    DragonModelTypes(String modelType) {
        this.modelType = modelType;
    }

    @Override
    public String getModelType() {
        return this.modelType;
    }
}
