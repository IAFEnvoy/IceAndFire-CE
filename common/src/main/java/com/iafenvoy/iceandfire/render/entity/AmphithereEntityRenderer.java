package com.iafenvoy.iceandfire.render.entity;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.entity.AmphithereEntity;
import com.iafenvoy.iceandfire.render.model.AmphithereModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AmphithereEntityRenderer extends MobEntityRenderer<AmphithereEntity, AmphithereModel> {
    public static final Identifier TEXTURE_BLUE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_blue.png");
    public static final Identifier TEXTURE_BLUE_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_blue_blink.png");
    public static final Identifier TEXTURE_GREEN = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_green.png");
    public static final Identifier TEXTURE_GREEN_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_green_blink.png");
    public static final Identifier TEXTURE_OLIVE = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_olive.png");
    public static final Identifier TEXTURE_OLIVE_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_olive_blink.png");
    public static final Identifier TEXTURE_RED = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_red.png");
    public static final Identifier TEXTURE_RED_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_red_blink.png");
    public static final Identifier TEXTURE_YELLOW = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_yellow.png");
    public static final Identifier TEXTURE_YELLOW_BLINK = Identifier.of(IceAndFire.MOD_ID, "textures/entity/amphithere/amphithere_yellow_blink.png");

    public AmphithereEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AmphithereModel(), 1.6F);
    }

    @Override
    protected void scale(AmphithereEntity entity, MatrixStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(2.0F, 2.0F, 2.0F);
    }

    @Override
    public Identifier getTexture(AmphithereEntity amphithere) {
        switch (amphithere.getVariant()) {
            case 0 -> {
                if (amphithere.isBlinking()) return TEXTURE_BLUE_BLINK;
                else return TEXTURE_BLUE;
            }
            case 1 -> {
                if (amphithere.isBlinking()) return TEXTURE_GREEN_BLINK;
                else return TEXTURE_GREEN;
            }
            case 2 -> {
                if (amphithere.isBlinking()) return TEXTURE_OLIVE_BLINK;
                else return TEXTURE_OLIVE;
            }
            case 3 -> {
                if (amphithere.isBlinking()) return TEXTURE_RED_BLINK;
                else return TEXTURE_RED;

            }
            case 4 -> {
                if (amphithere.isBlinking()) return TEXTURE_YELLOW_BLINK;
                else return TEXTURE_YELLOW;
            }
        }
        return TEXTURE_GREEN;
    }

}
