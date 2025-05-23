package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.EntityGorgon;
import com.iafenvoy.iceandfire.render.entity.layer.LayerGorgonEyes;
import com.iafenvoy.iceandfire.render.model.ModelGorgon;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RenderGorgon extends MobEntityRenderer<EntityGorgon, ModelGorgon> {
    public static final Identifier PASSIVE_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/gorgon/gorgon_passive.png");
    public static final Identifier AGRESSIVE_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/gorgon/gorgon_active.png");
    public static final Identifier DEAD_TEXTURE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/gorgon/gorgon_decapitated.png");

    public RenderGorgon(EntityRendererFactory.Context context) {
        super(context, new ModelGorgon(), 0.4F);
        this.features.add(new LayerGorgonEyes(this));
    }

    @Override
    public void scale(EntityGorgon LivingEntityIn, MatrixStack stack, float partialTickTime) {
        stack.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public Identifier getTexture(EntityGorgon gorgon) {
        if (gorgon.getAnimation() == EntityGorgon.ANIMATION_SCARE) return AGRESSIVE_TEXTURE;
        else if (gorgon.deathTime > 0) return DEAD_TEXTURE;
        else return PASSIVE_TEXTURE;
    }
}
