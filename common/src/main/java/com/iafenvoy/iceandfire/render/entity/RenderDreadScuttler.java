package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityDreadScuttler;
import com.iafenvoy.iceandfire.render.entity.layer.LayerGenericGlowing;
import com.iafenvoy.iceandfire.render.model.ModelDreadScuttler;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderDreadScuttler extends MobEntityRenderer<EntityDreadScuttler, ModelDreadScuttler> {
    public static final Identifier TEXTURE_EYES = Identifier.of(IceAndFire.MOD_ID, "textures/entity/dread/dread_scuttler_eyes.png");
    public static final Identifier TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/dread/dread_scuttler.png");

    public RenderDreadScuttler(EntityRendererFactory.Context context) {
        super(context, new ModelDreadScuttler(), 0.75F);
        this.addFeature(new LayerGenericGlowing<>(this, TEXTURE_EYES));
    }

    @Override
    public void scale(EntityDreadScuttler LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.scale(LivingEntityIn.getSize(), LivingEntityIn.getSize(), LivingEntityIn.getSize());
    }

    @Override
    public Identifier getTexture(EntityDreadScuttler beast) {
        return TEXTURE;
    }
}
