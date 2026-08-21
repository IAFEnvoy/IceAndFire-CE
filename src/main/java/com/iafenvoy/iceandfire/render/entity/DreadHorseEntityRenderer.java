package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.DreadHorseEntity;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EquineRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class DreadHorseEntityRenderer extends AbstractHorseRenderer<DreadHorseEntity, EquineRenderState, HorseModel> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_horse.png");
    public static final Identifier TEXTURE_EYES = Identifier.fromNamespaceAndPath(IceAndFire.MOD_ID, "textures/entity/dread/dread_knight_horse_eyes.png");

    public DreadHorseEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(ModelLayers.SKELETON_HORSE)), new HorseModel(context.bakeLayer(ModelLayers.SKELETON_HORSE_BABY)));
        this.shadowRadius = 0.75F;
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull EquineRenderState state) {
        return TEXTURE;
    }

    @Override
    public EquineRenderState createRenderState() {
        return new EquineRenderState();
    }
}
