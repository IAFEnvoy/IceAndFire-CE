package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityDreadHorse;
import com.iafenvoy.iceandfire.render.entity.layer.LayerGenericGlowing;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.util.Identifier;

public class RenderDreadHorse extends MobEntityRenderer<EntityDreadHorse, HorseEntityModel<EntityDreadHorse>> {
    public static final Identifier TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_horse.png");
    public static final Identifier TEXTURE_EYES = Identifier.of(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_horse_eyes.png");

    public RenderDreadHorse(EntityRendererFactory.Context context) {
        super(context, new HorseEntityModel<>(context.getPart(EntityModelLayers.HORSE)), 0.75F);
        this.addFeature(new LayerGenericGlowing<>(this, TEXTURE_EYES));
    }

    @Override
    public Identifier getTexture(EntityDreadHorse entity) {
        return TEXTURE;
    }
}
